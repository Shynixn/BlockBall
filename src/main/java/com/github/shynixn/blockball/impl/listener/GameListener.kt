package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerHubGame
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.event.BallRayTraceEvent
import com.github.shynixn.blockball.event.BallTouchPlayerEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.packet.api.event.PacketAsyncEvent
import com.github.shynixn.mcutils.packet.api.meta.enumeration.InteractionType
import com.github.shynixn.mcutils.packet.api.packet.PacketInInteractEntity
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
import org.bukkit.plugin.Plugin

/**
 * Game Listener for the most important game events.
 */
class GameListener (
    private val gameService: GameService,
    private val soccerBallFactory: SoccerBallFactory,
    private val plugin: Plugin,
    private val playerDataRepository: CachePlayerRepository<PlayerInformation>
) : Listener {
    private val playerCache = HashSet<Player>()

    /**
     * Gets called when a packet arrives.
     */
    @EventHandler
    fun onPacketEvent(event: PacketAsyncEvent) {
        val packet = event.packet

        if (packet !is PacketInInteractEntity) {
            return
        }

        plugin.launch {
            val game = gameService.getByPlayer(event.player) ?: return@launch
            val ball = soccerBallFactory.findBallByEntityId(packet.entityId) ?: return@launch

            if (game.ball != ball) {
                return@launch
            }

            if (packet.actionType == InteractionType.ATTACK) {
                ball.kickByPlayer(event.player)
            } else {
                ball.passByPlayer(event.player)
            }
        }
    }

    /**
     * Gets called when a player leaves the server and the game.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player

        val playerGame = gameService.getByPlayer(player)
        playerGame?.leave(player)

        val spectateGame = gameService.getByPlayer(player)
        spectateGame?.leave(player)

        plugin.launch {
            val existingPlayerData = playerDataRepository.getByPlayer(player)

            if (existingPlayerData != null) {
                playerDataRepository.save(existingPlayerData)
                playerDataRepository.clearByPlayer(player)
            }
        }
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        plugin.launch {
            val player = event.player
            val existingPlayerData = playerDataRepository.getByPlayer(player)

            if (existingPlayerData == null) {
                val playerData = PlayerInformation()
                playerData.playerUUID = player.uniqueId.toString()
                playerData.playerName = player.name
                playerData.statsMeta.version = 2
                playerDataRepository.save(playerData)
            } else {
                // Migration of stats meta from 7.x to 7.2.0
                if (existingPlayerData.statsMeta.version == 1) {
                    existingPlayerData.statsMeta.version = 2
                    existingPlayerData.statsMeta.scoredGoalsFull = existingPlayerData.statsMeta.scoredGoals
                    playerDataRepository.save(existingPlayerData)
                }
            }
        }
    }

    /**
     * Gets called when the player teleports and handles leaving the blockgame under certain conditions.
     */
    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        val game = gameService.getByPlayer(event.player) ?: return

        if (game !is SoccerHubGame) {
            return
        }

        if (game.arena.isLocationIn2dSelection(event.to!!.toVector3d())) {
            return
        }

        game.leave(event.player)
    }

    /**
     * Gets called when the foodLevel changes and cancels it if the player is inside of a game.
     */
    @EventHandler
    fun onPlayerHungerEvent(event: FoodLevelChangeEvent) {
        val game = gameService.getByPlayer(event.entity as Player)

        if (game != null) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when the player interacts with his inventory and cancels it.
     */
    @EventHandler
    fun onPlayerClickInventoryEvent(event: InventoryClickEvent) {
        val game = gameService.getByPlayer(event.whoClicked as Player)

        if (game != null && !(event.whoClicked as Player).hasPermission(Permission.OBSOLETE_INVENTORY.permission)) {
            event.isCancelled = true
            event.whoClicked.closeInventory()
        }
    }

    /**
     * Gets called when a player opens his inventory and cancels the action.
     */
    @EventHandler
    fun onPlayerOpenInventoryEvent(event: InventoryOpenEvent) {
        val game = gameService.getByPlayer(event.player as Player)

        if (game != null && !(event.player).hasPermission(Permission.OBSOLETE_INVENTORY.permission)) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player drops his item and cancels the action.
     */
    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val game = gameService.getByPlayer(event.player)

        if (game != null && !(event.player).hasPermission(Permission.OBSOLETE_INVENTORY.permission)) {
            event.isCancelled = true

            plugin.launch {
                delay(10.ticks)
                event.player.updateInventory()
            }
        }
    }

    /**
     * Gets called when a player dies by strange occasions which are not handled by the [EntityDamageEvent].
     */
    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        val game = gameService.getByPlayer(event.player) ?: return

        val team = game.ingamePlayersStorage[event.player]!!.goalTeam

        val teamMeta = if (team == Team.RED) {
            game.arena.meta.redTeamMeta
        } else if (team == Team.BLUE) {
            game.arena.meta.blueTeamMeta
        } else if (team == Team.REFEREE) {
            game.arena.meta.refereeTeamMeta
        } else {
            return
        }

        if (teamMeta.spawnpoint == null) {
            event.respawnLocation = game.arena.ballSpawnPoint!!.toLocation()
        } else {
            event.respawnLocation = teamMeta.spawnpoint!!.toLocation()
        }
    }

    /**
     * Player Death event.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val game = gameService.getByPlayer(event.entity) ?: return

        if (playerCache.contains(event.entity)) {
            return
        }

        game.applyDeathPoints(event.entity)
    }

    /**
     * Cancels all fall damage in the games.
     */
    @EventHandler
    fun onPlayerDamageEvent(event: EntityDamageEvent) {
        if (event.entity !is Player) {
            return
        }

        val player = event.entity as Player
        val game = gameService.getByPlayer(player) ?: return

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !game.arena.meta.customizingMeta.damageEnabled) {
            event.isCancelled = true
            return
        }

        if (player.health - event.finalDamage > 0) {
            return
        }

        @Suppress("DEPRECATION")
        player.health = player.maxHealth

        playerCache.add(player)

        game.applyDeathPoints(player)
        game.respawn(player)

        plugin.launch {
            delay(40.ticks)
            playerCache.remove(player)
        }
    }

    /**
     * Caches the last interacting entity with the ball.
     */
    @EventHandler
    fun onBallInteractEvent(event: BallTouchPlayerEvent) {
        val game = gameService.getAll().find { p -> p.ball != null && p.ball!! == event.ball }

        if (game != null) {
            game.lastInteractedEntity = event.player
        }
    }

    /**
     * Is called when the ball requests to move to a target position.
     * Handles the ball forceField of the soccerArena.
     */
    @EventHandler
    fun onBallRayTraceEvent(event: BallRayTraceEvent) {
        for (game in gameService.getAll()) {
            if (game.ball == event.ball && event.ball.isInteractable) {
                val targetPosition = event.targetLocation.toVector3d()
                val sourcePosition = event.ball.getLocation().toVector3d()

                if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(sourcePosition)) {
                    game.notifyBallInGoal(Team.RED)
                    return
                }

                if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(sourcePosition)) {
                    game.notifyBallInGoal(Team.BLUE)
                    return
                }

                if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(targetPosition)) {
                    return
                }

                if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(targetPosition)) {
                    return
                }

                if (!game.arena.isLocationIn2dSelection(targetPosition)) {
                    event.hitBlock = true
                    event.blockDirection = game.arena.getRelativeBlockDirectionToLocation(targetPosition)
                    game.ballBumperCounter++

                    if (game.ballBumperCounter > 60) {
                        // Rescue system, if the ball gets stuck in the walls.
                        event.ball.teleport(game.arena.ballSpawnPoint!!.toLocation())
                        game.ballBumperCounter = 0
                    }

                    return
                } else {
                    game.ballBumperCounter = 0
                }

                return
            }
        }
    }
}
