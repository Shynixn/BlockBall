package com.github.shynixn.blockball.bukkit.logic.business.entity.action

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method


@Suppress("MemberVisibilityCanBePrivate", "unused")
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
class SimpleHologram(private val plugin: Plugin, private val location: Location, lines: Collection<String>) : AutoCloseable, Runnable {

    companion object {
        private var reflectionCache: Array<Any?>? = null
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
    private val bukkitTask: BukkitTask

    init {
        if (reflectionCache == null) {
            initializeReflectionCache()
        }
        this.bukkitTask = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0L, REFRESH_RATE)
        this.addLines(lines)
    }

    /**
     * Adds a new armorstand for each text [lines] to build a full hologram.
     */
    fun addLines(vararg lines: String) {
        lines.forEach { l -> spawnEntityArmorstand(l) }
    }

    /**
     * Adds a new armorstand for each text [lines] to build a full hologram.
     */
    fun addLines(lines: Collection<String>) {
        lines.forEach { l -> spawnEntityArmorstand(l) }
    }

    /**
     * Sets the [lines] of the hologram. If the amount of lines do not match the armorstand amount
     * the hologram gets regenerated.
     */
    fun setLines(lines: Collection<String>) {
        plugin.server.scheduler.runTaskAsynchronously(plugin, {
            synchronized(armorstands) {
                if (this.armorstands.size != lines.size) {
                    this.clearLines()
                    this.addLines(lines)
                }
                lines.forEachIndexed { i, l ->
                    armorstands[i].customName = l
                    updateMetaData(armorstands[i])
                }
            }
        })
    }

    /**
     * Removes all visible information of the hologram.
     */
    private fun clearLines() {
        synchronized(watchers) {
            this.watchers.keys.toTypedArray().forEach { p ->
                this.removePlayer(p)
            }
        }
        this.armorstands.clear()
    }

    /**
     * Adds a new [player] to the watcher list of this hologram.
     */
    fun addPlayer(player: Player) {
        synchronized(watchers) {
            this.watchers[player] = false
        }
    }

    /**
     * Checks if the [player] is a hologram watcher.
     */
    fun containsPlayer(player: Player): Boolean {
        return this.watchers.containsKey(player)
    }

    /**
     * Removes the [player] from the watcher list.
     */
    fun removePlayer(player: Player) {
        synchronized(watchers) {
            this.watchers.remove(player)
        }
        this.sendRemovePacket(player)
    }

    /**
     * Updates changed entity meta values.
     */
    private fun updateMetaData(armorstand: ArmorStand) {
        (reflectionCache!![10] as Field).isAccessible = true
        val dataWatcher = (reflectionCache!![10] as Field).get(getEntityArmorstandFrom(armorstand))
        val packet = (reflectionCache!![9] as Constructor<*>).newInstance(armorstand.entityId, dataWatcher, true)
        synchronized(watchers) {
            watchers.keys.toTypedArray().forEach { p ->
                if (watchers[p] == true) {
                    sendPacket(p, packet)
                }
            }
        }
    }

    /**
     * Spawns a new packet armorstand.
     */
    private fun spawnEntityArmorstand(text: String) {
        val index = this.armorstands.size
        val upSet = index * 0.23
        val targetLocation = Location(location.world, location.x, location.y - upSet, location.z, location.yaw, location.pitch)

        val worldServer = (reflectionCache!![0] as Method).invoke(targetLocation.world)
        val entityArmorstand = (reflectionCache!![7] as Constructor<*>).newInstance(worldServer)
        val bukkitArmorstand = (reflectionCache!![11] as Method).invoke(entityArmorstand) as ArmorStand
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
     * Sends a spawn packet to the [player].
     */
    private fun sendSpawnPacket(player: Player) {
        this.armorstands.forEach { a ->
            val packet = (reflectionCache!![2] as Constructor<*>).newInstance(getEntityArmorstandFrom(a))
            sendPacket(player, packet)
        }
    }

    /**
     * Sends a remove packet to the [player].
     */
    private fun sendRemovePacket(player: Player) {
        this.armorstands.forEach { a ->
            val packet = (reflectionCache!![8] as Constructor<*>).newInstance(intArrayOf(a.entityId))
            sendPacket(player, packet)
        }
    }

    /**
     * Returns the entity armorstand from the bukkit Armorstand.
     */
    private fun getEntityArmorstandFrom(armorstand: ArmorStand): Any {
        return (reflectionCache!![6] as Method).invoke(armorstand)
    }

    /**
     * Sends a new [packet] to the given [player].
     */
    private fun sendPacket(player: Player, packet: Any) {
        val getEntityPlayerMethod = reflectionCache!![3] as Method
        val playerConnectionField = reflectionCache!![4] as Field
        val sendPacketMethod = reflectionCache!![5] as Method
        val entityPlayer = getEntityPlayerMethod.invoke(player)
        val playerConnection = playerConnectionField.get(entityPlayer)
        sendPacketMethod.invoke(playerConnection, packet)
    }

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
     * Initializes the reflection cache of the required classes.
     */
    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, NoSuchFieldException::class)
    private fun initializeReflectionCache() {
        reflectionCache = arrayOfNulls(12)
        reflectionCache!![0] = createClass("org.bukkit.craftbukkit.VERSION.CraftWorld").getDeclaredMethod("getHandle")
        reflectionCache!![1] = createClass("net.minecraft.server.VERSION.PacketPlayOutEntityTeleport").getDeclaredConstructor(createClass("net.minecraft.server.VERSION.Entity"))
        reflectionCache!![2] = createClass("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving").getDeclaredConstructor(createClass("net.minecraft.server.VERSION.EntityLiving"))
        reflectionCache!![3] = createClass("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer").getDeclaredMethod("getHandle")
        reflectionCache!![4] = createClass("net.minecraft.server.VERSION.EntityPlayer").getDeclaredField("playerConnection")
        reflectionCache!![5] = createClass("net.minecraft.server.VERSION.PlayerConnection").getDeclaredMethod("sendPacket", createClass("net.minecraft.server.VERSION.Packet"))
        reflectionCache!![6] = createClass("org.bukkit.craftbukkit.VERSION.entity.CraftArmorStand").getDeclaredMethod("getHandle")
        reflectionCache!![7] = createClass("net.minecraft.server.VERSION.EntityArmorStand").getDeclaredConstructor(createClass("net.minecraft.server.VERSION.World"))
        reflectionCache!![8] = createClass("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy").getDeclaredConstructor(IntArray::class.java)
        reflectionCache!![9] = createClass("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata").getDeclaredConstructor(Int::class.java, createClass("net.minecraft.server.VERSION.DataWatcher"), Boolean::class.java)
        reflectionCache!![10] = createClass("net.minecraft.server.VERSION.Entity").getDeclaredField("datawatcher")
        reflectionCache!![11] = createClass("net.minecraft.server.VERSION.Entity").getDeclaredMethod("getBukkitEntity")
    }

    /**
     * Creates a new version independent class.
     *
     * @param path path
     * @return class
     * @throws ClassNotFoundException exception
     */
    @Throws(ClassNotFoundException::class)
    private fun createClass(path: String): Class<*> {
        val version = Bukkit.getServer().javaClass.`package`.name.replace(".", ",").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
        return Class.forName(path.replace("VERSION", version))
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
        clearLines()
        watchers.clear()
        bukkitTask.cancel()
    }
}