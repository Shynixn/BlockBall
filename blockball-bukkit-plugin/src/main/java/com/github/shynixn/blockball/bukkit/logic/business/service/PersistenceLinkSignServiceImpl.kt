package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.PersistenceLinkSignService
import com.github.shynixn.blockball.api.persistence.entity.LinkSign
import com.github.shynixn.blockball.api.persistence.repository.ServerSignRepository
import com.github.shynixn.blockball.bukkit.logic.business.extension.async
import com.github.shynixn.blockball.bukkit.logic.business.extension.sync
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.plugin.Plugin
import java.util.*
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
class PersistenceLinkSignServiceImpl @Inject constructor(private val signRepository: ServerSignRepository, private val plugin: Plugin) : PersistenceLinkSignService {
    private var cache: HashSet<LinkSign>? = null

    /**
     * Returns the [LinkSign] from the given [location].
     */
    override fun <L> getFromLocation(location: L): CompletableFuture<Optional<LinkSign>> {
        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        val completableFuture = CompletableFuture<Optional<LinkSign>>()

        async(plugin) {
            if (cache == null) {
                refreshCache()
            }

            val result = cache!!.find { item ->
                val positon = item.position
                positon.blockX == location.blockX && positon.blockY == location.blockY && positon.blockZ == location.blockZ
            }

            sync(plugin) {
                if (result == null) {
                    completableFuture.complete(Optional.empty())
                } else {
                    completableFuture.complete(Optional.of(result))
                }
            }
        }

        return completableFuture
    }

    /**
     * Remove the given [linkSign] from the storage.
     */
    override fun remove(linkSign: LinkSign): CompletableFuture<Void> {
        val completableFuture = CompletableFuture<Void>()

        async(plugin) {
            if (cache == null) {
                refreshCache()
            }

            synchronized(this) {
                if (this.cache!!.contains(linkSign)) {
                    cache!!.remove(linkSign)
                }
            }

            signRepository.saveAll(cache!!.toList())

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Returns all stored signs in this repository.
     */
    override fun getAll(): CompletableFuture<List<LinkSign>> {
        val completableFuture = CompletableFuture<List<LinkSign>>()

        async(plugin) {
            if (cache == null) {
                refreshCache()
            }

            val sign = cache

            sync(plugin) {
                completableFuture.complete(sign!!.toList())
            }
        }

        return completableFuture
    }

    /**
     * Refreshes the runtime cache of the linked services.
     */
    override fun refresh(): CompletableFuture<Void> {
        val completableFuture = CompletableFuture<Void>()

        async(plugin) {
            refreshCache()

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Returns the amount of items in this repository.
     */
    override fun size(): CompletableFuture<Int> {
        val completableFuture = CompletableFuture<Int>()

        async(plugin) {
            if (cache == null) {
                refreshCache()
            }

            val amount = this.cache!!.size

            sync(plugin) {
                completableFuture.complete(amount)
            }
        }

        return completableFuture
    }

    /**
     * Saves the given [LinkSign] to the storage.
     */
    override fun save(linkSign: LinkSign): CompletableFuture<Void> {
        val completableFuture = CompletableFuture<Void>()

        async(plugin) {
            if (cache == null) {
                refreshCache()
            }

            synchronized(this) {
                cache!!.add(linkSign)
            }

            signRepository.saveAll(cache!!.toList())

            sync(plugin) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    private fun refreshCache() {
        if (cache == null) {
            cache = HashSet()
        }

        synchronized(this) {
            cache!!.clear()
            cache!!.addAll(signRepository.getAll())
        }
    }
}