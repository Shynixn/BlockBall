package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import org.bukkit.Location

/**
 * Event which gets called when the ball is teleporting.
 */
class BallTeleportEvent(ball: BallProxy, var targetLocation : Location) : BallEvent(ball)
