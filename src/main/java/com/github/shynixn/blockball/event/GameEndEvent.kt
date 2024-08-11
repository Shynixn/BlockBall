package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.enumeration.Team


/**
 * Game End event.
 */
class GameEndEvent(
        /**
         * Winning [Team]. Is null when the match ended in a draw.
         */
        val winningTeam: Team?, game: SoccerGame
) : GameEvent(game)
