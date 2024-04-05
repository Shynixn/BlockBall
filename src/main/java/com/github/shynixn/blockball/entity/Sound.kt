package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.EffectTargetType

class Sound(
        /**
         * Name of the sound.
         */
        var name: String = "none",
        /**
         * Pitch of the sound.
         */
        var pitch: Double = 1.0,
        /**
         * Volume of the sound.
         */
        var volume: Double = 1.0) {
    /**
     * Which players are effected.
     */
    var effectingType: EffectTargetType = EffectTargetType.RELATED_PLAYER
}
