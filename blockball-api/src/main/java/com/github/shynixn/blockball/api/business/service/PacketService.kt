package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position

interface PacketService {
    /**
     * Sends a velocity packet.
     */
    fun <P> sendEntityVelocityPacket(player: P, entityId: Int, velocity: Position)

    /**
     * Sends a destroy packet.
     */
    fun <P> sendEntityDestroyPacket(player: P, entityId: Int)

    /**
     * Sends a teleport packet.
     */
    fun <P> sendEntityTeleportPacket(player: P, entityId: Int, position: Position)

    /**
     * Sends a spawn packet.
     */
    fun <P> sendEntitySpawnPacket(player: P, entityId: Int, entityType: String, position: Position)

    /**
     * Sends a meta data packet.
     */
    fun <P> sendEntityMetaDataPacket(player: P, entityId: Int, entityMetaData: EntityMetaData)

    /**
     * Sends an equipment packet.
     */
    fun <P, I> sendEntityEquipmentPacket(player: P, entityId: Int, slot : CompatibilityArmorSlotType, itemStack: I)
}
