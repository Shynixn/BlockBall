@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_10_R1

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_10_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld
import org.bukkit.craftbukkit.v1_10_R1.SpigotTimings
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
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
            .newInstance(ballMeta, this.getBukkitEntity() as LivingEntity, hitbox.bukkitEntity as LivingEntity, uuid, owner, persistent) as BallProxy

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

        updatePosition()
        debugPosition()
    }

    /**
     * Update the yaw rotation.
     */
    override fun doTick() {
        super.doTick()

        if (!(this.passengers == null || this.passengers.isEmpty()) || this.getBukkitEntity().vehicle != null) {
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
    override fun recalcPosition() {
        val axisBoundingBox = this.boundingBox

        this.locX = (axisBoundingBox.a + axisBoundingBox.d) / 2.0

        this.locY = if (proxy.meta.size == BallSize.NORMAL) {
            axisBoundingBox.b + proxy.meta.hitBoxRelocation - 1
        } else {
            axisBoundingBox.b + proxy.meta.hitBoxRelocation - 0.4
        }

        this.locZ = (axisBoundingBox.c + axisBoundingBox.f) / 2.0

        if (!locX.equals(lastX) || !locY.equals(lastY) || !locZ.equals(lastZ)) {
            debugPosition()
        }
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

            this.world.methodProfiler.a("move")

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
            val flag1 = this.onGround || d7 != y && d7 < 0.0
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
            val d13: Double
            var d10: Double

            if (this.P > 0.0f && flag1 && (d6 != x || d8 != z)) {
                val d11 = x
                val d12 = y
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

                if (d11 * d11 + d13 * d13 >= x * x + z * z) {
                    x = d11
                    y = d12
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
        SpigotTimings.entityMoveTimer.stopTiming()
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

    /**
     * Updates the position of the entity manually.
     */
    private fun updatePosition() {
        val packet = PacketPlayOutEntityTeleport(this)
        this.world.players.forEach { p -> (p.bukkitEntity as CraftPlayer).handle.playerConnection.sendPacket(packet) }
    }

    /**
     * Prints a debugging message for this entity.
     */
    private fun debugPosition() {
        val loc = getBukkitEntity().location
        BlockBallApi.resolve(LoggingService::class.java).debug("Design at ${loc.x.toFloat()} ${loc.y.toFloat()} ${loc.z.toFloat()}")
    }
}