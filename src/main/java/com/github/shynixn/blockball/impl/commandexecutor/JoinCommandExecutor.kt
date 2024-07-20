package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.impl.extension.mergeArgs
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommandExecutor @Inject constructor(
    private val gameService: GameService
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

        val mergedArgs = mergeArgs(0, args.size, args)
        gameService.getAllGames().forEach { g ->
            if (g.arena.name.equals(mergedArgs, true)) {
                g.join(source)
                return true
            } else if (g.arena.displayName.translateChatColors().stripChatColors()
                    .equals(mergedArgs, true)
            ) {
                g.join(source)
                return true
            }
        }

        val success = attemptJoiningGame(source, mergedArgs.split("|"))
        if (!success) {
            attemptJoiningGame(source, mergedArgs.split("/"))
        }

        return true
    }

    /**
     * Tries to join the player with the given arguments.
     * Returns true if successful.
     */
    private fun attemptJoiningGame(player: Player, args: List<String>): Boolean {
        gameService.getAllGames().forEach { g ->
            if (g.arena.name == args[0] || g.arena.displayName.translateChatColors().stripChatColors()
                    .equals(args[0], true)
            ) {
                var team: Team? = null
                // TODO:  Work around will be removed with the command rework
                if (args[1].equals("team red", true) || args[1].equals("red", true)) {
                    team = Team.RED
                } else if (args[1].equals("team blue", true) || args[1].equals("blue", true)) {
                    team = Team.BLUE
                }

                g.join(player, team)
                return true
            }
        }

        return false
    }
}
