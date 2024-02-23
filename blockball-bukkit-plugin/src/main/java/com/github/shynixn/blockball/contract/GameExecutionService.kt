package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Game
import org.bukkit.entity.Player

interface GameExecutionService {
    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    fun <G : Game> respawn(game: G, player: Player)

    /**
     * Applies points to the belonging teams when the given [player] dies in the given [game].
     */
    fun <G : Game> applyDeathPoints(game: G, player: Player)
}
