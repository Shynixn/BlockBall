package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection
import org.bukkit.Location
import org.bukkit.block.BlockFace

/**
 * The ray trace event is called when the ball has got a velocity in a certain
 * direction which results in the current position and the target position of the ball.
 *
 * The result of the ray trace is already computed but can be modified for cases as such invisible walls which
 * is used for the ball forceField.
 */
class BallRayTraceEvent(
    /**
     * SoccerBall.
     */
    ball: SoccerBall,
    /**
     * Modifies if the raytrace has ended in a block hit.
     */
    var hasHitBlock: Boolean = false,

    /**
     * Gets the resulting position if the object actually
     * performs the raytrace. Current Position is the position of the ball.
     */
    var targetLocation: Location,

    /**
     * Block Direction which was hit. If hitBlock is false, it may contain any
     * direction. If changed and the hitBlock is true the outgoing Vector is differently calculated later.
     */
    var blockFace: BlockFace? = null
) : BallEvent(ball)
