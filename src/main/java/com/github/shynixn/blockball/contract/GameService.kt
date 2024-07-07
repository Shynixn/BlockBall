package com.github.shynixn.blockball.contract

import org.bukkit.Location
import org.bukkit.entity.Player

interface GameService : AutoCloseable {
    /**
     * Reloads all games.
     */
    suspend fun reloadAll()

    /**
     * Returns the game if the given [player] is playing a game.
     */
    fun getGameFromPlayer(player: Player): BlockBallGame?

    /**
     * Returns the game if the given [player] is spectating a game.
     */
    fun getGameFromSpectatingPlayer(player: Player):BlockBallGame?

    /**
     * Returns the game at the given location.
     */
    fun getGameFromLocation(location: Location): BlockBallGame?

    /**
     * Returns the game with the given name or displayName.
     */
    fun getGameFromName(name: String): BlockBallGame?

    /**
     * Returns all currently loaded games on the server.
     */
    fun getAllGames(): List<BlockBallGame>

    /**
     * Closes all games permanently and should be executed on server shutdown.
     */
    override fun close()
}
