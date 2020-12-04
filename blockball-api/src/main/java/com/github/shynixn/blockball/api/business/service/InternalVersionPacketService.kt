package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position

interface InternalVersionPacketService {
    /**
     * Creates a new teleport packet.
     */
    fun createEntityTeleportPacket(entityId: Int, position: Position): Any

    /**
     * Creates a spawn packet.
     */
    fun createEntitySpawnPacket(entityId: Int, entityType: String, position: Position): Any

    /**
     * Creates a entity metadata packet.
     */
    fun createEntityMetaDataPacket(entityId: Int, entityMetaData: EntityMetaData): Any

    /**
     * Creates an entity equipment packet.
     */
    fun <I> createEntityEquipmentPacket(
        entityId: Int,
        slot: CompatibilityArmorSlotType,
        itemStack: I
    ) : Any
}

