@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.PersistenceArenaService
import com.github.shynixn.blockball.api.business.service.TemplateService
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.api.business.service.YamlService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Template
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.TemplateEntity
import com.google.inject.Inject
import java.nio.file.Files
import kotlin.streams.toList

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
class TemplateServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val yamlService: YamlService,
    private val yamlSerializationService: YamlSerializationService,
    private val persistenceArenaService: PersistenceArenaService
) : TemplateService {
    private val templateNames = arrayOf(
        "arena-de.yml", "arena-en.yml", "arena-hu.yml", "arena-ko.yml", "arena-pl.yml", "arena-ru.yml"
    )

    /**
     * Returns a [List] of available
     */
    override fun getAvailableTemplates(): List<Template> {
        val templateFolders = listOf(
            "arena" to true,
            "template" to false
        )

        return templateFolders.flatMap { (folderName, existingArena) ->
            Files.walk(configurationService.applicationDir.resolve(folderName), 1)
                .filter { p -> p.toFile().absolutePath.endsWith(".yml") }
                .map { path ->
                    val configuration = yamlService.read(path)
                    val templateName = path.fileName.toString().replace(".yml", "")
                    val translator = configuration.getOrDefault("arena.translator", "unknown") as String

                    TemplateEntity(templateName, translator, existingArena)
                }
                .toList()
        }
    }

    /**
     * Generates a new [Arena] from the given [template].
     */
    override fun generateArena(template: Template): Arena {
        val folderName = if (template.existingArena) {
            "arena"
        } else {
            "template"
        }

        val filePath = configurationService.applicationDir.resolve(folderName + "/" + template.name + ".yml")
        val data = yamlService.read(filePath)
        val arena = yamlSerializationService.deserialize(ArenaEntity::class.java, data["arena"] as Map<String, Any?>)

        var idGen = 1
        persistenceArenaService.getArenas().forEach { cacheArena ->
            if (cacheArena.name == idGen.toString()) {
                idGen++
            }
        }

        arena.name = idGen.toString()
        arena.displayName = "Arena " + arena.name
        arena.meta.lobbyMeta.leaveSpawnpoint = null
        arena.meta.ballMeta.spawnpoint = null
        arena.lowerCorner = PositionEntity()
        arena.upperCorner = PositionEntity()

        arena.meta.redTeamMeta.goal.upperCorner = PositionEntity()
        arena.meta.blueTeamMeta.goal.upperCorner = PositionEntity()

        arena.meta.redTeamMeta.goal.lowerCorner = PositionEntity()
        arena.meta.blueTeamMeta.goal.lowerCorner = PositionEntity()

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
        val templateFolderPath = configurationService.applicationDir.resolve("template")
        val templateFolder = templateFolderPath.toFile()

        if (!templateFolder.exists()) {
            templateFolder.mkdir()
        }

        templateNames.forEach { t ->
            copyResourceToTarget("template/$t", t)
        }
    }

    private fun copyResourceToTarget(resource: String, target: String) {
        val filePath = configurationService.applicationDir.resolve("template/$target")

        if (!Files.exists(filePath)) {
            configurationService.openResource(resource).use { inputStream ->
                Files.copy(inputStream, filePath)
            }
        }
    }
}
