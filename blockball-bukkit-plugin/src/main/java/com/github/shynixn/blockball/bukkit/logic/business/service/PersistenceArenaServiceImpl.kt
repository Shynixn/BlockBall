package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.PersistenceArenaService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.bukkit.logic.business.extension.async
import com.github.shynixn.blockball.bukkit.logic.business.extension.sync
import com.google.inject.Inject
import org.bukkit.plugin.Plugin
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
class PersistenceArenaServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val arenaRepository: ArenaRepository
) : PersistenceArenaService {
    private var cache: MutableList<Arena> = ArrayList()

    /**
     * Refreshes the runtime cache of arenas.
     */
    override fun refresh(): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        async(plugin) {
            synchronized(cache) {
                cache.clear()
                cache.addAll(arenaRepository.getAll())
            }

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Accesses the cached arenas.
     */
    override fun getArenas(): List<Arena> {
        synchronized(cache) {
            return cache.toList()
        }
    }

    /**
     * Removes the given [arena].
     */
    override fun remove(arena: Arena): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        async(plugin) {
            synchronized(cache) {
                if (this.cache.contains(arena)) {
                    cache.remove(arena)
                }
            }

            arenaRepository.delete(arena)

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Saves the given [arena] to the storage.
     */
    override fun save(arena: Arena): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        async(plugin) {
            synchronized(cache) {
                if (!cache.contains(arena)) {
                    cache.add(arena)
                }
            }

            arenaRepository.save(arena)

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }
}