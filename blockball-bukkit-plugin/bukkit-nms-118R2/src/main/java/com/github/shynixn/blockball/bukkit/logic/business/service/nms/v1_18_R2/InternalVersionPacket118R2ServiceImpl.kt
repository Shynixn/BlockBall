package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_18_R2

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.InternalVersionPacketService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.google.inject.Inject
import com.mojang.datafixers.util.Pair
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.core.Registry
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundAddMobPacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ItemStack
import java.nio.charset.Charset
import java.util.*

class InternalVersionPacket118R2ServiceImpl @Inject constructor(
    private val pluginProxy: PluginProxy
) : InternalVersionPacketService {
    private val craftItemStackNmsMethod by lazy {
        pluginProxy.findClazz("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack")
            .getDeclaredMethod("asNMSCopy", pluginProxy.findClazz("org.bukkit.inventory.ItemStack"))
    }
    private val enumItemSlotClazz by lazy {
        try {
            pluginProxy.findClazz("net.minecraft.world.entity.EnumItemSlot")
        } catch (e: Exception) {
            pluginProxy.findClazz("net.minecraft.server.VERSION.EnumItemSlot")
        }
    }
    private val mojangPairClazz by lazy {
        pluginProxy.findClazz("com.mojang.datafixers.util.Pair")
    }

    /**
     * Creates a new teleport packet.
     */
    override fun createEntityTeleportPacket(entityId: Int, position: Position): Any {
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        buffer.writeId(entityId)
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeBoolean(false)
        return ClientboundTeleportEntityPacket(buffer)
    }

    /**
     * Creates a spawn packet.
     */
    override fun createEntitySpawnPacket(entityId: Int, entityType: String, position: Position): Any {
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        buffer.writeId(entityId)

        val entityUUID = UUID.randomUUID()
        buffer.writeLong(entityUUID.mostSignificantBits)
        buffer.writeLong(entityUUID.leastSignificantBits)
        val nmsEntityType = if (entityType == "ARMOR_STAND") {
            EntityType.ARMOR_STAND
        } else {
            EntityType.SLIME
        }

        val nmsEntityId = Registry.ENTITY_TYPE.getId(nmsEntityType)

        buffer.writeId(nmsEntityId)
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte(0)
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())

        return ClientboundAddMobPacket(buffer)
    }

    /**
     * Creates a entity metadata packet.
     */
    override fun createEntityMetaDataPacket(entityId: Int, entityMetaData: EntityMetaData): Any {
        // https://wiki.vg/Entity_metadata#Entity_Metadata_Format -> Value of Type field. Type of Value = Boolean -> 7.
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        buffer.writeId(entityId)

        val byteTypeValue = 0
        val intTypeValue = 1
        val booleanTypeValue = 7
        val optChatTypeValue = 5
        val rotationTypeValue = 8

        if (entityMetaData.customNameVisible != null) {
            buffer.writeByte(3)
            buffer.writeId(booleanTypeValue)
            buffer.writeBoolean(entityMetaData.customNameVisible!!)
        }

        if (entityMetaData.customname != null) {
            val payload = "{\"text\": \"${entityMetaData.customname}\"}".toByteArray(Charset.forName("UTF-8"))
            buffer.writeByte(2)
            buffer.writeId(optChatTypeValue)
            buffer.writeBoolean(true)
            buffer.writeId(payload.size)
            buffer.writeBytes(payload)
        }

        if (entityMetaData.slimeSize != null) {
            val slimeSizeIndex = 16
            buffer.writeByte(slimeSizeIndex)
            buffer.writeId(intTypeValue)
            buffer.writeId(entityMetaData.slimeSize!!)
        }

        if (entityMetaData.armorstandHeadRotation != null) {
            buffer.writeByte(16)
            buffer.writeId(rotationTypeValue)
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.x.toFloat())
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.y.toFloat())
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.z.toFloat())
        }

        if (entityMetaData.isInvisible != null) {
            buffer.writeByte(0)
            buffer.writeId(byteTypeValue)
            buffer.writeByte(0x20)
        }

        if (entityMetaData.isSmall != null && entityMetaData.isSmall!!) {
            buffer.writeByte(15)
            buffer.writeId(byteTypeValue)
            buffer.writeByte(0x01)
        }

        buffer.writeByte(255)
        return ClientboundSetEntityDataPacket(buffer)
    }

    /**
     * Creates an entity equipment packet.
     */
    override fun <I> createEntityEquipmentPacket(entityId: Int, slot: CompatibilityArmorSlotType, itemStack: I): Any {
        val nmsItemStack = craftItemStackNmsMethod.invoke(null, itemStack)
        val pair = mojangPairClazz.getDeclaredConstructor(Any::class.java, Any::class.java)
            .newInstance(enumItemSlotClazz.enumConstants[slot.id116], nmsItemStack)
        return ClientboundSetEquipmentPacket(
            entityId,
            mutableListOf(pair) as MutableList<Pair<EquipmentSlot, ItemStack>>?
        )
    }

    /**
     * Mathhelper
     */
    private fun mathhelperA(var0: Double, var2: Double, var4: Double): Double {
        return if (var0 < var2) {
            var2
        } else {
            if (var0 > var4) var4 else var0
        }
    }

    /**
     * Writes id.
     */
    private fun ByteBuf.writeId(id: Int) {
        var i = id
        while (i and -128 != 0) {
            writeByte(i and 127 or 128)
            i = i ushr 7
        }
        writeByte(i)
    }
}
