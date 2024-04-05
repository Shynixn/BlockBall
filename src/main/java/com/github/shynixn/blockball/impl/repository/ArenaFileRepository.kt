@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.github.shynixn.blockball.contract.ArenaRepository
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.mcutils.common.ConfigurationService
import com.google.inject.Inject
import org.bukkit.plugin.Plugin
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level

/**
 * Handles storing and retrieving arena from a persistence medium.
 */
class ArenaFileRepository @Inject constructor(
    private val configurationService: ConfigurationService,
    private val plugin: Plugin
) : ArenaRepository {
    private val objectMapper: ObjectMapper =
        ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))

    /**
     * Returns all stored arenas in this repository.
     */
    override fun getAll(): List<Arena> {
        var i = 0
        val arenas = ArrayList<Arena>()
        i = 0

        while (i < this.getFolder().toFile().list()!!.size) {
            val s = this.getFolder().toFile().list()!![i]
            try {
                if (s.contains("arena_")) {
                    val file = getFolder().resolve(s)
                    val arena = objectMapper.readValue(
                        file.toFile(),
                        Arena::class.java
                    )
                    if (arena.name.toIntOrNull() == null) {
                        throw RuntimeException("Arena name has to be a number!")
                    }

                    arenas.add(arena)
                }
            } catch (ex: Exception) {
                plugin.logger.log(Level.SEVERE, "Cannot read arena file $s.", ex)
            }

            i++
        }

        arenas.sortWith(Comparator { o1, o2 -> o1.name.toInt().compareTo(o2.name.toInt()) })
        plugin.logger.log(Level.INFO, "Reloaded [" + arenas.size + "] games.")

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
        objectMapper.writeValue(file.toFile(), arena)
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
