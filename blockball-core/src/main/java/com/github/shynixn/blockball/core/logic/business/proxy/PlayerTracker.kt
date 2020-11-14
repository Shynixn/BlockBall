package com.github.shynixn.blockball.core.logic.business.proxy

import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position

class PlayerTracker(
    private val location: Position,
    private val newPlayerFunction: (Any) -> Unit,
    private val oldPlayerFunction: (Any) -> Unit
) {
    private val cache = HashSet<Any>()
    lateinit var proxyService: ProxyService

    /**
     * Checks the players inthe world and returns the interesing ones.
     */
    fun checkAndGet(): List<Any> {
        val players = proxyService.getPlayersInWorld<Any, Any>(location)

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
