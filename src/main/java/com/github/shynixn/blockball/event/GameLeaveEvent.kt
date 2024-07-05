package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.BlockBallGame
import org.bukkit.entity.Player

/**
 * Game Leave event.
 */
class GameLeaveEvent(
        /**
         * Player joining the game.
         */
        val player: Player, game: BlockBallGame
) : GameEvent(game)
