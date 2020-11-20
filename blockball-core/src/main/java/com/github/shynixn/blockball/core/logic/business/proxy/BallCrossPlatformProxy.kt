@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.proxy

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.EventService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.core.logic.persistence.event.BallDeathEventEntity
import com.github.shynixn.blockball.core.logic.persistence.event.BallTeleportEventEntity

class BallCrossPlatformProxy(
    override val meta: BallMeta,
    private val ballDesignEntity: BallDesignEntity,
    private val ballHitBoxEntity: BallHitboxEntity
) : BallProxy {
    private var allPlayerTracker: AllPlayerTracker = AllPlayerTracker(
        {
            ballHitBoxEntity.position
        },
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
    var proxyService: ProxyService
        set(value) {
            this.allPlayerTracker.proxyService = value
        }
        get() {
            return allPlayerTracker.proxyService
        }

    /**
     * Event dependency.
     */
    lateinit var eventService: EventService

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
            return ballDesignEntity.entityId
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
        val ballTeleportEvent = BallTeleportEventEntity(this, location as Any)
        eventService.sendEvent(ballTeleportEvent)

        if (ballTeleportEvent.isCancelled) {
            return
        }

        ballHitBoxEntity.position = proxyService.toPosition(ballTeleportEvent.targetLocation)
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
    override fun <P> kickByPlayer(player: P) {
        require(player is Any)

        if (!meta.enabledKick) {
            return
        }

        ballHitBoxEntity.kickPlayer(player, meta.movementModifier.shotVelocity, false)
    }

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent
     *
     * @param player
     */
    override fun <P> passByPlayer(player: P) {
        require(player is Any)

        if (!meta.enabledPass) {
            return
        }

        ballHitBoxEntity.kickPlayer(player, meta.movementModifier.passVelocity, true)
    }

    /**
     * Removes the ball.
     */
    override fun remove() {
        if (isDead) {
            return
        }

        val ballDeathEvent = BallDeathEventEntity(this)
        eventService.sendEvent(ballDeathEvent)

        if (ballDeathEvent.isCancelled) {
            return
        }

        isDead = true
        allPlayerTracker.dispose()
    }

    /**
     * Runnable. Should not be called directly.
     */
    override fun run() {
        if (isDead) {
            return
        }

        try {
            val players = allPlayerTracker.checkAndGet()
            ballHitBoxEntity.tick(players)
            ballDesignEntity.tick(players)
        } catch (e: Exception) {
            loggingService.warn("Entity ticking exception.", e)
        }
    }
}
