package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.impl.extension.mergeArgs
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.command.CommandSender

class StopCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val proxyService: ProxyService,
): CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun onExecuteCommand(source: CommandSender, args: Array<out String>): Boolean {
        val mergedArgs = mergeArgs(0, args.size, args)

        for (game in gameService.getAllGames()) {
            if (game.arena.name.equals(mergedArgs, true)) {
                game.closing = true
                proxyService.sendMessage(
                    source,
                    BlockBallLanguage.stopGameMessage.format(
                        game.arena.name + 1,
                        game.arena.displayName.translateChatColors()
                    )
                )
                return true
            } else if (game.arena.displayName.translateChatColors().stripChatColors().equals(mergedArgs, true)) {
                game.closing = true
                proxyService.sendMessage(
                    source,
                    BlockBallLanguage.stopGameMessage.format(
                        game.arena.name + 1,
                        game.arena.displayName.translateChatColors()
                    )
                )
                return true
            }
        }

        return true
    }
}
