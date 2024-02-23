package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.BungeeCordGame
import org.bukkit.entity.Player

interface GameBungeeCordGameActionService{
    /**
     * Closes the given game and all underlying resources.
     */
     fun closeGame(game: BungeeCordGame)

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    fun leaveGame(game: BungeeCordGame, player: Player)

    /**
    * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
    * 0 - 20 for each second.
    */
    fun handle(game: BungeeCordGame, ticks: Int)
}
