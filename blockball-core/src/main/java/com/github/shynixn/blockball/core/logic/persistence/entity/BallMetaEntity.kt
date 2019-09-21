package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.enumeration.BallSize
import com.github.shynixn.blockball.api.persistence.entity.*

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
class BallMetaEntity(
        /** Skin of the ball.**/
        @YamlSerialize(orderNumber = 2, value = "skin")
        override var skin: String = "") : BallMeta {

    /** Size of the ball.**/
    @YamlSerialize(orderNumber = 1, value = "size")
    override var size: BallSize = BallSize.NORMAL

    /**
     * Size of the hitbox used for interaction detecting.
     */
    @YamlSerialize(orderNumber = 3, value = "hitbox-size")
    override var hitBoxSize: Double = 2.0

    /** Should the ball rotate? */
    @YamlSerialize(orderNumber = 4, value = "rotating")
    override var rotating: Boolean = true

    /**
     * Hitbox relocation value for ground heights.
     */
    @YamlSerialize(orderNumber = 5, value = "hitbox-relocation")
    override var hitBoxRelocation: Double = 0.0

    /**
     * Should the ball be able to carry.
     */
    @YamlSerialize(orderNumber = 6, value = "carry-able")
    override var carryAble: Boolean = false

    /**
     * Should the ball always bounce of walls?
     */
    @YamlSerialize(orderNumber = 7, value = "always-bounce")
    override var alwaysBounce: Boolean = true

    /**
     * Bouncing off from objects modifiers.
     */
    @YamlSerialize(orderNumber = 8, value = "wall-bouncing")
    override val bounceModifiers: MutableList<BounceConfiguration> = ArrayList()

    /**
     * Movement modifier.
     */
    @YamlSerialize(orderNumber = 9, value = "modifiers")
    override val movementModifier: MovementConfigurationEntity = MovementConfigurationEntity()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 10, value = "particle-effects", implementation = ParticleEntity::class)
    override val particleEffects: MutableMap<BallActionType, Particle> = HashMap()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 11, value = "sound-effects", implementation = SoundEntity::class)
    override val soundEffects: MutableMap<BallActionType, Sound> = HashMap()

    /** Spawning delay. */
    @YamlSerialize(orderNumber = 12, value = "spawn-delay")
    override var delayInTicks: Int = 0

    /** Spawnpoint of the ball. */
    @YamlSerialize(orderNumber = 13, value = "spawnpoint", implementation = PositionEntity::class)
    override var spawnpoint: Position? = null
}