package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.BallMeta
import org.bukkit.Location

interface SoccerBallFactory : AutoCloseable {
    /**
     * Creates a new SoccerBall.
     */
    fun createSoccerBall(location: Location, meta: BallMeta): SoccerBall

    /**
     * Tries to locate the ball by the given id.
     */
    fun findBallByEntityId(id: Int): SoccerBall?

    /**
     * Disables a ball from tracking.
     */
    fun removeTrackedBall(ball: SoccerBall)

    /**
     * Returns all balls managed by the plugin.
     */
    fun getAllBalls(): List<SoccerBall>
}
