package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.api.persistence.repository.StatsRepository
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.github.shynixn.blockball.core.logic.business.extension.sync
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
class PersistenceStatsServiceImpl @Inject constructor(private val statsRepository: StatsRepository, private val concurrencyService: ConcurrencyService) : PersistenceStatsService {
    /**
     * Returns the amount of stored stats.
     */
    override fun size(): CompletableFuture<Int> {
        val completableFuture = CompletableFuture<Int>()

        async(concurrencyService) {
            val amount = statsRepository.size()

            sync(concurrencyService) {
                completableFuture.complete(amount)
            }
        }

        return completableFuture
    }

    /**
     * Returns all stored stats.
     */
    override fun getAll(): CompletableFuture<List<Stats>> {
        val completableFuture = CompletableFuture<List<Stats>>()

        async(concurrencyService) {
            val items = statsRepository.getAll()

            sync(concurrencyService) {
                completableFuture.complete(items)
            }
        }

        return completableFuture
    }

    /**
     * Returns the [Stats] from the given [player] or allocates a new one.
     */
    override fun <P> getOrCreateFromPlayer(player: P): CompletableFuture<Stats> {
        val completableFuture = CompletableFuture<Stats>()

        async(concurrencyService) {
            val item = statsRepository.getOrCreateFromPlayer(player)

            sync(concurrencyService) {
                completableFuture.complete(item)
            }
        }

        return completableFuture
    }

    /**
     * Saves the given [Stats] to the storage.
     */
    override fun <P> save(player: P, stats: Stats): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        async(concurrencyService) {
            statsRepository.save(player, stats)

            sync(concurrencyService) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }
}