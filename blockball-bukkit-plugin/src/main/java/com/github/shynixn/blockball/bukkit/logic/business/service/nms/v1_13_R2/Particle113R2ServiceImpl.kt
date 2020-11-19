@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_13_R2

import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.ItemTypeService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.persistence.entity.ItemEntity
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import java.util.logging.Level

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class Particle113R2ServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val itemTypeService: ItemTypeService
) :
    ParticleService {
    /**
     * Plays the given [particle] at the given [location] for the given [player] or
     * all players in the world if the config option all alwaysVisible is enabled.
     */
    override fun <L, P> playParticle(location: L, particle: Particle, players: Collection<P>) {
        when (location) {
            is Position -> {
                playParticleEffect(
                    Location(
                        Bukkit.getWorld(location.worldName!!),
                        location.x,
                        location.y,
                        location.z,
                        location.yaw.toFloat(),
                        location.pitch.toFloat()
                    ), particle, players as Collection<Player>
                )
            }
            is Location -> {
                playParticleEffect(location, particle, players as Collection<Player>)
            }
            else -> {
                throw IllegalArgumentException("Location has to be a BukkitLocation!")
            }
        }
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

    /**
     * Plays the actual particle.
     */
    private fun playParticleEffect(location: Location, particle: Particle, playerList: Collection<Player>) {
        try {
            if (particle.typeName.equals("NONE", true)) {
                return
            }

            val partType = findParticleType(particle.typeName)
            val bukkitType = org.bukkit.Particle.values().asSequence()
                .first { p -> p.name.equals(particle.typeName, true) || partType.name == p.name }
            val dataType = bukkitType.dataType

            for (player in playerList) {
                when (dataType) {
                    Void::class.java -> player.spawnParticle(
                        bukkitType,
                        location,
                        particle.amount,
                        particle.offset.x,
                        particle.offset.y,
                        particle.offset.z,
                        particle.speed
                    )
                    org.bukkit.Particle.DustOptions::class.java -> {
                        val dustOptions =
                            org.bukkit.Particle.DustOptions(
                                org.bukkit.Color.fromRGB(
                                    particle.colorRed,
                                    particle.colorGreen,
                                    particle.colorBlue
                                ), 1.0F
                            )
                        player.spawnParticle(bukkitType, location, 0, dustOptions)
                    }
                    MaterialData::class.java -> {
                        val itemType = itemTypeService.findItemType<Material>(particle.materialName!!)
                        val materialData = MaterialData(itemType, particle.data.toByte())
                        player.spawnParticle(
                            bukkitType,
                            location,
                            particle.amount,
                            particle.offset.x,
                            particle.offset.y,
                            particle.offset.z,
                            particle.speed,
                            materialData
                        )
                    }
                    BlockData::class.java -> {
                        val itemType = itemTypeService.findItemType<Material>(particle.materialName!!)
                        val blockData = Bukkit.createBlockData(itemType)

                        player.spawnParticle(
                            bukkitType,
                            location,
                            particle.amount,
                            particle.offset.x,
                            particle.offset.y,
                            particle.offset.z,
                            particle.speed,
                            blockData
                        )
                    }
                    ItemStack::class.java -> {
                        val itemStack =
                            itemTypeService.toItemStack<ItemStack>(ItemEntity(particle.materialName!!, particle.data))

                        player.spawnParticle(
                            bukkitType,
                            location,
                            particle.amount,
                            particle.offset.x,
                            particle.offset.y,
                            particle.offset.z,
                            particle.speed,
                            itemStack
                        )
                    }
                    else -> {
                        throw IllegalArgumentException("Unknown particle!")
                    }
                }
            }
        } catch (e: Exception) {
            Bukkit.getServer().logger.log(Level.WARNING, "Failed to send particle.", e)
        }
    }
}
