@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.event.BallRemoveEvent
import com.github.shynixn.blockball.event.BallTeleportEvent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level

class BallCrossPlatformProxy(
    override val meta: BallMeta,
    private val ballDesignEntity: BallDesignEntity,
    private val ballHitBoxEntity: BallHitboxEntity,
    private val plugin: Plugin,
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
        require(location is Location)
        val ballTeleportEvent = BallTeleportEvent(this, location)
        Bukkit.getPluginManager().callEvent(ballTeleportEvent)

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
     * The calculated velocity can be manipulated by the BallLeftClickEvent.
     *
     * @param player
     */
    override fun <P> kickByPlayer(player: P) {
        require(player is Player)

        if (!meta.enabledKick) {
            return
        }

        ballHitBoxEntity.kickPlayer(player, meta.movementModifier.shotVelocity, false)
    }

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallLeftClickEvent
     *
     * @param player
     */
    override fun <P> passByPlayer(player: P) {
        require(player is Player)

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

        val ballDeathEvent = BallRemoveEvent(this)
        Bukkit.getPluginManager().callEvent(ballDeathEvent)

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
            plugin.logger.log(Level.SEVERE, "Entity ticking exception", e)
        }
    }
}
