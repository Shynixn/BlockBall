package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.Offset
import com.github.shynixn.blockball.api.persistence.entity.Particle

class ParticleEntity(
    @YamlSerialize(value = "name", orderNumber = 1)
    override var typeName: String = ParticleType.NONE.name
) : Particle {
    /**
     * Database Id.
     */
    var id: Long = 0
    /**
     * Amount of particles.
     */
    @YamlSerialize(value = "amount", orderNumber = 2)
    override var amount: Int = 1
    /**
     * Particle speed.
     */
    @YamlSerialize(value = "speed", orderNumber = 3)
    override var speed: Double = 1.0
    /**
     * Offset for the x coordinate.
     */
    @YamlSerialize(value = "offset", orderNumber = 4, implementation = OffsetEntity::class)
    override var offset: Offset = OffsetEntity()
    /**
     * Material value.
     */
    @YamlSerialize(value = "material", orderNumber = 5)
    override var materialName: String? = null
    /**
     * Data value.
     */
    @YamlSerialize(value = "data", orderNumber = 6)
    override var data: Int = 0

    /**
     * RGB Color code of red.
     */
    override var colorRed: Int
        get() = this.offset.x.toInt()
        set(value) {
            this.offset.x = value.toDouble()
        }

    /**
     * RGB Color code of green.
     */
    override var colorGreen: Int
        get() = this.offset.y.toInt()
        set(value) {
            this.offset.y = value.toDouble()
        }
    /**
     * RGB Color code of blue.
     */
    override var colorBlue: Int
        get() = this.offset.z.toInt()
        set(value) {
            this.offset.z = value.toDouble()
        }
}
