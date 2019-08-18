@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_14_R1

import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.service.SpigotTimingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_14_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.logging.Level

/**
 * Slime implementation for hitbox calculation.
 * <p>
 * Version 1.3
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
class BallHitBox(
    private val ballDesign: BallDesign,
    ballMeta: BallMeta,
    location: Location,
    private val timingService: SpigotTimingService
): EntitySlime(EntityTypes.SLIME, (location.world as CraftWorld).handle) {

    // BukkitEntity has to be self cached since 1.14.
    private var entityBukkit: Any? = null

    /**
     * Initializes the hitbox.
     */
    init {
        val mcWorld = (location.world as CraftWorld).handle
        val compound = NBTTagCompound()

        this.setPosition(location.x, location.y, location.z)
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)
        compound.setBoolean("Invulnerable", true)
        compound.setBoolean("PersistenceRequired", true)
        when (ballMeta.size) {
            BallSize.SMALL -> compound.setInt("Size", 0)
            else -> compound.setInt("Size", 1)
        }

        clearAI()
        this.a(compound)
        bukkitEntity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false))
    }

    /**
     * Recalculates y-axe hitbox offset in the world.
     */
    override fun recalcPosition() {
        val axisBoundingBox = this.boundingBox

        this.locX = (axisBoundingBox.minX + axisBoundingBox.maxX) / 2.0
        this.locY = axisBoundingBox.minY + ballDesign.proxy.meta.hitBoxRelocation
        this.locZ = (axisBoundingBox.minZ + axisBoundingBox.maxZ) / 2.0
    }

    /**
     * Override the default entity movement.
     */
    override fun move(enummovetype: EnumMoveType, vec3dmp: Vec3D) {
        var vec3d = vec3dmp
        var collision = false
        val motionVector = Vector(this.mot.x, this.mot.y, this.mot.z)
        val optSourceVector = ballDesign.proxy.calculateMoveSourceVectors(Vector(vec3d.x, vec3d.y, vec3d.z), motionVector, this.onGround)

        if (!optSourceVector.isPresent) {
            return
        }

        val sourceVector = optSourceVector.get()

        if (sourceVector.x != vec3d.x) {
            this.setMot(motionVector.x, motionVector.y, motionVector.z)
        }

        timingService.startTiming()

        if (this.noclip) {
            this.a(this.boundingBox.b(vec3d))
            this.recalcPosition()
        } else {
            this.world.methodProfiler.enter("move")
            if (this.B.g() > 1.0E-7) {
                vec3d = vec3d.h(this.B)
                this.B = Vec3D.a
                this.mot = Vec3D.a
            }

            vec3d = this.a(vec3d, enummovetype)

            val methodE = Entity::class.java.getDeclaredMethod("e", Vec3D::class.java)
            methodE.isAccessible = true

            val vec3d1 = methodE.invoke(this, vec3d) as Vec3D
            if (vec3d1.g() > 1.0E-7) {
                this.a(this.boundingBox.b(vec3d1))
                this.recalcPosition()
            }

            this.world.methodProfiler.exit()
            this.world.methodProfiler.enter("rest")
            this.positionChanged = !MathHelper.b(vec3d.x, vec3d1.x) || !MathHelper.b(vec3d.z, vec3d1.z)
            this.y = vec3d.y != vec3d1.y
            this.onGround = this.y && vec3d.y < 0.0
            this.z = this.positionChanged || this.y
            val i = MathHelper.floor(this.locX)
            val j = MathHelper.floor(this.locY - 0.20000000298023224)
            val k = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(i, j, k)
            var iblockdata = this.world.getType(blockposition)
            if (iblockdata.isAir) {
                val blockposition1 = blockposition.down()
                val iblockdata1 = this.world.getType(blockposition1)
                val block = iblockdata1.block
                if (block.a(TagsBlock.FENCES) || block.a(TagsBlock.WALLS) || block is BlockFenceGate) {
                    iblockdata = iblockdata1
                    blockposition = blockposition1
                }
            }

            this.a(vec3d1.y, this.onGround, iblockdata, blockposition)
            val vec3d2 = this.mot
            if (vec3d.x != vec3d1.x) {
                this.setMot(0.0, vec3d2.y, vec3d2.z)
            }

            if (vec3d.z != vec3d1.z) {
                this.setMot(vec3d2.x, vec3d2.y, 0.0)
            }

            val block1 = iblockdata.block
            if (vec3d.y != vec3d1.y) {
                block1.a(this.world, this)
            }

            try {
                this.checkBlockCollisions()
            } catch (var21: Throwable) {
                val crashreport = CrashReport.a(var21, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }

            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    collision = ballDesign.proxy.calculateKnockBack(sourceVector, sourceBlock, vec3d1.x, vec3d1.z, vec3d.x, vec3d1.z)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }

            this.world.methodProfiler.exit()
        }

        ballDesign.proxy.calculatePostMovement(collision)
        timingService.stopTiming()
    }

    /*
     * TODO Disable particle and sound effects
     * TODO see if slime dies in Peaceful difficulty
    override fun tick() {
        val fieldBA = this::class.java.superclass.getDeclaredField("bA")
        val bA: Boolean

        fieldBA.isAccessible = true
        bA = fieldBA.getBoolean(this)

        if (this.onGround) {
            if (!bA) {
                this.b = -0.5f
            }

            fieldBA.setBoolean(this, true)
        }

        super.tick()
    }
     */

    /**
     * Disable health.
     */
    override fun setHealth(f: Float) {}

    /**
     * Gets the bukkit entity.
     */
    override fun getBukkitEntity(): CraftHitboxSlime {
        if (this.entityBukkit == null) {
            this.entityBukkit = CraftHitboxSlime(this.world.server, this)
        }

        return this.entityBukkit as CraftHitboxSlime
    }

    private fun clearAI() {
        val dField = PathfinderGoalSelector::class.java.getDeclaredField("d")
        dField.isAccessible = true
        (dField.get(this.goalSelector) as MutableSet<*>).clear()
        (dField.get(this.targetSelector) as MutableSet<*>).clear()
        this.isSilent = true
        this.setJumping(false)
    }
}