@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.proxy

import com.github.shynixn.blockball.api.bukkit.event.BallDeathEvent
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.toEulerAngle
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toVector
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
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
     * Gets if the ball is on ground.
     */
    override val isOnGround: Boolean
        get() {
            return ballHitBoxEntity.isOnGround
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
     * Gets the velocity of the ball.
     */
    override fun <V> getVelocity(): V {
        return ballHitBoxEntity.motion.toVector() as V
    }

    /**
     * Rotation of the visible ball in euler angles.
     */
    override fun <V> getRotation(): V {
        return ballDesignEntity.rotation.toEulerAngle() as V
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
}
