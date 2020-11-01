@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.EntityBallProxy
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.ItemTypeService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.BounceConfiguration
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.persistence.entity.ItemEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector
import java.util.*
import java.util.logging.Level
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
class BallProxyImpl(
    private val design: ArmorStand,
    private val hitbox: LivingEntity
) {
/*
    companion object {
        private val itemService = BlockBallApi.resolve(ItemTypeService::class.java)
        private val excludedRelativeItems: Array<Material> = arrayOf(
            itemService.findItemType(MaterialType.OAK_FENCE),
            itemService.findItemType(MaterialType.OAK_FENCE),
            itemService.findItemType(MaterialType.IRON_BARS),
            itemService.findItemType(MaterialType.GLASS_PANE),
            itemService.findItemType(MaterialType.OAK_FENCE_GATE),
            itemService.findItemType(MaterialType.NETHER_FENCE),
            itemService.findItemType(MaterialType.COBBLESTONE_WALL),
            itemService.findItemType(MaterialType.STAINED_GLASS_PANE),
            Material.SPRUCE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.ACACIA_FENCE_GATE,
            Material.SPRUCE_FENCE,
            Material.BIRCH_FENCE,
            Material.JUNGLE_FENCE,
            Material.DARK_OAK_FENCE,
            Material.ACACIA_FENCE
        )
    }

    /** Design **/
    private val concurrencyService = BlockBallApi.resolve(ConcurrencyService::class.java)
    private var backAnimation = false
    private var interactionEntity: Entity? = null
    private var skipCounter = 20
    var yawChange: Float = -1.0F

    /** HitBox **/
    private var knockBackBumper: Int = 0

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    var angularVelocity: Double = 0.0

    /**
     * Entity id of the hitbox.
     */
    override val hitBoxEntityId: Int
        get() = TODO("Not yet implemented")

    /**
     * Entity id of the design.
     */
    override val designEntityId: Int
        get() = TODO("Not yet implemented")

    /**
     * Rotation of the visible ball in euler angles.
     */
    override var rotation: Position
        get() = TODO("Not yet implemented")
        set(value) {}

    /**
     * Remaining time in ticks until players regain the ability to kick this ball.
     */
    var skipKickCounter = 0

    /**
     * Is the entity dead?
     */
    override val isDead: Boolean
        get() = this.design.isDead




    override fun <V> calculateMoveSourceVectors(movementVector: V, motionVector: V, onGround: Boolean): Optional<V> {
        if (movementVector !is Vector) {
            throw IllegalArgumentException("MovementVector has to be a BukkitVector!")
        }

        if (motionVector !is Vector) {
            throw IllegalArgumentException("MotionVector has to be a BukkitVector!")
        }

        val preMoveEvent = BallPreMoveEvent(movementVector, this)
        Bukkit.getPluginManager().callEvent(preMoveEvent)

        if (preMoveEvent.isCancelled) {
            return Optional.empty()
        }

        if (this.knockBackBumper > 0) {
            this.knockBackBumper--
        }

        return if ((this.times > 0 || !onGround) && this.originVector != null) {
            this.originVector = this.originVector!!.subtract(this.reduceVector!!)

            if (this.times > 0) {
                motionVector.x = this.originVector!!.x
                motionVector.z = this.originVector!!.z
            }

            motionVector.y = this.originVector!!.y
            this.times--

            Optional.of(Vector(motionVector.x, motionVector.y, motionVector.z) as V)
        } else {
            Optional.of(Vector(movementVector.x, movementVector.y, movementVector.z) as V)
        }
    }

    /**
     * Calculates the knockback for the given [sourceVector] and [sourceBlock]. Uses the motion values to correctly adjust the
     * wall.
     */
    override fun <V, B> calculateKnockBack(
        sourceVector: V,
        sourceBlock: B,
        mot0: Double,
        mot2: Double,
        mot6: Double,
        mot8: Double
    ): Boolean {
        if (sourceVector !is Vector) {
            throw IllegalArgumentException("SourceVector has to be a BukkitVector!")
        }

        if (sourceBlock !is Block) {
            throw IllegalArgumentException("SourceBlock has to be a BukkitBlock!")
        }

        var knockBackBlock: Block = sourceBlock

        when {
            mot6 > mot0 -> {
                if (this.isValidKnockBackBlock(knockBackBlock)) {
                    knockBackBlock = knockBackBlock.getRelative(BlockFace.EAST)
                }

                val n = Vector(-1, 0, 0)
                return this.applyKnockBack(sourceVector, n, knockBackBlock, BlockFace.EAST)
            }
            mot6 < mot0 -> {
                if (this.isValidKnockBackBlock(knockBackBlock)) {
                    knockBackBlock = knockBackBlock.getRelative(BlockFace.WEST)
                }

                val n = Vector(1, 0, 0)
                return this.applyKnockBack(sourceVector, n, knockBackBlock, BlockFace.WEST)
            }
            mot8 > mot2 -> {
                if (this.isValidKnockBackBlock(knockBackBlock)) {
                    knockBackBlock = knockBackBlock.getRelative(BlockFace.SOUTH)
                }

                val n = Vector(0, 0, -1)
                return this.applyKnockBack(sourceVector, n, knockBackBlock, BlockFace.SOUTH)
            }
            mot8 < mot2 -> {
                if (this.isValidKnockBackBlock(knockBackBlock)) {
                    knockBackBlock = knockBackBlock.getRelative(BlockFace.NORTH)
                }

                val n = Vector(0, 0, 1)
                return this.applyKnockBack(sourceVector, n, knockBackBlock, BlockFace.NORTH)
            }
        }
        return false
    }

    /**
     * Calculates post movement.
     *
     * @param collision if knockback were applied during the movement
     */
    override fun calculatePostMovement(collision: Boolean) {
        if (this.originVector == null) {
            return
        }

        // Movement calculation
        calculateSpinMovement(collision)

        val postMovement = BallPostMoveEvent(this.originVector!!, true, this)
        Bukkit.getPluginManager().callEvent(postMovement)
    }

    /**
     * Calculates spin movement. The spinning will slow down
     * if the ball stops moving, hits the ground or hits the wall.
     *
     * @param collision if knockback were applied
     */
    override fun calculateSpinMovement(collision: Boolean) {
        if (abs(angularVelocity) < 0.01) {
            return
        }

        if (times <= 0 || getCalculationEntity<Entity>().isOnGround || collision) {
            angularVelocity /= 2
        }

        val event = BallSpinEvent(angularVelocity, this, false)
        Bukkit.getPluginManager().callEvent(event)

        if (!event.isCancelled) {
            angularVelocity = event.angularVelocity

            if (angularVelocity != 0.0) {
                val originUnit = this.originVector!!.clone().normalize()
                val x = -originUnit.z
                val z = originUnit.x
                val newVector = this.originVector!!.add(Vector(x, 0.0, z).multiply(angularVelocity.toFloat()))
                this.originVector = newVector.multiply(this.originVector!!.length() / newVector.length())
            }
        }
    }

    /**
     * Applies the wall knockback.
     *
     * @return whether the knockback was applied
     */
    private fun applyKnockBack(starter: Vector, n: Vector, block: Block, blockFace: BlockFace): Boolean {
        if (this.knockBackBumper <= 0) {
            val optBounce = getBounceConfigurationFromBlock(block)
            if (optBounce.isPresent || meta.alwaysBounce) {
                var r = starter.clone().subtract(n.multiply(2 * starter.dot(n))).multiply(0.75)

                r = if (optBounce.isPresent) {
                    r.multiply(optBounce.get().modifier)
                } else {
                    r.multiply(meta.movementModifier.defaultBounceModifier)
                }

                val event = BallWallCollideEvent(block, blockFace, starter.clone(), r.clone(), this)
                Bukkit.getPluginManager().callEvent(event)
                if (!event.isCancelled) {
                    this.setVelocity(r)
                    this.backAnimation = !backAnimation
                    this.knockBackBumper = 5
                    return true
                }
            }
        }
        return false
    }



    /**
     * Gets the bounce configuraiton for the given block.
     */
    private fun getBounceConfigurationFromBlock(block: Block): Optional<BounceConfiguration> {
        meta.bounceModifiers.forEach { modifier ->
            if (modifier.materialType == block.type) {
                @Suppress("DEPRECATION")
                if (modifier.materialDamage == block.data.toInt()) {
                    return Optional.of(modifier)
                }
            }
        }

        return Optional.empty()
    }

    /**
     * Gets if the given block is a valid knockback block.
     */
    private fun isValidKnockBackBlock(block: Block): Boolean {
        val material = block.type
        for (i in excludedRelativeItems) {
            if (i == material) {
                return false
            }
        }

        return true
    }

    /**
     * Gets the representative entity used for calculation.
     */
    private fun <A> getCalculationEntity(): A {
        return if (hitbox is Slime) {
            design as A
        } else {
            hitbox as A
        }
    }*/
}
