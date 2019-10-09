@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_9_R2

import net.minecraft.server.v1_9_R2.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.util.Vector
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
class BallHitBox(private val ballDesign: BallDesign, location: Location) : EntityArmorStand((location.world as CraftWorld).handle) {
    /**
     * Initializes the hitbox.
     */
    init {
        val mcWorld = (location.world as CraftWorld).handle
        this.setPosition(location.x, location.y, location.z)
        mcWorld.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM)

        val compound = NBTTagCompound()
        compound.setBoolean("invulnerable", true)
        compound.setBoolean("Invisible", true)
        compound.setBoolean("PersistenceRequired", true)
        compound.setBoolean("NoBasePlate", true)
        this.a(compound)
    }

    /**
     * Recalculates y-axe hitbox offset in the world.
     */
    override fun recalcPosition() {
        val axisBoundingBox = this.boundingBox
        this.locX = (axisBoundingBox.a + axisBoundingBox.d) / 2.0
        this.locY = axisBoundingBox.b + ballDesign.proxy.meta.hitBoxRelocation
        this.locZ = (axisBoundingBox.c + axisBoundingBox.f) / 2.0
    }

    /**
     * Override the default entity movement.
     */
    override fun move(d0m: Double, d1m: Double, d2m: Double) {
        var collision = false
        val motionVector = Vector(motX, motY, motZ)
        val optSourceVector = ballDesign.proxy.calculateMoveSourceVectors(Vector(d0m, d1m, d2m), motionVector, this.onGround)

        if (!optSourceVector.isPresent) {
            return
        }

        val sourceVector = optSourceVector.get()

        if (sourceVector.x != d0m) {
            this.motX = motionVector.x
            this.motY = motionVector.y
            this.motZ = motionVector.z
        }


        var d0 = d0m
        var d1 = d1m
        var d2 = d2m

        if (this.noclip) {
            this.a(this.boundingBox.c(d0, d1, d2))
            this.recalcPosition()
        } else {
            try {
                this.checkBlockCollisions()
            } catch (var79: Throwable) {
                val crashreport = CrashReport.a(var79, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }


            if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.isVehicle && this.isPassenger) {
                return
            }

            this.world.methodProfiler.a("move")

            if (this.E) {
                this.E = false
                d0 *= 0.25
                d1 *= 0.05000000074505806
                d2 *= 0.25
                this.motX = 0.0
                this.motY = 0.0
                this.motZ = 0.0
            }

            val d6 = d0
            val d7 = d1
            val d8 = d2

            val list = this.world.getCubes(this, this.boundingBox.a(d0, d1, d2))
            val axisalignedbb = this.boundingBox
            var i = 0

            var j: Int
            j = list.size
            while (i < j) {
                d1 = (list[i] as AxisAlignedBB).b(this.boundingBox, d1)
                ++i
            }

            this.a(this.boundingBox.c(0.0, d1, 0.0))
            val flag1 = this.onGround || d7 != d1 && d7 < 0.0
            j = 0

            var k: Int
            k = list.size
            while (j < k) {
                d0 = (list[j] as AxisAlignedBB).a(this.boundingBox, d0)
                ++j
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0))
            j = 0

            k = list.size
            while (j < k) {
                d2 = (list[j] as AxisAlignedBB).c(this.boundingBox, d2)
                ++j
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2))
            val d13: Double
            var d10: Double

            if (this.P > 0.0f && flag1 && (d6 != d0 || d8 != d2)) {
                val d11 = d0
                val d12 = d1
                d13 = d2
                val axisalignedbb1 = this.boundingBox
                this.a(axisalignedbb)
                d1 = this.P.toDouble()
                val list1 = this.world.getCubes(this, this.boundingBox.a(d6, d1, d8))
                var axisalignedbb2 = this.boundingBox
                val axisalignedbb3 = axisalignedbb2.a(d6, 0.0, d8)
                d10 = d1
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
                var d16 = d1
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
                    d0 = d14
                    d2 = d15
                    d1 = -d10
                    this.a(axisalignedbb2)
                } else {
                    d0 = d17
                    d2 = d18
                    d1 = -d16
                    this.a(axisalignedbb4)
                }

                var l3 = 0

                val i4 = list1.size
                while (l3 < i4) {
                    d1 = (list1[l3] as AxisAlignedBB).b(this.boundingBox, d1)
                    ++l3
                }

                this.a(this.boundingBox.c(0.0, d1, 0.0))
                if (d11 * d11 + d13 * d13 >= d0 * d0 + d2 * d2) {
                    d0 = d11
                    d1 = d12
                    d2 = d13
                    this.a(axisalignedbb1)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d6 != d0 || d8 != d2
            this.B = d7 != d1
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

            this.a(d1, this.onGround, iblockdata, blockposition)
            if (d6 != d0) {
                this.motX = 0.0
            }

            if (d8 != d2) {
                this.motZ = 0.0
            }

            val block1 = iblockdata.block
            if (d7 != d1) {
                block1.a(this.world, this)
            }


            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    collision = this.ballDesign.proxy.calculateKnockBack(sourceVector, sourceBlock, d0, d2, d6, d8)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }

        }

        this.ballDesign.proxy.calculatePostMovement(collision)
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