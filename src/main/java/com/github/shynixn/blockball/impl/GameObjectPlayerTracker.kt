package com.github.shynixn.blockball.impl

import org.bukkit.Location
import org.bukkit.entity.Player

class GameObjectPlayerTracker(
    private val renderDistance: Int,
    private val addPlayerFunction: (Player) -> Unit,
    private val removePlayerFunction: (Player) -> Unit,
    private val filterPlayerFunction: (Player) -> Boolean = { true }
) : AutoCloseable {
    val cache = HashMap<Player, Location>()
    /**
     * Checks the players in the world, updates the cache with their positions,
     * and returns the filtered ones within range.
     */
    fun update(location: Location) {
        val world = location.world ?: return

        // 1. Get all players in the world and apply filters right away
        val squaredDistance = (renderDistance * renderDistance).toDouble()
        val activePlayers = world.players.filter { player ->
            filterPlayerFunction(player) &&
                    player.location.distanceSquared(location) <= squaredDistance
        }

        // 2. Handle new players entering the tracking radius
        for (player in activePlayers) {
            if (!cache.containsKey(player)) {
                addPlayerFunction(player)
            }
            // Update/Cache the player's current position
            cache[player] = player.location
        }

        // 3. Handle players leaving (or missing from active players)
        // Using toTypedArray() or toList() prevents ConcurrentModificationException
        for (cachedPlayer in cache.keys.toTypedArray()) {
            if (!activePlayers.contains(cachedPlayer)) {
                removePlayerFunction(cachedPlayer)
                cache.remove(cachedPlayer)
            }
        }
    }

    /**
     * Disposes the tracker and triggers the cleanup function for all cached players.
     */
    override fun close() {
        for (player in cache.keys) {
            removePlayerFunction(player)
        }
        cache.clear()
    }
}