package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.GameActionService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.extension.stripChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

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
class JoinCommandExecutor @Inject constructor(private val gameService: GameService, private val gameActionService: GameActionService<Game>, plugin: Plugin) : SimpleCommandExecutor.UnRegistered(plugin.config.get("global-join"), plugin as JavaPlugin) {
    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    override fun onPlayerExecuteCommand(player: Player, args: Array<out String>) {
        if (args.isEmpty()) {
            return
        }

        val mergedArgs = mergeArgs(0, args.size, args)
        gameService.getAllGames().forEach { g ->
            if (g.arena.name.equals(mergedArgs, true)) {
                gameActionService.joinGame(g, player)
                return
            } else if (g.arena.displayName.convertChatColors().stripChatColors().equals(mergedArgs, true)) {
                gameActionService.joinGame(g, player)
                return
            }
        }

        try {
            val success = attemptJoiningGame(player, mergedArgs.split("|"))
            if (!success) {
                attemptJoiningGame(player, mergedArgs.split("/"))
            }
        } catch (e: Throwable) {
            // Ignore all errors.
        }
    }

    /**
     * Tries to join the player with the given arguments.
     * Returns true if successful.
     */
    private fun attemptJoiningGame(player: Player, args: List<String>): Boolean {
        gameService.getAllGames().forEach { g ->
            if (g.arena.name == args[0] || g.arena.displayName.convertChatColors().stripChatColors().equals(args[0], true)) {
                var team: Team? = null
                if (args[1].equals(g.arena.meta.redTeamMeta.displayName.convertChatColors().stripChatColors(), true)) {
                    team = Team.RED
                } else if (args[1].equals(g.arena.meta.blueTeamMeta.displayName.convertChatColors().stripChatColors(), true)) {
                    team = Team.BLUE
                }

                gameActionService.joinGame(g, player, team)
                return true
            }
        }

        return false
    }
}