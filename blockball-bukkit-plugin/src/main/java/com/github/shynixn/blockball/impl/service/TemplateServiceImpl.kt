package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Template
import com.github.shynixn.blockball.entity.ArenaEntity
import com.github.shynixn.blockball.entity.PositionEntity
import com.github.shynixn.blockball.entity.TemplateEntity
import com.github.shynixn.mcutils.common.ConfigurationService
import com.google.inject.Inject
import java.io.File
import java.nio.file.Files

class TemplateServiceImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val yamlService: YamlService,
    private val yamlSerializationService: YamlSerializationService,
    private val persistenceArenaService: PersistenceArenaService
) : TemplateService {
    private val templateNames = arrayOf(
        "arena.yml"
    )

    /**
     * Returns a [List] of available
     */
    override fun getAvailableTemplates(): List<Template> {
        val templates = ArrayList<Template>()

        for (file in File(configurationService.applicationDir.toFile(), "template").listFiles()!!) {
            if (file.absolutePath.endsWith(".yml")) {
                val configuration = yamlService.read(file.toPath())
                val templateName = file.toPath().fileName.toString().replace(".yml", "")
                val translator = configuration.getOrDefault("arena.translator", "unknown") as String
                templates.add(TemplateEntity(templateName, translator, false))
            }
        }

        for (file in File(configurationService.applicationDir.toFile(), "arena").listFiles()!!) {
            if (file.absolutePath.endsWith(".yml")) {
                val configuration = yamlService.read(file.toPath())
                val templateName = file.toPath().fileName.toString().replace(".yml", "")
                val translator = configuration.getOrDefault("arena.translator", "unknown") as String
                templates.add(TemplateEntity(templateName, translator, true))
            }
        }

        return templates
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
        val arena = ArenaEntity()

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
