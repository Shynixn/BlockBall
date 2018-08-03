package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.business.proxy.HighlightArmorstandProxy
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.ArmorStand
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
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
class HighlightArmorstandProxyImpl(private val uuid: UUID, initialLocation: Location) : HighlightArmorstandProxy {
    private var armorstand: ArmorStand
    private val version = VersionSupport.getServerVersion()

    init {
        val player = Bukkit.getPlayer(uuid)
        val armorstandConstructor = findClazz("net.minecraft.server.VERSION.EntityArmorstand").getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.World"))
        val nmsWorld = findClazz("org.bukkit.craftbukkit.VERSION.CraftWorld").getDeclaredMethod("getHandle").invoke(player.world)
        val nmsArmorstand = armorstandConstructor.newInstance(nmsWorld)
        armorstand = findClazz("net.minecraft.server.VERSION.Entity").getDeclaredMethod("getBukkitEntity").invoke(nmsArmorstand) as ArmorStand

        val nbtTagClazz = findClazz("net.minecraft.server.VERSION.NBTTagCompound")
        val applyNbtMethod = findClazz("net.minecraft.server.VERSION.Entity").getDeclaredMethod("a", nbtTagClazz)
        val nbtTagCompound = nbtTagClazz.newInstance()
        val nbtTagBooleanMethod = nbtTagClazz.getDeclaredMethod("setBoolean", String::class.java, Boolean::class.java)

        nbtTagBooleanMethod.invoke("invulnerable", true)
        nbtTagBooleanMethod.invoke("Invisible", true)
        nbtTagBooleanMethod.invoke("PersistenceRequired", true)
        nbtTagBooleanMethod.invoke("NoBasePlate", true)

        applyNbtMethod.invoke(nmsArmorstand, nbtTagCompound)
        val itemStack = ItemStack(Material.STAINED_GLASS_PANE, 1)

        with(armorstand) {
            helmet = itemStack
            bodyPose = EulerAngle(3.15, 0.0, 0.0)
            leftLegPose = EulerAngle(3.15, 0.0, 0.0)
            rightLegPose = EulerAngle(3.15, 0.0, 0.0)
        }

        try {
            val method = Class.forName("org.bukkit.entity.Entity").getDeclaredMethod("setGlowing")
            method.invoke(armorstand, true)
        } catch (e: NoSuchMethodError) {

        }

        armorstand.teleport(initialLocation)
    }

    /**
     * Spawns the armorstand. If it has not already.
     */
    override fun spawn() {
        val packetEntityLiving = findClazz("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving")
                .getDeclaredConstructor(findClazz("net.minecraft.server.VERSION.EntityLiving"))
                .newInstance(getNMSArmorstand())

        @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
        val enumTimesValue = java.lang.Enum.valueOf<Any>(Class.forName("net.minecraft.server.VERSION.EnumItemSlot") as Class<Any>, "HEAD")

        val packetEquipment = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityEquipment")
                .getDeclaredConstructor(Int::class.java, Class.forName("net.minecraft.server.VERSION.EnumItemSlot"), Class.forName("net.minecraft.server.VERSION.ItemStack"))
                .newInstance(armorstand.entityId, enumTimesValue, Class.forName("org.bukkit.craftbukkit.VERSION.inventory.CraftItemStack")
                        .getDeclaredMethod("asNMSCopy", ItemStack::class.java).invoke(null, armorstand.helmet))


        val player = Bukkit.getPlayer(uuid)
        player.sendPacket(packetEntityLiving)
        player.sendPacket(packetEquipment)
    }

    /**
     * Teleports the armorstand to the given [location].
     */
    override fun <L> teleport(location: L) {
        if (location !is Location) {
            throw IllegalArgumentException("Player has to be a BukkitLocation!")
        }

        armorstand.teleport(location)
    }

    /**
     * Removes the armorstand. If it is not already removed.
     */
    override fun remove() {
        val packetEntityDestroy = findClazz("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy")
                .getDeclaredConstructor(Int::class.java)
                .newInstance(armorstand.entityId)

        val player = Bukkit.getPlayer(uuid)
        player.sendPacket(packetEntityDestroy)
    }

    /**
     * Returns the location of the armorstand.
     */
    override fun <L> getLocation(): L {
        return armorstand.location as L
    }

    /**
     * Finds the class matching the version.
     */
    private fun findClazz(name: String): Class<*> {
        return Class.forName(name.replace("VERSION", version.versionText))
    }

    /**
     * Returns the nms armorstand.
     */
    private fun getNMSArmorstand(): Any {
        return findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftArmorStand").getDeclaredMethod("getHandle").invoke(armorstand)
    }
}