package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.InternalVersionPacketService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.google.inject.Inject
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class PacketJavaProtocolServiceImpl @Inject constructor(
    private val internalVersionPacketService: InternalVersionPacketService,
    private val proxyService: ProxyService,
    private val pluginProxy: PluginProxy
) :
    PacketService {
    private val dataSerializerClazz by lazy {
        try {
            pluginProxy.findClazz("net.minecraft.network.PacketDataSerializer")
        } catch (e: Exception) {
            pluginProxy.findClazz("net.minecraft.server.VERSION.PacketDataSerializer")
        }
    }
    private val dataSerializerConstructor by lazy { dataSerializerClazz.getDeclaredConstructor(ByteBuf::class.java) }
    private val dataDeSerializationPacketMethod by lazy {
        try {
            pluginProxy.findClazz("net.minecraft.network.protocol.Packet")
                .getDeclaredMethod("a", dataSerializerClazz)
        } catch (e: Exception) {
            pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
                .getDeclaredMethod("a", dataSerializerClazz)
        }
    }
    private val packetPlayOutEntityDestroyClazz by lazy {
        try {
            pluginProxy.findClazz("net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy")
        } catch (e: Exception) {
            pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy")
        }
    }
    private val packetPlayOutEntityVelocity by lazy {
        try {
            pluginProxy.findClazz("net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity")
        } catch (e: Exception) {
            pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityVelocity")
        }
    }
    private val intArrayListConstructor by lazy {
        try {
            // Paper Spigot shades a different IntList
            pluginProxy.findClazz("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntArrayList")
                .getDeclaredConstructor(IntArray::class.java)
        } catch (e: Exception) {
            pluginProxy.findClazz("it.unimi.dsi.fastutil.ints.IntArrayList")
                .getDeclaredConstructor(IntArray::class.java)
        }
    }
    private val packetPlayOutEntityDestroyIntListConstructor by lazy {
        try {
            // Paper Spigot shades a different IntList
            packetPlayOutEntityDestroyClazz.getDeclaredConstructor(pluginProxy.findClazz("org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntList"))
        } catch (e: Exception) {
            packetPlayOutEntityDestroyClazz.getDeclaredConstructor(pluginProxy.findClazz("it.unimi.dsi.fastutil.ints.IntList"))
        }
    }

    /**
     * Sends a velocity packet.
     */
    override fun <P> sendEntityVelocityPacket(player: P, entityId: Int, velocity: Position) {
        if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_17_R1)) {
            val packet = packetPlayOutEntityVelocity.getDeclaredConstructor(
                Int::class.java,
                findClazz("net.minecraft.world.phys.Vec3D")
            )
                .newInstance(
                    entityId, findClazz("net.minecraft.world.phys.Vec3D")
                        .getDeclaredConstructor(Double::class.java, Double::class.java, Double::class.java)
                        .newInstance(velocity.x, velocity.y, velocity.z)
                )
            proxyService.sendPacket(player, packet)
        } else {
            val buffer = Unpooled.buffer()
            writeId(buffer, entityId)
            buffer.writeShort((mathhelperA(velocity.x, -3.9, 3.9) * 8000.0).toInt())
            buffer.writeShort((mathhelperA(velocity.y, -3.9, 3.9) * 8000.0).toInt())
            buffer.writeShort((mathhelperA(velocity.z, -3.9, 3.9) * 8000.0).toInt())
            sendPacket(player, packetPlayOutEntityVelocity, buffer)
        }
    }

    /**
     * Sends a destroy packet.
     */
    override fun <P> sendEntityDestroyPacket(player: P, entityId: Int) {
        val buffer = Unpooled.buffer()

        if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_17_R1)) {
            val packet = try {
                val intList = intArrayListConstructor.newInstance(intArrayOf(entityId))
                packetPlayOutEntityDestroyIntListConstructor.newInstance(intList)
            } catch (e: Exception) {
                throw RuntimeException("BlockBall does not support 1.17.0. Upgrade to 1.17.1.!", e)
            }

            proxyService.sendPacket(player, packet)
        } else {
            writeId(buffer, 1)
            writeId(buffer, entityId)
            sendPacket(player, packetPlayOutEntityDestroyClazz, buffer)
        }
    }

    /**
     * Sends a teleport packet.
     */
    override fun <P> sendEntityTeleportPacket(player: P, entityId: Int, position: Position) {
        val packet = internalVersionPacketService.createEntityTeleportPacket(entityId, position)
        proxyService.sendPacket(player, packet)
    }

    /**
     * Sends a spawn packet.
     */
    override fun <P> sendEntitySpawnPacket(player: P, entityId: Int, entityType: String, position: Position) {
        val packet = internalVersionPacketService.createEntitySpawnPacket(entityId, entityType, position)
        proxyService.sendPacket(player, packet)
    }

    /**
     * Sends a meta data packet.
     */
    override fun <P> sendEntityMetaDataPacket(player: P, entityId: Int, entityMetaData: EntityMetaData) {
        val packet = internalVersionPacketService.createEntityMetaDataPacket(entityId, entityMetaData)
        proxyService.sendPacket(player, packet)
    }

    /**
     * Sends an equipment packet.
     */
    override fun <P, I> sendEntityEquipmentPacket(
        player: P,
        entityId: Int,
        slot: CompatibilityArmorSlotType,
        itemStack: I
    ) {
        val packet = internalVersionPacketService.createEntityEquipmentPacket(entityId, slot, itemStack)
        proxyService.sendPacket(player, packet)
    }

    /**
     * Sends a packet.
     */
    private fun <P> sendPacket(player: P, packetClazz: Class<*>, byteBuf: ByteBuf) {
        val packet = packetClazz.newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(byteBuf)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
        proxyService.sendPacket(player, packet)
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
