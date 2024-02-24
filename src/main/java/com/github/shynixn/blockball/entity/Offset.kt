package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize

class Offset {
    /**
     * Offset x.
     */
    @YamlSerialize(value = "x", orderNumber = 1)
    var x: Double = 1.0

    /**
     * Offset y.
     */
    @YamlSerialize(value = "y", orderNumber = 2)
    var y: Double = 1.0

    /**
     * Offset z.
     */
    @YamlSerialize(value = "z", orderNumber = 3)
    var z: Double = 1.0
}
