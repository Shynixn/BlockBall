package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Arena
import java.util.concurrent.CompletableFuture

interface PersistenceArenaService {
    /**
     * Refreshes the runtime cache of arenas.
     */
    fun refresh(): CompletableFuture<Void?>

    /**
     * Accesses the cached arenas.
     */
    fun getArenas(): List<Arena>

    /**
     * Removes the given [arena].
     */
    fun remove(arena: Arena): CompletableFuture<Void?>

    /**
     * Saves the given [arena] to the storage.
     */
    fun save(arena: Arena): CompletableFuture<Void?>
}
