@file:Suppress("PackageName")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_8_R3

import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
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
    private fun recalcPosition() {
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
            } catch (var84: Throwable) {
                val crashreport = CrashReport.a(var84, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }


            if (d0 == 0.0 && d1 == 0.0 && d2 == 0.0 && this.vehicle == null && this.passenger == null) {
                return
            }

            this.world.methodProfiler.a("move")

            if (this.H) {
                this.H = false
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

            var axisalignedbb1: AxisAlignedBB
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                axisalignedbb1 = iterator.next() as AxisAlignedBB
                d1 = axisalignedbb1.b(this.boundingBox, d1)
            }

            this.a(this.boundingBox.c(0.0, d1, 0.0))
            val flag1 = this.onGround || d7 != d1 && d7 < 0.0

            var iterator1: Iterator<*>
            var axisalignedbb2: AxisAlignedBB
            iterator1 = list.iterator()
            while (iterator1.hasNext()) {
                axisalignedbb2 = iterator1.next() as AxisAlignedBB
                d0 = axisalignedbb2.a(this.boundingBox, d0)
            }

            this.a(this.boundingBox.c(d0, 0.0, 0.0))

            iterator1 = list.iterator()
            while (iterator1.hasNext()) {
                axisalignedbb2 = iterator1.next() as AxisAlignedBB
                d2 = axisalignedbb2.c(this.boundingBox, d2)
            }

            this.a(this.boundingBox.c(0.0, 0.0, d2))
            if (this.S > 0.0f && flag1 && (d6 != d0 || d8 != d2)) {
                val d10 = d0
                val d11 = d1
                val d12 = d2
                val axisalignedbb3 = this.boundingBox
                this.a(axisalignedbb)
                d1 = this.S.toDouble()
                val list1 = this.world.getCubes(this, this.boundingBox.a(d6, d1, d8))
                var axisalignedbb4 = this.boundingBox
                val axisalignedbb5 = axisalignedbb4.a(d6, 0.0, d8)
                var d13 = d1

                var axisalignedbb6: AxisAlignedBB
                val iterator2 = list1.iterator()
                while (iterator2.hasNext()) {
                    axisalignedbb6 = iterator2.next() as AxisAlignedBB
                    d13 = axisalignedbb6.b(axisalignedbb5, d13)
                }

                axisalignedbb4 = axisalignedbb4.c(0.0, d13, 0.0)
                var d14 = d6

                var axisalignedbb7: AxisAlignedBB
                val iterator3 = list1.iterator()
                while (iterator3.hasNext()) {
                    axisalignedbb7 = iterator3.next() as AxisAlignedBB
                    d14 = axisalignedbb7.a(axisalignedbb4, d14)
                }

                axisalignedbb4 = axisalignedbb4.c(d14, 0.0, 0.0)
                var d15 = d8

                var axisalignedbb8: AxisAlignedBB
                val iterator4 = list1.iterator()
                while (iterator4.hasNext()) {
                    axisalignedbb8 = iterator4.next() as AxisAlignedBB
                    d15 = axisalignedbb8.c(axisalignedbb4, d15)
                }

                axisalignedbb4 = axisalignedbb4.c(0.0, 0.0, d15)
                var axisalignedbb9 = this.boundingBox
                var d16 = d1

                var axisalignedbb10: AxisAlignedBB
                val iterator5 = list1.iterator()
                while (iterator5.hasNext()) {
                    axisalignedbb10 = iterator5.next() as AxisAlignedBB
                    d16 = axisalignedbb10.b(axisalignedbb9, d16)
                }

                axisalignedbb9 = axisalignedbb9.c(0.0, d16, 0.0)
                var d17 = d6

                var axisalignedbb11: AxisAlignedBB
                val iterator6 = list1.iterator()
                while (iterator6.hasNext()) {
                    axisalignedbb11 = iterator6.next() as AxisAlignedBB
                    d17 = axisalignedbb11.a(axisalignedbb9, d17)
                }

                axisalignedbb9 = axisalignedbb9.c(d17, 0.0, 0.0)
                var d18 = d8

                var axisalignedbb12: AxisAlignedBB
                val iterator7 = list1.iterator()
                while (iterator7.hasNext()) {
                    axisalignedbb12 = iterator7.next() as AxisAlignedBB
                    d18 = axisalignedbb12.c(axisalignedbb9, d18)
                }

                axisalignedbb9 = axisalignedbb9.c(0.0, 0.0, d18)
                val d19 = d14 * d14 + d15 * d15
                val d20 = d17 * d17 + d18 * d18
                if (d19 > d20) {
                    d0 = d14
                    d2 = d15
                    d1 = -d13
                    this.a(axisalignedbb4)
                } else {
                    d0 = d17
                    d2 = d18
                    d1 = -d16
                    this.a(axisalignedbb9)
                }

                var axisalignedbb13: AxisAlignedBB
                val iterator8 = list1.iterator()
                while (iterator8.hasNext()) {
                    axisalignedbb13 = iterator8.next() as AxisAlignedBB
                    d1 = axisalignedbb13.b(this.boundingBox, d1)
                }

                this.a(this.boundingBox.c(0.0, d1, 0.0))
                if (d10 * d10 + d12 * d12 >= d0 * d0 + d2 * d2) {
                    d0 = d10
                    d1 = d11
                    d2 = d12
                    this.a(axisalignedbb3)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d6 != d0 || d8 != d2
            this.E = d7 != d1
            this.onGround = this.E && d7 < 0.0
            this.F = this.positionChanged || this.E
            val i = MathHelper.floor(this.locX)
            val j = MathHelper.floor(this.locY - 0.20000000298023224)
            val k = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(i, j, k)
            var block: net.minecraft.server.v1_8_R3.Block = this.world.getType(blockposition).block
            if (block.material === Material.AIR) {
                val block1 = this.world.getType(blockposition.down()).block
                if (block1 is BlockFence || block1 is BlockCobbleWall || block1 is BlockFenceGate) {
                    block = block1
                    blockposition = blockposition.down()
                }
            }

            this.a(d1, this.onGround, block, blockposition)
            if (d6 != d0) {
                this.motX = 0.0
            }

            if (d8 != d2) {
                this.motZ = 0.0
            }

            if (d7 != d1) {
                block.a(this.world, this)
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