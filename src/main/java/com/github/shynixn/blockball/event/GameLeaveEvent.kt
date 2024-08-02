package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerGame
import org.bukkit.entity.Player

/**
 * Game Leave event.
 */
class GameLeaveEvent(
        /**
         * Player joining the game.
         */
        val player: Player, game: SoccerGame
) : GameEvent(game)
