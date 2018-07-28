package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor

import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.Minigame
import com.github.shynixn.blockball.bukkit.logic.business.helper.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.helper.stripChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

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
class SpectateCommandExecutor @Inject constructor(plugin: Plugin) : SimpleCommandExecutor.UnRegistered(plugin.config.get("global-spectate"), plugin as JavaPlugin) {

    @Inject
    private lateinit var gameController: GameRepository

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

        if (gameController.getGameFromPlayer(player) != null) {
            return
        }

        val mergedArgs = mergeArgs(0, args.size, args)

        gameController.getAll().filter { g -> g.arena.meta.spectatorMeta.spectatorModeEnabled }.forEach { g ->
            if (g is Minigame) {
                if (g.arena.name.equals(mergedArgs, true)) {
                    g.spectate(player)
                    return
                } else if (g.arena.displayName.convertChatColors().stripChatColors().equals(mergedArgs, true)) {
                    g.spectate(player)
                    return
                }
            }
        }
    }
}