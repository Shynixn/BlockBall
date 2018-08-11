package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.SimpleCommandExecutor.UnRegistered.mergeArgs
import com.github.shynixn.blockball.bukkit.logic.business.extension.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.extension.stripChatColors
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * Handles all command patterns to stop running games.
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
class StopCommandExecutor @Inject constructor(plugin: Plugin, private val gameService: GameService, private val configurationService: ConfigurationService) : SimpleCommandExecutor.Registered("blockballstop", plugin as JavaPlugin) {

    /**
     * Can be overwritten to listener to all executed commands.
     *
     * @param sender sender
     * @param args   args
     */
    override fun onCommandSenderExecuteCommand(sender: CommandSender, args: Array<out String>) {
        val prefix = configurationService.findValue<String>("messages.prefix")
        val mergedArgs = mergeArgs(0, args.size, args)

        gameService.getAllGames().forEach { g ->
            if (g.arena.name.equals(mergedArgs, true)) {
                g.closing = true
                sender.sendMessage(prefix + "Stopped game " + (g.arena.name + 1) + " named " + g.arena.displayName.convertChatColors() + ".")
                return
            } else if (g.arena.displayName.convertChatColors().stripChatColors().equals(mergedArgs, true)) {
                g.closing = true
                sender.sendMessage(prefix + "Stopped game " + (g.arena.name + 1) + " named " + g.arena.displayName.convertChatColors() + ".")
                return
            }
        }
    }
}