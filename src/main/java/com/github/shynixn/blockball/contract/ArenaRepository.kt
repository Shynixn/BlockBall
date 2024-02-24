package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Arena

interface ArenaRepository {
    /**
     * Returns all stored arenas in this repository.
     */
    fun getAll(): List<Arena>

    /**
     * Delets the given arena in the storage.
     */
    fun delete(arena: Arena)

    /**
     * Saves the given [arena] to the storage.
     */
    fun save(arena: Arena)
}
