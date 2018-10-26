@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import com.github.shynixn.blockball.bukkit.logic.business.nms.VersionSupport
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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
class ParticleServiceImpl @Inject constructor(private val itemService: ItemService) : ParticleService {
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
            val particleParamClazz = findClazz("net.minecraft.server.VERSION.ParticleParam")
            val particleClazz = findClazz("net.minecraft.server.VERSION.Particle")

            if (dataType == ItemStack::class.java && particle.materialName != null) {
                val itemStack = findClazz("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack").getDeclaredMethod("asNMSCopy")
                        .invoke(null, ItemStack(Material.getMaterial(particle.materialName), 1, particle.data.toShort()))

                internalParticleType = findClazz("net.minecraft.server.VERSION.ParticleParamItem")
                        .getDeclaredConstructor(particleClazz, itemStack.javaClass).newInstance(internalParticleType, itemStack)
            } else if (particle.type == ParticleType.BLOCK_CRACK || particle.type == ParticleType.BLOCK_DUST) {
                val craftBlockStateClazz = findClazz("org.bukkit.craftbukkit.VERSION.block.CraftBlockState")

                val data = craftBlockStateClazz
                        .getDeclaredConstructor(Material::class.java)
                        .newInstance(Material.getMaterial(particle.materialName))

                craftBlockStateClazz.getDeclaredMethod("setRawData", Byte::class.java).invoke(data, particle.data.toByte())
                val handle = craftBlockStateClazz.getDeclaredMethod("getHandle").invoke(data)

                internalParticleType = findClazz("net.minecraft.server.VERSION.ParticleParamBlock")
                        .getDeclaredConstructor(particleClazz, findClazz("net.minecraft.server.VERSION.IBlockData"))
                        .newInstance(internalParticleType, handle)
            } else if (particle.type == ParticleType.REDSTONE) {
                internalParticleType = findClazz("net.minecraft.server.VERSION.ParticleParamRedstone")
                        .getDeclaredConstructor(Float::class.java, Float::class.java, Float::class.java, Float::class.java)
                        .newInstance(particle.colorRed.toFloat() / 255.0f, particle.colorGreen.toFloat() / 255.0f, particle.colorBlue.toFloat() / 255.0f, 1.0F)
            }

            findClazz("net.minecraft.server.VERSION.PacketPlayOutWorldParticles")
                    .getDeclaredConstructor(particleParamClazz, Boolean::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java, Float::class.java, Int::class.java)
                    .newInstance(internalParticleType, isLongDistance(location, targets), location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), particle.offset.x.toFloat(), particle.offset.y.toFloat(), particle.offset.z.toFloat(), particle.speed.toFloat(), particle.amount)
        } else {
            var additionalPayload: IntArray? = null

            if (particle.materialName != null) {
                additionalPayload = if (particle.type == ParticleType.ITEM_CRACK) {
                    intArrayOf(itemService.getNumericMaterialValue(Material.getMaterial(particle.materialName)), particle.data)
                } else {
                    intArrayOf(itemService.getNumericMaterialValue(Material.getMaterial(particle.materialName)), (particle.data shl 12))
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
                constructor.newInstance(internalParticleType, isLongDistance(location, targets), location.x.toFloat(), location.y.toFloat(), location.z.toFloat(), particle.offset.x.toFloat(), particle.offset.y.toFloat(), particle.offset.z.toFloat(), particle.speed.toFloat(), particle.amount, additionalPayload)
            }
        }

        try {
            players.forEach { p ->
                p.sendPacket(packet)
            }
        } catch (e: Exception) {
            Bukkit.getServer().logger.log(Level.WARNING, "Failed to send particle.", e)
        }
    }

    /**
     * Finds the version dependent class.
     */
    private fun findClazz(name: String): Class<*> {
        return Class.forName(name.replace("VERSION", version.versionText))
    }

    private fun isLongDistance(location: Location, players: Array<out Player>): Boolean {
        return players.any { location.world.name == it.location.world.name && it.location.distanceSquared(location) > 65536 }
    }

    private fun getInternalEnumValue(particle: ParticleType): Any {
        try {
            return when {
                version.isVersionLowerThan(VersionSupport.VERSION_1_13_R1) -> {
                    val clazz = Class.forName("net.minecraft.server.VERSION.EnumParticle".replace("VERSION", version.versionText))
                    val method = clazz.getDeclaredMethod("valueOf", String::class.java)
                    method.invoke(null, particle.name)
                }
                version == VersionSupport.VERSION_1_13_R1 -> {
                    val minecraftKey = findClazz("net.minecraft.server.VERSION.MinecraftKey").getDeclaredConstructor(String::class.java).newInstance(particle.gameId_113)
                    val registry = findClazz("net.minecraft.server.VERSION.Particle").getDeclaredField("REGISTRY").get(null)

                    findClazz("net.minecraft.server.VERSION.RegistryMaterials").getDeclaredMethod("get", Any::class.java).invoke(registry, minecraftKey)
                }
                else -> {
                    val minecraftKey = findClazz("net.minecraft.server.VERSION.MinecraftKey").getDeclaredConstructor(String::class.java).newInstance(particle.gameId_113)
                    val registry = findClazz("net.minecraft.server.VERSION.IRegistry").getDeclaredField("PARTICLE_TYPE").get(null)
                    findClazz("net.minecraft.server.VERSION.RegistryMaterials").getDeclaredMethod("get", findClazz("net.minecraft.server.VERSION.MinecraftKey")).invoke(registry, minecraftKey)
                }
            }
        } catch (e: Exception) {
            Bukkit.getServer().logger.log(Level.WARNING, "Failed to load enum value.", e)
            throw RuntimeException(e)
        }
    }
}