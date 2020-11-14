@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.EventService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta

class BallCrossPlatformProxy(
    override val meta: BallMeta,
    val ballDesignEntity: BallDesignEntity,
    val ballHitBoxEntity: BallHitboxEntity
) : BallProxy {
    private var playerTracker: PlayerTracker? = PlayerTracker(ballHitBoxEntity.position,
        { player ->
            ballDesignEntity.spawn(player, ballHitBoxEntity.position)
            ballHitBoxEntity.spawn(player, ballHitBoxEntity.position)
        }, { player ->
            ballDesignEntity.destroy(player)
            ballHitBoxEntity.destroy(player)
        })

    /**
     * Logging dependency.
     */
    lateinit var loggingService: LoggingService

    /**
     * Proxy dependency.
     */
    lateinit var proxyService: ProxyService

    /**
     * Event dependency.
     */
    lateinit var eventService: EventService // TODO:

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
        ballHitBoxEntity.position = proxyService.toPosition(location)
        ballHitBoxEntity.requestTeleport = true
    }

    /**
     * Gets the location of the ball.
     */
    override fun <L> getLocation(): L {
        return proxyService.toLocation(ballHitBoxEntity.position)
    }

    /**
     * Gets the velocity of the ball.
     */
    override fun <V> getVelocity(): V {
        return proxyService.toVector(ballHitBoxEntity.motion)
    }

    /**
     * Rotation of the visible ball in euler angles.
     */
    override fun <V> getRotation(): V {
        return proxyService.toVector(ballDesignEntity.rotation)
    }

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param player
     */
    override fun <E> shootByPlayer(player: E) {
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

        // TODO: Bukkit.getPluginManager().callEvent(BallDeathEvent(this))

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
            loggingService.warn("Entity ticking exception.", e)
        }
    }
}
