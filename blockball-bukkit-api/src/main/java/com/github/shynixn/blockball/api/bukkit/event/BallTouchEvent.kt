package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import org.bukkit.entity.Player
import org.bukkit.util.Vector


/**
 * Event which gets called when the ball is touched.
 */
open class BallTouchEvent(
    ball: BallProxy,
    /**
     * Interacting player.
     */
    val player: Player,

    /**
     * Velocity of the ball after being touched.
     */
    val velocity: Vector
) : BallEvent(ball)

