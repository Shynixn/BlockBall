package com.github.shynixn.blockball.bukkit.logic.persistence.repository

import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.persistence.context.FileContext
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.repository.ArenaRepository
import com.github.shynixn.blockball.bukkit.logic.business.extension.YamlSerializer
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.ParticleEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.SoundEntity
import com.google.inject.Inject
import org.bukkit.configuration.Configuration
import org.bukkit.plugin.Plugin
import java.io.File
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
class ArenaFileRepository @Inject constructor(private val plugin: Plugin, private val fileContext: FileContext) : ArenaRepository {
    /**
     * Returns all stored arenas in this repository.
     */
    override fun getAll(): List<Arena> {
        val arenas = ArrayList<Arena>()
        var i = 0
        while (i < this.getFolder().list()!!.size) {
            val s = this.getFolder().list()!![i]
            try {
                if (s.contains("arena_")) {
                    val data = fileContext.loadOrCreateYamlFile(File(getFolder(), s).toPath(), "arena", true)
                    val arenaEntity = YamlSerializer.deserializeObject(ArenaEntity::class.java, null, data)

                    // Compatibility added in v6.1.0
                    if (!arenaEntity.meta.ballMeta.soundEffects.containsKey(BallActionType.ONGOAL)) {
                        arenaEntity.meta.ballMeta.soundEffects[BallActionType.ONGOAL] = SoundEntity()
                    }

                    // Compatibility added in v6.1.0
                    if (!arenaEntity.meta.ballMeta.particleEffects.containsKey(BallActionType.ONGOAL)) {
                        arenaEntity.meta.ballMeta.particleEffects[BallActionType.ONGOAL] = ParticleEntity()
                    }

                    arenas.add(arenaEntity)
                }
            } catch (ex: Exception) {
                plugin.logger.log(Level.WARNING, "Cannot read arena file $s.", ex)
            }

            i++
        }

        plugin.logger.log(Level.INFO, "Reloaded [" + arenas.size + "] games.")

        return arenas
    }

    /**
     * Delets the given arena in the storage.
     */
    override fun delete(arena: Arena) {
        try {
            val file = File(this.getFolder(), "arena_" + arena.name + ".yml")

            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to delete file.", e)
        }
    }

    /**
     * Saves the given [arena] to the storage.
     */
    override fun save(arena: Arena) {
        val file = File(this.getFolder(), "arena_" + arena.name + ".yml")

        if (file.exists()) {
            if (!file.delete()) {
                throw IllegalStateException("Cannot delete file!")
            }
        }

        fileContext.saveAndCreateYamlFile<Configuration>(file.toPath()) { configuration ->
            val data = YamlSerializer.serialize(arena)
            for (key in data.keys) {
                configuration.set("arena.$key", data[key])
            }
        }
    }

    /**
     * Gets the arena folder and recreates it if it does not exist.
     */
    private fun getFolder(): File {
        val file = File(this.plugin.dataFolder, "arena")
        if (!file.exists()) {
            if (!file.mkdir()) {
                throw IllegalStateException("Cannot create folder!")
            }
        }
        return file
    }
}