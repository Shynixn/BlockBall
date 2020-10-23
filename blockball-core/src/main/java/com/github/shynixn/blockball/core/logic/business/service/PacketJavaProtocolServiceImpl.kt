package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.api.business.service.PacketService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

class PacketJavaProtocolServiceImpl(private val packageService: PackageService, private val pluginProxy: PluginProxy) : PacketService {
    private val dataSerializerClazz = pluginProxy.findClazz("net.minecraft.server.VERSION.PacketDataSerializer")
    private val dataSerializerConstructor = dataSerializerClazz.getDeclaredConstructor(ByteBuf::class.java)
    private val dataSerializationPacketMethod = pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
        .getDeclaredMethod("b", dataSerializerClazz)
    private val dataDeSerializationPacketMethod = pluginProxy.findClazz("net.minecraft.server.VERSION.Packet")
        .getDeclaredMethod("a", dataSerializerClazz)


    private fun <P> sendPacket(player: List<P>, packetClazz: Class<*>, byteBuf: ByteBuf) {
        val packet = packetClazz.newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(byteBuf)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
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
     * Sends an entity move packet.l
     */
    override fun <P> sendEntityMovePacket(
        player: P,
        entityId: Int,
        previousPosition: Position,
        nextPosition: Position,
        isOnGround: Boolean
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a velocity packet.
     */
    override fun <P> sendEntityVelocityPacket(player: P, entityId: Int, velocity: Position) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a destroy packet.
     */
    override fun <P> sendEntityDestroyPacket(player: P, entityId: Int) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a teleport packet.
     */
    override fun <P> sendEntityTeleportPacket(player: P, entityId: Int, position: Position) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a spawn packet.
     */
    override fun <P> sendEntitySpawnPacket(player: P, entityId: Int, entityType: String, position: Position) {
        TODO("Not yet implemented")
    }

    /**
     * Sends a meta data packet.
     */
    override fun <P> sendEntityMetaDataPacket(player: P, entityId: Int, entityMetaData: EntityMetaData) {
        TODO("Not yet implemented")
    }
}
