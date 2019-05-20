package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.google.inject.Inject
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

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
class BungeeCordgameListener @Inject constructor(private val gameService: GameService, private val rightClickManageService: RightclickManageService, itemService: ItemService, private val gameActionService: GameActionService<Game>) : Listener {
    private val signPostMaterial = itemService.getMaterialFromMaterialType<Material>(MaterialType.SIGN_POST)

    /**
     * Joins the game for a bungeecord player.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val game = gameService.getAllGames().find { p -> p.arena.gameType == GameType.BUNGEE }

        if (game != null) {
            val success = gameActionService.joinGame(game, event.player)
            if (!success) {
                event.player.kickPlayer(game.arena.meta.bungeeCordMeta.kickMessage)
            }
        }
    }

    /**
     * Handles click on signs to create new server signs or to connect players to the server
     * written on the sign.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.clickedBlock!!.type != signPostMaterial && event.clickedBlock!!.type != Material.WALL_SIGN) {
            return
        }

        rightClickManageService.executeWatchers(event.player, event.clickedBlock!!.location)
        //  bungeeCordService.clickOnConnectSign(event.player, event.clickedBlock.state as Sign)
    }
}