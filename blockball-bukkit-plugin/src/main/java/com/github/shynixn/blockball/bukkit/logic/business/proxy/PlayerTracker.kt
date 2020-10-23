package com.github.shynixn.blockball.bukkit.logic.business.proxy

import org.bukkit.World
import org.bukkit.entity.Player

class PlayerTracker(
    private val world: World,
    private val newPlayerFunction: (Player) -> Unit,
    private val oldPlayerFunction: (Player) -> Unit
) {
    private val cache = HashSet<Player>()

    /**
     * Checks the players inthe world and returns the interesing ones.
     */
    fun checkAndGet(): List<Player> {
        val players = world.players

        for (player in players) {
            if (!cache.contains(player)) {
                newPlayerFunction.invoke(player)
                cache.add(player)
            }
        }

        for (player in cache.toTypedArray()) {
            if (!players.contains(player)) {
                oldPlayerFunction.invoke(player)
                cache.remove(player)
            }
        }

        return players
    }

    /**
     * Disposes the tracker.
     */
    fun dispose() {
        for (player in cache) {
            oldPlayerFunction.invoke(player)
        }

        cache.clear()
    }
}
