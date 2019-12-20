package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.DoubleJumpMeta
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Sound

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
class DoubleJumpMetaEntity : DoubleJumpMeta {
    /** Is the effect enabled or disabled?*/
    @YamlSerialize(orderNumber = 1, value = "enabled")
    override var enabled: Boolean = true
    /** Cooldown between activating this effect.*/
    @YamlSerialize(orderNumber = 2, value = "cooldown")
    override var cooldown: Int = 2
    /** Vertical strength modifier.*/
    @YamlSerialize(orderNumber = 3, value = "vertical-strength")
    override var verticalStrength: Double = 1.0
    /** Horizontal strength modifier.*/
    @YamlSerialize(orderNumber = 4, value = "horizontal-strength")
    override var horizontalStrength: Double = 2.0
    /** ParticleEffect being played when activating this.*/
    @YamlSerialize(orderNumber = 5, value = "particle-effect", implementation = ParticleEntity::class)
    override val particleEffect: Particle = ParticleEntity(ParticleType.EXPLOSION_NORMAL.name)
    /** SoundEffect being played when activating this.*/
    @YamlSerialize(orderNumber = 6, value = "sound-effect", implementation = SoundEntity::class)
    override val soundEffect: Sound = SoundEntity("GHAST_FIREBALL", 10.0, 1.0)

    init {
        particleEffect.amount = 4
        particleEffect.speed = 0.0002
        particleEffect.offset.x = 2.0
        particleEffect.offset.y = 2.0
        particleEffect.offset.z = 2.0
    }
}