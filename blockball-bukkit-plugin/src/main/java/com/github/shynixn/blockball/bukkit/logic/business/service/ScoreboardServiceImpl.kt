package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.ScoreboardService
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.blockball.core.logic.business.extension.cast
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Team

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
class ScoreboardServiceImpl @Inject constructor() : ScoreboardService {

    companion object {
        private const val defaultObjective: String = "def_obj"
    }

    /**
     * Sets the configuration of the given scoreboard.
     */
    override fun <S> setConfiguration(scoreboard: S, displaySlot: Any, title: String) {
        if (scoreboard !is org.bukkit.scoreboard.Scoreboard) {
            throw IllegalArgumentException("Scoreboard has to be a Bukkit Scoreboard!")
        }

        if (displaySlot !is DisplaySlot) {
            throw IllegalArgumentException("Displayslot has to be a Bukkit Displayslot!")
        }

        @Suppress("DEPRECATION")
        val objective = scoreboard.registerNewObjective(defaultObjective, "dummy")
        objective.displaySlot = displaySlot
        objective.displayName = title.translateChatColors()
    }

    /**
     * Sets the [text] at the given [scoreboard] and [lineNumber].
     */
    override fun <S> setLine(scoreboard: S, lineNumber: Int, text: String) {
        if (scoreboard !is org.bukkit.scoreboard.Scoreboard) {
            throw IllegalArgumentException("Scoreboard has to be a Bukkit Scoreboard!")
        }

        val teamFinder = StringBuilder(ChatColor.values()[lineNumber].toString()).append("&r")
        var team = scoreboard.teams.firstOrNull { t -> t.name == teamFinder.toString().translateChatColors() }
        val objective = scoreboard.getObjective(defaultObjective)

        if (team == null) {
            team = scoreboard.registerNewTeam(teamFinder.toString().translateChatColors())
            team.addEntry(teamFinder.toString().translateChatColors())

            objective!!.getScore(teamFinder.toString().translateChatColors()).score = lineNumber
        }

        team.cast<Team?>()!!.prefix = text.translateChatColors()
    }
}