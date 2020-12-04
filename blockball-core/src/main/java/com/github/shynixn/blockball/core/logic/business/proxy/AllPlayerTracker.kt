package com.github.shynixn.blockball.core.logic.business.proxy

import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position

class AllPlayerTracker(
    private val locationFunction: () -> Position,
    private val newPlayerFunction: (Any) -> Unit,
    private val oldPlayerFunction: (Any) -> Unit,
    private val filterPlayerFunction: (Any) -> Boolean = { true }
) {
    private val cache = HashSet<Any>()
    lateinit var proxyService: ProxyService

    /**
     * Checks the players inthe world and returns the interesing ones.
     */
    fun checkAndGet(): List<Any> {
        val players = proxyService.getPlayersInWorld<Any, Any>(locationFunction.invoke()).toMutableList()

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
