package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.mcutils.common.EffectTargetType

class SoundEntity(
        /**
         * Name of the sound.
         */
        @YamlSerialize(value = "name", orderNumber = 1)
        override var name: String = "none",
        /**
         * Pitch of the sound.
         */
        @YamlSerialize(value = "pitch", orderNumber = 2)
        override var pitch: Double = 1.0,
        /**
         * Volume of the sound.
         */
        @YamlSerialize(value = "volume", orderNumber = 3)
        override var volume: Double = 1.0) : Sound {
    /**
     * Which players are effected.
     */
    @YamlSerialize(value = "effecting", orderNumber = 4)
    override var effectingType: EffectTargetType = EffectTargetType.NOBODY
}
