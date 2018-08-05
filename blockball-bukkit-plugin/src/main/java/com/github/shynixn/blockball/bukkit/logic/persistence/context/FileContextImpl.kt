package com.github.shynixn.blockball.bukkit.logic.persistence.context

import com.github.shynixn.blockball.api.persistence.context.FileContext
import com.google.inject.Inject
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
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
class FileContextImpl @Inject constructor(private val plugin: Plugin) : FileContext {
    /**
     * Executes the given function with the configuration for the given [path].
     */
    override fun <F> saveAndCreateYamlFile(path: Path, f: (F) -> Unit) {
        synchronized(this) {
            try {
                val file = path.toFile()

                if (file.exists()) {
                    file.delete()
                }

                file.createNewFile()

                val configuration = YamlConfiguration()
                configuration.load(file)

                f.invoke(configuration as F)

                configuration.save(file)
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Failed to save file $path.")
                throw RuntimeException(e)
            }
        }
    }

    /**
     * Returns the content of the given [path] and [yamlPath].
     * Handles locking for asynchronous operations.
     * Creates the file if it does not already exist.
     */
    override fun loadOrCreateYamlFile(path: Path, yamlPath: String): Map<String, Any> {
        synchronized(this) {
            try {
                val file = path.toFile()

                if (!file.exists()) {
                    file.createNewFile()
                }

                val configuration = YamlConfiguration()
                configuration.load(file)

                if (!configuration.contains(yamlPath)) {
                    throw IllegalArgumentException("Yamlfile $path does not contain path $yamlPath.")
                }

                configuration.load(path.toFile())

                return configuration.getConfigurationSection("signs").getValues(false)
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Failed to load file $path with $yamlPath.")
                throw RuntimeException(e)
            }
        }
    }
}