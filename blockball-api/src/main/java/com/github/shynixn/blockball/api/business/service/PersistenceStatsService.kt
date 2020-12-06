package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.persistence.entity.Stats
import kotlinx.coroutines.Deferred

/**
 * Handles loading, caching and persisting player stats.
 */
interface PersistenceStatsService {
    /**
     * Gets the [Stats] from the given player.
     * This call will never return null.
     */
    suspend fun <P> getStatsFromPlayerAsync(player: P): Deferred<Stats>

    /**
     * Closes all resources immediately.
     */
    suspend fun close()
}
