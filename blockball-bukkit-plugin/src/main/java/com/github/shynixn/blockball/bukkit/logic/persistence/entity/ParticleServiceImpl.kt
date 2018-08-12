@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.ball.bukkit.core.nms.v1_13_R1.MaterialCompatibility13
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.bukkit.logic.business.extension.async
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import com.google.inject.Inject
import net.minecraft.server.v1_13_R1.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_13_R1.block.CraftBlockState
import org.bukkit.craftbukkit.v1_13_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_13_R1.util.CraftMagicNumbers
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.plugin.Plugin
import java.util.logging.Level

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class ParticleServiceImpl @Inject constructor(private val plugin: Plugin) : ParticleService {
    private val version = VersionSupport.getServerVersion()

    /**
     * Plays the given [particle] at the given [location] for the given [players].
     */
    override fun <L, P> playParticle(location: L, particle: Particle, players: Collection<P>) {
        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        if (particle.type == ParticleType.NONE) {
            return
        }

        val targets = (players as Collection<Player>).toTypedArray()

        if (particle.type == ParticleType.REDSTONE || particle.type == ParticleType.NOTE) {
            particle.amount = 0
            particle.speed = 1.0f.toDouble()
        }

        var internalParticleType = getInternalEnumValue(particle.type)

        val packet = if (version.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_13_R1)) {
            val dataType = internalParticleType

            if (dataType == ItemStack::class.java && particle.materialName != null) {
                val itemStack = CraftItemStack.asNMSCopy(ItemStack(Material.getMaterial(particle.materialName), 1, particle.data.toShort()))
                internalParticleType = ParticleParamItem(internalParticleType as net.minecraft.server.v1_13_R1.Particle<ParticleParamItem>, itemStack)
            } else if (dataType == MaterialData::class.java) {
                val data = MaterialData(Material.getMaterial(particle.materialName), particle.data.toByte())
                internalParticleType = ParticleParamBlock(internalParticleType as net.minecraft.server.v1_13_R1.Particle<ParticleParamBlock>, CraftMagicNumbers.getBlock(data))
            } else if (particle.type == ParticleType.BLOCK_CRACK || particle.type == ParticleType.BLOCK_DUST) {
                val data = CraftBlockState(Material.getMaterial(particle.materialName))
                data.rawData = particle.data.toByte()
                internalParticleType = ParticleParamBlock(internalParticleType as net.minecraft.server.v1_13_R1.Particle<ParticleParamBlock>, data.handle)
            } else if (particle.type == ParticleType.REDSTONE) {
                internalParticleType = ParticleParamRedstone(particle.colorRed.toFloat() / 255.0f, particle.colorGreen.toFloat() / 255.0f, particle.colorBlue.toFloat() / 255.0f, 1.0F)
            }

            PacketPlayOutWorldParticles(internalParticleType as ParticleParam, isLongDistance(location, targets), location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), particle.offSetX.toFloat(), particle.offSetY.toFloat(), particle.offSetZ.toFloat(), particle.speed.toFloat(), particle.amount)
        } else {
            var additionalPayload: IntArray? = null

            if (particle.materialName != null) {
                additionalPayload = if (particle.type == ParticleType.ITEM_CRACK) {
                    intArrayOf(MaterialCompatibility13.getIdFromMaterial(Material.getMaterial(particle.materialName)), particle.data)
                } else {
                    intArrayOf(MaterialCompatibility13.getIdFromMaterial(Material.getMaterial(particle.materialName)), (particle.data shl 12))
                }
            }

            if (particle.type == ParticleType.REDSTONE) {
                var red = particle.colorRed.toFloat() / 255.0F
                if (red <= 0) {
                    red = Float.MIN_VALUE
                }

                val constructor = Class.forName("net.minecraft.server.VERSION.PacketPlayOutWorldParticles".replace("VERSION", version.versionText))
                        .getDeclaredConstructor(internalParticleType.javaClass, Boolean::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Int::class.javaPrimitiveType, IntArray::class.java)
                constructor.newInstance(internalParticleType, isLongDistance(location, targets), location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), red, particle.colorGreen.toFloat() / 255.0f, particle.colorBlue.toFloat() / 255.0f, particle.speed.toFloat(), particle.amount, additionalPayload)
            } else {

                val constructor = Class.forName("net.minecraft.server.VERSION.PacketPlayOutWorldParticles".replace("VERSION", version.versionText))
                        .getDeclaredConstructor(internalParticleType.javaClass, Boolean::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Float::class.javaPrimitiveType, Int::class.javaPrimitiveType, IntArray::class.java)
                constructor.newInstance(internalParticleType, isLongDistance(location, targets), location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), particle.offSetX.toFloat(), particle.offSetY.toFloat(), particle.offSetZ.toFloat(), particle.speed.toFloat(), particle.amount, additionalPayload)
            }
        }

        async(plugin) {
            try {
                players.forEach { p ->
                    p.sendPacket(packet)
                }
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Failed to send particle.", e)
            }
        }
    }

    private fun isLongDistance(location: Location, players: Array<out Player>): Boolean {
        return players.any { location.world.name == it.location.world.name && it.location.distanceSquared(location) > 65536 }
    }

    private fun getInternalEnumValue(particle: ParticleType): Any {
        try {
            return if (version.isVersionLowerThan(VersionSupport.VERSION_1_13_R1)) {
                val clazz = Class.forName("net.minecraft.server.VERSION.EnumParticle".replace("VERSION", version.versionText))
                val method = clazz.getDeclaredMethod("valueOf", String::class.java)
                method.invoke(null, particle.name)
            } else {
                val minecraftKey = MinecraftKey(particle.gameId_113)
                net.minecraft.server.v1_13_R1.Particle.REGISTRY.get(minecraftKey) as Any
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to load enum value.", e)
            throw RuntimeException(e)
        }
    }
}