package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.plugin.Plugin

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
class GameListener @Inject constructor(plugin: Plugin) : SimpleListener(plugin) {

    @Inject
    private var gameController: GameRepository? = null

    private val lastLocation: MutableMap<Player, LocationBuilder> = HashMap()
    private val toggledPlayers: MutableList<Player> = ArrayList()
    private val moveCounter: MutableMap<Player, Int> = HashMap()

    /** Handles the forcefield of hubGames. */
    @EventHandler
    fun onPlayerMoveAgainstHubForceField(event: PlayerMoveEvent) {
        val player = event.player
        if (event.to.distance(event.from) <= 0)
            return
        if (gameController!!.getGameFromPlayer(player) != null) {
            return
        }
        var inArea: Boolean = false;
        gameController!!.games.forEach { game ->
            if (game.arena.enabled && game.arena.gameType == GameType.HUBGAME && game.arena.isLocationInSelection(event.to)) {
                inArea = true;
                if (!this.lastLocation.containsKey(player)) {
                    player.velocity = game.arena.meta.protectionMeta.rejoinProtection
                } else {
                    if (!this.moveCounter.containsKey(player))
                        this.moveCounter.put(player, 1)
                    else if (this.moveCounter[player]!! < 50)
                        this.moveCounter.put(player, this.moveCounter[player]!! + 1)
                    if (this.moveCounter[player]!! > 20) {
                        player.velocity = game.arena.meta.protectionMeta.rejoinProtection
                        print("JOIN")

                    } else {
                        print("KNOCKBACK")
                        val knockback = this.lastLocation[player]!!.toVector().subtract(player.location.toVector())
                        player.location.direction = knockback
                        player.velocity = knockback
                        player.allowFlight = true
                        this.toggledPlayers.add(player)
                    }
                    print(moveCounter[player])
                }
            }
        }
        if (!inArea) {
            if (this.moveCounter.containsKey(event.player)) {
                this.moveCounter.remove(event.player);
            }
        }
        this.lastLocation.put(player, LocationBuilder(event.player.location))
    }
}
