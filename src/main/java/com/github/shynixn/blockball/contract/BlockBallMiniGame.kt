package com.github.shynixn.blockball.contract

import org.bukkit.entity.Player

interface BlockBallMiniGame : BlockBallGame {
    /**
     * Actual game coutndown.
     */
    var gameCountdown: Int

    /**
     * List of players which are spectating the game.
     */
    val spectatorPlayers: List<Player>

    /**
     * Index of the current match time.
     */
    var matchTimeIndex: Int

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    fun switchToNextMatchTime()

    /**
     * Lets the given [player] leave spectate the given [game].
     * Does nothing if the player is already spectating a Game.
     */
    fun spectate(player: Player)
}
