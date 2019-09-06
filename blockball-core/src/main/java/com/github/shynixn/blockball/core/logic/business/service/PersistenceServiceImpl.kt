package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.PersistenceService
import com.github.shynixn.blockball.api.persistence.repository.Repository
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.github.shynixn.blockball.core.logic.business.extension.sync
import java.util.concurrent.CompletableFuture

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
open class PersistenceServiceImpl<E>(private val repository: Repository<E>, private val concurrencyService: ConcurrencyService) : PersistenceService<E> {
    /**
     * Gets the currently cached items.
     */
    override val items: MutableList<E> = ArrayList()

    /**
     * Refreshes the runtime cache of items.
     */
    override fun refresh(): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        async(concurrencyService) {
            val items = repository.getAll()

            sync(concurrencyService) {
                this.items.clear()
                this.items.addAll(items)

                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Removes the given [item].
     */
    override fun remove(item: E): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        if (this.items.contains(item)) {
            items.remove(item)
        }

        async(concurrencyService) {
            repository.delete(item)

            sync(concurrencyService) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }

    /**
     * Saves the given item to the storage.
     */
    override fun save(item: E): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        if (!items.contains(item)) {
            items.add(item)
        }

        async(concurrencyService) {
            repository.save(item)

            sync(concurrencyService) {
                completableFuture.complete(null)
            }
        }

        return completableFuture
    }
}