package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.BlockDirection
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.Selection

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
open class SelectionEntity : Selection {

    /** [upperCorner] of the selected square arena. */
    @YamlSerialize(value = "corner-1", orderNumber = 5, implementation = PositionEntity::class)
    final override var upperCorner: Position = PositionEntity()

    /** [lowerCorner] of the selected square arena. */
    @YamlSerialize(value = "corner-2", orderNumber = 6, implementation = PositionEntity::class)
    final override var lowerCorner: Position = PositionEntity()

    /** [center] of the arena */
    override val center: Position
        get() {
            return PositionEntity(
                this.lowerCorner.worldName!!,
                (this.lowerCorner.blockX + this.offsetX / 2).toDouble(),
                (this.lowerCorner.blockY + offsetX / 2).toDouble(), (this.lowerCorner.blockZ + offsetZ / 2).toDouble()
            )
        }

    /** Length of the x axe. */
    override val offsetX: Int
        get() {
            return this.upperCorner.blockX - this.lowerCorner.blockX + 1
        }

    /** Length of the y axe. */
    override val offsetY: Int
        get() {
            return this.upperCorner.blockY - this.lowerCorner.blockY + 1
        }

    /** Length of the z axe. */
    override val offsetZ: Int
        get() {
            return this.upperCorner.blockZ - this.lowerCorner.blockZ
        }


    /** Sets the corners between [corner1] and [corner2]. Automatically sets lowerCorner and upperCorner. */
    override fun setCorners(corner1: Position, corner2: Position) {
        this.calculateDownLocation(corner1, corner2)
        this.calculateUpLocation(corner1, corner2)
    }

    /**
     * Is location inside of this selection.
     */
    override fun isLocationInSelection(location: Position): Boolean {
        if (location.worldName != null && location.worldName == this.upperCorner.worldName) {
            if (this.upperCorner.x >= location.x && this.lowerCorner.x <= location.x) {
                if (this.upperCorner.y >= location.y + 1 && this.lowerCorner.y <= location.y + 1) {
                    if (this.upperCorner.z >= location.z && this.lowerCorner.z <= location.z) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * If the given location is outside the arena it returns the block direction
     * in which the arena can be reached.
     */
    override fun getRelativeBlockDirectionToLocation(location: Position): BlockDirection {
        if (location.blockX >= upperCorner.blockX && this.upperCorner.z >= location.z && this.lowerCorner.z <= location.z) {
            return BlockDirection.WEST
        }

        if (location.blockX <= lowerCorner.blockX && this.upperCorner.z >= location.z && this.lowerCorner.z <= location.z) {
            return BlockDirection.EAST
        }

        if (location.blockZ >= upperCorner.blockZ && this.upperCorner.x >= location.x && this.lowerCorner.x <= location.x) {
            return BlockDirection.NORTH
        }

        if (location.blockZ <= lowerCorner.blockZ && this.upperCorner.x >= location.x && this.lowerCorner.x <= location.x) {
            return BlockDirection.SOUTH
        }

        return BlockDirection.DOWN
    }

    private fun calculateUpLocation(corner1: Position, corner2: Position) {
        val x: Int = if (corner1.blockX > corner2.blockX) {
            corner1.blockX
        } else {
            corner2.blockX
        }
        val y: Int = if (corner1.blockY > corner2.blockY) {
            corner1.blockY
        } else {
            corner2.blockY
        }
        val z: Int = if (corner1.blockZ > corner2.blockZ) {
            corner1.blockZ
        } else {
            corner2.blockZ
        }
        this.upperCorner = PositionEntity(corner1.worldName!!, x.toDouble(), y.toDouble(), z.toDouble())
    }

    private fun calculateDownLocation(corner1: Position, corner2: Position) {
        val x: Int = if (corner1.blockX < corner2.blockX) {
            corner1.blockX
        } else {
            corner2.blockX
        }
        val y: Int = if (corner1.blockY < corner2.blockY) {
            corner1.blockY
        } else {
            corner2.blockY
        }
        val z: Int = if (corner1.blockZ < corner2.blockZ) {
            corner1.blockZ
        } else {
            corner2.blockZ
        }
        this.lowerCorner = PositionEntity(corner1.worldName!!, x.toDouble(), y.toDouble(), z.toDouble())
    }
}
