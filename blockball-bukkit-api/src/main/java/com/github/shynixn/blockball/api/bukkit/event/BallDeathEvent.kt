package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

/**
 * Event which gets sent when the ball is requested to get removed.
 */
class BallDeathEvent(ball: BallProxy) : BallEvent(ball)
