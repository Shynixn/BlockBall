@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_12_R1

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_12_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
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
    override fun move(enummovetype: EnumMoveType?, d0: Double, d1: Double, d2: Double) {
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
            this.a(this.boundingBox.d(x, y, z))
            this.recalcPosition()
        } else {
            try {
                this.checkBlockCollisions()
            } catch (var80: Throwable) {
                val crashreport = CrashReport.a(var80, "Checking entity block collision")
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
                x *= 0.25
                y *= 0.05000000074505806
                z *= 0.25
                this.motX = 0.0
                this.motY = 0.0
                this.motZ = 0.0
            }

            val d7 = x
            val d8 = y
            val d9 = z

            val list = this.world.getCubes(this, this.boundingBox.b(x, y, z))
            val axisalignedbb = this.boundingBox
            var k: Int
            var l: Int

            if (y != 0.0) {
                k = 0

                l = list.size
                while (k < l) {
                    y = (list[k] as AxisAlignedBB).b(this.boundingBox, y)
                    ++k
                }

                this.a(this.boundingBox.d(0.0, y, 0.0))
            }

            if (x != 0.0) {
                k = 0

                l = list.size
                while (k < l) {
                    x = (list[k] as AxisAlignedBB).a(this.boundingBox, x)
                    ++k
                }

                if (x != 0.0) {
                    this.a(this.boundingBox.d(x, 0.0, 0.0))
                }
            }

            if (z != 0.0) {
                k = 0

                l = list.size
                while (k < l) {
                    z = (list[k] as AxisAlignedBB).c(this.boundingBox, z)
                    ++k
                }

                if (z != 0.0) {
                    this.a(this.boundingBox.d(0.0, 0.0, z))
                }
            }

            val flag = this.onGround || y != y && y < 0.0
            var d11: Double

            if (this.P > 0.0f && flag && (d7 != x || d9 != z)) {
                val d12 = x
                val d13 = y
                val d14 = z
                val axisalignedbb1 = this.boundingBox
                this.a(axisalignedbb)
                y = this.P.toDouble()
                val list1 = this.world.getCubes(this, this.boundingBox.b(d7, y, d9))
                var axisalignedbb2 = this.boundingBox
                val axisalignedbb3 = axisalignedbb2.b(d7, 0.0, d9)
                d11 = y
                var i1 = 0

                val j1 = list1.size
                while (i1 < j1) {
                    d11 = (list1[i1] as AxisAlignedBB).b(axisalignedbb3, d11)
                    ++i1
                }

                axisalignedbb2 = axisalignedbb2.d(0.0, d11, 0.0)
                var d15 = d7
                var k1 = 0

                val l1 = list1.size
                while (k1 < l1) {
                    d15 = (list1[k1] as AxisAlignedBB).a(axisalignedbb2, d15)
                    ++k1
                }

                axisalignedbb2 = axisalignedbb2.d(d15, 0.0, 0.0)
                var d16 = d9
                var i2 = 0

                val j2 = list1.size
                while (i2 < j2) {
                    d16 = (list1[i2] as AxisAlignedBB).c(axisalignedbb2, d16)
                    ++i2
                }

                axisalignedbb2 = axisalignedbb2.d(0.0, 0.0, d16)
                var axisalignedbb4 = this.boundingBox
                var d17 = y
                var k2 = 0

                val l2 = list1.size
                while (k2 < l2) {
                    d17 = (list1[k2] as AxisAlignedBB).b(axisalignedbb4, d17)
                    ++k2
                }

                axisalignedbb4 = axisalignedbb4.d(0.0, d17, 0.0)
                var d18 = d7
                var i3 = 0

                val j3 = list1.size
                while (i3 < j3) {
                    d18 = (list1[i3] as AxisAlignedBB).a(axisalignedbb4, d18)
                    ++i3
                }

                axisalignedbb4 = axisalignedbb4.d(d18, 0.0, 0.0)
                var d19 = d9
                var k3 = 0

                val l3 = list1.size
                while (k3 < l3) {
                    d19 = (list1[k3] as AxisAlignedBB).c(axisalignedbb4, d19)
                    ++k3
                }

                axisalignedbb4 = axisalignedbb4.d(0.0, 0.0, d19)
                val d20 = d15 * d15 + d16 * d16
                val d21 = d18 * d18 + d19 * d19

                if (d20 > d21) {
                    x = d15
                    z = d16
                    y = -d11
                    this.a(axisalignedbb2)
                } else {
                    x = d18
                    z = d19
                    y = -d17
                    this.a(axisalignedbb4)
                }

                var i4 = 0
                val j4 = list1.size

                while (i4 < j4) {
                    y = (list1[i4] as AxisAlignedBB).b(this.boundingBox, y)
                    ++i4
                }

                this.a(this.boundingBox.d(0.0, y, 0.0))

                if (d12 * d12 + d14 * d14 >= x * x + z * z) {
                    x = d12
                    y = d13
                    z = d14
                    this.a(axisalignedbb1)
                }
            }

            this.world.methodProfiler.b()
            this.world.methodProfiler.a("rest")
            this.recalcPosition()
            this.positionChanged = d7 != x || d9 != z
            this.B = y != d8
            this.onGround = this.B && d8 < 0.0
            this.C = this.positionChanged || this.B
            l = MathHelper.floor(this.locX)
            val k4 = MathHelper.floor(this.locY - 0.20000000298023224)
            val l4 = MathHelper.floor(this.locZ)
            var blockposition = BlockPosition(l, k4, l4)
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

            if (d7 != x) {
                this.motX = 0.0
            }

            if (d9 != z) {
                this.motZ = 0.0
            }

            val block1 = iblockdata.block

            if (d8 != y) {
                block1!!.a(this.world, this)
            }

            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    collision = proxy.calculateKnockBack(sourceVector, sourceBlock, x, z, d7, d9)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }

            this.world.methodProfiler.b()
        }

        proxy.calculatePostMovement(collision)
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