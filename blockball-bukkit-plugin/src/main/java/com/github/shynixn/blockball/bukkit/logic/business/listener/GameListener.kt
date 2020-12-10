package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.BallRayTraceEvent
import com.github.shynixn.blockball.api.bukkit.event.BallTouchEvent
import com.github.shynixn.blockball.api.bukkit.event.PacketEvent
import com.github.shynixn.blockball.api.business.enumeration.Permission
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.HubGame
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.bukkit.logic.business.extension.hasPermission
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.google.inject.Inject
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class GameListener @Inject constructor(
    private val gameService: GameService,
    private val rightClickManageService: RightclickManageService,
    private val gameActionService: GameActionService,
    private val gameExecutionService: GameExecutionService,
    private val concurrencyService: ConcurrencyService,
    private val gameSoccerService: GameSoccerService,
    private val proxyService: ProxyService,
    private val ballEntityService: BallEntityService
) : Listener {
    private val playerCache = HashSet<Player>()
    private val packetPlayInUseEntityActionField by lazy {
        findClazz("net.minecraft.server.VERSION.PacketPlayInUseEntity")
            .getDeclaredField("action").accessible(true)
    }
    private val packetPlayInUseEntityIdField by lazy {
        findClazz("net.minecraft.server.VERSION.PacketPlayInUseEntity")
            .getDeclaredField("a").accessible(true)
    }

    /**
     * Gets called when a packet arrives.
     */
    @EventHandler
    fun onPacketEvent(event: PacketEvent) {
        val game = gameService.getGameFromPlayer(event.player)

        if (!game.isPresent) {
            return
        }

        val action = packetPlayInUseEntityActionField.get(event.packet)
        val entityId = packetPlayInUseEntityIdField.get(event.packet) as Int
        val ball = ballEntityService.findBallByEntityId(entityId) ?: return
        val isPass = action.toString() != "ATTACK"

        if (game.get().ball != ball) {
            return
        }

        if (isPass) {
            ball.passByPlayer(event.player)
        } else {
            ball.kickByPlayer(event.player)
        }
    }

    /**
     * Gets called when a player leaves the server and the game.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val playerGame = gameService.getGameFromPlayer(event.player)
        val spectateGame = gameService.getGameFromSpectatingPlayer(event.player)

        if (playerGame.isPresent) {
            gameActionService.leaveGame(playerGame.get(), event.player)
        }

        if (spectateGame.isPresent) {
            gameActionService.leaveGame(spectateGame.get(), event.player)
        }

        rightClickManageService.cleanResources(event.player)
    }

    /**
     * Gets called when the player teleports and handles leaving the blockgame under certain conditions.
     */
    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        val optPlayerGame = gameService.getGameFromPlayer(event.player)

        if (!optPlayerGame.isPresent) {
            return
        }

        val game = optPlayerGame.get()

        if (game !is HubGame) {
            return
        }

        if (game.arena.isLocationInSelection(event.to!!.toPosition())) {
            return
        }

        gameActionService.leaveGame(game, event.player)
    }

    /**
     * Gets called when the foodLevel changes and cancels it if the player is inside of a game.
     */
    @EventHandler
    fun onPlayerHungerEvent(event: FoodLevelChangeEvent) {
        val game = gameService.getGameFromPlayer(event.entity as Player)

        if (game.isPresent) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when the player interacts with his inventory and cancels it.
     */
    @EventHandler
    fun onPlayerClickInventoryEvent(event: InventoryClickEvent) {
        val game = gameService.getGameFromPlayer(event.whoClicked as Player)

        if (game.isPresent && !Permission.INVENTORY.hasPermission(event.whoClicked as Player)) {
            event.isCancelled = true
            event.whoClicked.closeInventory()
        }
    }

    /**
     * Gets called when a player opens his inventory and cancels the action.
     */
    @EventHandler
    fun onPlayerOpenInventoryEvent(event: InventoryOpenEvent) {
        val game = gameService.getGameFromPlayer(event.player as Player)

        if (game.isPresent && !Permission.INVENTORY.hasPermission(event.player as Player)) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player drops his item and cancels the action.
     */
    @EventHandler
    fun onPlayerDropItemEvent(event: PlayerDropItemEvent) {
        val game = gameService.getGameFromPlayer(event.player)

        if (game.isPresent && !Permission.INVENTORY.hasPermission(event.player)) {
            event.isCancelled = true

            sync(concurrencyService, 10L) {
                event.player.updateInventory()
            }
        }
    }

    /**
     * Gets called when a player dies by strange occasions which are not handled by the [EntityDamageEvent].
     */
    @EventHandler
    fun onPlayerRespawnEvent(event: PlayerRespawnEvent) {
        val game = gameService.getGameFromPlayer(event.player)

        if (!game.isPresent) {
            return
        }

        val playerStorage = game.get().ingamePlayersStorage[event.player]!!

        val teamMeta = if (playerStorage.team == Team.RED) {
            game.get().arena.meta.redTeamMeta
        } else {
            game.get().arena.meta.blueTeamMeta
        }

        if (teamMeta.spawnpoint == null) {
            event.respawnLocation = game.get().arena.meta.ballMeta.spawnpoint!!.toLocation()
        } else {
            event.respawnLocation = teamMeta.spawnpoint!!.toLocation()
        }
    }

    /**
     * Player Death event.
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        val game = gameService.getGameFromPlayer(event.entity)

        if (!game.isPresent) {
            return
        }

        if (playerCache.contains(event.entity)) {
            return
        }

        gameExecutionService.applyDeathPoints(game.get(), event.entity)
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
        val game = gameService.getGameFromPlayer(player)

        if (!game.isPresent) {
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !game.get().arena.meta.customizingMeta.damageEnabled) {
            event.isCancelled = true
            return
        }

        if (player.health - event.finalDamage > 0) {
            return
        }

        @Suppress("DEPRECATION")
        player.health = player.maxHealth

        playerCache.add(player)

        gameExecutionService.applyDeathPoints(game.get(), player)
        gameExecutionService.respawn(game.get(), player)

        sync(concurrencyService, 40L) {
            playerCache.remove(player)
        }
    }

    /**
     * Caches the last interacting entity with the ball.
     */
    @EventHandler
    fun onBallInteractEvent(event: BallTouchEvent) {
        val game = gameService.getAllGames().find { p -> p.ball != null && p.ball!! == event.ball }

        if (game != null) {
            game.lastInteractedEntity = event.player
        }
    }

    /**
     * Is called when the ball requests to move to a target position.
     * Handles the ball forceField of the arena.
     */
    @EventHandler
    fun onBallRayTraceEvent(event: BallRayTraceEvent) {
        for (game in gameService.getAllGames()) {
            if (game.ball == event.ball) {
                val targetPosition = event.targetLocation.toPosition()
                val sourcePosition = proxyService.toPosition(event.ball.getLocation<Any>())

                if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(sourcePosition)) {
                    gameSoccerService.notifyBallInGoal(game, Team.RED)
                    return
                }

                if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(sourcePosition)) {
                    gameSoccerService.notifyBallInGoal(game, Team.BLUE)
                    return
                }

                if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(targetPosition)) {
                    return
                }

                if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(targetPosition)) {
                    return
                }

                if (!game.arena.isLocationInSelection(targetPosition)) {
                    event.hitBlock = true
                    event.blockDirection = game.arena.getRelativeBlockDirectionToLocation(targetPosition)
                    return
                }

                return
            }
        }
    }

    /**
     * Handles clicking and joining on signs.
     */
    @EventHandler
    fun onClickOnPlacedSign(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.clickedBlock!!.state !is Sign) {
            return
        }

        val location = event.clickedBlock!!.location.toPosition()

        if (rightClickManageService.executeWatchers(event.player, event.clickedBlock!!.location)) {
            return
        }

        for (game in gameService.getAllGames()) {
            when {
                game.arena.meta.lobbyMeta.joinSigns.contains(location) -> {
                    gameActionService.joinGame(game, event.player)
                }
                game.arena.meta.redTeamMeta.signs.contains(location) -> {
                    gameActionService.joinGame(game, event.player, Team.RED)
                }
                game.arena.meta.blueTeamMeta.signs.contains(location) -> {
                    gameActionService.joinGame(game, event.player, Team.BLUE)
                }
                game.arena.meta.lobbyMeta.leaveSigns.contains(location) -> {
                    gameActionService.leaveGame(game, event.player)
                }
            }
        }
    }
}
