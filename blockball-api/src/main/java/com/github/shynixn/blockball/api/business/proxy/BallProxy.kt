package com.github.shynixn.blockball.api.business.proxy

import com.github.shynixn.blockball.api.persistence.entity.BallMeta

/**
 * Ball interface of the complex ball entity.
 */
interface BallProxy {
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
    fun <L> teleport(location: L)

    /**
     * Gets the location of the ball.
     */
    fun <L> getLocation(): L

    /**
     * Gets the velocity of the ball.
     */
    fun <V> getVelocity(): V

    /**
     * Rotation of the visible ball in euler angles.
     */
    fun <V> getRotation(): V

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param player
     */
    fun <P> kickByPlayer(player: P)

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent
     *
     * @param player
     */
    fun <P> passByPlayer(player: P)

    /**
     * Removes the ball.
     */
    fun remove()

    /**
     * Runnable. Should not be called directly.
     */
    fun run()
}
