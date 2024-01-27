package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.DoubleJumpMeta
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Sound

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
    override val soundEffect: Sound = SoundEntity("ENTITY_GHAST_SHOOT,GHAST_FIREBALL", 1.0, 10.0)

    init {
        particleEffect.amount = 4
        particleEffect.speed = 0.0002
        particleEffect.offset.x = 2.0
        particleEffect.offset.y = 2.0
        particleEffect.offset.z = 2.0
    }
}
