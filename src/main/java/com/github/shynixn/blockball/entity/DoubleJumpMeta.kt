package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.sound.SoundMeta
import com.github.shynixn.shyparticles.entity.ParticleEffectMeta

class DoubleJumpMeta {
    @Comment("Are double jumps enabled or disabled?")
    var enabled: Boolean = true

    @Comment("Cooldown in seconds between double jumps.")
    var cooldown: Int = 2

    @Comment("The vertical velocity modifier.")
    var verticalStrength: Double = 1.0

    @Comment("The horizontal velocity modifier.")
    var horizontalStrength: Double = 2.0

    @Comment("The double jump effect being played. See the effect folder.")
    val effectName: String = "double_jump"
}
