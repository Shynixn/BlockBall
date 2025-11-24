package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerGame

/**
 * Game Start event.
 */
class GameStartEvent(
    game: SoccerGame
) : GameEvent(game)
