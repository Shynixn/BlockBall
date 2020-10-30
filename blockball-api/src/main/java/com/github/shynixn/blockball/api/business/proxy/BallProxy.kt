package com.github.shynixn.blockball.api.business.proxy

import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
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
     * Rotation of the visible ball in euler angles.
     */
    var rotation: Position

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
     * Calculates the angular velocity in order to spin the ball.
     *
     * @return The angular velocity
     */
    fun <V> calculateSpinVelocity(postVector: V, initVector: V): Double

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
    fun <V, B> calculateKnockBack(
        sourceVector: V,
        sourceBlock: B,
        mot0: Double,
        mot2: Double,
        mot6: Double,
        mot8: Double
    ): Boolean
}
