package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.BallMeta
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.EulerAngle
import org.bukkit.util.Vector

/**
 * Ball interface of the complex ball entity.
 */
interface Ball {
    /**
     * Gets the meta data.
     */
    val meta: BallMeta

    /**
     * Is the entity dead?
     */
    val isDead: Boolean

    /**
     * Entity id of the hitbox.
     */
    val hitBoxEntityId: Int

    /**
     * Entity id of the design.
     */
    val designEntityId: Int

    /**
     * Gets if the ball is on ground.
     */
    val isOnGround: Boolean

    /**
     * Teleports the ball to the given [location].
     */
    fun teleport(location: Location)

    /**
     * Gets the location of the ball.
     */
    fun getLocation(): Location

    /**
     * Gets the velocity of the ball.
     */
    fun  getVelocity(): Vector

    /**
     * Rotation of the visible ball in euler angles.
     */
    fun getRotation(): Vector

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param player
     */
    fun kickByPlayer(player: Player)

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent
     *
     * @param player
     */
    fun passByPlayer(player: Player)

    /**
     * Removes the ball.
     */
    fun remove()

    /**
     * Runnable. Should not be called directly.
     */
    fun run()
}
