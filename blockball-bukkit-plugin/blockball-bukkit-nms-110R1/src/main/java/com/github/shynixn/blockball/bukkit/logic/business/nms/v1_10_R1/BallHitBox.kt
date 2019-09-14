@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_10_R1

import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_10_R1.EntitySlime
import net.minecraft.server.v1_10_R1.NBTTagCompound
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityTeleport
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer
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

        val entity = getBukkitEntity()
        entity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false))
        entity.isCollidable = false
        entity.isSilent = true
        entity.setGravity(false)
    }

    /**
     * Overridden onTick()
     * 1. Prevent slime from dying in Peaceful difficulty
     * 2. Constantly move hitbox to overlap with ballDesign
     */
    override fun m() {
        super.m()

        val loc = ballDesign.bukkitEntity.location
        if (ballDesign.isSmall) {
            this.setPositionRotation(loc.x, loc.y + 1.05, loc.z, loc.yaw, loc.pitch)
        } else {
            this.setPositionRotation(loc.x, loc.y + 1.05, loc.z, loc.yaw, loc.pitch)
        }

        val packet = PacketPlayOutEntityTeleport(this)
        this.world.players.forEach{p -> (p.bukkitEntity as CraftPlayer).handle.playerConnection.sendPacket(packet)}
    }

    /*
    override fun checkBlockCollisions() {

    }

    override fun move(d0: Double, d1: Double, d2: Double) {
        var x = d0
        var y = d1
        var z = d2

        SpigotTimings.entityMoveTimer.startTiming()

        if (this.noclip) {
            this.a(this.boundingBox.c(x, y, z))
            this.recalcPosition()
        } else {
            try {
                this.checkBlockCollisions()
            } catch (var77: Throwable) {
                val crashreport = CrashReport.a(var77, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }

            if (x == 0.0 && y == 0.0 && z == 0.0 && this.isVehicle && this.isPassenger) {
                return
            }

            this.world.methodProfiler.a("move")
            val d3 = this.locX
            val d4 = this.locY
            val d5 = this.locZ
            if (this.E) {
                this.E = false
                x *= 0.25
                y *= 0.05000000074505806
                z *= 0.25
                this.motX = 0.0
                this.motY = 0.0
                this.motZ = 0.0
            }

            val d6 = x
            val d7 = y
            val d8 = z

            val list = this.world.getCubes(this, this.boundingBox.a(x, y, z))
            val axisalignedbb = this.boundingBox
            var i = 0

            var j: Int
            j = list.size
            while (i < j) {
                y = (list[i] as AxisAlignedBB).b(this.boundingBox, y)
                ++i
            }

            this.a(this.boundingBox.c(0.0, y, 0.0))
            val flag1 = this.onGround || y != y && y < 0.0
            j = 0

            var k: Int
            k = list.size
            while (j < k) {
                x = (list[j] as AxisAlignedBB).a(this.boundingBox, x)
                ++j
            }

            this.a(this.boundingBox.c(x, 0.0, 0.0))
            j = 0

            k = list.size
            while (j < k) {
                z = (list[j] as AxisAlignedBB).c(this.boundingBox, z)
                ++j
            }

            this.a(this.boundingBox.c(0.0, 0.0, z))
            var d13: Double
            var d10: Double
            if (this.P > 0.0f && flag1 && (d6 != x || d8 != z)) {
                d13 = z
                val axisalignedbb1 = this.boundingBox
                this.a(axisalignedbb)
                y = this.P.toDouble()
                val list1 = this.world.getCubes(this, this.boundingBox.a(d6, y, d8))
                var axisalignedbb2 = this.boundingBox
                val axisalignedbb3 = axisalignedbb2.a(d6, 0.0, d8)
                d10 = y
                var l = 0

                val i1 = list1.size
                while (l < i1) {
                    d10 = (list1[l] as AxisAlignedBB).b(axisalignedbb3, d10)
                    ++l
                }

                axisalignedbb2 = axisalignedbb2.c(0.0, d10, 0.0)
                var d14 = d6
                var j1 = 0

                val k1 = list1.size
                while (j1 < k1) {
                    d14 = (list1[j1] as AxisAlignedBB).a(axisalignedbb2, d14)
                    ++j1
                }

                axisalignedbb2 = axisalignedbb2.c(d14, 0.0, 0.0)
                var d15 = d8
                var l1 = 0

                val i2 = list1.size
                while (l1 < i2) {
                    d15 = (list1[l1] as AxisAlignedBB).c(axisalignedbb2, d15)
                    ++l1
                }

                axisalignedbb2 = axisalignedbb2.c(0.0, 0.0, d15)
                var axisalignedbb4 = this.boundingBox
                var d16 = y
                var j2 = 0

                val k2 = list1.size
                while (j2 < k2) {
                    d16 = (list1[j2] as AxisAlignedBB).b(axisalignedbb4, d16)
                    ++j2
                }

                axisalignedbb4 = axisalignedbb4.c(0.0, d16, 0.0)
                var d17 = d6
                var l2 = 0

                val i3 = list1.size
                while (l2 < i3) {
                    d17 = (list1[l2] as AxisAlignedBB).a(axisalignedbb4, d17)
                    ++l2
                }

                axisalignedbb4 = axisalignedbb4.c(d17, 0.0, 0.0)
                var d18 = d8
                var j3 = 0

                val k3 = list1.size
                while (j3 < k3) {
                    d18 = (list1[j3] as AxisAlignedBB).c(axisalignedbb4, d18)
                    ++j3
                }

                axisalignedbb4 = axisalignedbb4.c(0.0, 0.0, d18)
                val d19 = d14 * d14 + d15 * d15
                val d20 = d17 * d17 + d18 * d18
                if (d19 > d20) {
                    x = d14
                    z = d15
                    y = -d10
                    this.a(axisalignedbb2)
                } else {
                    x = d17
                    z = d18
                    y = -d16
                    this.a(axisalignedbb4)
                }

                var l3 = 0

                val i4 = list1.size
                while (l3 < i4) {
                    y = (list1[l3] as AxisAlignedBB).b(this.boundingBox, y)
                    ++l3
                }

                this.a(this.boundingBox.c(0.0, y, 0.0))
                if (x * x + d13 * d13 >= x * x + z * z) {
                    x = x
                    y = y
                    z = d13
                    this.a(axisalignedbb1)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d6 != x || d8 != z
            this.B = d7 != y
            this.onGround = this.B && d7 < 0.0
            this.C = this.positionChanged || this.B
            j = MathHelper.floor(this.locX)
            k = MathHelper.floor(this.locY - 0.20000000298023224)
            val j4 = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(j, k, j4)
            var iblockdata = this.world.getType(blockposition)
            if (iblockdata.material === Material.AIR) {
                val blockposition1 = blockposition.down()
                val iblockdata1 = this.world.getType(blockposition1)
                val block = iblockdata1.block
                if (block is BlockFence || block is BlockCobbleWall || block is BlockFenceGate) {
                    iblockdata = iblockdata1
                    blockposition = blockposition1
                }
            }

            this.a(y, this.onGround, iblockdata, blockposition)
            if (d6 != x) {
                this.motX = 0.0
            }

            if (d8 != z) {
                this.motZ = 0.0
            }

            val block1 = iblockdata.block
            if (d7 != y) {
                block1!!.a(this.world, this)
            }

            this.world.methodProfiler.b()
        }

        SpigotTimings.entityMoveTimer.stopTiming()
    }
     */

    /**
     * Gets the bukkit entity.
     */
    override fun getBukkitEntity(): CraftHitboxSlime {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftHitboxSlime(this.world.server, this)
        }

        return this.bukkitEntity as CraftHitboxSlime
    }

    @Deprecated("Failed attempt to resolve particles")
    private fun clearPathfinders() {
        val bField = PathfinderGoalSelector::class.java.getDeclaredField("b")
        val cField = PathfinderGoalSelector::class.java.getDeclaredField("c")
        bField.isAccessible = true
        cField.isAccessible = true
        bField.set(this.goalSelector, HashSet<PathfinderGoalSelector>())
        bField.set(this.targetSelector, HashSet<PathfinderGoalSelector>())
        cField.set(this.goalSelector, HashSet<PathfinderGoalSelector>())
        cField.set(this.targetSelector, HashSet<PathfinderGoalSelector>())
    }
}