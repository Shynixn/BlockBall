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
    override var skin: String = ""
) : BallMeta {

    /** Size of the ball.**/
    @YamlSerialize(orderNumber = 1, value = "size")
    override var size: BallSize = BallSize.NORMAL

    /**
     * Size of the hitbox used for interaction detecting.
     */
    @YamlSerialize(orderNumber = 3, value = "interaction-hitbox-size")
    override var interactionHitBoxSize: Double = 2.0

    /**
     * Size of the hitbox used for kicking and passing detecting.
     */
    @YamlSerialize(orderNumber = 4, value = "kickpass-hitbox-size")
    override var kickPassHitBoxSize: Double = 5.0

    /**
     * Delay in ticks until the ball executes the kick pass request by the player.
     * Is useful for magnus force calculation.
     */
    @YamlSerialize(orderNumber = 5, value = "kickpass-delay-ticks")
    override var kickPassDelay: Int = 5

    /**
     * Amount of ticks until the ball can intercept interaction again after
     * performing 1 interaction.
     */
    @YamlSerialize(orderNumber = 6, value = "interaction-cooldown-ticks")
    override var interactionCoolDown: Int = 20


    /** Should the ball rotate? */
    @YamlSerialize(orderNumber = 7, value = "rotating")
    override var rotating: Boolean = true

    /**
     * Is leftclick kicking the ball enabled?
     */
    @YamlSerialize(orderNumber = 8, value = "enable-kick")
    override var enabledKick: Boolean = true

    /**
     * Is the rightclick passing the ball enabled?
     */
    @YamlSerialize(orderNumber = 9, value = "enable-pass")
    override var enabledPass: Boolean = true

    /**
     * Is the moving into the ball enabled?
     */
    @YamlSerialize(orderNumber = 10, value = "enable-interact")
    override var enabledInteract: Boolean = true

    /**
     * Hitbox relocation value for ground heights.
     */
    @YamlSerialize(orderNumber = 11, value = "hitbox-relocation")
    override var hitBoxRelocation: Double = 0.0

    /**
     * Should the ball always bounce of walls?
     */
    @YamlSerialize(orderNumber = 12, value = "always-bounce")
    override var alwaysBounce: Boolean = true

    /**
     * Movement modifier.
     */
    @YamlSerialize(orderNumber = 13, value = "modifiers")
    override val movementModifier: MovementConfigurationEntity = MovementConfigurationEntity()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 14, value = "particle-effects", implementation = ParticleEntity::class)
    override val particleEffects: MutableMap<BallActionType, Particle> = HashMap()

    /**
     * Particle effects.
     */
    @YamlSerialize(orderNumber = 15, value = "sound-effects", implementation = SoundEntity::class)
    override val soundEffects: MutableMap<BallActionType, Sound> = HashMap()

    /** Spawning delay. */
    @YamlSerialize(orderNumber = 16, value = "spawn-delay")
    override var delayInTicks: Int = 0

    /** Spawnpoint of the ball. */
    @YamlSerialize(orderNumber = 17, value = "spawnpoint", implementation = PositionEntity::class)
    override var spawnpoint: Position? = null
}
