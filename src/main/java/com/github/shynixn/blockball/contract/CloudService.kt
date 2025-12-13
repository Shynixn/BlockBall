package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.StatsGame
import org.bukkit.command.CommandSender

interface CloudService {
    /**
     * Performs the login flow.
     */
    suspend fun performLoginFlow(sender: CommandSender)

    /**
     * Publishes the game stats.
     */
    suspend fun publishGameStats(statsGame: StatsGame)
}