@file:Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")

package com.github.shynixn.blockball.core.logic.persistence.repository

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.api.business.service.YamlService
import com.github.shynixn.blockball.api.persistence.entity.LinkSign
import com.github.shynixn.blockball.api.persistence.repository.LinkSignRepository
import com.github.shynixn.blockball.core.logic.business.commandmenu.LinkSignParsingContainer
import com.google.inject.Inject
import java.nio.file.Files

/**
 * Handles storing and retrieving signs from a persistence medium.
 */
class LinkSignFileRepository @Inject constructor(
    private val configurationService: ConfigurationService,
    private val yamlSerializationService: YamlSerializationService,
    private val yamlService: YamlService,
    private val loggingService: LoggingService
) : LinkSignRepository {

    /**
     * Gets all signs from the repository.
     */
    override fun getAll(): List<LinkSign> {
        val file = configurationService.applicationDir.resolve("bungeecord-signs.yml")
        val signs = ArrayList<LinkSign>()

        if (!Files.exists(file)) {
            return signs
        }

        // Compatibility 6.10.0.
        val oldFile = configurationService.applicationDir.resolve("bungeecord_signs.yml")

        if (Files.exists(oldFile)) {
            Files.delete(oldFile)
        }

        try {
            val data = yamlService.read(file)
            val linkSigns = yamlSerializationService.deserialize(LinkSignParsingContainer::class.java, data).signs

            signs.addAll(linkSigns)
        } catch (ex: Exception) {
            loggingService.error("Cannot parse bungeecord-signs.yml.", ex)
        }

        return signs
    }

    /**
     * Saves all signs to the repository.
     */
    override fun save(signs: List<LinkSign>) {
        val file = configurationService.applicationDir.resolve("bungeecord-signs.yml")

        if (Files.exists(file)) {
            Files.delete(file)
        }

        val data = yamlSerializationService.serialize(signs)
        val finalData = HashMap<String, Any>()
        finalData["signs"] = data
        yamlService.write(file, finalData)
    }
}
