package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall

/**
 * Event which gets sent when the ball is spawned.
 */
class BallSpawnEvent(ball: SoccerBall) : BallEvent(ball)
