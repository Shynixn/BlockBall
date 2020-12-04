package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.business.proxy.BallProxy

class BallRayTraceEventEntity(
    /**
     * Ball.
     */
    ball: BallProxy,
    /**
     * Modifies if the raytrace has ended in a block hit.
     */
    var hitBlock: Boolean = false,

    /**
     * Gets the resulting position if the object actually
     * performs the raytrace. Current Position is the position of the ball.
     */
    var targetLocation: Any,

    /**
     * Block Direction which was hit. If hitBlock is false, it may contain any
     * direction. If changed and the hitBlock is true the outgoing Vector is differently calculated later.
     */
    var blockDirection: BlockDirection
) : BallEventEntity(ball)
