package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.Ball
import com.github.shynixn.blockball.enumeration.BlockDirection
import org.bukkit.Location

/**
 * The ray trace event is called when the ball has got a velocity in a certain
 * direction which results in the current position and the target position of the ball.
 *
 * The result of the ray trace is already computed but can be modified for cases as such invisible walls which
 * is used for the ball forceField.
 */
class BallRayTraceEvent(
    /**
     * Ball.
     */
    ball: Ball,
    /**
     * Modifies if the raytrace has ended in a block hit.
     */
    var hitBlock: Boolean = false,

    /**
     * Gets the resulting position if the object actually
     * performs the raytrace. Current Position is the position of the ball.
     */
    val targetLocation: Location,

    /**
     * Block Direction which was hit. If hitBlock is false, it may contain any
     * direction. If changed and the hitBlock is true the outgoing Vector is differently calculated later.
     */
    var blockDirection: BlockDirection
) : BallEvent(ball)
