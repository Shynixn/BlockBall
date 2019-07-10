package com.github.shynixn.blockball.core.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.executor.CommandExecutor
import com.github.shynixn.blockball.api.business.service.GameActionService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.core.logic.business.extension.mergeArgs
import com.github.shynixn.blockball.core.logic.business.extension.stripChatColors
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.google.inject.Inject

/**
 * Handles all command patterns to join games and arenas.
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
class JoinCommandExecutor @Inject constructor(private val gameService: GameService, private val gameActionService: GameActionService) : CommandExecutor {
    /**
     * Gets called when the given [source] executes the defined command with the given [args].
     */
    override fun <S> onExecuteCommand(source: S, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return false
        }

        val mergedArgs = mergeArgs(0, args.size, args)
        gameService.getAllGames().forEach { g ->
            if (g.arena.name.equals(mergedArgs, true)) {
                gameActionService.joinGame(g, source)
                return true
            } else if (g.arena.displayName.translateChatColors().stripChatColors().equals(mergedArgs, true)) {
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
    private fun <S> attemptJoiningGame(player: S, args: List<String>): Boolean {
        gameService.getAllGames().forEach { g ->
            if (g.arena.name == args[0] || g.arena.displayName.translateChatColors().stripChatColors().equals(args[0], true)) {
                var team: Team? = null
                if (args[1].equals(g.arena.meta.redTeamMeta.displayName.translateChatColors().stripChatColors(), true)) {
                    team = Team.RED
                } else if (args[1].equals(g.arena.meta.blueTeamMeta.displayName.translateChatColors().stripChatColors(), true)) {
                    team = Team.BLUE
                }

                gameActionService.joinGame(g, player, team)
                return true
            }
        }

        return false
    }
}