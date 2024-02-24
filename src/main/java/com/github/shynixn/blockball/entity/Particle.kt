package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.ParticleType


class Particle(
    @YamlSerialize(value = "name", orderNumber = 1)
  var typeName: String = ParticleType.NONE.name
) {
    /**
     * Database Id.
     */
    var id: Long = 0
    /**
     * Amount of particles.
     */
    @YamlSerialize(value = "amount", orderNumber = 2)
    var amount: Int = 1
    /**
     * Particle speed.
     */
    @YamlSerialize(value = "speed", orderNumber = 3)
    var speed: Double = 1.0
    /**
     * Offset for the x coordinate.
     */
    @YamlSerialize(value = "offset", orderNumber = 4, implementation = Offset::class)
    var offset: Offset = Offset()
    /**
     * Material value.
     */
    @YamlSerialize(value = "material", orderNumber = 5)
    var materialName: String? = null
    /**
     * Data value.
     */
    @YamlSerialize(value = "data", orderNumber = 6)
    var data: Int = 0

    /**
     * RGB Color code of red.
     */
    var colorRed: Int
        get() = this.offset.x.toInt()
        set(value) {
            this.offset.x = value.toDouble()
        }

    /**
     * RGB Color code of green.
     */
    var colorGreen: Int
        get() = this.offset.y.toInt()
        set(value) {
            this.offset.y = value.toDouble()
        }
    /**
     * RGB Color code of blue.
     */
    var colorBlue: Int
        get() = this.offset.z.toInt()
        set(value) {
            this.offset.z = value.toDouble()
        }
}
