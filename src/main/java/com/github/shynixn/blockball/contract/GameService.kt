package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Game
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

interface GameService : AutoCloseable {
    /**
     * Reloads all games.
     */
    suspend fun reloadAll()

    /**
     * Returns the game if the given [player] is playing a game.
     */
    fun getGameFromPlayer(player: Player): Optional<Game>

    /**
     * Returns the game if the given [player] is spectating a game.
     */
    fun getGameFromSpectatingPlayer(player: Player): Optional<Game>

    /**
     * Returns the game at the given location.
     */
    fun getGameFromLocation(location: Location): Optional<Game>

    /**
     * Returns the game with the given name or displayName.
     */
    fun getGameFromName(name: String): Optional<Game>

    /**
     * Returns all currently loaded games on the server.
     */
    fun getAllGames(): List<Game>

    /**
     * Closes all games permanently and should be executed on server shutdown.
     */
    override fun close()
}
