package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.enumeration.ClickType
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * SoccerBall interface of the complex ball entity.
 */
interface SoccerBall {
    /**
     * Gets the metadata.
     */
    val meta: SoccerBallMeta

    /**
     * Gets if the entity is dead.
     */
    val isDead: Boolean

    /**
     * Entity id of the hitbox.
     */
    val hitBoxEntityId: Int

    /**
     * Entity id of the render.
     */
    val renderEntityId: Int

    /**
     * Gets if the ball is on ground.
     */
    val isOnGround: Boolean

    /**
     * If set, the ball is currently grabbed by a player.
     */
    val grabbingPlayer: Player?

    /**
     * Sets or gets if the ball can be interacted with.
     */
    var isInteractable: Boolean

    /**
     * If set, only this player can interact with the ball.
     */
    var lockedPlayer: Player?

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
    fun getVelocity(): Vector

    /**
     * Sets the velocity of the ball.
     * Resets the spin value.
     */
    fun setVelocity(velocity: Vector)

    /**
     * Sets the velocity of the ball.
     * Sets the y value of the spin vector to the horizontal spin.
     */
    fun setVelocity(velocity: Vector, spin: Vector)

    /**
     * Checks if the player is interacting with the ball in some way.
     */
    fun applyInteraction(player: Player, clickType: ClickType)

    /**
     * Removes the ball.
     */
    fun remove()
}
