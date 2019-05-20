@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordConfiguration
import com.github.shynixn.blockball.bukkit.logic.business.extension.deserializeToMap
import com.github.shynixn.blockball.core.logic.persistence.entity.BungeeCordConfigurationEntity
import com.google.inject.Inject
import org.bukkit.ChatColor
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.nio.file.Path
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
class ConfigurationServiceImpl @Inject constructor(private val plugin: Plugin, private val yamlSerializationService: YamlSerializationService) :
    ConfigurationService {
    /**
     * Gets the path to the folder where the application is allowed to store
     * save data.
     */
    override val applicationDir: Path
        get() = plugin.dataFolder.toPath()

    /**
     * Tries to load the config values into the given configuration [clazz] from the given [path]
     * Throws a [IllegalArgumentException] if the path could not be correctly
     * loaded.
     */
    override fun <C> findConfiguration(clazz: Class<C>, path: String): C {
        if (clazz == BungeeCordConfiguration::class.java) {
            try {
                val file = File(plugin.dataFolder, "bungeecord.yml")

                if (!file.exists()) {
                    file.createNewFile()
                    val configuration = YamlConfiguration()
                    configuration.set("bungeecord", yamlSerializationService.serialize(BungeeCordConfigurationEntity()))
                    configuration.save(file)
                }

                val configuration = YamlConfiguration()
                configuration.load(file)
                return yamlSerializationService.deserialize(BungeeCordConfigurationEntity::class.java, configuration.deserializeToMap("bungeecord")) as C
            } catch (e: IOException) {
                plugin.logger.log(Level.WARNING, "Failed to load bungeecord.yml.", e)
            }
        }

        throw IllegalArgumentException("Cannot find configuration.")
    }

    /**
     * Tries to load the config value from the given [path].
     * Throws a [IllegalArgumentException] if the path could not be correctly
     * loaded.
     */
    override fun <C> findValue(path: String): C {
        if (!plugin.config.contains(path)) {
            throw IllegalArgumentException("Path '$path' could not be found!")
        }

        var data = this.plugin.config.get(path)

        if (data is String) {
            data = ChatColor.translateAlternateColorCodes('&', data)
        }

        return data as C
    }
}