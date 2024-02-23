package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Game
import com.github.shynixn.blockball.entity.TeamMeta
import org.bukkit.entity.Player

interface PlaceHolderService {
    /**
     * Replaces the given text with properties from the given [game], optional [teamMeta] and optional size.
     */
    fun replacePlaceHolders(text: String, player : Player? = null, game: Game? = null, teamMeta: TeamMeta? = null, currentTeamSize: Int? = null): String
}
