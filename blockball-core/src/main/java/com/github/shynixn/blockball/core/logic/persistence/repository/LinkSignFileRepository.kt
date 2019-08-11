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
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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