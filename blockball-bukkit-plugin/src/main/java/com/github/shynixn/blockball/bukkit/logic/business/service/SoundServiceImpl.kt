@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Player

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
class SoundServiceImpl @Inject constructor(
    private val plugin: PluginProxy,
    private val loggingService: LoggingService,
    private val concurrencyService: ConcurrencyService
) : SoundService {
    /**
     * Gets all available sound names.
     */
    override val soundNames: List<String>
        get() {
            return org.bukkit.Sound.values().map { s -> s.name }
        }

    /**
     * Plays the given [sound] at the given [location] for the given [players].
     */
    override fun <L, P> playSound(location: L, sound: Sound, players: Collection<P>) {
        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        if (sound.name.equals("none", true)) {
            return
        }

        val targets = (players as Collection<Player>).toTypedArray()

        async(concurrencyService) {
            val name = convertName(sound.name.toUpperCase())

            try {
                targets.forEach { p ->
                    p.playSound(location, org.bukkit.Sound.valueOf(name), sound.volume.toFloat(), sound.pitch.toFloat())
                }
            } catch (e: Exception) {
                loggingService.warn("Failed to send sound. Is the sound '" + sound.name + "' supported by this server version?", e)
            }
        }
    }

    /**
     * Converts the given [name].
     */
    private fun convertName(name: String): String {
        val version = plugin.getServerVersion()

        if (version.isVersionSameOrGreaterThan(Version.VERSION_1_13_R1)) {
            when (name) {
                "MAGMACUBE_WALK" -> {
                    return "ENTITY_MAGMA_CUBE_JUMP"
                }
                "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD" ->
                    return "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR"
                "NOTE_PLING" ->
                    return "BLOCK_NOTE_BLOCK_PLING"
            }
        }
        if (version.isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            when (name) {
                "ENDERMAN_IDLE" -> {
                    return "ENTITY_ENDERMEN_AMBIENT"
                }
                "MAGMACUBE_WALK" -> {
                    return "ENTITY_MAGMACUBE_JUMP"
                }
                "SLIME_WALK" -> {
                    return "ENTITY_SLIME_JUMP"
                }
                "EXPLODE" -> {
                    return "ENTITY_GENERIC_EXPLODE"
                }

                "EAT" -> {
                    return "ENTITY_GENERIC_EAT"
                }
                "WOLF_GROWL" -> {
                    return "ENTITY_WOLF_GROWL"
                }
                "CAT_MEOW" -> {
                    return "ENTITY_CAT_PURREOW"
                }
                "HORSE_GALLOP" -> {
                    return "ENTITY_GENERIC_EXPLODE"
                }
                "ENTITY_HORSE_GALLOP" -> {
                    return "ENTITY_GENERIC_EXPLODE"
                }
                "BAT_LOOP" -> {
                    return "ENTITY_BAT_LOOP"
                }
                "GHAST_SCREAM" -> {
                    return "ENTITY_GHAST_SCREAM"
                }
                "BLAZE_BREATH" -> {
                    return "ENTITY_BLAZE_AMBIENT"
                }
                "ENDERDRAGON_WINGS" -> {
                    return "ENTITY_ENDERDRAGON_FLAP"
                }
                "ENDERDRAGON_GROWL" -> {
                    return "ENTITY_ENDERDRAGON_GROWL"
                }
                "NOTE_PLING" -> {
                    return "BLOCK_NOTE_PLING"
                }
                "none" -> {
                    return "none"
                }
                else -> {
                    if (name.contains("WALK")) {
                        return "ENTITY_" + name.toUpperCase().split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "_STEP"
                    } else if (name.contains("IDLE")) {
                        return "ENTITY_" + name.toUpperCase().split("_".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0] + "_AMBIENT"
                    }
                }
            }
        } else {
            when (name) {
                "ENTITY_GHAST_SHOOT" -> {
                    return "GHAST_FIREBALL"
                }
                "ENTITY_ZOMBIE_ATTACK_DOOR_WOOD" -> {
                    return "ZOMBIE_WOOD"
                }
                "none" -> {
                    return "none"
                }
            }
        }

        return name
    }
}