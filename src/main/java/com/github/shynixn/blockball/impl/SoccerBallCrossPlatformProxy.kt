@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallSettings
import com.github.shynixn.blockball.event.BallRemoveEvent
import com.github.shynixn.blockball.event.BallTeleportEvent
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector
import com.github.shynixn.mcutils.common.toVector3d
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.util.logging.Level

class SoccerBallCrossPlatformProxy(
    override val meta: SoccerBallSettings,
    private val ballDesignEntity: BallDesignEntity,
    private val ballHitBoxEntity: BallHitboxEntity,
    private val plugin: Plugin,
) : SoccerBall {
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
    override fun teleport(location: Location) {
        val ballTeleportEvent = BallTeleportEvent(this, location)
        Bukkit.getPluginManager().callEvent(ballTeleportEvent)

        if (ballTeleportEvent.isCancelled) {
            return
        }

        ballHitBoxEntity.position = ballTeleportEvent.targetLocation.toVector3d()
        ballHitBoxEntity.requestTeleport = true
    }

    /**
     * Gets the location of the ball.
     */
    override fun getLocation(): Location {
        return ballHitBoxEntity.position.toLocation()
    }

    /**
     * Gets the velocity of the ball.
     */
    override fun getVelocity(): Vector {
        return ballHitBoxEntity.motion.toVector()
    }

    /**
     * Rotation of the visible ball in euler angles.
     */
    override fun getRotation(): Vector {
        return ballDesignEntity.rotation.toVector()
    }

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallLeftClickEvent.
     *
     * @param player
     */
    override fun kickByPlayer(player: Player) {
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
    override fun passByPlayer(player: Player) {
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
