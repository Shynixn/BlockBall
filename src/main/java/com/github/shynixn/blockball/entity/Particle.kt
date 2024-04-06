package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.ParticleType


class Particle(
  var typeName: String = ParticleType.NONE.name
) {
    /**
     * Database Id.
     */
    var id: Long = 0
    /**
     * Amount of particles.
     */
    var amount: Int = 1
    /**
     * Particle speed.
     */
    var speed: Double = 1.0
    /**
     * Offset for the x coordinate.
     */
    var offset: Offset = Offset()
    /**
     * Material value.
     */
    var materialName: String? = null
    /**
     * Data value.
     */
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
