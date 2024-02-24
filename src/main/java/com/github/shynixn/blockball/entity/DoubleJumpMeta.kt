package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.ParticleType


class DoubleJumpMeta {
    /** Is the effect enabled or disabled?*/
    @YamlSerialize(orderNumber = 1, value = "enabled")
    var enabled: Boolean = true
    /** Cooldown between activating this effect.*/
    @YamlSerialize(orderNumber = 2, value = "cooldown")
    var cooldown: Int = 2
    /** Vertical strength modifier.*/
    @YamlSerialize(orderNumber = 3, value = "vertical-strength")
    var verticalStrength: Double = 1.0
    /** Horizontal strength modifier.*/
    @YamlSerialize(orderNumber = 4, value = "horizontal-strength")
    var horizontalStrength: Double = 2.0
    /** ParticleEffect being played when activating this.*/
    @YamlSerialize(orderNumber = 5, value = "particle-effect", implementation = Particle::class)
    val particleEffect: Particle = Particle(ParticleType.EXPLOSION_NORMAL.name)
    /** SoundEffect being played when activating this.*/
    @YamlSerialize(orderNumber = 6, value = "sound-effect", implementation = Sound::class)
    val soundEffect: Sound = Sound("ENTITY_GHAST_SHOOT,GHAST_FIREBALL", 1.0, 10.0)

    init {
        particleEffect.amount = 4
        particleEffect.speed = 0.0002
        particleEffect.offset.x = 2.0
        particleEffect.offset.y = 2.0
        particleEffect.offset.z = 2.0
    }
}
