package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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
class HologramProxyImpl(private val plugin: Plugin, private val version : com.github.shynixn.blockball.api.business.enumeration.Version, private var location: Location) : HologramProxy, Runnable {
    companion object {
        /**
         * Max distance when holograms should be visible to.
         */
        const val MAX_DISTANCE = 50
        /**
         * Refresh rate of the max distance check.
         */
        const val REFRESH_RATE = 60L
    }

    private val armorstands = ArrayList<ArmorStand>()
    private val watchers = HashMap<Player, Boolean>()
    private val bukkitTask = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0L, REFRESH_RATE)

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        synchronized(armorstands) {
            synchronized(watchers) {
                watchers.keys.toTypedArray().forEach { p ->
                    if (p.location.world == location.world && p.location.distance(location) < MAX_DISTANCE) {
                        if (this.watchers[p] == false) {
                            sendSpawnPacket(p)
                            this.watchers[p] = true
                        }
                    } else {
                        if (this.watchers[p] == true) {
                            sendRemovePacket(p)
                            this.watchers[p] = false
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a watcher to this hologram.
     * Does nothing if already added.
     */
    override fun <P> addWatcher(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        synchronized(watchers) {
            if (!this.watchers.containsKey(player)) {
                this.watchers[player] = false
            }
        }
    }

    /**
     * Removes a watcher from this hologram.
     * Does nothing if the [player] is not aded.
     */
    override fun <P> removeWatcher(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        synchronized(watchers) {
            if (watchers.containsKey(player)) {
                this.watchers.remove(player)
                this.sendRemovePacket(player)
            }
        }
    }

    /**
     * Adds a line to the hologram.
     */
    override fun addLine(line: String) {
        spawnEntityArmorstand(line)
    }

    /**
     * Changes the lines of the hologram.
     */
    override fun setLines(lines: Collection<String>) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, {
            synchronized(armorstands) {
                if (this.armorstands.size != lines.size) {
                    this.clearLines()
                    lines.forEach { line ->
                        addLine(line)
                    }
                }
                lines.forEachIndexed { i, l ->
                    armorstands[i].customName = l
                    updateMetaData(armorstands[i])
                }
            }
        })
    }

    /**
     * Removes the hologram. If it is not already removed.
     */
    override fun remove() {
        clearLines()
        watchers.clear()
        bukkitTask.cancel()
    }

    /**
     * Sends a remove packet to the [player].
     */
    private fun sendRemovePacket(player: Player) {
        this.armorstands.forEach { a ->
            val packet = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy").getDeclaredConstructor(IntArray::class.java).newInstance(intArrayOf(a.entityId))
            player.sendPacket(packet)
        }
    }

    private fun spawnEntityArmorstand(text: String) {
        val index = this.armorstands.size
        val upSet = index * 0.23
        val targetLocation = Location(location.world, location.x, location.y - upSet, location.z, location.yaw, location.pitch)

        val nmsWorld = findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld").getDeclaredMethod("getHandle").invoke(targetLocation.world)
        val entityArmorstand = findClazz("net.minecraft.server.VERSION.EntityArmorStand")
                .getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.World"))
                .newInstance(nmsWorld)

        val bukkitArmorstand = findClazz("net.minecraft.server.VERSION.Entity").getDeclaredMethod("getBukkitEntity").invoke(entityArmorstand) as ArmorStand
        bukkitArmorstand.teleport(Location(location.world, targetLocation.x, targetLocation.y, targetLocation.z, 0F, 0F))
        bukkitArmorstand.customName = text
        bukkitArmorstand.isCustomNameVisible = true
        bukkitArmorstand.setGravity(false)
        bukkitArmorstand.isSmall = true
        bukkitArmorstand.isVisible = false

        synchronized(armorstands) {
            this.armorstands.add(bukkitArmorstand)
        }
    }

    /**
     * Updates changed entity meta values.
     */
    private fun updateMetaData(armorstand: ArmorStand) {
        val dataWatcherField = findClazz("net.minecraft.server.VERSION.Entity").getDeclaredField("datawatcher")
        dataWatcherField.isAccessible = true
        val dataWatcher = dataWatcherField.get(getEntityArmorstandFrom(armorstand))

        val packet = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata")
                .getDeclaredConstructor(Int::class.java, findClazz("net.minecraft.server.VERSION.DataWatcher"), Boolean::class.java)
                .newInstance(armorstand.entityId, dataWatcher, true)

        synchronized(watchers) {
            watchers.keys.toTypedArray().forEach { p ->
                if (watchers[p] == true) {
                    p.sendPacket(packet)
                }
            }
        }
    }

    /**
     * Sends a spawn packet to the [player].
     */
    private fun sendSpawnPacket(player: Player) {
        this.armorstands.forEach { a ->
            findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
                    .getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.EntityLiving"))
                    .newInstance(getEntityArmorstandFrom(a))

            player.sendPacket(a)
        }
    }

    /**
     * Returns the entity armorstand from the bukkit Armorstand.
     */
    private fun getEntityArmorstandFrom(armorstand: ArmorStand): Any {
        return findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftArmorStand").getDeclaredMethod("getHandle").invoke(armorstand)
    }

    /**
     * Removes all visible information of the hologram.
     */
    private fun clearLines() {
        synchronized(watchers) {
            this.watchers.keys.toTypedArray().forEach { p ->
                removeWatcher(p)
            }
        }
        this.armorstands.clear()
    }

    private fun findClazz(classPath: String): Class<*> {
        return Class.forName(classPath.replace("VERSION", version.bukkitId))
    }
}