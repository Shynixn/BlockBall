@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_8_R3

import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_8_R3.EntitySlime
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

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
class BallHitBox(
    private val ballDesign: BallDesign,
    ballMeta: BallMeta,
    location: Location
) : EntitySlime((location.world as CraftWorld).handle) {

    /**
     * Initializes the hitbox.
     */
    init {
        val mcWorld = (location.world as CraftWorld).handle
        this.setPosition(location.x, location.y, location.z)
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)

        val compound = NBTTagCompound()
        compound.setBoolean("Invulnerable", true)
        compound.setBoolean("PersistenceRequired", true)
        compound.setBoolean("NoAI", true)
        compound.setInt("Size", ballMeta.hitBoxSize.toInt() - 1)
        this.a(compound)
        getBukkitEntity().addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false))
    }

    /**
     * Overridden onTick()
     * 1. Prevent slime from dying in Peaceful difficulty
     * 2. Constantly move hitbox to overlap with ballDesign
     */
    override fun t_() {
        super.t_()
        this.dead = false

        val loc = ballDesign.bukkitEntity.location
        if (ballDesign.isSmall) {
            this.setPositionRotation(loc.x, loc.y + 1, loc.z, loc.yaw, loc.pitch)
        } else {
            this.setPositionRotation(loc.x, loc.y + 1, loc.z, loc.yaw, loc.pitch)
        }
    }

    /**
     * Gets the bukkit entity.
     */
    override fun getBukkitEntity(): CraftHitboxSlime {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftHitboxSlime(this.world.server, this)
        }

        return this.bukkitEntity as CraftHitboxSlime
    }
}