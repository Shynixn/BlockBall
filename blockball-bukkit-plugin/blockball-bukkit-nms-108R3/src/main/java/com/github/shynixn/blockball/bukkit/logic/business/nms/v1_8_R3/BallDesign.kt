@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_8_R3

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.SpigotTimingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Slime
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
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
class BallDesign(location: Location, ballMeta: BallMeta, persistent: Boolean, uuid: UUID = UUID.randomUUID(), owner: LivingEntity?) :
    EntityArmorStand((location.world as CraftWorld).handle), NMSBallProxy {

    private val itemService = BlockBallApi.resolve(ItemService::class.java)
    private val hitbox = BallHitBox(this, ballMeta, location)
    private var internalProxy: BallProxy? = null
    private val timingService = BlockBallApi.resolve(SpigotTimingService::class.java)

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
                ArmorStand::class.java,
                Slime::class.java,
                UUID::class.java,
                LivingEntity::class.java,
                Boolean::class.java
            )
            .newInstance(ballMeta, this.getBukkitEntity() as ArmorStand, hitbox.bukkitEntity as Slime, uuid, owner, persistent) as BallProxy

        val compound = NBTTagCompound()
        compound.setBoolean("invulnerable", true)
        compound.setBoolean("Invisible", true)
        compound.setBoolean("PersistenceRequired", true)
        compound.setBoolean("NoBasePlate", true)
        this.a(compound)

        val itemStack = itemService.createItemStack<ItemStack>(MaterialType.SKULL_ITEM, 3)
        itemService.setSkin(itemStack, proxy.meta.skin)

        when (proxy.meta.size) {
            BallSize.SMALL -> {
                (bukkitEntity as ArmorStand).isSmall = true
                (bukkitEntity as ArmorStand).helmet = itemStack
            }
            BallSize.NORMAL -> (bukkitEntity as ArmorStand).helmet = itemStack
        }
    }

    /**
     * Update the yaw rotation.
     */
    override fun doTick() {
        super.doTick()

        if (this.passenger != null || this.getBukkitEntity().vehicle != null) {
            return
        }

        if (proxy.yawChange > 0) {
            this.hitbox.yaw = proxy.yawChange
            proxy.yawChange = -1.0F
        }

        proxy.run()
    }

    /**
     * Recalculates y-axe design offset in the world.
     */
    fun recalcPosition() {
        val axisBoundingBox = this.boundingBox
        this.locX = (axisBoundingBox.a + axisBoundingBox.d) / 2.0

        this.locY = if (proxy.meta.size == BallSize.NORMAL) {
            axisBoundingBox.b + proxy.meta.hitBoxRelocation - 1
        } else {
            axisBoundingBox.b + proxy.meta.hitBoxRelocation - 0.4
        }

        this.locZ = (axisBoundingBox.c + axisBoundingBox.f) / 2.0
    }

    /**
     * Override the default entity movement.
     */
    override fun move(d0: Double, d1: Double, d2: Double) {
        var x = d0
        var y = d1
        var z = d2
        var collision = false
        val motionVector = Vector(this.motX, this.motY, this.motZ)
        val optSourceVector = proxy.calculateMoveSourceVectors(Vector(x, y, z), motionVector, this.onGround)

        if (!optSourceVector.isPresent) {
            return
        }

        val sourceVector = optSourceVector.get()

        if (sourceVector.x != x) {
            motX = motionVector.x
            motY = motionVector.y
            motZ = motionVector.z
        }

        timingService.startTiming()

        if (this.noclip) {
            this.a(this.boundingBox.c(x, y, z))
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

            if (x == 0.0 && y == 0.0 && z == 0.0 && this.vehicle == null && this.passenger == null) {
                return
            }

            this.world.methodProfiler.a("move")
            if (this.H) {
                this.H = false
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

            var axisalignedbb1: AxisAlignedBB
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                axisalignedbb1 = iterator.next() as AxisAlignedBB
                y = axisalignedbb1.b(this.boundingBox, y)
            }

            this.a(this.boundingBox.c(0.0, y, 0.0))
            val flag1 = this.onGround || y != y && y < 0.0

            var iterator1: Iterator<*>
            var axisalignedbb2: AxisAlignedBB
            iterator1 = list.iterator()
            while (iterator1.hasNext()) {
                axisalignedbb2 = iterator1.next() as AxisAlignedBB
                x = axisalignedbb2.a(this.boundingBox, x)
            }

            this.a(this.boundingBox.c(x, 0.0, 0.0))

            iterator1 = list.iterator()
            while (iterator1.hasNext()) {
                axisalignedbb2 = iterator1.next() as AxisAlignedBB
                z = axisalignedbb2.c(this.boundingBox, z)
            }

            this.a(this.boundingBox.c(0.0, 0.0, z))
            if (this.S > 0.0f && flag1 && (d6 != x || d8 != z)) {
                val d10 = x
                val d11 = y
                val d12 = z
                val axisalignedbb3 = this.boundingBox
                this.a(axisalignedbb)
                y = this.S.toDouble()
                val list1 = this.world.getCubes(this, this.boundingBox.a(d6, y, d8))
                var axisalignedbb4 = this.boundingBox
                val axisalignedbb5 = axisalignedbb4.a(d6, 0.0, d8)
                var d13 = y

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
                var d16 = y

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
                    x = d14
                    z = d15
                    y = -d13
                    this.a(axisalignedbb4)
                } else {
                    x = d17
                    z = d18
                    y = -d16
                    this.a(axisalignedbb9)
                }

                var axisalignedbb13: AxisAlignedBB
                val iterator8 = list1.iterator()
                while (iterator8.hasNext()) {
                    axisalignedbb13 = iterator8.next() as AxisAlignedBB
                    y = axisalignedbb13.b(this.boundingBox, y)
                }

                this.a(this.boundingBox.c(0.0, y, 0.0))
                if (d10 * d10 + d12 * d12 >= x * x + z * z) {
                    x = d10
                    y = d11
                    z = d12
                    this.a(axisalignedbb3)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d6 != x || d8 != z
            this.E = d7 != y
            this.onGround = this.E && d7 < 0.0
            this.F = this.positionChanged || this.E
            val i = MathHelper.floor(this.locX)
            val j = MathHelper.floor(this.locY - 0.20000000298023224)
            val k = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(i, j, k)
            var block: Block? = this.world.getType(blockposition).block
            if (block!!.material === Material.AIR) {
                val block1 = this.world.getType(blockposition.down()).block
                if (block1 is BlockFence || block1 is BlockCobbleWall || block1 is BlockFenceGate) {
                    block = block1
                    blockposition = blockposition.down()
                }
            }

            this.a(y, this.onGround, block, blockposition)
            if (d6 != x) {
                this.motX = 0.0
            }

            if (d8 != z) {
                this.motZ = 0.0
            }

            if (d7 != y) {
                block!!.a(this.world, this)
            }

            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    collision = proxy.calculateKnockBack(sourceVector, sourceBlock, x, z, d6, d8)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }

            this.world.methodProfiler.b()
        }

        proxy.calculatePostMovement(collision)
        timingService.stopTiming()
    }

    /**
     * Gets the bukkit entity.
     */
    override fun getBukkitEntity(): CraftDesignArmorstand {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = CraftDesignArmorstand(this.world.server, this)
        }

        return this.bukkitEntity as CraftDesignArmorstand
    }
}