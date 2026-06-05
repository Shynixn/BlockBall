package com.github.shynixn.blockball.contract

import org.bukkit.Location

interface SoccerBallService : AutoCloseable {
    /**
     * Spawns a soccer ball of the given name.
     */
    fun spawn(name: String, location: Location): SoccerBall

    /**
     * Spawns a soccer ball of the given name and attaches it to a game.
     */
    fun spawnForGame(name: String, location: Location, game: SoccerGame): SoccerBall

    /**
     * Tries to get the ball by entity id.
     */
    fun getByEntityId(id: Int): SoccerBall?

    /**
     * Gets all active soccer balls.
     */
    fun getAllActive(): List<SoccerBall>
}