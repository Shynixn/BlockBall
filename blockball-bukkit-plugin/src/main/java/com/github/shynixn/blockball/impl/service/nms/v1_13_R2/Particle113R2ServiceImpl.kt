@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service.nms.v1_13_R2

import com.github.shynixn.blockball.contract.ItemTypeService
import com.github.shynixn.blockball.contract.ParticleService
import com.github.shynixn.blockball.entity.Particle
import com.github.shynixn.blockball.enumeration.ParticleType
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.data.BlockData
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import java.util.logging.Level

class Particle113R2ServiceImpl @Inject constructor(
    private val itemTypeService: ItemTypeService
) :
    ParticleService {
    /**
     * Plays the given [particle] at the given [location] for the given [player] or
     * all players in the world if the config option all alwaysVisible is enabled.
     */
    override fun  playParticle(location: Location, particle: Particle, players: Collection<Player>) {
        try {
            if (particle.typeName.equals("NONE", true)) {
                return
            }

            val partType = findParticleType(particle.typeName)
            val bukkitType = org.bukkit.Particle.values().asSequence()
                .first { p -> p.name.equals(particle.typeName, true) || partType.name == p.name }
            val dataType = bukkitType.dataType

            for (player in players) {
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
                        val itemStack = ItemStack(itemTypeService.findItemType(particle.materialName!!), 1, particle.data.toShort())
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
}
