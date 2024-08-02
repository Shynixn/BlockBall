package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import org.bukkit.Location

/**
 * Event which gets called when the ball is teleporting.
 */
class BallTeleportEvent(ball: SoccerBall, var targetLocation : Location) : BallEvent(ball)
