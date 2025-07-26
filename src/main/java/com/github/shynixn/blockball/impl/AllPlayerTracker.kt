package com.github.shynixn.blockball.impl

import checkForPluginMainThread
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.common.toLocation
import org.bukkit.entity.Player

class AllPlayerTracker(
    private val locationFunction: () -> Vector3d,
    private val newPlayerFunction: (Player) -> Unit,
    private val oldPlayerFunction: (Player) -> Unit,
    private val filterPlayerFunction: (Player) -> Boolean = { true }
) {
    private val cache = HashSet<Player>()

    /**
     * Checks the players in the world and returns the interesting ones.
     */
    fun checkAndGet(): List<Player> {
        checkForPluginMainThread()

        val players = locationFunction.invoke().toLocation().world!!.players

        for (player in players.toTypedArray()) {
            if (!filterPlayerFunction.invoke(player)) {
                players.remove(player)
            }
        }

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
