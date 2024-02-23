package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.BallMeta
import org.bukkit.Location

interface BallEntityService : AutoCloseable{
    /**
     * Spawns a temporary ball.
     * Returns a ball or null if the ball spawn event was cancelled.
     */
    fun spawnTemporaryBall(location: Location, meta: BallMeta): Ball?

    /**
     * Tries to locate the ball by the given id.
     */
    fun findBallByEntityId(id: Int): Ball?

    /**
     * Disables a ball from tracking.
     */
    fun removeTrackedBall(ball: Ball)

    /**
     * Returns all balls managed by the plugin.
     */
    fun getAllBalls(): List<Ball>
}
