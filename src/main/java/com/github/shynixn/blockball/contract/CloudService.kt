package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.CloudGame
import org.bukkit.command.CommandSender

interface CloudService {
    /**
     * Performs the login flow.
     */
    suspend fun performLoginFlow(sender: CommandSender)

    /**
     * Performs logout.
     */
    suspend fun performLogout(sender: CommandSender)

    /**
     * Publishes the game stats.
     */
    suspend fun publishGameStats(cloudGame: CloudGame)
}