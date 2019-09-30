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

    /** Spawning delay. */
    var delayInTicks: Int

    /** Spawnpoint of the ball. */
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
    var hitBoxSize: Double

    /**
     * Should the ball be able to carry.
     */
    var carryAble: Boolean

    /**
     * Should the ball always bounce of walls?
     */
    var alwaysBounce: Boolean

    /**
     * Bouncing off from objects modifiers.
     */
    val bounceModifiers: MutableList<BounceConfiguration>

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