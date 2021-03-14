package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_8_R3

import com.github.shynixn.blockball.api.business.enumeration.CompatibilityArmorSlotType
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.InternalVersionPacketService
import com.github.shynixn.blockball.api.persistence.entity.EntityMetaData
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.google.inject.Inject
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlin.math.floor

class InternalVersionPacket18R3ServiceImpl @Inject constructor(private val pluginProxy: PluginProxy) :
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
    private val dataWatcherClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.DataWatcher")
    }
    private val dataWatcherConstructor by lazy {
        dataWatcherClazz.getDeclaredConstructor(pluginProxy.findClazz("net.minecraft.server.VERSION.Entity"))
    }
    private val packetPlayOutEntitySpawnLiving by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
    }
    private val vector3fClazz by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.Vector3f")
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

    companion object {

    }

    private val packetPlayOutEntityEquipment by lazy {
        pluginProxy.findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityEquipment")
    }

    /**
     * Creates a new teleport packet.
     */
    override fun createEntityTeleportPacket(entityId: Int, position: Position): Any {
        val buffer = Unpooled.buffer()
        buffer.writeId(entityId)
        buffer.writeInt(floor(position.x * 32.0).toInt())
        buffer.writeInt(floor(position.y * 32.0).toInt())
        buffer.writeInt(floor(position.z * 32.0).toInt())
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

        buffer.writeByte(entityCompatibilityCache[entityType]!! and 255)
        buffer.writeInt(floor(position.x * 32.0).toInt())
        buffer.writeInt(floor(position.y * 32.0).toInt())
        buffer.writeInt(floor(position.z * 32.0).toInt())
        buffer.writeByte((position.yaw * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte((position.pitch * 256.0f / 360.0f).toInt().toByte().toInt())
        buffer.writeByte(0)
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())
        buffer.writeShort((mathhelperA(0.0, -3.9, 3.9) * 8000.0).toInt())

        buffer.writeByte(127)
        val packet = packetPlayOutEntitySpawnLiving.getDeclaredConstructor().newInstance()
        val dataSerializer = dataSerializerConstructor.newInstance(buffer)
        dataDeSerializationPacketMethod.invoke(packet, dataSerializer)
        packetPlayOutEntitySpawnLiving.getDeclaredField("l")
            .accessible(true).set(packet, dataWatcherConstructor.newInstance(null))
        return packet
    }

    /**
     * Creates a entity metadata packet.
     */
    override fun createEntityMetaDataPacket(entityId: Int, entityMetaData: EntityMetaData): Any {
        // https://wiki.vg/index.php?title=Entity_metadata&oldid=6611 -> Value of Type field. Type of Value = Boolean -> 7.
        val dataWatcher = WrappedDataWatcher18R1(pluginProxy)

        if (entityMetaData.customNameVisible != null) {
            dataWatcher.set(3, boolToInt(entityMetaData.customNameVisible!!).toByte())
        }

        if (entityMetaData.customname != null) {
            dataWatcher.set(2, entityMetaData.customname)
        }

        if (entityMetaData.isInvisible != null) {
            if (entityMetaData.isInvisible!!) {
                dataWatcher.set(0, (0 or (1 shl 5)).toByte())
            } else {
                dataWatcher.set(0, (0 and (1 shl 5)).toByte())
            }
        }

        if (entityMetaData.slimeSize != null) {
            dataWatcher.set(16, entityMetaData.slimeSize!!.toByte())
        }

        if (entityMetaData.armorstandHeadRotation != null) {
            val vector3f = vector3fClazz.getDeclaredConstructor(Float::class.java, Float::class.java, Float::class.java)
                .newInstance(
                    entityMetaData.armorstandHeadRotation!!.x.toFloat(),
                    entityMetaData.armorstandHeadRotation!!.y.toFloat(),
                    entityMetaData.armorstandHeadRotation!!.z.toFloat()
                )
            dataWatcher.set(11, vector3f)
        }

        if (entityMetaData.isSmall != null && entityMetaData.isSmall!!) {
            dataWatcher.set(10, 1.toByte())
        }

        val packet = packetPlayOutEntityMetaData.getDeclaredConstructor(
            Int::class.java,
            pluginProxy.findClazz("net.minecraft.server.VERSION.DataWatcher"),
            Boolean::class.java
        ).newInstance(entityId, dataWatcher.getHandle(), true)
        return packet
    }

    /**
     * Creates an entity equipment packet.
     */
    override fun <I> createEntityEquipmentPacket(entityId: Int, slot: CompatibilityArmorSlotType, itemStack: I): Any {
        val nmsItemStack = craftItemStackNmsMethod.invoke(null, itemStack)
        return packetPlayOutEntityEquipment
            .getDeclaredConstructor(Int::class.java, Int::class.java, nmsItemStackClazz)
            .newInstance(entityId, slot.id18, nmsItemStack)
    }

    /**
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

    /**
     * Bool to int helper.
     */
    private fun boolToInt(boolean: Boolean): Int {
        if (boolean) {
            return 1
        }
        return 0
    }

    class WrappedDataWatcher18R1(private val pluginProxy: PluginProxy) {
        private val dataWatcherClazz by lazy {
            pluginProxy.findClazz("net.minecraft.server.VERSION.DataWatcher")
        }
        private val dataWatcherConstructor by lazy {
            dataWatcherClazz.getDeclaredConstructor(pluginProxy.findClazz("net.minecraft.server.VERSION.Entity"))
        }
        private val dataWatcher = dataWatcherConstructor.newInstance(null)
        private val dataWatcherMethod by lazy {
            dataWatcherClazz.getDeclaredMethod("a", Int::class.java, Any::class.java)
        }

        /**
         * Sets the value at the given index.
         */
        fun <T> set(index: Int, value: T) {
            dataWatcherMethod.invoke(dataWatcher, index, value)
        }

        /**
         * Gets the nms handle.
         */
        fun getHandle(): Any {
            return dataWatcher
        }
    }
}
