@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_9_R2

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_9_R2.EntityArmorStand
import net.minecraft.server.v1_9_R2.EnumItemSlot
import net.minecraft.server.v1_9_R2.NBTTagCompound
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
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
class BallDesign(
    location: Location,
    ballMeta: BallMeta,
    persistent: Boolean,
    uuid: UUID = UUID.randomUUID(),
    owner: LivingEntity?
) : EntityArmorStand((location.world as CraftWorld).handle), NMSBallProxy {
    private val hitbox = BallHitBox(this, location)
    private var internalProxy: BallProxy? = null

    /**
     * Proxy handler.
     */
    override val proxy: BallProxy get() = internalProxy!!

    /**
     * Initializes the nms design.
     */
    init {
        val mcWorld = (location.world as CraftWorld).handle
        this.setPositionRotation(location.x, location.y, location.z, location.yaw, location.pitch)
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)

        internalProxy = Class.forName("com.github.shynixn.blockball.bukkit.logic.business.proxy.BallProxyImpl")
            .getDeclaredConstructor(
                BallMeta::class.java,
                LivingEntity::class.java,
                LivingEntity::class.java,
                UUID::class.java,
                LivingEntity::class.java,
                Boolean::class.java
            )
            .newInstance(
                ballMeta,
                this.getBukkitEntity() as LivingEntity,
                hitbox.bukkitEntity as LivingEntity,
                uuid,
                owner,
                persistent
            ) as BallProxy

        val compound = NBTTagCompound()
        compound.setBoolean("invulnerable", true)
        compound.setBoolean("Invisible", true)
        compound.setBoolean("PersistenceRequired", true)
        compound.setBoolean("NoBasePlate", true)
        compound.setInt("DisabledSlots", 2039583)
        this.a(compound)
    }

    /**
     * Disable setting slots.
     */
    override fun setSlot(enumitemslot: EnumItemSlot?, itemstack: net.minecraft.server.v1_9_R2.ItemStack?) {
    }

    /**
     * Sets the slot securely.
     */
    fun setSecureSlot(enumitemslot: EnumItemSlot?, itemstack: net.minecraft.server.v1_9_R2.ItemStack?) {
        super.setSlot(enumitemslot, itemstack)
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

        if (proxy.yawChange > 0) {
            this.hitbox.yaw = proxy.yawChange
            proxy.yawChange = -1.0F
        }

        proxy.run()
    }

    /**
     * Gets the bukkit entity.
     */
    override fun getBukkitEntity(): CraftBallArmorstand {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftBallArmorstand(this.world.server, this)
        }

        return this.bukkitEntity as CraftBallArmorstand
    }
}