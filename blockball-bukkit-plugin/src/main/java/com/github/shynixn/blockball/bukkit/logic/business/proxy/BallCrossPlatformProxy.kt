@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.bukkit.event.BallDeathEvent
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toVector
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.logging.Level

class BallCrossPlatformProxy(
    override val meta: BallMeta,
    val ballDesignEntity: BallDesignEntity,
    val ballHitBoxEntity: BallHitboxEntity
) : BallProxy {
    private var playerTracker: PlayerTracker? = PlayerTracker(ballHitBoxEntity.position.toLocation().world!!,
        { player ->
            ballDesignEntity.spawn(player, ballHitBoxEntity.position)
            ballHitBoxEntity.spawn(player, ballHitBoxEntity.position)
        }, { player ->
            ballDesignEntity.destroy(player)
            ballHitBoxEntity.destroy(player)
        })

    /**
     * Is the entity dead?
     */
    override var isDead: Boolean = false

    /**
     * Entity id of the hitbox.
     */
    override val hitBoxEntityId: Int
        get() {
            return ballHitBoxEntity.entityId
        }

    /**
     * Entity id of the design.
     */
    override val designEntityId: Int
        get() {
            return ballHitBoxEntity.entityId
        }

    /**
     * Rotation of the visible ball in euler angles.
     */
    override var rotation: Position
        get() {
            return ballDesignEntity.rotation
        }
        set(value) {
            ballDesignEntity.rotation = value
            ballDesignEntity.requestRotationChange = true
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
        ballHitBoxEntity.setVelocity(vector)
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
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    override fun <V> calculateSpinVelocity(postVector: V, initVector: V): Double {
        return 0.0
    }

    /**
     * Removes the ball.
     */
    override fun remove() {
        if (isDead) {
            return
        }

        Bukkit.getPluginManager().callEvent(BallDeathEvent(this))

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

        try {
            val players = playerTracker!!.checkAndGet()
            ballHitBoxEntity.tick(players)
            ballDesignEntity.tick(players)
        } catch (e: Exception) {
            Bukkit.getLogger().log(Level.WARNING, "Entity ticking exception.", e)
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
