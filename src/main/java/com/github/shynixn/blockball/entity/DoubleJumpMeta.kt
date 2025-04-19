package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.sound.SoundMeta

class DoubleJumpMeta {
    @Comment("Are double jumps enabled or disabled?")
    var enabled: Boolean = true
    @Comment("Cooldown in seconds between double jumps.")
    var cooldown: Int = 2
    @Comment("The vertical velocity modifier.")
    var verticalStrength: Double = 1.0
    @Comment("The horizontal velocity modifier.")
    var horizontalStrength: Double = 2.0
   @Comment("The sound effect being played. The names are separated by comma to support different Minecraft versions.")
    val soundEffect: SoundMeta = SoundMeta().also {
        it.name = "ENTITY_WIND_CHARGE_WIND_BURST,ENTITY_GHAST_SHOOT,GHAST_FIREBALL"
        it.pitch = 1.0
        it.volume = 10.0
    }
}
