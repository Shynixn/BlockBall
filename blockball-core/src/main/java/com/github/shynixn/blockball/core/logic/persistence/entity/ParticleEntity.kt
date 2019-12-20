package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.Offset
import com.github.shynixn.blockball.api.persistence.entity.Particle

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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