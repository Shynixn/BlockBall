package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

/**
 * Event which gets sent when the ball is requested to get removed.
 */
class BallRemoveEvent(ball: BallProxy) : BallEvent(ball)
