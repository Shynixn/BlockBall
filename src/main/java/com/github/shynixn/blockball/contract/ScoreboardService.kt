package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.enumeration.ScoreboardDisplaySlot
import org.bukkit.scoreboard.Scoreboard

interface ScoreboardService {
    /**
     * Sets the configuration of the given scoreboard.
     */
    fun setConfiguration(scoreboard: Scoreboard, displaySlot: ScoreboardDisplaySlot, title: String)

    /**
     * Sets the [text] at the given [scoreboard] and [lineNumber].
     */
    fun setLine(scoreboard: Scoreboard, lineNumber: Int, text: String)
}
