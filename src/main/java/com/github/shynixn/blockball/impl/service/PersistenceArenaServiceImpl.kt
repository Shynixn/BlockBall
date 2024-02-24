package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.ArenaRepository
import com.github.shynixn.blockball.contract.PersistenceArenaService
import com.github.shynixn.blockball.entity.Arena
import com.google.inject.Inject
import org.bukkit.plugin.Plugin
import java.util.concurrent.CompletableFuture

class PersistenceArenaServiceImpl @Inject constructor(
    private val arenaRepository: ArenaRepository,
    private val plugin: Plugin
) : PersistenceArenaService {
    private var cache: MutableList<Arena> = ArrayList()

    /**
     * Refreshes the runtime cache of arenas.
     */
    override fun refresh(): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            val arenas = arenaRepository.getAll()
            plugin.server.scheduler.runTask(plugin, Runnable {
                cache.clear()
                cache.addAll(arenas)
                completableFuture.complete(null)
            })
        })

        return completableFuture
    }

    /**
     * Accesses the cached arenas.
     */
    override fun getArenas(): List<Arena> {
        return cache
    }

    /**
     * Removes the given [arena].
     */
    override fun remove(arena: Arena): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        if (this.cache.contains(arena)) {
            cache.remove(arena)
        }

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            arenaRepository.delete(arena)
            plugin.server.scheduler.runTask(plugin, Runnable {
                completableFuture.complete(null)
            })
        })

        return completableFuture
    }

    /**
     * Saves the given [arena] to the storage.
     */
    override fun save(arena: Arena): CompletableFuture<Void?> {
        val completableFuture = CompletableFuture<Void?>()

        if (!cache.contains(arena)) {
            cache.add(arena)
        }

        plugin.server.scheduler.runTaskAsynchronously(plugin, Runnable {
            arenaRepository.save(arena)
            plugin.server.scheduler.runTask(plugin, Runnable {
                completableFuture.complete(null)
            })
        })

        return completableFuture
    }
}
