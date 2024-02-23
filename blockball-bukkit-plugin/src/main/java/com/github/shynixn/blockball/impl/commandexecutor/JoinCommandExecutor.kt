package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.impl.extension.mergeArgs
import com.github.shynixn.blockball.impl.extension.stripChatColors
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class JoinCommandExecutor @Inject constructor(
    private val gameService: GameService,
    private val gameActionService: GameActionService
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
            if (g.arena.name.equals(mergedArgs, true) && source is Player) {
                gameActionService.joinGame(g, source)
                return true
            } else if (g.arena.displayName.translateChatColors().stripChatColors()
                    .equals(mergedArgs, true) && source is Player
            ) {
                gameActionService.joinGame(g, source)
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
                if (args[1].equals(
                        g.arena.meta.redTeamMeta.displayName.translateChatColors().stripChatColors(),
                        true
                    )
                ) {
                    team = Team.RED
                } else if (args[1].equals(
                        g.arena.meta.blueTeamMeta.displayName.translateChatColors().stripChatColors(),
                        true
                    )
                ) {
                    team = Team.BLUE
                }

                gameActionService.joinGame(g, player, team)
                return true
            }
        }

        return false
    }
}
