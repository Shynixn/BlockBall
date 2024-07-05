package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard

class BlockBallHubGameImpl(
    override val arena: Arena,
    gameService: GameService,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val bossBarService: BossBarService,
    packetService: PacketService,
    scoreboardService: ScoreboardService,
    ballEntityService: BallEntityService,
    chatMessageService: ChatMessageService
) : BlockBallGameImpl(
    arena,
    gameService,
    placeHolderService,
    packetService,
    plugin,
    bossBarService,
    scoreboardService,
    ballEntityService,
    chatMessageService,
    playerDataRepository
),
    BlockBallHubGame {

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun join(player: Player, team: Team?): JoinResult {
        if (!isAllowedToJoinWithPermissions(player)) {
            return JoinResult.NO_PERMISSION
        }

        val otherGame = gameService.getGameFromPlayer(player)

        if (otherGame != null) {
            leave(player)
        }

        val event = GameJoinEvent(player, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return JoinResult.EVENT_CANCELLED
        }

        var joiningTeam = team

        if (arena.meta.lobbyMeta.onlyAllowEventTeams) {
            if (joiningTeam == Team.RED && redTeam.size > blueTeam.size) {
                joiningTeam = null
            } else if (joiningTeam == Team.BLUE && blueTeam.size > redTeam.size) {
                joiningTeam = null
            }
        }

        if (joiningTeam == null) {
            joiningTeam = Team.BLUE
            if (redTeam.size < blueTeam.size) {
                joiningTeam = Team.RED
            }
        }

        val result = if (joiningTeam == Team.RED && redTeam.size < arena.meta.redTeamMeta.maxAmount) {
            this.prepareLobbyStorageForPlayer(player, joiningTeam, arena.meta.redTeamMeta)
            JoinResult.SUCCESS_RED
        } else if (joiningTeam == Team.BLUE && blueTeam.size < arena.meta.blueTeamMeta.maxAmount) {
            this.prepareLobbyStorageForPlayer(player, joiningTeam, arena.meta.blueTeamMeta)
            JoinResult.SUCCESS_BLUE
        } else {
            JoinResult.TEAM_FULL
        }

        if (result == JoinResult.TEAM_FULL) {
            return result
        }

        plugin.launch {
            val playerData = playerDataRepository.getByPlayer(player)
            if (playerData != null) {
                playerData.statsMeta.joinedGames++
            }
        }

        return result
    }

    /**
     * Leaves the given player.
     */
    override fun leave(player: Player): LeaveResult {
        if (!ingamePlayersStorage.containsKey(player)) {
            return LeaveResult.NOT_IN_MATCH
        }

        val event = GameLeaveEvent(player, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return LeaveResult.EVENT_CANCELLED
        }

        if (scoreboard != null && player.scoreboard == scoreboard) {
            player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        }

        if (bossBar != null) {
            bossBarService.removePlayer(bossBar, player)
        }

        for (hologram in holograms) {
            if (hologram.players.contains(player)) {
                hologram.players.remove(player)
            }
        }

        val stats = ingamePlayersStorage[player]!!
        player.gameMode = stats.gameMode
        player.allowFlight = stats.gameMode == GameMode.CREATIVE
        player.isFlying = false
        player.walkSpeed = stats.walkingSpeed.toFloat()
        player.scoreboard = stats.scoreboard as Scoreboard

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents.clone()
            player.inventory.setArmorContents(stats.armorContents.clone())
            player.updateInventory()
        }

        if (stats.team == Team.RED) {
            player.sendMessage(
                placeHolderService.replacePlaceHolders(
                    arena.meta.redTeamMeta.leaveMessage,
                    player,
                    this,
                    null,
                    null
                )
            )
        } else if (stats.team == Team.BLUE) {
            player.sendMessage(
                placeHolderService.replacePlaceHolders(
                    arena.meta.redTeamMeta.leaveMessage,
                    player,
                    this,
                    null,
                    null
                )
            )
        }

        ingamePlayersStorage.remove(player)

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
        }

        return LeaveResult.SUCCESS
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(ticks: Int) {
        // Handle HubGame ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            onUpdateSigns()
            close()
            if (ingamePlayersStorage.isNotEmpty() && arena.gameType != GameType.BUNGEE) {
                closing = true
            }
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            closing = true
        }

        // Handle Ball.
        this.fixBallPositionSpawn()
        if (ticks >= 20) {
            this.handleBallSpawning()
        }

        // Update signs and protections.
        super.handle(ticks)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (closed) {
            return
        }

        status = GameState.DISABLED
        closed = true
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
        holograms.forEach { h -> h.remove() }
        holograms.clear()

        if (bossBar != null) {
            bossBarService.cleanResources(bossBar)
        }
    }

    /**
     * Prepares the storage for a hubgame.
     */
    private fun prepareLobbyStorageForPlayer(player: Player, team: Team, teamMeta: TeamMeta) {
        val stats = GameStorage()
        ingamePlayersStorage[player] = stats

        stats.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        stats.team = team
        stats.goalTeam = team
        stats.gameMode = player.gameMode
        stats.flying = player.isFlying
        stats.allowedFlying = player.allowFlight
        stats.walkingSpeed = player.walkSpeed.toDouble()
        stats.scoreboard = player.scoreboard
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
        stats.level = player.level
        stats.exp = player.exp.toDouble()
        stats.maxHealth = player.maxHealth
        stats.health = player.health
        stats.hunger = player.foodLevel

        player.gameMode = arena.meta.lobbyMeta.gamemode
        player.allowFlight = false
        player.isFlying = false
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = teamMeta.inventory.map {
                if (it != null) {
                    val configuration = org.bukkit.configuration.file.YamlConfiguration()
                    configuration.loadFromString(it)
                    configuration.getItemStack("item")
                } else {
                    null
                }
            }.toTypedArray()
            player.inventory.setArmorContents(
                teamMeta.armor.map {
                    if (it != null) {
                        val configuration = org.bukkit.configuration.file.YamlConfiguration()
                        configuration.loadFromString(it)
                        configuration.getItemStack("item")
                    } else {
                        null
                    }
                }.toTypedArray()
            )
            player.updateInventory()
        }

        if (arena.meta.hubLobbyMeta.teleportOnJoin) {
            this.respawn(player)
        } else {
            val velocityIntoArena = player.location.direction.normalize().multiply(0.5)
            player.velocity = velocityIntoArena
        }

        val message = placeHolderService.replacePlaceHolders(teamMeta.joinMessage, player, this, teamMeta, 0)
        player.sendMessage(message)
    }
}
