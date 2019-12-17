@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
class HologramProxyImpl(
    /**
     * Location of the hologram.
     */
    override var location: Any = Unit,
    /**
     * List of lines being displayed on the hologram.
     */
    override var lines: MutableList<String> = ArrayList(),
    /**
     * List of players being able to see this hologram.
     */
    override val players: MutableSet<Any> = HashSet()
) : HologramProxy {
    /**
     * Armorstands.
     */
    val armorstands = ArrayList<ArmorStand>()

    private val visibleTo = HashSet<Player>()
    private var previousAmount = 0

    /**
     * Gets if this hologram was removed.
     */
    override var isDead: Boolean = false

    /**
     * Updates the hologram visibility handling.
     */
    override fun update() {
        for (player in players) {
            if (isInVisibilityDistance(player as Player)) {
                if (!visibleTo.contains(player)) {
                    addArmorstandsForPlayer(player)
                }
            } else {
                if (visibleTo.contains(player)) {
                    removeArmorstandsForPlayer(player)
                }
            }
        }

        for (player in visibleTo.toTypedArray()) {
            if (!players.contains(player)) {
                removeArmorstandsForPlayer(player)
                visibleTo.remove(player)
            }
        }

        if (previousAmount != lines.size) {
            clearAllArmorstands()

            for (line in lines) {
                spawnArmorstand(line)
            }

            previousAmount = lines.size
        } else {
            updateMetaDataArmorstands()
        }
    }

    /**
     * Removes this hologram permanently.
     */
    override fun remove() {
        clearAllArmorstands()
        players.clear()
        visibleTo.clear()
        lines.clear()
        isDead = true
    }

    /**
     * Updates changed entity meta values.
     */
    private fun updateMetaDataArmorstands() {
        for (i in 0 until armorstands.size) {
            val armorstand = armorstands[i]

            val dataWatcherField = findClazz("net.minecraft.server.VERSION.Entity").getDeclaredField("datawatcher")
            dataWatcherField.isAccessible = true
            val dataWatcher = dataWatcherField.get(
                findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftArmorStand").getDeclaredMethod("getHandle")
                    .invoke(armorstand)
            )

            val packet = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityMetadata")
                .getDeclaredConstructor(
                    Int::class.java,
                    findClazz("net.minecraft.server.VERSION.DataWatcher"),
                    Boolean::class.java
                )
                .newInstance(armorstand.entityId, dataWatcher, true)

            armorstand.customName = lines[i]

            for (watcher in visibleTo) {
                watcher.sendPacket(packet)
            }
        }
    }

    /**
     * SpawnArmorstand for player.
     */
    private fun spawnArmorstand(text: String) {
        val index = this.armorstands.size
        val upSet = index * 0.23
        val locationInWorld = location as Location
        val targetLocation =
            Location(locationInWorld.world, locationInWorld.x, locationInWorld.y - upSet, locationInWorld.z, locationInWorld.yaw, locationInWorld.pitch)

        val nmsWorld = findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld").getDeclaredMethod("getHandle")
            .invoke(targetLocation.world)
        val entityArmorstand = findClazz("net.minecraft.server.VERSION.EntityArmorStand")
            .getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.World"), Double::class.java, Double::class.java, Double::class.java)
            .newInstance(nmsWorld, targetLocation.x, targetLocation.y, targetLocation.z)
        val bukkitArmorstand =
            findClazz("net.minecraft.server.VERSION.Entity").getDeclaredMethod("getBukkitEntity").invoke(
                entityArmorstand
            ) as ArmorStand

        bukkitArmorstand.teleport(
            Location(
                locationInWorld.world,
                targetLocation.x,
                targetLocation.y,
                targetLocation.z,
                0F,
                0F
            )
        )

        bukkitArmorstand.customName = text.translateChatColors()
        bukkitArmorstand.isCustomNameVisible = true
        bukkitArmorstand.setGravity(false)
        bukkitArmorstand.isSmall = true
        bukkitArmorstand.isVisible = false
        bukkitArmorstand.equipment!!.boots = generateMarkerItemStack()

        this.armorstands.add(bukkitArmorstand)
    }

    /**
     * Clears all armorstands.
     */
    private fun clearAllArmorstands() {
        for (player in visibleTo.toList()) {
            removeArmorstandsForPlayer(player)
        }

        for (armorstand in armorstands) {
            armorstand.remove()
        }

        armorstands.clear()
    }

    /**
     * Is player in visibility distance.
     */
    private fun isInVisibilityDistance(p: Player): Boolean {
        return p.location.world == (location as Location).world && p.location.distance(location as Location) < 60
    }

    /**
     * Add Armorstands for a player.
     */
    private fun addArmorstandsForPlayer(player: Player) {
        for (armorstand in armorstands) {
            val packet = findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
                .getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.EntityLiving"))
                .newInstance(
                    findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftArmorStand").getDeclaredMethod("getHandle")
                        .invoke(armorstand)
                )

            player.sendPacket(packet)
        }

        if (!visibleTo.contains(player)) {
            visibleTo.add(player)
        }
    }

    /**
     * Removes the armorstand for a player.
     */
    private fun removeArmorstandsForPlayer(player: Player) {
        val packet = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy")
            .getDeclaredConstructor(IntArray::class.java)
            .newInstance(armorstands.map { a -> a.entityId }.toIntArray())
        player.sendPacket(packet)

        if (visibleTo.contains(player)) {
            visibleTo.remove(player)
        }
    }

    /**
     * Gets a new marker itemstack.
     */
    private fun generateMarkerItemStack(): ItemStack {
        val item = ItemStack(Material.APPLE)
        val meta = item.itemMeta
        meta!!.lore = arrayListOf("BlockBallHologram")
        item.itemMeta = meta

        return item
    }
}