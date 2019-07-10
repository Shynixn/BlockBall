package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.GameBungeeCordGameActionService
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordConfiguration
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordGame
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player

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
class GameBungeeCordGameActionServiceImpl @Inject constructor(private val plugin: PluginProxy, configurationService: ConfigurationService) : GameBungeeCordGameActionService {
    private val bungeeCordConfiguration = configurationService.findConfiguration(BungeeCordConfiguration::class.java, "")

    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: BungeeCordGame) {
        plugin.setMotd(bungeeCordConfiguration.restartingMotd)
        Bukkit.getServer().shutdown()
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun <P> leaveGame(game: BungeeCordGame, player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        player.kickPlayer("")
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: BungeeCordGame, ticks: Int) {
        if (ticks < 20) {
            return
        }

        if (game.playing) {
            plugin.setMotd(bungeeCordConfiguration.inGameMotd)
        } else {
            plugin.setMotd(bungeeCordConfiguration.waitingForPlayersMotd)
        }
    }
}