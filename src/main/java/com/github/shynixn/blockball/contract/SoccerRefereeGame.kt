package com.github.shynixn.blockball.contract

interface SoccerRefereeGame : SoccerMiniGame {
    /**
     * Is the timer blocker enabled.
     */
    var isTimerBlockerEnabled : Boolean

    /**
     * Toggles the lobby countdown if the game is not running yet.
     */
    fun setLobbyCountdownActive(enabled: Boolean)

    /**
     * Stops the game and sets it to the last match time.
     */
    fun stopGame()
}
