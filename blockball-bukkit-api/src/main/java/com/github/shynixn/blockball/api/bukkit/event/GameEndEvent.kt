package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.Game

/**
 * Game End event.
 */
class GameEndEvent(
        /**
         * Winning [Team]. Is null when the match ended in a draw.
         */
        val winningTeam: Team?, game: Game) : GameEvent(game)
