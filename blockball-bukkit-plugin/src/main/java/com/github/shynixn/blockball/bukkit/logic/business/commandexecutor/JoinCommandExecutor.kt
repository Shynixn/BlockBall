package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.helper.stripChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.util.regex.Pattern

/**
 * Created by Shynixn 2018.
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
class JoinCommandExecutor @Inject constructor(plugin: Plugin) : SimpleCommandExecutor.UnRegistered(plugin.config.get("global-join"), plugin as JavaPlugin) {

    @Inject
    private var gameController: GameRepository? = null

    /**
     * Can be overwritten to listen to player executed commands.
     *
     * @param player player
     * @param args   args
     */
    override fun onPlayerExecuteCommand(player: Player, args: Array<out String>?) {
        if (args!!.size >= 2) {
            gameController!!.games.forEach { game ->
                val redTeamArgumentsAmount = game.arena.meta.redTeamMeta.displayName.split(Pattern.quote(" ")).size
                val reddata = mergeArgs(0, redTeamArgumentsAmount + 1, args)
                if (reddata.equals(game.arena.meta.redTeamMeta.displayName.stripChatColors(), true)) {
                    val gameArgumentsAmount = game.arena.name.split(Pattern.quote(" ")).size
                    val result = mergeArgs(redTeamArgumentsAmount + 1, gameArgumentsAmount, args)
                    if (result == game.arena.name) {
                        game.join(player, Team.RED)
                        return
                    }
                }
                val blueTeamArgumentsAmount = game.arena.meta.blueTeamMeta.displayName.split(Pattern.quote(" ")).size
                val bluedata = mergeArgs(0, blueTeamArgumentsAmount + 1, args)
                if (bluedata.equals(game.arena.meta.blueTeamMeta.displayName.stripChatColors(), true)) {
                    val gameArgumentsAmount = game.arena.name.split(Pattern.quote(" ")).size
                    val result = mergeArgs(blueTeamArgumentsAmount + 1, gameArgumentsAmount, args)
                    println(result)
                    if (result == game.arena.name) {
                        game.join(player, Team.BLUE)
                        return
                    }
                }
            }
        }
        if (args.isNotEmpty()) {
            val mergedArgs = mergeArgs(0, args.size, args)
            gameController!!.getAll().forEach { g ->
                if (g.arena.name.equals(mergedArgs, true)) {
                    g.join(player)
                    return
                } else if (g.arena.displayName.equals(mergedArgs, true)) {
                    g.join(player)
                    return
                }
            }

            try {
                val data = mergedArgs.split("|")

                gameController!!.getAll().forEach { g ->
                    if (g.arena.name == data[1]) {
                        var team: Team? = null
                        if (data[0] == g.arena.meta.redTeamMeta.displayName) {
                            team = Team.RED
                        } else if (data[1] == g.arena.meta.blueTeamMeta.displayName) {
                            team = Team.BLUE
                        }

                        g.join(player, team)
                    }
                }

            } catch (e: Exception) {

            }
        }
    }
}