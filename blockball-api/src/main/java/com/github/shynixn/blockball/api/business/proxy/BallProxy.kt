package com.github.shynixn.blockball.api.business.proxy

import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import java.util.*

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS Oo89R
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
interface BallProxy {
    /**
     * Gets the meta data.
     */
    val meta: BallMeta

    /**
     * Runnable Value yaw change which reprents internal yaw change calculation.
     * Returns below 0 if yaw did not change.
     */
    var yawChange: Float

    /**
     * Is the ball currently grabbed by some entity?
     */
    val isGrabbed: Boolean

    /**
     * Is the entity dead?
     */
    val isDead: Boolean

    /**
     * Unique id.
     */
    val uuid: UUID

    /**
     * Is the entity persistent and can be stored.
     */
    var persistent: Boolean

    /**
     * Remaining time in ticks until players regain the ability to kick this ball.
     */
    var skipKickCounter: Int

    /**
     * Current angular velocity that determines the intensity of Magnus effect.
     */
    var angularVelocity: Double

    /**
     * Returns the armorstand for the design.
     */
    fun <A> getDesignArmorstand(): A

    /**
     * Returns the hitbox entity.
     */
    fun <A> getHitbox(): A

    /**
     * Gets the optional living entity owner of the ball.
     */
    fun <L> getOwner(): Optional<L>

    /**
     * Gets the last interaction entity.
     * TODO 'interaction' can be interpreted as kick or dribbling
     */
    fun <L> getLastInteractionEntity(): Optional<L>

    /**
     * Teleports the ball to the given [location].
     */
    fun <L> teleport(location: L)

    /**
     * Gets the location of the ball.
     */
    fun <L> getLocation(): L

    /**
     * Sets the velocity of the ball.
     */
    fun <V> setVelocity(vector: V)

    /**
     * Gets the velocity of the ball.
     */
    fun <V> getVelocity(): V

    /**
     * Shoot the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param player
     */
    fun <E> shootByPlayer(player: E)

    /**
     * Pass the ball by the given player.
     * The calculated velocity can be manipulated by the BallKickEvent
     *
     * @param player
     */
    fun <E> passByPlayer(player: E)

    /**
     * Throws the ball by the given player.
     * The calculated velocity can be manipulated by the BallThrowEvent.
     *
     * @param player
     */
    fun <E> throwByPlayer(player: E)

    /**
     * Lets the given living entity grab the ball.
     */
    fun <L> grab(entity: L)

    /**
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    fun <V> calculateSpinVelocity(postVector: V, initVector: V): Double

    /**
     * DeGrabs the ball.
     */
    fun deGrab()

    /**
     * Removes the ball.
     */
    fun remove()

    /**
     * Runnable. Should not be called directly.
     */
    fun run()

    /**
     * Calculates post movement.
     *
     * @param collision if knockback were applied during the movement
     */
    fun calculatePostMovement(collision: Boolean)

    /**
     * Calculates spin movement. The spinning will slow down
     * if the ball stops moving, hits the ground or hits the wall.
     *
     * @param collision if knockback were applied
     */
    fun calculateSpinMovement(collision: Boolean)

    /**
     * Calculates the movement vectors.
     */
    fun <V> calculateMoveSourceVectors(movementVector: V, motionVector: V, onGround: Boolean): Optional<V>

    /**
     * Calculates the knockback for the given [sourceVector] and [sourceBlock]. Uses the motion values to correctly adjust the
     * wall.
     *
     * @return if collision was detected and the knockback was applied
     */
    fun <V, B> calculateKnockBack(sourceVector: V, sourceBlock: B, mot0: Double, mot2: Double, mot6: Double, mot8: Double): Boolean
}