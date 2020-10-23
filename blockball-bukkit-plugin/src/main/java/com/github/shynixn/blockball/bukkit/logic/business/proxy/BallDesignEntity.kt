package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position
import org.bukkit.entity.Player

class BallDesignEntity(val entityId: Int) {
    /**
     * Proxy service dependency.
     */
    lateinit var proxyService: ProxyService

    /**
     * Packet service dependency.
     */
    lateinit var packetService: PacketService

    /**
     * Reference.
     */
    lateinit var ball: BallProxy

    /**
     * Ticks the hitbox.
     * @param players watching this hitbox.
     */
    fun <P> tick(players: List<P>) {
        packetService.sendEntityTeleportPacket(players, entityId, ball.getLocation())
    }

    /**
     * Spawns the ball for the given player.
     */
    fun spawn(player: Player, position: Position) {
        packetService.sendEntitySpawnPacket(player, entityId, "ARMOR_STAND", position)
    }

    /**
     * Destroys the ball for the given player.
     */
    fun destroy(player: Player) {
        packetService.sendEntityDestroyPacket(player, entityId)
    }
}
