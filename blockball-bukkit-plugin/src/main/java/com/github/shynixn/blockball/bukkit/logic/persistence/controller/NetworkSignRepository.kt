package com.github.shynixn.blockball.bukkit.logic.persistence.controller

import com.github.shynixn.blockball.api.persistence.controller.LinkSignController
import com.github.shynixn.blockball.api.persistence.entity.LinkSign
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LinkSignEntity
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.ArrayList
import java.util.HashSet
import java.util.logging.Level
import java.util.logging.Logger

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
@Singleton
class NetworkSignRepository : LinkSignController<Location> {

    private val signs = ArrayList<LinkSign>()

    @Inject
    private val plugin: Plugin? = null

    @Inject
    private val logger: Logger? = null

    /** Creates a new linking sign. */
    override fun create(server: String, location: Location): LinkSign {
        val network = LinkSignEntity()
        network.server = server
        network.position = LocationBuilder(location)
        return network
    }

    /** Stores a new item into the repository. */
    override fun store(item: LinkSign) {
        if (!this.signs.contains(item)) {
            this.signs.add(item)
        }
        this.saveSignsAsynchronly()
    }

    /** Removes an item from the repository. */
    override fun remove(item: LinkSign) {
        if (this.signs.contains(item)) {
            this.signs.remove(item)
        }
        this.saveSignsAsynchronly()
    }

    /** Returns the amount of items in the repository. */
    override val count: Int
        get() {
            return this.signs.size
        }

    /** All servers which are linked via signs. */
    override val linkedServers: Set<String>
        get() {
            val servers = HashSet<String>()
            for (signInfo in this.getAll()) {
                signInfo.server?.let { servers.add(it) }
            }
            return servers
        }

    /** Returns all items from the repository. */
    override fun getAll(): List<LinkSign> {
        return signs
    }

    /** Reloads the contents in the cache of the controller. */
    override fun reload() {
        try {
            val configuration = YamlConfiguration()
            val file = File(this.plugin!!.dataFolder, "bungeecord_signs.yml")
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    logger!!.log(Level.WARNING, "File cannot get created.")
                }
            }
            configuration.load(file)
            if (configuration.getConfigurationSection("signs") != null) {
                val data = configuration.getConfigurationSection("signs").getValues(false)
                for (s in data.keys) {
                    this.signs.add(YamlSerializer.deserializeObject(LinkSignEntity::class.java, null,(data[s] as ConfigurationSection).getValues(true)))
                }
            }
        } catch (e: IOException) {
            logger!!.log(Level.WARNING, "Save load location.", e)
        } catch (e: InvalidConfigurationException) {
            logger!!.log(Level.WARNING, "Save load location.", e)
        }

    }

    /**
     * Stores the signs asynchronly to the fileSystem.
     */
    private fun saveSignsAsynchronly() {
        val signs = this.getAll().toTypedArray()
        Bukkit.getServer().scheduler.runTaskAsynchronously(this.plugin) {
            try {
                val configuration = YamlConfiguration()
                val file = File(this.plugin!!.dataFolder, "bungeecord_signs.yml")
                if (file.exists()) {
                    if (!file.delete()) {
                        Bukkit.getLogger().log(Level.WARNING, "File cannot get deleted.")
                    }
                }

                configuration.save(file)
            } catch (e: IOException) {
                Bukkit.getLogger().log(Level.WARNING, "Save sign location.", e)
            }
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        signs.clear()
    }
}