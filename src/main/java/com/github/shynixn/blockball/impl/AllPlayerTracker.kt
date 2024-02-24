package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.entity.Position
import org.bukkit.entity.Player

class AllPlayerTracker(
    private val locationFunction: () -> Position,
    private val newPlayerFunction: (Player) -> Unit,
    private val oldPlayerFunction: (Player) -> Unit,
    private val filterPlayerFunction: (Player) -> Boolean = { true }
) {
    private val cache = HashSet<Player>()
    lateinit var proxyService: ProxyService

    /**
     * Checks the players in the world and returns the interesting ones.
     */
    fun checkAndGet(): List<Player> {
        val players = proxyService.getPlayersInWorld<Player, Any>(locationFunction.invoke()).toMutableList()

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
