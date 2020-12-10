package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.GameEndEvent
import com.github.shynixn.blockball.api.bukkit.event.GameGoalEvent
import com.github.shynixn.blockball.api.bukkit.event.GameJoinEvent
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.google.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Handles stats changes listening.
 */
class StatsListener @Inject constructor(
    private val persistenceStatsService: PersistenceStatsService
) : Listener {

    /**
     * Preloads the stats.
     *
     * @param event event.
     */
    @EventHandler
    suspend fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        persistenceStatsService.getStatsFromPlayerAsync(event.player).await()
    }

    /**
     * Updates the goals of a player when he shoots a goal.
     *
     * @param event event
     */
    @EventHandler
    suspend fun onPlayerShootGoalEvent(event: GameGoalEvent) {
        persistenceStatsService.getStatsFromPlayerAsync(event.player).await().amountOfGoals += 1
    }

    /**
     * Gets called when a player joins the match
     *
     * @param event event
     */
    @EventHandler
    suspend fun onPlayerJoinGameEvent(event: GameJoinEvent) {
        persistenceStatsService.getStatsFromPlayerAsync(event.player).await().amountOfPlayedGames += 1
    }

    /**
     * Gets called when a game gets won.
     *
     * @param event event
     */
    @EventHandler
    suspend fun onTeamWinEvent(event: GameEndEvent) {
        var winningPlayers = event.game.redTeam

        if (event.winningTeam == Team.BLUE) {
            winningPlayers = event.game.blueTeam
        }

        winningPlayers.forEach { p ->
            persistenceStatsService.getStatsFromPlayerAsync(p).await().amountOfWins += 1
        }
    }
}
