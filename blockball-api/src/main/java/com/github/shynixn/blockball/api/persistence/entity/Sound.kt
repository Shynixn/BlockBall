package com.github.shynixn.blockball.api.persistence.entity

import com.github.shynixn.mcutils.common.EffectTargetType

interface Sound {
    /**
     * Name of the sound.
     */
    var name: String

    /**
     * Pitch of the sound.
     */
    var pitch: Double

    /**
     * Volume of the sound.
     */
    var volume: Double

    /**
     * Which players are effected.
     */
    var effectingType: EffectTargetType
}
