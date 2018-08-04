package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
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
class ReloadCommandExecutor @Inject constructor(plugin: Plugin, private val gameController: GameRepository, private val configurationService: ConfigurationService) : SimpleCommandExecutor.Registered("blockballreload", plugin as JavaPlugin) {
    /**
     * Reloads the config and the games when executed.
     *
     * @param sender sender
     * @param args   args
     */
    override fun onCommandSenderExecuteCommand(sender: CommandSender, args: Array<out String>) {
        val prefix = configurationService.findValue<String>("messages.prefix")

        plugin.reloadConfig()
        this.gameController.reload()
        if (sender is Player) {
            sender.sendMessage(prefix + ChatColor.GREEN + "Reloaded BlockBall.")
        } else {
            sender.sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.GREEN + "Reloaded BlockBall.")
        }

    }
}