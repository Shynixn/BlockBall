@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_13_R2

import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.bukkit.logic.business.proxy.BallProxyImpl
import com.github.shynixn.blockball.bukkit.logic.business.service.SpigotTimingServiceImpl
import com.github.shynixn.blockball.bukkit.logic.compatibility.SkinHelper
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BallMetaEntity
import net.minecraft.server.v1_13_R2.EntityArmorStand
import net.minecraft.server.v1_13_R2.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.configuration.MemorySection
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import java.util.*
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
class BallDesign(location: Location, ballMeta: BallMeta, persistent: Boolean, uuid: UUID = UUID.randomUUID(), owner: LivingEntity?) : EntityArmorStand((location.world as CraftWorld).handle), NMSBallProxy {
    private val hitbox = BallHitBox(this, location, SpigotTimingServiceImpl())
    private var internalProxy: BallProxyImpl? = null
    /**
     * Proxy handler.
     */
    override val proxy: BallProxyImpl get() = internalProxy!!

    constructor(uuid: String, data: Map<String, Any>) : this(Location.deserialize((data["location"] as MemorySection).getValues(true)),
            BallMetaEntity((data["meta"] as MemorySection).getValues(true)), true, UUID.fromString(uuid), null)

    /**
     * Initializes the nms design.
     */
    init {
        val mcWorld = (location.world as CraftWorld).handle
        this.setPositionRotation(location.x, location.y, location.z, location.yaw, location.pitch)
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)

        internalProxy = BallProxyImpl(ballMeta, this.bukkitEntity as ArmorStand, hitbox.bukkitEntity as ArmorStand, uuid, owner, persistent)

        val compound = NBTTagCompound()
        compound.setBoolean("invulnerable", true)
        compound.setBoolean("Invisible", true)
        compound.setBoolean("PersistenceRequired", true)
        compound.setBoolean("NoBasePlate", true)
        this.a(compound)

        val itemStack = ItemStack(Material.SKULL_ITEM, 1, 3.toShort())
        try {
            SkinHelper.setItemStackSkin(itemStack, proxy.meta.skin)
        } catch (e1: Exception) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to respawn entity.", e1)
        }

        when (proxy.meta.size!!) {
            BallSize.SMALL -> {
                (bukkitEntity as ArmorStand).isSmall = true
                (bukkitEntity as ArmorStand).helmet = itemStack
            }
            BallSize.NORMAL -> (bukkitEntity as ArmorStand).helmet = itemStack
        }
    }

    /**
     * OnTick.
     */
    override fun doTick() {
        super.doTick()

        if (!(this.passengers == null || this.passengers.isEmpty()) || this.getBukkitEntity().vehicle != null) {
            return
        }

        val loc = hitbox.bukkitEntity.location
        if (this.isSmall) {
            this.setPositionRotation(loc.x, loc.y, loc.z, loc.yaw, loc.pitch)
        } else {
            this.setPositionRotation(loc.x, loc.y - 1.0, loc.z, loc.yaw, loc.pitch)
        }

        proxy.run()
    }
}