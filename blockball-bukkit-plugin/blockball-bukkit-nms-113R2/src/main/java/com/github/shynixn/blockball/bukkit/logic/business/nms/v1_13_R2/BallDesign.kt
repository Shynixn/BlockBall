@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_13_R2

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_13_R2.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import java.util.logging.Level
import kotlin.math.abs

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

    private var internalProxy: BallProxy? = null
    private val itemService = BlockBallApi.resolve(ItemService::class.java)
    private val hitbox = BallHitBox(this, ballMeta, location)
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
                (bukkitEntity as ArmorStand).setHelmet(itemStack)
            }
            BallSize.NORMAL -> (bukkitEntity as ArmorStand).setHelmet(itemStack)
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

        this.locX = (axisBoundingBox.minX + axisBoundingBox.maxX) / 2.0

        this.locY = if (proxy.meta.size == BallSize.NORMAL) {
            axisBoundingBox.minY + proxy.meta.hitBoxRelocation - 1
        } else {
            axisBoundingBox.minY + proxy.meta.hitBoxRelocation - 0.4
        }

        this.locZ = (axisBoundingBox.minZ + axisBoundingBox.maxZ) / 2.0

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
            if (enummovetype == EnumMoveType.PISTON) {
                val i = this.world.time
                val aM = Entity::class.java.getDeclaredField("aM")
                val aL = Entity::class.java.getDeclaredField("aL")
                aM.isAccessible = true
                aL.isAccessible = true

                if (i != aM.getLong(this)) {
                    Arrays.fill(aL.get(this) as DoubleArray, 0.0)
                    aM.setLong(this, i)
                }

                val j: Int
                val d3: Double
                if (d0 != 0.0) {
                    val arr = aL.get(this) as DoubleArray
                    j = EnumDirection.EnumAxis.X.ordinal
                    d3 = MathHelper.a(d0 + arr[j], -0.51, 0.51)
                    x = d3 - arr[j]
                    arr[j] = d3
                    aL.set(this, arr)
                    if (abs(d0) <= 9.999999747378752E-6) {
                        return
                    }
                } else if (d1 != 0.0) {
                    val arr = aL.get(this) as DoubleArray
                    j = EnumDirection.EnumAxis.Y.ordinal
                    d3 = MathHelper.a(d1 + arr[j], -0.51, 0.51)
                    y = d3 - arr[j]
                    arr[j] = d3
                    aL.set(this, arr)
                    if (abs(d1) <= 9.999999747378752E-6) {
                        return
                    }
                } else {
                    if (d2 == 0.0) {
                        return
                    }

                    val arr = aL.get(this) as DoubleArray
                    j = EnumDirection.EnumAxis.Z.ordinal
                    d3 = MathHelper.a(d2 + arr[j], -0.51, 0.51)
                    z = d3 - arr[j]
                    arr[j] = d3
                    aL.set(this, arr)
                    if (abs(d2) <= 9.999999747378752E-6) {
                        return
                    }
                }
            }

            this.world.methodProfiler.enter("move")
            val d4 = this.locX
            val d5 = this.locY
            val d6 = this.locZ
            if (this.F) {
                this.F = false
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

            val axisalignedbb = this.boundingBox
            if (x != 0.0 || y != 0.0 || z != 0.0) {
                val streamaccumulator = StreamAccumulator(this.world.a(this, this.boundingBox, x, y, z))
                if (y != 0.0) {
                    y = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.boundingBox, streamaccumulator.a(), y)
                    this.a(this.boundingBox.d(0.0, y, 0.0))
                }

                if (x != 0.0) {
                    x = VoxelShapes.a(EnumDirection.EnumAxis.X, this.boundingBox, streamaccumulator.a(), x)
                    if (x != 0.0) {
                        this.a(this.boundingBox.d(x, 0.0, 0.0))
                    }
                }

                if (z != 0.0) {
                    z = VoxelShapes.a(EnumDirection.EnumAxis.Z, this.boundingBox, streamaccumulator.a(), z)
                    if (z != 0.0) {
                        this.a(this.boundingBox.d(0.0, 0.0, z))
                    }
                }
            }

            val flag = this.onGround || y != y && y < 0.0
            var d11: Double
            if (this.Q > 0.0f && flag && (d7 != x || d9 != z)) {
                val axisalignedbb1 = this.boundingBox
                this.a(axisalignedbb)
                x = d7
                y = this.Q.toDouble()
                z = d9
                if (d7 != 0.0 || y != 0.0 || d9 != 0.0) {
                    val streamaccumulator1 = StreamAccumulator(this.world.a(this, this.boundingBox, d7, y, d9))
                    var axisalignedbb2 = this.boundingBox
                    val axisalignedbb3 = axisalignedbb2.b(d7, 0.0, d9)
                    d11 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb3, streamaccumulator1.a(), y)
                    if (d11 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(0.0, d11, 0.0)
                    }

                    val d15 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb2, streamaccumulator1.a(), d7)
                    if (d15 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(d15, 0.0, 0.0)
                    }

                    val d16 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb2, streamaccumulator1.a(), d9)
                    if (d16 != 0.0) {
                        axisalignedbb2 = axisalignedbb2.d(0.0, 0.0, d16)
                    }

                    var axisalignedbb4 = this.boundingBox
                    val d17 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb4, streamaccumulator1.a(), y)
                    if (d17 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(0.0, d17, 0.0)
                    }

                    val d18 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb4, streamaccumulator1.a(), d7)
                    if (d18 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(d18, 0.0, 0.0)
                    }

                    val d19 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb4, streamaccumulator1.a(), d9)
                    if (d19 != 0.0) {
                        axisalignedbb4 = axisalignedbb4.d(0.0, 0.0, d19)
                    }

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

                    y = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.boundingBox, streamaccumulator1.a(), y)
                    if (y != 0.0) {
                        this.a(this.boundingBox.d(0.0, y, 0.0))
                    }
                }

                if (d0 * d0 + d2 * d2 >= x * x + z * z) {
                    x = d0
                    y = d1
                    z = d2
                    this.a(axisalignedbb1)
                }
            }

            this.world.methodProfiler.exit()
            this.world.methodProfiler.enter("rest")
            this.recalcPosition()
            this.positionChanged = d7 != x || d9 != z
            this.C = y != d8
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

            if (this.playStepSound() && !this.isPassenger) {
                val d22 = this.locX - d4
                var d23 = this.locY - d5
                d11 = this.locZ - d6
                if (block1 !== Blocks.LADDER) {
                    d23 = 0.0
                }

                if (block1 != null && this.onGround) {
                    block1.stepOn(this.world, blockposition, this)
                }

                this.K = (this.K.toDouble() + MathHelper.sqrt(d22 * d22 + d11 * d11).toDouble() * 0.6).toFloat()
                this.L =
                    (this.L.toDouble() + MathHelper.sqrt(d22 * d22 + d23 * d23 + d11 * d11).toDouble() * 0.6).toFloat()
                val field1 = Entity::class.java.getDeclaredField("aA")
                val field2 = Entity::class.java.getDeclaredField("aB")
                field1.isAccessible = true
                field2.isAccessible = true
                val aA = field1.getFloat(this)
                val aB = field2.getFloat(this)

                if (this.L > aA && !iblockdata.isAir) {
                    field1.setFloat(this, this.ab())
                    if (!this.isInWater) {
                        this.a(blockposition, iblockdata)
                    } else {
                        val entity = if (this.isVehicle && this.bO() != null) this.bO() else this
                        val f = if (entity === this) 0.35f else 0.4f
                        var f1 =
                            MathHelper.sqrt(entity!!.motX * entity.motX * 0.20000000298023224 + entity.motY * entity.motY + entity.motZ * entity.motZ * 0.20000000298023224) * f
                        if (f1 > 1.0f) {
                            f1 = 1.0f
                        }

                        this.d(f1)
                    }
                } else if (this.L > aB && this.ah() && iblockdata.isAir) {
                    field2.setFloat(this, this.e(this.L))
                }
            }

            try {
                this.checkBlockCollisions()
            } catch (var49: Throwable) {
                val crashreport = CrashReport.a(var49, "Checking entity block collision")
                val crashreportsystemdetails = crashreport.a("Entity being checked for collision")
                this.appendEntityCrashDetails(crashreportsystemdetails)
                throw ReportedException(crashreport)
            }

            if (this.positionChanged) {
                try {
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))
                    collision = proxy.calculateKnockBack(sourceVector, sourceBlock, x, z, d7, d9)
                } catch (e: Exception) {
                    Bukkit.getLogger().log(Level.WARNING, "Critical exception.", e)
                }
            }

            this.world.methodProfiler.exit()
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