@file:Suppress("unused")

package com.github.shynixn.blockball.bukkit.logic.business.nms.v1_15_R1

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import net.minecraft.server.v1_15_R1.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import java.util.logging.Level

/**
 * Armorstand implementation for displaying.
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
class BallDesign(location: Location, ballMeta: BallMeta, persistent: Boolean, uuid: UUID = UUID.randomUUID(), owner: LivingEntity?) :
    EntityArmorStand((location.world as CraftWorld).handle, location.x, location.y, location.z), NMSBallProxy {

    private var internalProxy: BallProxy? = null
    private var entityBukkit: Any? = null // BukkitEntity has to be self cached since 1.14.
    private val itemService = BlockBallApi.resolve(ItemService::class.java)
    private val hitBox = BallHitBox(this, ballMeta, location)
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
            .newInstance(ballMeta, this.bukkitEntity as LivingEntity, hitBox.bukkitEntity as LivingEntity, uuid, owner, persistent) as BallProxy

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
            BallSize.NORMAL -> {
                (bukkitEntity as ArmorStand).setHelmet(itemStack)
            }
        }

        updatePosition()
        debugPosition()
    }

    /**
     * Update the yaw rotation.
     */
    override fun doTick() {
        super.doTick()

        if (!(this.passengers == null || this.passengers.isEmpty()) || this.bukkitEntity.vehicle != null) {
            return
        }

        if (proxy.yawChange > 0) {
            this.hitBox.yaw = proxy.yawChange
            proxy.yawChange = -1.0F
        }

        proxy.run()
    }

    /**
     * Recalculates y-axe design offset in the world.
     */
    override fun recalcPosition() {
        val axisBoundingBox = this.boundingBox
        val locX = (axisBoundingBox.minX + axisBoundingBox.maxX) / 2.0

        val locY = if (proxy.meta.size == BallSize.NORMAL) {
            axisBoundingBox.minY + proxy.meta.hitBoxRelocation - 1
        } else {
            axisBoundingBox.minY + proxy.meta.hitBoxRelocation - 0.4
        }

        val locZ = (axisBoundingBox.minZ + axisBoundingBox.maxZ) / 2.0

        if (!locX.equals(lastX) || !locY.equals(lastY) || !locZ.equals(lastZ)) {
            debugPosition()
        }

        setPositionRaw(locX, locY, locZ)
    }

    /**
     * Override the default entity movement.
     */
    override fun move(enummovetype: EnumMoveType, vec3dmp: Vec3D) {
        var vec3d = vec3dmp
        var collision = false
        val motionVector = Vector(this.mot.x, this.mot.y, this.mot.z)
        val optSourceVector = proxy.calculateMoveSourceVectors(Vector(vec3d.x, vec3d.y, vec3d.z), motionVector, this.onGround)

        if (!optSourceVector.isPresent) {
            return
        }

        val sourceVector = optSourceVector.get()

        if (sourceVector.x != vec3d.x) {
            this.setMot(motionVector.x, motionVector.y, motionVector.z)
        }

        if (this.noclip) {
            this.a(this.boundingBox.b(vec3d))
            this.recalcPosition()
        } else {
            this.world.methodProfiler.enter("move")
            if (this.y.g() > 1.0E-7) {
                vec3d = vec3d.h(this.y)
                this.y = Vec3D.a
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
            this.v = vec3d.y != vec3d1.y
            this.onGround = this.v && vec3d.y < 0.0
            this.w = this.positionChanged || this.v
            val i = MathHelper.floor(this.locX())
            val j = MathHelper.floor(this.locY() - 0.20000000298023224)
            val k = MathHelper.floor(this.locZ())
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
                    val sourceBlock = this.world.world.getBlockAt(MathHelper.floor(this.locX()), MathHelper.floor(this.locY()), MathHelper.floor(this.locZ()))
                    collision = proxy.calculateKnockBack(sourceVector, sourceBlock, vec3d1.x, vec3d1.z, vec3d.x, vec3d.z)
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
        if (this.entityBukkit == null) {
            this.entityBukkit = CraftDesignArmorstand(this.world.server, this)
        }

        return this.entityBukkit as CraftDesignArmorstand
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
        val loc = bukkitEntity.location
        BlockBallApi.resolve(LoggingService::class.java).debug("Design at ${loc.x.toFloat()} ${loc.y.toFloat()} ${loc.z.toFloat()}")
    }
}