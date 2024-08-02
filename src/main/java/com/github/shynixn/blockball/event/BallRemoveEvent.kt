package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall


/**
 * Event which gets sent when the ball is requested to get removed.
 */
class BallRemoveEvent(ball: SoccerBall) : BallEvent(ball)
