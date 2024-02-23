package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LeaveCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val gameActionService: GameActionService
) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        if (source !is Player) {
            return false
        }

        val playerGame = gameService.getGameFromPlayer(source)

        if (playerGame.isPresent) {
            gameActionService.leaveGame(playerGame.get(), source)
        }

        val spectatorGame = gameService.getGameFromSpectatingPlayer(source)

        if (spectatorGame.isPresent) {
            gameActionService.leaveGame(spectatorGame.get(), source)
        }

        return true
    }
}
