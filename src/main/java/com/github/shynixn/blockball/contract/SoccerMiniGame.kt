package com.github.shynixn.blockball.contract

interface SoccerMiniGame : SoccerGame {
    /**
     * Actual game coutndown.
     */
    var gameCountdown: Int

    /**
     * Index of the current match time.
     */
    var matchTimeIndex: Int

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    fun switchToNextMatchTime()
}
