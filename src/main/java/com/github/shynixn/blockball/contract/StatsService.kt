package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.LeaderBoardStats

interface StatsService : AutoCloseable {
    /**
     * Registers tracking the stats.
     */
    fun register()

    /**
     * Gets the leaderBoardStats.
     */
    fun getLeaderBoard(): LeaderBoardStats?
}
