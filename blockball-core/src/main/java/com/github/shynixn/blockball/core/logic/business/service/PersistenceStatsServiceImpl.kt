package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.service.CoroutineSessionService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.google.inject.Inject
import kotlinx.coroutines.*

/**
 * Handles loading, caching and persisting player stats.
 */
class PersistenceStatsServiceImpl @Inject constructor(
    private val statsRepository: StatsRepository,
    private val proxyService: ProxyService,
    private val coroutineSessionService: CoroutineSessionService
) : PersistenceStatsService {
    private val cacheInternal = HashMap<Any, Deferred<Stats>>()
    private var queryPlayersInterval = 1000 * 60 * 5L

    /**
     * Initialize.
     */
    init {
        coroutineSessionService.launch {
            while (true) {
                val stats = cacheInternal.values.toTypedArray()

                withContext(coroutineSessionService.asyncDispatcher) {
                    for (stat in stats){
                        statsRepository.save(stat.await())
                    }
                }

                for (key in cacheInternal.keys.toTypedArray()) {
                    if (!proxyService.isPlayerOnline(key)) {
                        cacheInternal.remove(key)
                    }
                }

                delay(queryPlayersInterval)
            }
        }
    }

    /**
     * Gets the [Stats] from the given player.
     * This call will never return null.
     */
    override suspend fun <P> getStatsFromPlayerAsync(player: P): Deferred<Stats> {
        require(player is Any)

        val playerUUID = proxyService.getPlayerUUID(player)
        val playerName = proxyService.getPlayerName(player)

        if (cacheInternal.containsKey(player)) {
            return cacheInternal[player]!!
        }

        return coroutineScope {
            cacheInternal[player] = async(coroutineSessionService.asyncDispatcher) {
                statsRepository.getOrCreateFromPlayer(playerName, playerUUID)
            }

            cacheInternal[player]!!
        }
    }

    /**
     * Closes all resources immediately.
     */
    override suspend fun close() {
        for (player in cacheInternal.keys) {
            statsRepository.save(cacheInternal[player]!!.await())
        }

        cacheInternal.clear()
    }
}
