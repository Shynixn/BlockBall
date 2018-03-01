package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.helper.stripChatColors
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
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
class HubGameListener @Inject constructor(plugin: Plugin) : SimpleListener(plugin) {

    private val lastLocation: MutableMap<Player, LocationBuilder> = HashMap()
    private val moveCounter: MutableMap<Player, Int> = HashMap()
    private val togglePlayers: MutableSet<Player> = HashSet()

    /**
     * Repository.
     */
    @Inject
    private var gameController: GameRepository? = null


    /** Handles the forcefield of hubGames. */
    @EventHandler
    fun onPlayerMoveAgainstHubForceField(event: PlayerMoveEvent) {
        val player = event.player
        if (event.to.distance(event.from) <= 0)
            return
        var game = gameController!!.getGameFromPlayer(player)
        if (game != null) {
            if (game.arena.gameType == GameType.HUBGAME && !game.arena.isLocationInSelection(player.location)) {
                game.leave(player)
            }
            return
        }
        var inArea = false
        gameController!!.games.forEach { game ->
            if (game.arena.enabled && game.arena.gameType == GameType.HUBGAME && game.arena.isLocationInSelection(event.to)) {
                inArea = true
                if (game.arena.meta.hubLobbyMeta.instantForcefieldJoin) {
                    game.join(player, null)
                    return
                }

                if (!this.lastLocation.containsKey(player)) {
                    if (game.arena.meta.protectionMeta.rejoinProtectionEnabled) {
                        player.velocity = game.arena.meta.protectionMeta.rejoinProtection
                    }
                } else {
                    if (!this.moveCounter.containsKey(player))
                        this.moveCounter[player] = 1
                    else if (this.moveCounter[player]!! < 50)
                        this.moveCounter[player] = this.moveCounter[player]!! + 1
                    if (this.moveCounter[player]!! > 20) {
                        player.velocity = game.arena.meta.protectionMeta.rejoinProtection

                    } else {
                        val knockback = this.lastLocation[player]!!.toVector().subtract(player.location.toVector())
                        player.location.direction = knockback
                        player.velocity = knockback
                        player.allowFlight = true
                        if (!togglePlayers.contains(player)) {
                            ChatBuilder().text(Config.prefix + game.arena.meta.hubLobbyMeta.joinMessage[0].convertChatColors())
                                    .nextLine()
                                    .component(game.arena.meta.hubLobbyMeta.joinMessage[1].convertChatColors())
                                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND
                                            , "/" + plugin.config.getString("global-join.command") + " " + game.arena.meta.redTeamMeta.displayName.stripChatColors() + " " + game.arena.name)
                                    .setHoverText(" ")
                                    .builder().text(" ").component(game.arena.meta.hubLobbyMeta.joinMessage[2].convertChatColors())
                                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND
                                            , "/" + plugin.config.getString("global-join.command") + " " + game.arena.meta.blueTeamMeta.displayName.stripChatColors() + " " + game.arena.name)
                                    .setHoverText(" ")
                                    .builder().sendMessage(player)
                            togglePlayers.add(player)
                        }
                    }
                }
            }
        }
        if (!inArea) {
            if (this.moveCounter.containsKey(event.player)) {
                this.moveCounter.remove(event.player)
            }
            if (togglePlayers.contains(player)) {
                if (event.player.gameMode != GameMode.CREATIVE) {
                    event.player.allowFlight = false
                }
                togglePlayers.remove(player)
            }
        }
        this.lastLocation[player] = LocationBuilder(event.player.location)
    }

    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (event.player.gameMode != GameMode.CREATIVE && togglePlayers.contains(event.player)) {
            event.player.allowFlight = false
            event.player.isFlying = false
            event.isCancelled = true
            this.togglePlayers.remove(event.player)
        }
    }
}