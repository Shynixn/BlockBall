package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Event which gets called when the ball is kicked.
 */
class BallKickEvent(
    ball: BallProxy,
    player: Player,
    velocity: Vector
) : BallTouchEvent(ball, player, velocity)
