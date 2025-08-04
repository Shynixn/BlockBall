package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.SoccerArena
import org.bukkit.entity.Player

interface GameService : AutoCloseable {
    /**
     * Reloads all games.
     */
    suspend fun reloadAll()

    /**
     * Reloads the specific game.
     */
    suspend fun reload(arena: SoccerArena)

    /**
     * Gets all running games.
     */
    fun getAll(): List<SoccerGame>

    /**
     * Tries to locate a game this player is playing.
     */
    fun getByPlayer(player: Player): SoccerGame?

    /**
     * Tries to locate a game of the given name.
     */
    fun getByName(name: String): SoccerGame?
}
