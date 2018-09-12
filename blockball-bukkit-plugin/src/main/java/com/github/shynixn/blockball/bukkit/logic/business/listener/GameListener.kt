package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.BallInteractEvent
import com.github.shynixn.blockball.api.bukkit.event.PlaceHolderRequestEvent
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.DoubleJumpService
import com.github.shynixn.blockball.api.business.service.GameActionService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.RightclickManageService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.isTouchingGround
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toBukkitMaterial
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.nms.MaterialCompatibility13
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.Material
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
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

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
class GameListener @Inject constructor(private val gameService: GameService, private val rightClickManageService: RightclickManageService, private val doubleJumpService: DoubleJumpService, private val gameActionService: GameActionService<Game>) : Listener {
    private val signPostMaterial = MaterialType.SIGN_POST.toBukkitMaterial()

    /**
     * Gets called when a player leaves the server and the game.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val game = gameService.getGameFromPlayer(event.player)

        if (game.isPresent) {
            gameActionService.leaveGame(game.get(), event.player)
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
     * Allows the player to start flying as long he is inside of a game.
     */
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (!event.player.isTouchingGround()) {
            return
        }

        val game = gameService.getGameFromPlayer(event.player)

        if (game.isPresent) {
            event.player.allowFlight = true
        }
    }

    /**
     * Gets called when the player interacts with his inventory and cancels it.
     */
    @EventHandler
    fun onPlayerClickInventoryEvent(event: InventoryClickEvent) {
        val game = gameService.getGameFromPlayer(event.whoClicked as Player)

        if (game.isPresent) {
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

        if (game.isPresent) {
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

        if (game.isPresent && event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
        }

        if (game.isPresent && event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK && !game.get().arena.meta.customizingMeta.damageEnabled) {
            event.isCancelled = true
        }
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

        if (event.clickedBlock.type != signPostMaterial && event.clickedBlock.type != Material.WALL_SIGN) {
            return
        }

        val location = event.clickedBlock.location.toPosition()

        rightClickManageService.executeWatchers(event.player, event.clickedBlock.state as Sign)
        gameService.getAllGames().forEach { p ->
            when {
                p.arena.meta.lobbyMeta.joinSigns.contains(location) -> gameActionService.joinGame(p, event.player)
                p.arena.meta.lobbyMeta.leaveSigns.contains(location) -> gameActionService.leaveGame(p, event.player)
                p.arena.meta.redTeamMeta.signs.contains(location) -> gameActionService.joinGame(p, event.player, Team.RED)
                p.arena.meta.blueTeamMeta.signs.contains(location) -> gameActionService.joinGame(p, event.player, Team.BLUE)
            }
        }
    }

    /**
     * Handles executing the double jump.
     */
    @EventHandler
    fun onToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (event.player.gameMode == GameMode.CREATIVE) {
            return
        }

        event.isCancelled = doubleJumpService.handleDoubleClick(event.player)
    }

    /**
     * Handles placeholder requests.
     */
    @EventHandler
    fun onPlaceHolderRequestEvent(event: PlaceHolderRequestEvent) {
        try {
            PlaceHolder.values().forEach { p ->
                if (event.name.startsWith(p.placeHolder)) {
                    val data = event.name.split("_")
                    val game = this.gameService.getGameFromName(data[1])

                    if (game.isPresent) {
                        event.result = data[0].replaceGamePlaceholder(game.get())
                    }

                    return
                }
            }
        } catch (e: Exception) {
            //Ignored. Simple parsing error that another plugin is responsible for.
        }
    }
}