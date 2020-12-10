package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_9_R2

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.InternalVersionPacketService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.google.inject.Inject
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.nio.charset.Charset
import java.util.*

class InternalVersionPacket19R2ServiceImpl @Inject constructor(private val pluginProxy: PluginProxy) :
    InternalVersionPacketService {
    private val entityCompatibilityCache = hashMapOf("ARMOR_STAND" to 30, "SLIME" to 55)
    private val dataSerializerClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.PacketDataSerializer") }
    private val dataSerializerConstructor by lazy { dataSerializerClazz.getDeclaredConstructor(ByteBuf::class.java) }
    private val dataDeSerializationPacketMethod by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
            .getDeclaredMethod("a", dataSerializerClazz)
    }
    private val packetPlayOutEntityTeleport by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityTeleport")
    }
    private val packetPlayOutEntitySpawnLiving by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
    }
    private val dataWatcherClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.DataWatcher")
    }
    private val dataWatcherConstructor by lazy {
        dataWatcherClazz.getDeclaredConstructor(pluginProxy.findClazz("net.minecraft.server.VERSION.Entity"))
    }
    private val packetPlayOutEntityMetaData by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata")
    }
    private val craftItemStackNmsMethod by lazy {
        pluginProxy.findClazz("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack")
            .getDeclaredMethod("asNMSCopy", pluginProxy.findClazz("org.bukkit.inventory.ItemStack"))
    }
    private val nmsItemStackClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.ItemStack")
    }
    private val packetPlayOutEntityEquipment by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityEquipment")
    }
    private val enumItemSlotClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.EnumItemSlot")
    }
    private val iRegistryClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.IRegistry") }
    private val entityRegistry by lazy { iRegistryClazz.getDeclaredField("ENTITY_TYPE").get(null) }
    private val iRegistryRegisterMethod by lazy { iRegistryClazz.getDeclaredMethod("a", Any::class.java) }
    private val entityTypesClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.EntityTypes") }
    private val mojangPairClazz by lazy {
        pluginProxy.findClazz("com.mojang.datafixers.util.Pair")
    }

    /**
     * Creates a new teleport packet.
     */
    override fun createEntityTeleportPacket(entityId: Int, position: Position): Any {
        val buffer = Unpooled.buffer()
        buffer.writeId(entityId)
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeBoolean(false)
        return createPacketFromBuffer(packetPlayOutEntityTeleport, buffer)
    }

    /**
     * Creates a spawn packet.
     */
    override fun createEntitySpawnPacket(entityId: Int, entityType: String, position: Position): Any {
        val buffer = Unpooled.buffer()
        buffer.writeId(entityId)

        val entityUUID = UUID.randomUUID()
        buffer.writeLong(entityUUID.mostSignificantBits)
        buffer.writeLong(entityUUID.leastSignificantBits)

        when {
            pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_13_R1) -> {
                val nmsEntityType = entityTypesClazz.getDeclaredField(entityType).get(null)
                val nmsEntityId = iRegistryRegisterMethod.invoke(entityRegistry, nmsEntityType) as Int
                buffer.writeId(nmsEntityId)
            }
            pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_11_R1) -> {
                buffer.writeId(entityCompatibilityCache[entityType]!!)
            }
            else -> {
                buffer.writeByte(entityCompatibilityCache[entityType]!! and 255)
            }
        }

        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte(0)
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())

        if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_15_R1)) {
            return createPacketFromBuffer(packetPlayOutEntitySpawnLiving, buffer)
        }

        buffer.writeByte(255)
        val packet = packetPlayOutEntitySpawnLiving.getDeclaredConstructor().newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(buffer)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
        packetPlayOutEntitySpawnLiving.getDeclaredField("m")
            .accessible(true).set(packet, dataWatcherConstructor.newInstance(null))
        return packet
    }

    /**
     * Creates a entity metadata packet.
     */
    override fun createEntityMetaDataPacket(entityId: Int, entityMetaData: EntityMetaData): Any {
        // https://wiki.vg/Entity_metadata#Entity_Metadata_Format -> Value of Type field. Type of Value = Boolean -> 7.
        // 1.9.4 -> https://wiki.vg/index.php?title=Entity_metadata&oldid=7864
        val buffer = Unpooled.buffer()
        buffer.writeId(entityId)

        val byteTypeValue = 0
        val intTypeValue = 1

        val booleanTypeValue = if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_13_R1)) {
            7
        } else {
            6
        }

        val optChatTypeValue = 5

        val rotationTypeValue =
            if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_13_R1)) {
                8
            } else {
                7
            }

        if (entityMetaData.customNameVisible != null) {
            buffer.writeByte(3)
            buffer.writeId(booleanTypeValue)
            buffer.writeBoolean(entityMetaData.customNameVisible!!)
        }

        if (entityMetaData.customname != null) {
            if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_13_R1)) {
                val payload = "{\"text\": \"${entityMetaData.customname}\"}".toByteArray(Charset.forName("UTF-8"))
                buffer.writeByte(2)
                buffer.writeId(optChatTypeValue)
                buffer.writeBoolean(true)
                buffer.writeId(payload.size)
                buffer.writeBytes(payload)
            } else {
                val stringType = 3
                val payload = entityMetaData.customname!!.toByteArray(Charset.forName("UTF-8"))
                buffer.writeByte(2)
                buffer.writeId(stringType)
                buffer.writeId(payload.size)
                buffer.writeBytes(payload)
            }
        }

        if (entityMetaData.slimeSize != null) {
            val slimeSizeIndex =
                when {
                    pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_15_R1) -> {
                        15
                    }
                    pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_10_R1) -> {
                        12
                    }
                    else -> {
                        11
                    }
                }

            buffer.writeByte(slimeSizeIndex)
            buffer.writeId(intTypeValue)
            buffer.writeId(entityMetaData.slimeSize!!)
        }

        if (entityMetaData.armorstandHeadRotation != null) {
            when {
                pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_15_R1) -> {
                    buffer.writeByte(15)
                }
                pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_10_R1) -> {
                    buffer.writeByte(12)
                }
                else -> {
                    buffer.writeByte(11)
                }
            }

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

        buffer.writeByte(255)

        return createPacketFromBuffer(packetPlayOutEntityMetaData, buffer)
    }

    /**
     * Creates an entity equipment packet.
     */
    override fun <I> createEntityEquipmentPacket(entityId: Int, slot: CompatibilityArmorSlotType, itemStack: I): Any {
        val nmsItemStack = craftItemStackNmsMethod.invoke(null, itemStack)

        if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_16_R1)) {
            val pair = mojangPairClazz.getDeclaredConstructor(Any::class.java, Any::class.java)
                .newInstance(enumItemSlotClazz.enumConstants[slot.id116], nmsItemStack)
            return packetPlayOutEntityEquipment
                .getDeclaredConstructor(Int::class.java, List::class.java)
                .newInstance(entityId, listOf(pair))
        }

        return packetPlayOutEntityEquipment
            .getDeclaredConstructor(Int::class.java, enumItemSlotClazz, nmsItemStackClazz)
            .newInstance(entityId, enumItemSlotClazz.enumConstants[slot.id116], nmsItemStack)
    }


    /*
    * Creates a packet from buffer.
    */
    private fun createPacketFromBuffer(packetClazz: Class<*>, byteBuf: ByteBuf): Any {
        val packet = packetClazz.getDeclaredConstructor().newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(byteBuf)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
        return packet
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

}
