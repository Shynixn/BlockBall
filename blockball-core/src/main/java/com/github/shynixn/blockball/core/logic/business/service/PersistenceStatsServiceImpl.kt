package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.business.extension.thenAcceptSafely
import com.google.inject.Inject
import java.util.concurrent.CompletableFuture

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class PersistenceStatsServiceImpl @Inject constructor(
    private val statsRepository: StatsRepository,
    private val proxyService: ProxyService,
    private val concurrencyService: ConcurrencyService
) : PersistenceStatsService {
    private val cacheInternal = HashMap<String, Stats>()

    /**
     * Initialize.
     */
    init {
        sync(concurrencyService, 0L, 20 * 60L * 5) {
            cacheInternal.values.forEach { p ->
                save(p)
            }
        }
    }

    /**
     * Clears the cache of the player and saves the allocated resources.
     */
    override fun <P> clearResources(player: P): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()
        val playerUUID = proxyService.getPlayerUUID(player)

        if (!cacheInternal.containsKey(playerUUID)) {
            return completableFuture
        }

        val petMeta = cacheInternal[playerUUID]!!
        val completable = save(petMeta)
        cacheInternal.remove(playerUUID)

        completable.thenAcceptSafely {
            completableFuture.complete(null)
        }

        return completableFuture
    }

    /**
     * Gets the [Stats] from the given player.
     * This call will never return null.
     */
    override fun <P> getStatsFromPlayer(player: P): Stats {
        val playerUUID = proxyService.getPlayerUUID(player)
        val playerName = proxyService.getPlayerName(player)

        if (!cacheInternal.containsKey(playerUUID)) {
            // Blocks the calling (main) thread and should not be executed on an normal server.
            cacheInternal[playerUUID] = statsRepository.getOrCreateFromPlayer(playerName, playerUUID)
        }

        return cacheInternal[playerUUID]!!
    }

    /**
     * Gets or creates stats from the player.
     * Call getsStatsFromPlayer instead. This is only intended for internal useage.
     */
    override fun <P> refreshStatsFromPlayer(player: P): CompletableFuture<Stats> {
        val playerUUID = proxyService.getPlayerUUID(player)
        val playerName = proxyService.getPlayerName(player)
        val completableFuture = CompletableFuture<Stats>()

        async(concurrencyService) {
            val petMeta = statsRepository.getOrCreateFromPlayer(playerName, playerUUID)

            sync(concurrencyService) {
                cacheInternal[playerUUID] = petMeta
                completableFuture.complete(petMeta)
            }
        }

        return completableFuture
    }

    /**
     * Saves the given [Stats] to the storage.
     */
    override fun save(stats: Stats): CompletableFuture<Stats> {
        val completableFuture = CompletableFuture<Stats>()

        async(concurrencyService) {
            statsRepository.save(stats)

            sync(concurrencyService) {
                completableFuture.complete(stats)
            }
        }

        return completableFuture
    }

    /**
     * Closes all resources immediately.
     */
    override fun close() {
        for (player in cacheInternal.keys) {
            statsRepository.save(cacheInternal[player]!!)
        }

        cacheInternal.clear()
    }
}