package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.bukkit.logic.business.controller.BungeeCordPingManager
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

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
class BungeeCordSignCommandExecutor @Inject constructor(plugin: Plugin, private val bungeeCordPingManager: BungeeCordPingManager, private val configurationService: ConfigurationService) : SimpleCommandExecutor.Registered("blockballbungeecord", plugin as JavaPlugin) {

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    override fun onPlayerExecuteCommand(player: Player, args: Array<out String>) {
        val prefix = configurationService.findValue<String>("messages.prefix")

        if (args.size == 1) {
            this.bungeeCordPingManager.signCache[player] = args[0]
            player.sendMessage(prefix + "Rightclick on a sign to connect it to the server [" + args[0] + "].")
        } else {
            player.sendMessage("$prefix/blockballbungeecord <server>")
        }
    }
}