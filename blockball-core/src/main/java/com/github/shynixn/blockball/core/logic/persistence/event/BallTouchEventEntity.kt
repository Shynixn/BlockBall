package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

open class BallTouchEventEntity(
    ball: BallProxy,
    /**
     * Player.
     */
    val player: Any,

    /**
     * Velocity of the ball after being kicked.
     */
    var velocity: Any
) : BallEventEntity(ball)
