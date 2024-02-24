package com.github.shynixn.blockball.contract

import org.bukkit.command.CommandSender

interface CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean
}
