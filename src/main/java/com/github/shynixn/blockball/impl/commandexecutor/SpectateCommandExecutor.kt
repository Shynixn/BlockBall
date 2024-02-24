package com.github.shynixn.blockball.impl.commandexecutor


import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameMiniGameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.MiniGame
import com.github.shynixn.blockball.impl.extension.mergeArgs
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SpectateCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val miniGameActionService: GameMiniGameActionService
) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }

        if (source !is Player) {
            return false
        }

        if (gameService.getGameFromPlayer(source).isPresent) {
            return true
        }

        val mergedArgs = mergeArgs(0, args.size, args)

        gameService.getAllGames().filter { g -> g.arena.meta.spectatorMeta.spectatorModeEnabled }.forEach { g ->
            if (g is MiniGame) {
                if (g.arena.name.equals(mergedArgs, true)) {
                    miniGameActionService.spectateGame(g, source)
                    return true
                } else if (g.arena.displayName.translateChatColors().stripChatColors().equals(mergedArgs, true)) {
                    miniGameActionService.spectateGame(g, source)
                    return true
                }
            }
        }

        return true
    }
}
