@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Player
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
class SoundServiceImpl @Inject constructor(private val plugin: Plugin) : SoundService {
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
        val name = convertName(sound.name)

        try {
            targets.forEach { p ->
                p.playSound(location, org.bukkit.Sound.valueOf(name), sound.volume.toFloat(), sound.pitch.toFloat())
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to send sound. Is the sound '" + sound.name + "' supported by this server version?", e)
        }
    }

    private fun convertName(name: String): String {
        val version = VersionSupport.getServerVersion()

        if (version.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_13_R1)) {
            when (name) {
                "MAGMACUBE_WALK" -> {
                    return "ENTITY_MAGMA_CUBE_JUMP"
                }
                "ENDERMAN_IDLE" -> {
                    return "ENTITY_ENDERMAN_AMBIENT"
                }
                "ENDERDRAGON_GROWL" -> {
                    return "ENTITY_ENDER_DRAGON_GROWL"
                }
                "ENDERDRAGON_WINGS" -> {
                    return "ENTITY_ENDER_DRAGON_FLAP"
                }
                "IRONGOLEM_WALK" -> {
                    return "ENTITY_IRON_GOLEM_STEP"
                }
            }
        }
        if (version.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_9_R1)) {
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
                "none" -> {
                    return "none"
                }
                else -> {
                    if (name.contains("WALK")) {
                        return "ENTITY_" + name.toUpperCase().split("_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0] + "_STEP"
                    } else if (name.contains("IDLE")) {
                        return "ENTITY_" + name.toUpperCase().split("_".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0] + "_AMBIENT"
                    }
                }
            }
        }

        return name
    }
}