package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameService
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommandExecutor @Inject constructor(
    private val gameService: GameService
) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        if (source !is Player) {
            return false
        }

        val playerGame = gameService.getGameFromPlayer(source)

        if (playerGame != null) {
            playerGame.leave(source)
        }

        val spectatorGame = gameService.getGameFromSpectatingPlayer(source)

        if (spectatorGame != null) {
            spectatorGame.leave(source)
        }

        return true
    }
}
