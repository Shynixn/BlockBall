package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.HubGame
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.entity.Player

interface GameHubGameActionService {
    /**
     * Lets the given [player] leave join the given [game]. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    fun joinGame(game: HubGame, player: Player, team: Team?): Boolean

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    fun leaveGame(game: HubGame, player: Player)

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    fun handle(game: HubGame, ticks: Int)
}
