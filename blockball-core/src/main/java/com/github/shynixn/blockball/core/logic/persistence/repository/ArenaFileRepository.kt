@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.repository

import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.api.business.service.YamlService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.ParticleEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.SoundEntity
import com.google.inject.Inject
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.HashMap

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
class ArenaFileRepository @Inject constructor(
    private val configurationService: ConfigurationService,
    private val yamlSerializationService: YamlSerializationService,
    private val yamlService: YamlService,
    private val loggingService: LoggingService
) : ArenaRepository {
    /**
     * Returns all stored arenas in this repository.
     */
    override fun getAll(): List<Arena> {
        val arenas = ArrayList<Arena>()
        var i = 0

        while (i < this.getFolder().toFile().list()!!.size) {
            val s = this.getFolder().toFile().list()!![i]
            try {
                if (s.contains("arena_")) {
                    val file = getFolder().resolve(s)
                    val data = yamlService.read(file)

                    val arenaEntity = yamlSerializationService.deserialize(
                        ArenaEntity::class.java,
                        data["arena"] as Map<String, Any?>
                    )

                    // Compatibility added in v6.1.0
                    if (!arenaEntity.meta.ballMeta.soundEffects.containsKey(BallActionType.ONGOAL)) {
                        arenaEntity.meta.ballMeta.soundEffects[BallActionType.ONGOAL] = SoundEntity()
                    }

                    // Compatibility added in v6.1.0
                    if (!arenaEntity.meta.ballMeta.particleEffects.containsKey(BallActionType.ONGOAL)) {
                        arenaEntity.meta.ballMeta.particleEffects[BallActionType.ONGOAL] = ParticleEntity()
                    }

                    // Compatibility added in v6.22.1
                    if (!arenaEntity.meta.ballMeta.particleEffects.containsKey(BallActionType.ONPASS)) {
                        arenaEntity.meta.ballMeta.particleEffects[BallActionType.ONPASS] = ParticleEntity()
                    }

                    // Compatibility added in v6.22.1
                    if (!arenaEntity.meta.ballMeta.soundEffects.containsKey(BallActionType.ONPASS)) {
                        arenaEntity.meta.ballMeta.soundEffects[BallActionType.ONPASS] = SoundEntity()
                    }

                    if (arenaEntity.name.toIntOrNull() == null) {
                        throw RuntimeException("Arena name has to be a number!")
                    }

                    arenas.add(arenaEntity)
                }
            } catch (ex: Exception) {
                loggingService.error("Cannot read arena file $s.", ex)
            }

            i++
        }

        arenas.sortWith(Comparator { o1, o2 -> o1.name.toInt().compareTo(o2.name.toInt()) })

        loggingService.info("Reloaded [" + arenas.size + "] games.")

        return arenas
    }

    /**
     * Delets the given arena in the storage.
     */
    override fun delete(arena: Arena) {
        val file = this.getFolder().resolve("arena_" + arena.name + ".yml")

        if (Files.exists(file)) {
            Files.delete(file)
        }
    }

    /**
     * Saves the given [arena] to the storage.
     */
    override fun save(arena: Arena) {
        val file = this.getFolder().resolve("arena_" + arena.name + ".yml")

        if (Files.exists(file)) {
            Files.delete(file)
        }

        // Compatibility added in v6.22.1
        if (arena.meta.ballMeta.particleEffects.containsKey(BallActionType.ONGRAB)) {
            arena.meta.ballMeta.particleEffects.remove(BallActionType.ONGRAB)
        }
        if (arena.meta.ballMeta.particleEffects.containsKey(BallActionType.ONTHROW)) {
            arena.meta.ballMeta.particleEffects.remove(BallActionType.ONTHROW)
        }
        if (arena.meta.ballMeta.soundEffects.containsKey(BallActionType.ONGRAB)) {
            arena.meta.ballMeta.soundEffects.remove(BallActionType.ONGRAB)
        }
        if (arena.meta.ballMeta.soundEffects.containsKey(BallActionType.ONTHROW)) {
            arena.meta.ballMeta.soundEffects.remove(BallActionType.ONTHROW)
        }

        val data = yamlSerializationService.serialize(arena)
        val finalData = HashMap<String, Any>()

        finalData["arena"] = data
        yamlService.write(file, finalData)
    }

    /**
     * Gets the arena folder and recreates it if it does not exist.
     */
    private fun getFolder(): Path {
        val folder = configurationService.applicationDir.resolve("arena")

        if (!Files.exists(folder)) {
            Files.createDirectories(folder)
        }

        return folder
    }
}
