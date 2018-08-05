package com.github.shynixn.blockball.bukkit.logic.persistence.repository

import com.github.shynixn.blockball.api.persistence.context.FileContext
import com.github.shynixn.blockball.api.persistence.entity.LinkSign
import com.github.shynixn.blockball.api.persistence.repository.ServerSignRepository
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LinkSignEntity
import com.google.inject.Inject
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.Plugin
import java.io.File
import java.nio.file.Paths
import java.util.*

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
class ServerSignFileRepository @Inject constructor(private val plugin: Plugin, private val fileContext: FileContext) : ServerSignRepository {
    private val path = Paths.get(File(this.plugin.dataFolder, "bungeecord_signs.yml").toURI())

    /**
     * Saves the given [signs] to the storage.
     */
    override fun saveAll(signs: List<LinkSign>) {
        fileContext.saveAndCreateYamlFile<FileConfiguration>(path, { configuration ->
            for (i in signs.indices) {
                configuration.set("signs.$i", (signs[i] as ConfigurationSerializable).serialize())
            }
        })
    }

    /**
     * Returns all stored signs in this repository.
     */
    override fun getAll(): List<LinkSign> {
        val signs = ArrayList<LinkSign>()
        val data = fileContext.loadOrCreateYamlFile(path, "signs")

        for (s in data.keys) {
            signs.add(YamlSerializer.deserializeObject(LinkSignEntity::class.java, null, (data[s] as ConfigurationSection).getValues(true)))
        }

        return signs
    }
}