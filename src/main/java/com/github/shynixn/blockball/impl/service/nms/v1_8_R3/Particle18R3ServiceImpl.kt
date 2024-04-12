@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service.nms.v1_8_R3

import com.github.shynixn.blockball.contract.ParticleService
import com.github.shynixn.blockball.entity.Particle
import com.github.shynixn.blockball.enumeration.ParticleType
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.packet.api.PacketService
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.lang.reflect.Method
import java.util.logging.Level

class Particle18R3ServiceImpl @Inject constructor(
    private val version: Version,
    private val plugin: Plugin,
    private val packetService: PacketService
) : ParticleService {
    private val getIdFromMaterialMethod: Method = { Material::class.java.getDeclaredMethod("getId") }.invoke()

    /**
     * Plays the given [particle] at the given [location] for the given [player] or
     * all players in the world if the config option all alwaysVisible is enabled.
     */
    override fun playParticle(location: Location, particle: Particle, players: Collection<Player>) {
        val partType = findParticleType(particle.typeName)

        if (partType == ParticleType.NONE) {
            return
        }

        val targets = players.toTypedArray()

        if (partType == ParticleType.REDSTONE || partType == ParticleType.NOTE) {
            particle.amount = 0
            particle.speed = 1.0f.toDouble()
        }

        val internalParticleType = getInternalEnumValue(partType)

        var additionalPayload: IntArray? = null

        if (particle.materialName != null) {
            additionalPayload = if (partType == ParticleType.ITEM_CRACK) {
                intArrayOf(
                    getIdFromMaterialMethod.invoke(Material.getMaterial(particle.materialName!!)) as Int,
                    particle.data
                )
            } else {
                intArrayOf(
                    getIdFromMaterialMethod.invoke(Material.getMaterial(particle.materialName!!)) as Int,
                    (particle.data shl 12)
                )
            }
        }

        val packet = if (partType == ParticleType.REDSTONE) {
            var red = particle.colorRed.toFloat() / 255.0F
            if (red <= 0) {
                red = Float.MIN_VALUE
            }

            val constructor = Class.forName(
                "net.minecraft.server.VERSION.PacketPlayOutWorldParticles".replace(
                    "VERSION",
                    version.bukkitId
                )
            )
                .getDeclaredConstructor(
                    internalParticleType.javaClass,
                    Boolean::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    IntArray::class.java
                )
            constructor.newInstance(
                internalParticleType,
                isLongDistance(location, targets),
                location.x.toFloat(),
                location.y.toFloat(),
                location.z.toFloat(),
                red,
                particle.colorGreen.toFloat() / 255.0f,
                particle.colorBlue.toFloat() / 255.0f,
                particle.speed.toFloat(),
                particle.amount,
                additionalPayload
            )
        } else {
            val constructor = Class.forName(
                "net.minecraft.server.VERSION.PacketPlayOutWorldParticles".replace(
                    "VERSION",
                    version.bukkitId
                )
            )
                .getDeclaredConstructor(
                    internalParticleType.javaClass,
                    Boolean::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Float::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    IntArray::class.java
                )
            constructor.newInstance(
                internalParticleType,
                isLongDistance(location, targets),
                location.x.toFloat(),
                location.y.toFloat(),
                location.z.toFloat(),
                particle.offset.x.toFloat(),
                particle.offset.y.toFloat(),
                particle.offset.z.toFloat(),
                particle.speed.toFloat(),
                particle.amount,
                additionalPayload
            )
        }

        try {
            players.forEach { p ->
                packetService.sendNativePacket(p, packet)
            }
        } catch (e: Exception) {
            Bukkit.getServer().logger.log(Level.WARNING, "Failed to send particle.", e)
        }
    }

    private fun isLongDistance(location: Location, players: Array<out Player>): Boolean {
        return players.any { location.world!!.name == it.location.world!!.name && it.location.distanceSquared(location) > 65536 }
    }

    /**
     * Finds the particle type.
     */
    private fun findParticleType(item: String): ParticleType {
        ParticleType.values().forEach { p ->
            if (p.name == item || p.gameId_18 == item || p.gameId_113 == item || p.minecraftId_112 == item) {
                return p
            }
        }

        return ParticleType.NONE
    }

    private fun getInternalEnumValue(particle: ParticleType): Any {
        try {
            val clazz =
                Class.forName("net.minecraft.server.VERSION.EnumParticle".replace("VERSION", version.bukkitId))
            val method = clazz.getDeclaredMethod("valueOf", String::class.java)
            return method.invoke(null, particle.name)
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING,"Failed to load enum value.")
            throw RuntimeException(e)
        }
    }
}
