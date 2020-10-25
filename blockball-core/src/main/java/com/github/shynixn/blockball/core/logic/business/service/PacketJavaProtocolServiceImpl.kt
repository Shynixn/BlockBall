package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.*
import kotlin.math.abs

class PacketJavaProtocolServiceImpl(private val packageService: PackageService, private val pluginProxy: PluginProxy) :
    PacketService {
    private val dataSerializerClazz = pluginProxy.findClazz("net.minecraft.server.VERSION.PacketDataSerializer")
    private val dataSerializerConstructor = dataSerializerClazz.getDeclaredConstructor(ByteBuf::class.java)
    private val dataDeSerializationPacketMethod = pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
        .getDeclaredMethod("a", dataSerializerClazz)
    private val packetPlayOutEntityDestroyClazz =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy")
    private val packetPlayOutEntityRelMove =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntity\$PacketPlayOutRelEntityMove")
    private val packetPlayOutEntityVelocity =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityVelocity")
    private val packetPlayOutEntityTeleport =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityTeleport")
    private val packetPlayOutEntitySpawnLiving =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
    private val packetPlayOutEntityMetaData =
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata")

    private val iRegistryClazz = pluginProxy.findClazz("net.minecraft.server.VERSION.IRegistry")
    private val entityRegistry = iRegistryClazz.getDeclaredField("ENTITY_TYPE").get(null)
    private val iRegistryRegisterMethod = iRegistryClazz.getDeclaredMethod("a", Any::class.java)
    private val entityTypesClazz = pluginProxy.findClazz("net.minecraft.server.VERSION.EntityTypes")

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

        buffer.writeByte(4)
        writeId(buffer, booleanTypeValue)
        buffer.writeBoolean(false)
        buffer.writeByte(255)
        sendPacket(player, packetPlayOutEntityMetaData, buffer)
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
