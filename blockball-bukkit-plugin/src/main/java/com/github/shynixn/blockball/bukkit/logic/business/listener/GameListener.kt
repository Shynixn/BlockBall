package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.BallInteractEvent
import com.github.shynixn.blockball.api.bukkit.event.BallPostMoveEvent
import com.github.shynixn.blockball.api.business.enumeration.Permission
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.BallForceFieldService
import com.github.shynixn.blockball.api.business.service.GameActionService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.RightclickManageService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.hasPermission
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

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
    private val gameActionService: GameActionService<Game>,
    private val forceFieldService: BallForceFieldService
) : Listener {
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
     * Gets called when a ball move and calculates forcefield interactions.
     *
     * @param event event
     */
    @EventHandler
    fun onBallPostMoveEvent(event: BallPostMoveEvent) {
        val game = this.gameService.getAllGames().firstOrNull { g -> g.ball != null && g.ball == event.ball }

        if (game == null) {
            return
        }

        forceFieldService.calculateForcefieldInteractions(game, event.ball)
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

        val playerStorage = game.get().ingamePlayersStorage[player]!!

        if (playerStorage.team == null) {
            return
        }

        if (playerStorage.team == Team.RED) {
            val spawnpoint = game.get().arena.meta.redTeamMeta.spawnpoint

            if (spawnpoint != null) {
                player.teleport(spawnpoint.toLocation())
                return
            }
        }

        if (playerStorage.team == Team.BLUE) {
            val spawnpoint = game.get().arena.meta.blueTeamMeta.spawnpoint

            if (spawnpoint != null) {
                player.teleport(spawnpoint.toLocation())
                return
            }
        }

        player.teleport(game.get().arena.meta.ballMeta.spawnpoint!!.toLocation())
    }

    /**
     * Caches the last interacting entity with the ball.
     */
    @EventHandler
    fun onBallInteractEvent(event: BallInteractEvent) {
        val game = gameService.getAllGames().find { p -> p.ball != null && p.ball!! == event.ball }

        if (game != null) {
            if (event.entity is Player && (event.entity as Player).gameMode == GameMode.SPECTATOR) {
                event.isCancelled = true
            }

            game.lastInteractedEntity = event.entity
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