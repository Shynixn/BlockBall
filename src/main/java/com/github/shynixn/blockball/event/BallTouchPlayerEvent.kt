package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import org.bukkit.entity.Player
import org.bukkit.util.Vector


/**
 * Event which gets called when the ball is touched.
 */
open class BallTouchPlayerEvent(
    ball: SoccerBall,
    /**
     * Interacting player.
     */
    val player: Player,

    /**
     * Velocity of the ball after being touched.
     */
    val velocity: Vector
) : BallEvent(ball)

