package com.github.shynixn.blockball.contract

import org.bukkit.Location

interface SoccerBallService : AutoCloseable {
    /**
     * Spawns a soccer ball of the given name.
     */
    fun spawn(name: String, location: Location): SoccerBall

    /**
     * Tries to get the ball by entity id.
     */
    fun getByEntityId(id: Int): SoccerBall?

    /**
     * Gets all active soccer balls.
     */
    fun getAll(): List<SoccerBall>
}