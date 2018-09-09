package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.compatibility.BallData
import org.bukkit.configuration.MemorySection

@Suppress("unused")
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
class BallMetaEntity : BallData, BallMeta {
    /** Spawning delay. */
    override var delayInTicks: Int = 0
    /** Spawnpoint of the ball. */
    override var spawnpoint: Position? = null

    constructor(skin: String) : super(skin)

    /**
     * Deserializes a ballData.
     *
     * @param data data
     */
    constructor(data: Map<String, Any>) : super(data) {
        this.delayInTicks = data["spawn-delay"] as Int
        this.spawnpoint = YamlSerializer.deserializeObject(PositionEntity::class.java, PositionEntity::class.java, (data["spawnpoint"] as MemorySection).getValues(false))
    }

    /**
     * Serializes the given content.
     *
     * @return serializedContent
     */
    override fun serialize(): Map<String, Any> {
        val data = super.serialize()
        data["spawn-delay"] = this.delayInTicks
        data["spawnpoint"] = YamlSerializer.serialize(this.spawnpoint as PositionEntity)
        return data
    }
}