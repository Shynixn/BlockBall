package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.executor.CommandExecutor
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.CommandService
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.SimpleCommandMap
import org.bukkit.command.defaults.BukkitCommand
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.ArrayList

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class CommandServiceImpl @Inject constructor(private val plugin: Plugin) : CommandService, org.bukkit.command.CommandExecutor {
    private var commandExecutor: CommandExecutor? = null

    /**
     * Creates an unmanaged instance for handling internal commands.
     */
    constructor(plugin: Plugin, commandExecutorInstance: CommandExecutor) : this(plugin) {
        this.commandExecutor = commandExecutorInstance
    }

    /**
     * Handles command events from bukkit.
     */
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (commandExecutor == null) {
            return false
        }

        return this.commandExecutor!!.onExecuteCommand(commandSender, args)
    }

    /**
     * Registers a command executor from new [commandConfiguration] with gets executed by the [commandExecutor].
     */
    override fun registerCommandExecutor(commandConfiguration: Map<String, String>, commandExecutor: CommandExecutor) {
        if (plugin !is JavaPlugin) {
            throw IllegalArgumentException("Plugin has to be a JavaPlugin!")
        }

        val command = commandConfiguration["command"] as String
        val description = commandConfiguration["description"] as String
        val usage = commandConfiguration["useage"] as String
        val permission = commandConfiguration["permission"] as String
        val permissionMessage = commandConfiguration["permission-message"] as String

        val internalExecutor = InternalBukkitCommand(command, description, usage, permission, permissionMessage, commandExecutor)
        val clazz = findClazz("org.bukkit.craftbukkit.VERSION.CraftServer")
        val server = clazz.cast(Bukkit.getServer())
        val map = server.javaClass.getDeclaredMethod("getCommandMap").invoke(server) as SimpleCommandMap
        map.register(command, internalExecutor)
    }

    /**
     * Registers a command executor from a pre defined [command] with gets executed by the [commandExecutor].
     */
    override fun registerCommandExecutor(command: String, commandExecutor: CommandExecutor) {
        if (plugin !is JavaPlugin) {
            throw IllegalArgumentException("Plugin has to be a JavaPlugin!")
        }

        plugin.getCommand(command)!!.setExecutor(CommandServiceImpl(plugin, commandExecutor))
    }

    /**
     * Handles internal bukkit registration.
     */
    private class InternalBukkitCommand(
        command: String,
        description: String,
        usage: String,
        permission: String,
        permissionMessage: String,
        private val commandExecutor: CommandExecutor
    ) : BukkitCommand(command) {
        init {
            this.setDescription(description)
            this.usage = usage
            this.permission = permission
            this.permissionMessage = permissionMessage
            this.aliases = ArrayList()
        }

        /**
         * Gets called when the user enters a command.
         */
        override fun execute(commandSender: CommandSender, alias: String, args: Array<out String>): Boolean {
            if (!commandSender.hasPermission(this.permission!!)) {
                commandSender.sendMessage(this.permissionMessage!!)
                return true
            }

            return this.commandExecutor.onExecuteCommand(commandSender, args)
        }
    }
}