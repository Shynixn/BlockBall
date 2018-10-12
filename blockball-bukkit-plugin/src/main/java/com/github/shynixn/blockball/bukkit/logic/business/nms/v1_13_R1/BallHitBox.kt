@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_13_R1

import com.github.shynixn.blockball.api.business.service.SpigotTimingService
import net.minecraft.server.v1_13_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld
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
            } catch (var49: Throwable) {
                val crashreport = CrashReport.a(var49, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }

            this.world.methodProfiler.a("move")

            if (this.F) {
                this.F = false
                d0 *= 0.25
                d1 *= 0.05000000074505806
                d2 *= 0.25
                this.motX = 0.0
                this.motY = 0.0
                this.motZ = 0.0
            }

            val d7 = d0
            val d8 = d1
            val d9 = d2

            val axisalignedbb = this.boundingBox
            if (d0 != 0.0 || d1 != 0.0 || d2 != 0.0) {
                val voxelshape = this.world.a(this, this.boundingBox, d0, d1, d2)
                if (d1 != 0.0) {
                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.boundingBox, voxelshape, d1)
                    this.a(this.boundingBox.d(0.0, d1, 0.0))
                }

                if (d0 != 0.0) {
                    d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, this.boundingBox, voxelshape, d0)
                    if (d0 != 0.0) {
                        this.a(this.boundingBox.d(d0, 0.0, 0.0))
                    }
                }

                if (d2 != 0.0) {
                    d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, this.boundingBox, voxelshape, d2)
                    if (d2 != 0.0) {
                        this.a(this.boundingBox.d(0.0, 0.0, d2))
                    }
                }
            }

            val flag = this.onGround || d1 != d1 && d1 < 0.0
            val d11: Double
            if (this.Q > 0.0f && flag && (d7 != d0 || d9 != d2)) {
                val d12 = d0
                val d13 = d1
                val d14 = d2
                val axisalignedbb1 = this.boundingBox
                this.a(axisalignedbb)
                d0 = d7
                d1 = this.Q.toDouble()
                d2 = d9
                if (d7 != 0.0 || d1 != 0.0 || d9 != 0.0) {
                    val voxelshape1 = this.world.a(this, this.boundingBox, d7, d1, d9)
                    var axisalignedbb2 = this.boundingBox
                    val axisalignedbb3 = axisalignedbb2.b(d7, 0.0, d9)
                    d11 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb3, voxelshape1, d1)
                    if (d11 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(0.0, d11, 0.0)
                    }

                    val d15 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb2, voxelshape1, d7)
                    if (d15 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(d15, 0.0, 0.0)
                    }

                    val d16 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb2, voxelshape1, d9)
                    if (d16 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(0.0, 0.0, d16)
                    }

                    var axisalignedbb4 = this.boundingBox
                    val d17 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb4, voxelshape1, d1)
                    if (d17 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(0.0, d17, 0.0)
                    }

                    val d18 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb4, voxelshape1, d7)
                    if (d18 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(d18, 0.0, 0.0)
                    }

                    val d19 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb4, voxelshape1, d9)
                    if (d19 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(0.0, 0.0, d19)
                    }

                    val d20 = d15 * d15 + d16 * d16
                    val d21 = d18 * d18 + d19 * d19
                    if (d20 > d21) {
                        d0 = d15
                        d2 = d16
                        d1 = -d11
                        this.a(axisalignedbb2)
                    } else {
                        d0 = d18
                        d2 = d19
                        d1 = -d17
                        this.a(axisalignedbb4)
                    }

                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.boundingBox, voxelshape1, d1)
                    if (d1 != 0.0) {
                        this.a(this.boundingBox.d(0.0, d1, 0.0))
                    }
                }

                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12
                    d1 = d13
                    d2 = d14
                    this.a(axisalignedbb1)
                }
            }

            this.world.methodProfiler.e()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d7 != d0 || d9 != d2
            this.C = d1 != d8
            this.onGround = this.C && d8 < 0.0
            this.D = this.positionChanged || this.C
            val k = MathHelper.floor(this.locX)
            val l = MathHelper.floor(this.locY - 0.20000000298023224)
            val i1 = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(k, l, i1)
            var iblockdata = this.world.getType(blockposition)
            if (iblockdata.isAir) {
                val blockposition1 = blockposition.down()
                val iblockdata1 = this.world.getType(blockposition1)
                val block = iblockdata1.block
                if (block is BlockFence || block is BlockCobbleWall || block is BlockFenceGate) {
                    iblockdata = iblockdata1
                    blockposition = blockposition1
                }
            }

            this.a(d1, this.onGround, iblockdata, blockposition)
            if (d7 != d0) {
                this.motX = 0.0
            }

            if (d9 != d2) {
                this.motZ = 0.0
            }

            val block1 = iblockdata.block
            if (d8 != d1) {
                block1.a(this.world, this)
            }

            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    this.ballDesign.proxy.calculateKnockBack(sourceVector, sourceBlock, d0, d2, d7, d9)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }
        }

        this.ballDesign.proxy.calculatePostMovement()
        timingService.stopTiming()
    }
}