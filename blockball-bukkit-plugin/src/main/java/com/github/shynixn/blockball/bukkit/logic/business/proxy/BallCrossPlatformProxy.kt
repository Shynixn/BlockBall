@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toVector
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class BallCrossPlatformProxy(
    override val meta: BallMeta,
    private val ballDesignEntity: BallDesignEntity,
    private val ballHitBoxEntity: BallHitboxEntity
) : BallProxy {
    private var interactionEntity: Entity? = null

    private var playerTracker: PlayerTracker? = PlayerTracker(ballHitBoxEntity.position.toLocation().world!!,
        { player ->
            ballDesignEntity.spawn(player, ballHitBoxEntity.position)
            ballHitBoxEntity.spawn(player, ballHitBoxEntity.position)
        }, { player ->
            ballDesignEntity.destroy(player)
            ballHitBoxEntity.destroy(player)
        })

    /**
     * Runnable Value yaw change which reprents internal yaw change calculation.
     * Returns below 0 if yaw did not change.
     */
    override var yawChange: Float = 0.0F

    /**
     * Is the ball currently grabbed by some entity?
     */
    override val isGrabbed: Boolean
        get() {
            return this.ballHitBoxEntity.isGrabbed
        }

    /**
     * Is the entity dead?
     */
    override var isDead: Boolean = false

    /**
     * Unique id.
     */
    override val uuid: UUID
        get() = TODO("Not yet implemented")

    /**
     * Is the entity persistent and can be stored.
     */
    override var persistent: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    /**
     * Remaining time in ticks until players regain the ability to kick this ball.
     */
    override var skipKickCounter: Int
        get() = TODO("Not yet implemented")
        set(value) {}

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    override var angularVelocity: Double
        get() = TODO("Not yet implemented")
        set(value) {}

    /**
     * Returns the armorstand for the design.
     */
    override fun <A> getDesignArmorstand(): A {
        throw IllegalArgumentException("DesignArmorstand!")
    }

    /**
     * Returns the hitbox entity.
     */
    override fun <A> getHitbox(): A {
        throw IllegalArgumentException("Hitbox!")
    }

    /**
     * Gets the optional living entity owner of the ball.
     */
    override fun <L> getOwner(): Optional<L> {
        TODO("Not yet implemented")
    }

    /**
     * Gets the last interaction entity.
     * TODO 'interaction' can be interpreted as kick or dribbling
     */
    override fun <L> getLastInteractionEntity(): Optional<L> {
        return Optional.ofNullable(interactionEntity as L)
    }

    /**
     * Teleports the ball to the given [location].
     */
    override fun <L> teleport(location: L) {
        require(location is Location)
        ballHitBoxEntity.position = location.toPosition()
        ballHitBoxEntity.requestTeleport = true
    }

    /**
     * Gets the location of the ball.
     */
    override fun <L> getLocation(): L {
        return ballHitBoxEntity.position.toLocation() as L
    }

    /**
     * Sets the velocity of the ball.
     */
    override fun <V> setVelocity(vector: V) {
        require(vector is Vector)
        ballHitBoxEntity.motion = vector.toPosition()
    }

    /**
     * Gets the velocity of the ball.
     */
    override fun <V> getVelocity(): V {
        return ballHitBoxEntity.motion.toVector() as V
    }

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param player
     */
    override fun <E> shootByPlayer(player: E) {
        require(player is Player)

        if (!meta.enabledKick) {
            return
        }

        ballHitBoxEntity.kickPlayer(player, 6, meta.movementModifier.shotVelocity)
    }

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent
     *
     * @param player
     */
    override fun <E> passByPlayer(player: E) {
        require(player is Player)

        if (!meta.enabledPass) {
            return
        }

        ballHitBoxEntity.kickPlayer(player, 4, meta.movementModifier.passVelocity)
    }

    /**
     * Throws the ball by the given player.
     * The calculated velocity can be manipulated by the BallThrowEvent.
     *
     * @param player
     */
    override fun <E> throwByPlayer(player: E) {
    }

    /**
     * Lets the given living entity grab the ball.
     */
    override fun <L> grab(entity: L) {
    }

    /**
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    override fun <V> calculateSpinVelocity(postVector: V, initVector: V): Double {
        return 0.0
    }

    /**
     * DeGrabs the ball.
     */
    override fun deGrab() {
    }

    /**
     * Removes the ball.
     */
    override fun remove() {
        if (isDead) {
            return
        }

        isDead = true
        playerTracker!!.dispose()
        playerTracker = null
    }

    /**
     * Runnable. Should not be called directly.
     */
    override fun run() {
        if (isDead) {
            return
        }

    }

    /**
     * Calculates post movement.
     *
     * @param collision if knockback were applied during the movement
     */
    override fun calculatePostMovement(collision: Boolean) {
    }

    /**
     * Calculates spin movement. The spinning will slow down
     * if the ball stops moving, hits the ground or hits the wall.
     *
     * @param collision if knockback were applied
     */
    override fun calculateSpinMovement(collision: Boolean) {
    }

    /**
     * Calculates the movement vectors.
     */
    override fun <V> calculateMoveSourceVectors(movementVector: V, motionVector: V, onGround: Boolean): Optional<V> {
        return Optional.empty()
    }

    /**
     * Calculates the knockback for the given [sourceVector] and [sourceBlock]. Uses the motion values to correctly adjust the
     * wall.
     *
     * @return if collision was detected and the knockback was applied
     */
    override fun <V, B> calculateKnockBack(
        sourceVector: V,
        sourceBlock: B,
        mot0: Double,
        mot2: Double,
        mot6: Double,
        mot8: Double
    ): Boolean {
        return true
    }
}
