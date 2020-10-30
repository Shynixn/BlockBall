package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.google.inject.Inject
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import net.minecraft.server.v1_14_R1.DataWatcherRegistry
import net.minecraft.server.v1_14_R1.EntityArmorStand
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityMetadata
import java.io.OutputStream
import java.util.*
import kotlin.math.abs

class PacketJavaProtocolServiceImpl @Inject constructor(
    private val packageService: PackageService,
    private val pluginProxy: PluginProxy
) :
    PacketService {
    private val dataSerializerClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.PacketDataSerializer") }
    private val dataSerializerConstructor by lazy { dataSerializerClazz.getDeclaredConstructor(ByteBuf::class.java) }
    private val dataDeSerializationPacketMethod by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
            .getDeclaredMethod("a", dataSerializerClazz)
    }
    private val packetPlayOutEntityDestroyClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy")
    }
    private val packetPlayOutEntityRelMove by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntity\$PacketPlayOutRelEntityMove")
    }
    private val packetPlayOutEntityVelocity by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityVelocity")
    }
    private val packetPlayOutEntityTeleport by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityTeleport")
    }
    private val packetPlayOutEntitySpawnLiving by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
    }
    private val packetPlayOutEntityMetaData by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata")
    }
    private val packetPlayOutEntityEquipment by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityEquipment")
    }

    private val iRegistryClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.IRegistry") }
    private val entityRegistry by lazy { iRegistryClazz.getDeclaredField("ENTITY_TYPE").get(null) }
    private val iRegistryRegisterMethod by lazy { iRegistryClazz.getDeclaredMethod("a", Any::class.java) }
    private val entityTypesClazz by lazy { pluginProxy.findClazz("net.minecraft.server.VERSION.EntityTypes") }
    private val itemIdMethod by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.Item")
            .getDeclaredMethod("getId", pluginProxy.findClazz("net.minecraft.server.VERSION.Item"))
    }
    private val craftItemStackNmsMethod by lazy {
        pluginProxy.findClazz("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack")
            .getDeclaredMethod("asNMSCopy", pluginProxy.findClazz("org.bukkit.inventory.ItemStack"))
    }
    private val getItemFromNmsItemMethod by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.ItemStack")
            .getDeclaredMethod("getItem")
    }
    private val getItemCount by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.ItemStack")
            .getDeclaredMethod("getCount")
    }
    private val getItemNbt by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.ItemStack")
            .getDeclaredMethod("getTag")
    }
    private val nbtToOutputStream by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.NBTCompressedStreamTools")
            .getDeclaredMethod(
                "a", pluginProxy.findClazz("net.minecraft.server.VERSION.NBTTagCompound"),
                OutputStream::class.java
            )
    }

    /**
     * Sends an entity move packet.
     */
    override fun <P> sendEntityMovePacket(
        player: P,
        entityId: Int,
        previousPosition: Position,
        nextPosition: Position,
        isOnGround: Boolean
    ) {
        if (previousPosition.worldName != nextPosition.worldName) {
            sendEntityTeleportPacket(player, entityId, nextPosition)
            return
        }

        if (absoluteDifference(nextPosition.x, previousPosition.x) > 8
            || absoluteDifference(nextPosition.y, previousPosition.y) > 8
            || absoluteDifference(nextPosition.z, previousPosition.z) > 8
        ) {
            sendEntityTeleportPacket(player, entityId, nextPosition)
            return
        }

        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)
        buffer.writeShort(((nextPosition.x * 32 - previousPosition.x * 32) * 128).toInt())
        buffer.writeShort(((nextPosition.y * 32 - previousPosition.y * 32) * 128).toInt())
        buffer.writeShort(((nextPosition.z * 32 - previousPosition.z * 32) * 128).toInt())
        buffer.writeBoolean(isOnGround)
        sendPacket(player, packetPlayOutEntityRelMove, buffer)
    }

    /**
     * Sends a velocity packet.
     */
    override fun <P> sendEntityVelocityPacket(player: P, entityId: Int, velocity: Position) {
        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)
        buffer.writeShort((mathhelperA(velocity.x, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(velocity.y, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(velocity.z, -3.9, 3.9) * 8000.0).toInt())
        sendPacket(player, packetPlayOutEntityVelocity, buffer)
    }

    /**
     * Sends a destroy packet.
     */
    override fun <P> sendEntityDestroyPacket(player: P, entityId: Int) {
        val buffer = Unpooled.buffer()
        writeId(buffer, 1)
        writeId(buffer, entityId)
        sendPacket(player, packetPlayOutEntityDestroyClazz, buffer)
    }

    /**
     * Sends a teleport packet.
     */
    override fun <P> sendEntityTeleportPacket(player: P, entityId: Int, position: Position) {
        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)
        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeBoolean(false)
        sendPacket(player, packetPlayOutEntityTeleport, buffer)
    }

    /**
     * Sends a spawn packet.
     */
    override fun <P> sendEntitySpawnPacket(player: P, entityId: Int, entityType: String, position: Position) {
        val entityUUID = UUID.randomUUID()
        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)
        buffer.writeLong(entityUUID.mostSignificantBits)
        buffer.writeLong(entityUUID.leastSignificantBits)

        val nmsEntityType = entityTypesClazz.getDeclaredField(entityType).get(null)
        val nmsEntityId = iRegistryRegisterMethod.invoke(entityRegistry, nmsEntityType) as Int
        writeId(buffer, nmsEntityId)

        buffer.writeDouble(position.x)
        buffer.writeDouble(position.y)
        buffer.writeDouble(position.z)
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte(0)
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())

        sendPacket(player, packetPlayOutEntitySpawnLiving, buffer)
    }

    /**
     * Sends a meta data packet.
     */
    override fun <P> sendEntityMetaDataPacket(player: P, entityId: Int, entityMetaData: EntityMetaData) {
        // https://wiki.vg/Entity_metadata#Entity_Metadata_Format -> Value of Type field. Type of Value = Boolean -> 7.
        val booleanTypeValue = 7
        val intTypeValue = 1
        val rotationTypeValue = 8

        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)

        if (entityMetaData.customNameVisible != null) {
            buffer.writeByte(3)
            writeId(buffer, booleanTypeValue)
            buffer.writeBoolean(entityMetaData.customNameVisible!!)
        }

        if (entityMetaData.slimeSize != null) {
            buffer.writeByte(15)
            writeId(buffer, intTypeValue)
            writeId(buffer, entityMetaData.slimeSize!!)
        }

        if (entityMetaData.armorstandHeadRotation != null) {
            buffer.writeByte(15)
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.x.toFloat())
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.y.toFloat())
            buffer.writeFloat(entityMetaData.armorstandHeadRotation!!.z.toFloat())
            writeId(buffer, rotationTypeValue)
        }

        buffer.writeByte(4)
        writeId(buffer, booleanTypeValue)
        buffer.writeBoolean(false)
        buffer.writeByte(255)
        sendPacket(player, packetPlayOutEntityMetaData, buffer)
    }

    /**
     * Sends an equipment packet.
     */
    override fun <P, I> sendEntityEquipmentPacket(player: P, entityId: Int, slotId: Int, itemStack: I) {
        val buffer = Unpooled.buffer()
        writeId(buffer, entityId)
        writeId(buffer, slotId)

        if (itemStack == null) {
            buffer.writeBoolean(false)
        } else {
            buffer.writeBoolean(true)
            val nmsItemStack = craftItemStackNmsMethod.invoke(null, itemStack)
            val nmsItem = getItemFromNmsItemMethod.invoke(nmsItemStack)
            val nmsItemId = itemIdMethod.invoke(null, nmsItem) as Int
            writeId(buffer, nmsItemId)
            buffer.writeByte(getItemCount.invoke(nmsItemStack) as Int)
            val nbtTag = getItemNbt.invoke(nmsItemStack)

            if (nbtTag != null) {
                nbtToOutputStream.invoke(null, nbtTag, ByteBufOutputStream(buffer))
            } else {
                buffer.writeByte(0)
            }
        }

        sendPacket(player, packetPlayOutEntityEquipment, buffer)
    }

    /**
     * Sends a packet.
     */
    private fun <P> sendPacket(player: P, packetClazz: Class<*>, byteBuf: ByteBuf) {
        val packet = packetClazz.newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(byteBuf)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
        packageService.sendPacket(player, packet)
    }

    /**
     * Writes id bytes.
     */
    private fun writeId(byteBuf: ByteBuf, id: Int) {
        var i = id
        while (i and -128 != 0) {
            byteBuf.writeByte(i and 127 or 128)
            i = i ushr 7
        }
        byteBuf.writeByte(i)
    }

    /**
     * Absolute difference in values.
     */
    private fun absoluteDifference(value1: Double, value2: Double): Double {
        return abs(value1 - value2)
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
