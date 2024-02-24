package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.MiniGame
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.entity.Player


interface GameMiniGameActionService {
    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    fun joinGame(game: MiniGame, player: Player, team: Team?): Boolean

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    fun leaveGame(game: MiniGame, player: Player)

    /**
     * Closes the given game and all underlying resources.
     */
    fun closeGame(game: MiniGame)

    /**
     * Lets the given [player] leave spectate the given [game].
     * Does nothing if the player is already spectating a Game.
     */
    fun spectateGame(game: MiniGame, player: Player)

    /**
     * Gets called when the given [game] ends with a draw.
     */
    fun onDraw(game: MiniGame)

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    fun switchToNextMatchTime(game: MiniGame)

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    fun handle(game: MiniGame, ticks: Int)
}
