package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Event which gets called when the ball is kicked.
 */
class BallLeftClickEvent(
    ball: SoccerBall,
    player: Player,
    velocity: Vector
) : BallTouchPlayerEvent(ball, player, velocity)
