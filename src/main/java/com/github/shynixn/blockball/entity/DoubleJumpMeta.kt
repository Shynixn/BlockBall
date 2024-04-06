package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.ParticleType


class DoubleJumpMeta {
    /** Is the effect enabled or disabled?*/
    var enabled: Boolean = true
    /** Cooldown between activating this effect.*/
    var cooldown: Int = 2
    /** Vertical strength modifier.*/
    var verticalStrength: Double = 1.0
    /** Horizontal strength modifier.*/
    var horizontalStrength: Double = 2.0
    /** ParticleEffect being played when activating this.*/
    val particleEffect: Particle = Particle(ParticleType.EXPLOSION_NORMAL.name)
    /** SoundEffect being played when activating this.*/
    val soundEffect: Sound = Sound("ENTITY_GHAST_SHOOT,GHAST_FIREBALL", 1.0, 10.0)

    init {
        particleEffect.amount = 4
        particleEffect.speed = 0.0002
        particleEffect.offset.x = 2.0
        particleEffect.offset.y = 2.0
        particleEffect.offset.z = 2.0
    }
}
