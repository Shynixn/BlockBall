package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.BlockBallGame


/**
 * Base Event for all game events.
 */
open class GameEvent(
    /**
     * Game firing this event.
     */
    val game: BlockBallGame
) : BlockBallEvent()
