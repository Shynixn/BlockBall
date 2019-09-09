package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.persistence.entity.Stats
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
interface PersistenceStatsService {
    /**
     * Gets the [Stats] from the given player.
     * This call will never return null.
     */
    fun <P> getStatsFromPlayer(player: P): Stats

    /**
     * Gets or creates stats from the player.
     * Call getsStatsFromPlayer instead. This is only intended for internal useage.
     */
    fun <P> refreshStatsFromPlayer(player: P): CompletableFuture<Stats>

    /**
     * Saves the given [Stats] to the storage.
     */
    fun save(stats: Stats): CompletableFuture<Stats>

    /**
     * Clears the cache of the player and saves the allocated resources.
     */
    fun <P> clearResources(player: P): CompletableFuture<Void?>

    /**
     * Closes all resources immediately.
     */
    fun close()
}