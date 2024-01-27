package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.persistence.entity.Offset

class OffsetEntity : Offset {
    /**
     * Offset x.
     */
    @YamlSerialize(value = "x", orderNumber = 1)
    override var x: Double = 1.0

    /**
     * Offset y.
     */
    @YamlSerialize(value = "y", orderNumber = 2)
    override var y: Double = 1.0

    /**
     * Offset z.
     */
    @YamlSerialize(value = "z", orderNumber = 3)
    override var z: Double = 1.0
}
