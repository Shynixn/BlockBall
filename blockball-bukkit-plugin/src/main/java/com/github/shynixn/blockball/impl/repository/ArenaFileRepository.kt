@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.github.shynixn.blockball.contract.ArenaRepository
import com.github.shynixn.blockball.deprecated.YamlSerializationService
import com.github.shynixn.blockball.deprecated.YamlService
import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.Particle
import com.github.shynixn.blockball.entity.Sound
import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.mcutils.common.ConfigurationService
import com.google.inject.Inject
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.logging.Level
import kotlin.collections.HashMap
import kotlin.io.path.name

/**
 * Handles storing and retrieving arena from a persistence medium.
 */
class ArenaFileRepository @Inject constructor(
    private val configurationService: ConfigurationService,
    private val yamlSerializationService: YamlSerializationService,
    private val yamlService: YamlService,
    private val plugin: Plugin
) : ArenaRepository {
    private val objectMapper: ObjectMapper =
        ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))

    /**
     * Returns all stored arenas in this repository.
     */
    override fun getAll(): List<Arena> {
        var i = 0

        // Conversion code.
        while (i < this.getFolder().toFile().list()!!.size) {
            val s = this.getFolder().toFile().list()!![i]
            try {
                if (s.contains("arena_")) {
                    val file = getFolder().resolve(s)
                    val fileContents = String(Files.readAllBytes(file), Charset.forName("UTF-8"))

                    if (fileContents.contains("customizing-meta")) {
                        plugin.logger.log(
                            Level.INFO,
                            "Detected outdated arena file format in file ${file.toFile().absolutePath}. Starting conversion..."
                        )
                        val data = yamlService.read(file)

                        val arena = yamlSerializationService.deserialize(
                            Arena::class.java,
                            data["arena"] as Map<String, Any?>
                        )

                        // Compatibility added in v6.1.0
                        if (!arena.meta.ballMeta.soundEffects.containsKey(BallActionType.ONGOAL)) {
                            arena.meta.ballMeta.soundEffects[BallActionType.ONGOAL] = Sound()
                        }

                        // Compatibility added in v6.1.0
                        if (!arena.meta.ballMeta.particleEffects.containsKey(BallActionType.ONGOAL)) {
                            arena.meta.ballMeta.particleEffects[BallActionType.ONGOAL] = Particle()
                        }

                        // Compatibility added in v6.22.1
                        if (!arena.meta.ballMeta.particleEffects.containsKey(BallActionType.ONPASS)) {
                            arena.meta.ballMeta.particleEffects[BallActionType.ONPASS] = Particle()
                        }

                        // Compatibility added in v6.22.1
                        if (!arena.meta.ballMeta.soundEffects.containsKey(BallActionType.ONPASS)) {
                            arena.meta.ballMeta.soundEffects[BallActionType.ONPASS] = Sound()
                        }

                        val obsoleteDirectory = getFolder().resolve("obsolete")

                        if (!Files.exists(obsoleteDirectory)) {
                            obsoleteDirectory.toFile().mkdir()
                        }

                        Files.copy(file, obsoleteDirectory.resolve(file.name), StandardCopyOption.REPLACE_EXISTING)

                        arena.meta.redTeamMeta.armor = arena.meta.redTeamMeta.armorContents.map { e ->
                            val yamlConfiguration = YamlConfiguration()
                            yamlConfiguration.set("item", e)
                            yamlConfiguration.saveToString()
                        }.toTypedArray()
                        arena.meta.redTeamMeta.inventory = arena.meta.redTeamMeta.inventoryContents.map { e ->
                            val yamlConfiguration = YamlConfiguration()
                            yamlConfiguration.set("item", e)
                            yamlConfiguration.saveToString()
                        }.toTypedArray()
                        arena.meta.blueTeamMeta.armor = arena.meta.blueTeamMeta.armorContents.map { e ->
                            val yamlConfiguration = YamlConfiguration()
                            yamlConfiguration.set("item", e)
                            yamlConfiguration.saveToString()
                        }.toTypedArray()
                        arena.meta.blueTeamMeta.inventory = arena.meta.blueTeamMeta.inventoryContents.map { e ->
                            val yamlConfiguration = YamlConfiguration()
                            yamlConfiguration.set("item", e)
                            yamlConfiguration.saveToString()
                        }.toTypedArray()

                        save(arena)
                        plugin.logger.log(
                            Level.INFO,
                            "Converted file ${file.toFile().absolutePath}."
                        )
                    }
                }
            } catch (ex: Exception) {
                plugin.logger.log(Level.SEVERE, "Cannot read arena file $s.", ex)
            }

            i++
        }

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
