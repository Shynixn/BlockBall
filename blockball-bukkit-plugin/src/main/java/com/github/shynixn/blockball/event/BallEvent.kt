package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

/**
 * Base Event for all Ball events.
 */
open class BallEvent(
    /**
     * Ball.
     */
    val ball: BallProxy
) : BlockBallEvent()
