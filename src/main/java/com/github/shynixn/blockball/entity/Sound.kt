package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.mcutils.common.EffectTargetType

class Sound(
        /**
         * Name of the sound.
         */
        @YamlSerialize(value = "name", orderNumber = 1)
        var name: String = "none",
        /**
         * Pitch of the sound.
         */
        @YamlSerialize(value = "pitch", orderNumber = 2)
        var pitch: Double = 1.0,
        /**
         * Volume of the sound.
         */
        @YamlSerialize(value = "volume", orderNumber = 3)
        var volume: Double = 1.0) {
    /**
     * Which players are effected.
     */
    @YamlSerialize(value = "effecting", orderNumber = 4)
    var effectingType: EffectTargetType = EffectTargetType.RELATED_PLAYER
}
