package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.Ball
import org.bukkit.Location

/**
 * Event which gets called when the ball is teleporting.
 */
class BallTeleportEvent(ball: Ball, var targetLocation : Location) : BallEvent(ball)
