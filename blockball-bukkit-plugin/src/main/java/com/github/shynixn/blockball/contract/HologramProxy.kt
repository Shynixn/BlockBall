package com.github.shynixn.blockball.contract

import org.bukkit.Location
import org.bukkit.entity.Player

interface HologramProxy {
    /**
     * List of players being able to see this hologram.
     */
    val players: MutableSet<Player>

    /**
     * List of lines being displayed on the hologram.
     */
    var lines: List<String>

    /**
     * Location of the hologram.
     */
    var location: Location

    /**
     * Gets if this hologram was removed.
     */
    val isDead: Boolean

    /**
     * Updates changes of the hologram.
     */
    fun update()

    /**
     * Removes this hologram permanently.
     */
    fun remove()
}
