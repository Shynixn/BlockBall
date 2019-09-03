@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_12_R1

import com.github.shynixn.blockball.api.business.service.SpigotTimingService
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
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
class BallHitBox(private val ballDesign: BallDesign, location: Location, private val timingService: SpigotTimingService) : EntityArmorStand((location.world as CraftWorld).handle) {
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
    override fun move(enummovetype: EnumMoveType, d0m: Double, d1m: Double, d2m: Double) {
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

        timingService.startTiming()

        var d0 = d0m
        var d1 = d1m
        var d2 = d2m

        if (this.noclip) {
            this.a(this.boundingBox.d(d0, d1, d2))
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
            if (this.E) { //Changing
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
            val list = this.world.getCubes(this, this.boundingBox.b(d0, d1, d2))
            val axisalignedbb = this.boundingBox
            var i: Int
            var j: Int
            if (d1 != 0.0) {
                i = 0

                j = list.size
                while (i < j) {
                    d1 = (list[i] as AxisAlignedBB).b(this.boundingBox, d1)
                    ++i
                }

                this.a(this.boundingBox.d(0.0, d1, 0.0))
            }

            if (d0 != 0.0) {
                i = 0

                j = list.size
                while (i < j) {
                    d0 = (list[i] as AxisAlignedBB).a(this.boundingBox, d0)
                    ++i
                }

                if (d0 != 0.0) {
                    this.a(this.boundingBox.d(d0, 0.0, 0.0))
                }
            }

            if (d2 != 0.0) {
                i = 0

                j = list.size
                while (i < j) {
                    d2 = (list[i] as AxisAlignedBB).c(this.boundingBox, d2)
                    ++i
                }

                if (d2 != 0.0) {
                    this.a(this.boundingBox.d(0.0, 0.0, d2))
                }
            }

            val flag1 = this.onGround || d1 != d7 && d1 < 0.0
            var d10: Double

            if (this.P > 0.0f && flag1 && (d6 != d0 || d8 != d2)) {
                val d11 = d0
                val d12 = d1
                val d13 = d2
                val event = this.boundingBox
                this.a(axisalignedbb)
                d1 = this.P.toDouble()
                val event1 = this.world.getCubes(this, this.boundingBox.b(d6, d1, d8))
                var axisalignedbb2 = this.boundingBox
                val f = axisalignedbb2.b(d6, 0.0, d8)
                d10 = d1
                var k = 0

                val l = event1.size
                while (k < l) {
                    d10 = (event1[k] as AxisAlignedBB).b(f, d10)
                    ++k
                }

                axisalignedbb2 = axisalignedbb2.d(0.0, d10, 0.0)
                var d14 = d6
                var i1 = 0

                val j1 = event1.size
                while (i1 < j1) {
                    d14 = (event1[i1] as AxisAlignedBB).a(axisalignedbb2, d14)
                    ++i1
                }

                axisalignedbb2 = axisalignedbb2.d(d14, 0.0, 0.0)
                var d15 = d8
                var k1 = 0

                val axisalignedbb4 = event1.size
                while (k1 < axisalignedbb4) {
                    d15 = (event1[k1] as AxisAlignedBB).c(axisalignedbb2, d15)
                    ++k1
                }

                axisalignedbb2 = axisalignedbb2.d(0.0, 0.0, d15)  //Changing
                var var85 = this.boundingBox
                var d16 = d1
                var i2 = 0

                val j2 = event1.size
                while (i2 < j2) {
                    d16 = (event1[i2] as AxisAlignedBB).b(var85, d16)
                    ++i2
                }

                var85 = var85.d(0.0, d16, 0.0)
                var d17 = d6
                var k2 = 0

                val l2 = event1.size
                while (k2 < l2) {
                    d17 = (event1[k2] as AxisAlignedBB).a(var85, d17)
                    ++k2
                }

                var85 = var85.d(d17, 0.0, 0.0)
                var d18 = d8
                var i3 = 0

                val j3 = event1.size
                while (i3 < j3) {
                    d18 = (event1[i3] as AxisAlignedBB).c(var85, d18)
                    ++i3
                }

                var85 = var85.d(0.0, 0.0, d18)
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
                    this.a(var85)
                }

                var k3 = 0

                val l3 = event1.size
                while (k3 < l3) {
                    d1 = (event1[k3] as AxisAlignedBB).b(this.boundingBox, d1)
                    ++k3
                }

                this.a(this.boundingBox.d(0.0, d1, 0.0))
                if (d11 * d11 + d13 * d13 >= d0 * d0 + d2 * d2) {
                    d0 = d11
                    d1 = d12
                    d2 = d13
                    this.a(event)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d6 != d0 || d8 != d2
            this.B = d1 != d7
            this.onGround = this.B && d7 < 0.0
            this.C = this.positionChanged || this.B
            j = MathHelper.floor(this.locX)
            val i4 = MathHelper.floor(this.locY - 0.20000000298023224)
            val j4 = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(j, i4, j4)
            var iblockdata = this.world.getType(blockposition)
            if (iblockdata.material === Material.AIR) {
                val block1 = blockposition.down()
                val flag2 = this.world.getType(block1)
                val var80 = flag2.block
                if (var80 is BlockFence || var80 is BlockCobbleWall || var80 is BlockFenceGate) {
                    iblockdata = flag2
                    blockposition = block1
                }
            }

            this.a(d1, this.onGround, iblockdata, blockposition)
            if (d6 != d0) {
                this.motX = 0.0
            }

            if (d8 != d2) {
                this.motZ = 0.0
            }

            val var86 = iblockdata.block
            if (d7 != d1) {
                var86.a(this.world, this)
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
        timingService.stopTiming()
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