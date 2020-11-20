package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.enumeration.BallSize

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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
interface BallMeta {
    /**
     * Is leftclick kicking the ball enabled?
     */
    var enabledKick: Boolean

    /**
     * Is the rightclick passing the ball enabled?
     */
    var enabledPass: Boolean

    /**
     * Is the moving into the ball enabled?
     */
    var enabledInteract: Boolean

    /** Spawning delay. TODO: Move this to game. */
    var delayInTicks: Int

    /** Spawnpoint of the ball. TODO: Move this to game. */
    var spawnpoint: Position?

    /** Size of the ball.**/
    var size: BallSize

    /** Skin of the ball.**/
    var skin: String

    /** Should the ball rotate? */
    var rotating: Boolean

    /**
     * Hitbox relocation value for ground heights.
     */
    var hitBoxRelocation: Double

    /**
     * Size of the hitbox used for interaction detecting.
     */
    var interactionHitBoxSize: Double

    /**
     * Size of the hitbox used for kicking and passing detecting.
     */
    var kickPassHitBoxSize: Double

    /**
     * Delay in ticks until the ball executes the kick pass request by the player.
     * Is useful for magnus force calculation.
     */
    var kickPassDelay: Int

    /**
     * Amount of ticks until the ball can intercept interaction again after
     * performing 1 interaction.
     */
    var interactionCoolDown: Int

    /**
     * Should the ball always bounce of walls?
     */
    var alwaysBounce: Boolean

    /**
     * Movement modifier.
     */
    val movementModifier: MovementConfiguration

    /**
     * Particle effects.
     */
    val particleEffects: MutableMap<BallActionType, Particle>

    /**
     * Particle effects.
     */
    val soundEffects: MutableMap<BallActionType, Sound>
}
