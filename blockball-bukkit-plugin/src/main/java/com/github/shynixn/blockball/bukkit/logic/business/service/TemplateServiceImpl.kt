package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.service.TemplateService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Template
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BlockBallArena
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.TemplateData
import com.google.inject.Inject
import org.apache.commons.io.IOUtils
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.FileOutputStream

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
class TemplateServiceImpl @Inject constructor(private val plugin: Plugin, private val arenaRepository: ArenaRepository) : TemplateService {
    private val templateNames = arrayOf("arena-de.yml", "arena-en.yml")

    /**
     * Returns a [List] of available
     */
    override fun getAvailableTemplates(): List<Template> {
        val templates = ArrayList<Template>()

        File(plugin.dataFolder, "arena").listFiles().forEach { f ->
            if (f.name.endsWith(".yml")) {
                val configuration = YamlConfiguration()
                configuration.load(f)

                val translator = if (configuration.contains("arena.translator")) {
                    configuration.getString("arena.translator")
                } else {
                    "unknown"
                }

                templates.add(TemplateData(f.name.replace(".yml", ""), translator, true))
            }
        }

        File(plugin.dataFolder, "template").listFiles().forEach { f ->
            if (f.name.endsWith(".yml")) {
                val configuration = YamlConfiguration()
                configuration.load(f)

                val translator = if (configuration.contains("arena.translator")) {
                    configuration.getString("arena.translator")
                } else {
                    "Unknown"
                }

                templates.add(TemplateData(f.name.replace(".yml", ""), translator, false))
            }
        }

        return templates
    }

    /**
     * Generates a new [Arena] from the given [template].
     */
    override fun generateArena(template: Template): Arena<*, *, *, *, *> {
        val arenaSource = arenaRepository.create()

        val configuration = YamlConfiguration()
        val arena: BukkitArena

        arena = if (template.existingArena) {
            val file = File(plugin.dataFolder, "arena/" + template.name + ".yml")
            configuration.load(file)

            val data = configuration.getConfigurationSection("arena").getValues(true)
            YamlSerializer.deserializeObject(BlockBallArena::class.java, null, data)
        } else {
            val file = File(plugin.dataFolder, "template/" + template.name + ".yml")
            configuration.load(file)

            val data = configuration.getConfigurationSection("arena").getValues(true)
            YamlSerializer.deserializeObject(BlockBallArena::class.java, null, data)
        }

        arena.name = arenaSource.name
        arena.displayName = arenaSource.displayName
        arena.meta.lobbyMeta.leaveSpawnpoint = null
        arena.meta.ballMeta.spawnpoint = null
        arena.lowerCorner = null
        arena.upperCorner = null

        arena.meta.redTeamMeta.goal.upperCorner = null
        arena.meta.blueTeamMeta.goal.upperCorner = null

        arena.meta.redTeamMeta.goal.lowerCorner = null
        arena.meta.blueTeamMeta.goal.lowerCorner = null

        arena.meta.redTeamMeta.spawnpoint = null
        arena.meta.blueTeamMeta.spawnpoint = null

        arena.meta.minigameMeta.lobbySpawnpoint = null

        return arena
    }

    /**
     * Copies the stored templateNames file to the template folder if they
     * do not already exist.
     */
    override fun copyTemplateFilesFromResources() {
        val templateFolder = File(plugin.dataFolder, "template")

        if (!templateFolder.exists()) {
            templateFolder.mkdir()
        }

        templateNames.forEach { t ->
            copyResourceToTarget("template/" + t, t)
        }
    }

    private fun copyResourceToTarget(resource: String, target: String) {
        val file = File(plugin.dataFolder, "template/" + target)

        if (!file.exists()) {
            val fileOutputStream = FileOutputStream(file)
            val inputStream = plugin.getResource(resource)

            fileOutputStream.use {
                inputStream.use {
                    IOUtils.copy(inputStream, fileOutputStream)
                }
            }
        }
    }
}