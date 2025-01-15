package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.ScoreboardService
import com.github.shynixn.blockball.enumeration.ScoreboardDisplaySlot
import com.github.shynixn.mcutils.common.translateChatColors
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Scoreboard

class ScoreboardServiceImpl: ScoreboardService {
    companion object {
        private const val defaultObjective: String = "def_obj"
    }

    /**
     * Sets the configuration of the given scoreboard.
     */
    override fun setConfiguration(scoreboard: Scoreboard, displaySlot: ScoreboardDisplaySlot, title: String) {
        @Suppress("DEPRECATION")
        val objective = scoreboard.registerNewObjective(defaultObjective, "dummy")
        objective.displaySlot = DisplaySlot.values().first { v -> v.name == displaySlot.name }
        objective.displayName = title.translateChatColors()
    }

    /**
     * Sets the [text] at the given [scoreboard] and [lineNumber].
     */
    override fun setLine(scoreboard: Scoreboard, lineNumber: Int, text: String) {
        val teamFinder = StringBuilder(ChatColor.values()[lineNumber].toString()).append("&r")
        var team = scoreboard.teams.firstOrNull { t -> t.name == teamFinder.toString().translateChatColors() }
        val objective = scoreboard.getObjective(defaultObjective)

        if (team == null) {
            team = scoreboard.registerNewTeam(teamFinder.toString().translateChatColors())
            team.addEntry(teamFinder.toString().translateChatColors())

            objective!!.getScore(teamFinder.toString().translateChatColors()).score = lineNumber
        }

        team.prefix = text.translateChatColors()
    }
}
