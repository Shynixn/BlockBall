package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall


/**
 * Base Event for all SoccerBall events.
 */
open class BallEvent(
    /**
     * SoccerBall.
     */
    val ball: SoccerBall
) : BlockBallEvent()
