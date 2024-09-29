package com.github.shynixn.blockball.contract

interface SoccerRefereeGame : SoccerMiniGame {
    /**
     * Toggles the lobby countdown if the game is not running yet.
     */
    fun setLobbyCountdownActive(enabled: Boolean)
}
