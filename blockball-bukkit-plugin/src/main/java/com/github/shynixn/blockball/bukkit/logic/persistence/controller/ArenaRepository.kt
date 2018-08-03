package com.github.shynixn.blockball.bukkit.logic.persistence.controller

import com.github.shynixn.blockball.api.bukkit.persistence.controller.BukkitArenaController
import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BlockBallArena
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby.HubLobbyProperties
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Location
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.util.*
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
class ArenaRepository(private val items: MutableList<BukkitArena> = ArrayList()) : BukkitArenaController {
    @Inject
    private var plugin: Plugin? = null

    @Inject
    private val logger: Logger? = null

    /** Creates a new arena with the given properties. */
    override fun create(name: String, corner1: Location, corner2: Location): BukkitArena {
        val arena = BlockBallArena(name, corner1, corner2)
        arena.name = getNewId()
        return arena
    }

    internal fun create(): BukkitArena {
        val arena = BlockBallArena()
        arena.name = getNewId()
        arena.displayName = "Arena: " + arena.name
        return arena
    }

    /** Stores a new item into the repository. */
    override fun store(item: BukkitArena) {
        if (!this.items.contains(item)) {
            this.items.add(item)
        }
        saveArenaFile(item)
    }

    /** Removes an item from the repository. */
    override fun remove(item: BukkitArena) {
        try {
            val file = File(this.getFolder(), "arena_" + item.name + ".yml")
            if (file.exists()) {
                if (!file.delete())
                    throw IllegalStateException("Cannot delete file!")
            }
        } catch (ex: Exception) {
            logger!!.log(Level.WARNING, "Cannot delete arena file.", ex)
        }
        this.reload()
    }

    /** Returns the arena by name if found. */
    override fun getArenaByName(name: String): BukkitArena? {
        return this.items.firstOrNull { it.name.equals(name, ignoreCase = true) }
    }

    /** Returns all items from the repository. */
    override fun getAll(): List<BukkitArena> {
        return items
    }

    /** Returns the amount of items in the repository. */
    override val count: Int
        get() {
            return items.size
        }

    /** Reloads the contents in the cache of the controller. */
    override fun reload() {
        this.items.clear()
        var i = 0
        while (i < this.getFolder().list()!!.size) {
            val s = this.getFolder().list()!![i]
            try {
                if (s.contains("arena_")) {
                    val configuration = YamlConfiguration()
                    val file = File(this.getFolder(), s)
                    configuration.load(file)
                    val data = configuration.getConfigurationSection("arena").getValues(true)
                    val arenaEntity = YamlSerializer.deserializeObject(BlockBallArena::class.java, null, data)
                    runVersionStateChecks(arenaEntity)
                    this.items.add(arenaEntity)
                }
            } catch (ex: Exception) {
                logger!!.log(Level.WARNING, "Cannot read arena file $s.", ex)
            }

            i++
        }
        logger!!.log(Level.INFO, "Reloaded [" + items.size + "] games.")
    }

    private fun runVersionStateChecks(arena: BlockBallArena) {
        // For Version 5.0.2+
        if (arena.meta.hubLobbyMeta.joinMessage[1] == "&c[Team Red]") {
            val hubLobby = HubLobbyProperties()
            arena.meta.hubLobbyMeta.joinMessage[1] = hubLobby.joinMessage[1]
            arena.meta.hubLobbyMeta.joinMessage[2] = hubLobby.joinMessage[2]

            saveArenaFile(arena)

            plugin!!.logger.log(Level.INFO, "Upgraded arena file [" + arena.name + "] to v5.0.2")
        }
    }

    private fun saveArenaFile(item: BukkitArena) {
        try {
            val configuration = YamlConfiguration()
            val file = File(this.getFolder(), "arena_" + item.name + ".yml")
            if (file.exists()) {
                if (!file.delete())
                    throw IllegalStateException("Cannot delete file!")
            }
            if (!file.createNewFile())
                throw IllegalStateException("Cannot create file!")
            configuration.load(file)
            val serializable = item as ConfigurationSerializable
            val data = serializable.serialize()
            for (key in data.keys) {
                configuration.set("arena.$key", data[key])
            }
            configuration.save(file)
        } catch (ex: IOException) {
            logger!!.log(Level.WARNING, "Cannot save arena.", ex)
        } catch (ex: InvalidConfigurationException) {
            logger!!.log(Level.WARNING, "Cannot save arena.", ex)
        }
    }

    private fun getNewId(): String {
        for (i in 1 until Integer.MAX_VALUE) {
            if (getArenaByName(i.toString()) == null) {
                return i.toString()
            }
        }
        throw RuntimeException("Failed to gather id.")
    }

    private fun getFolder(): File {
        if (this.plugin == null)
            throw IllegalStateException("Plugin cannot be null!")
        val file = File(this.plugin!!.dataFolder, "arena")
        if (!file.exists()) {
            if (!file.mkdir())
                throw IllegalStateException("Cannot create folder!")
        }
        return file
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        items.clear()
        plugin = null
    }
}