package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.Ball


/**
 * Base Event for all Ball events.
 */
open class BallEvent(
    /**
     * Ball.
     */
    val ball: Ball
) : BlockBallEvent()
