package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.sound.SoundMeta

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
    /** SoundEffect being played when activating this.*/
    val soundEffect: SoundMeta = SoundMeta().also {
        it.name = "ENTITY_GHAST_SHOOT,GHAST_FIREBALL"
        it.pitch = 1.0
        it.volume = 10.0
    }
}
