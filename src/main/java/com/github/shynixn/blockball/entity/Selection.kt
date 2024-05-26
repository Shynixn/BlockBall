package com.github.shynixn.blockball.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection

@JsonIgnoreProperties("center", "offsetX", "offsetY", "offsetZ")
open class Selection {

    /** [upperCorner] of the selected square arena. */
    @JsonProperty("corner1")
    final var upperCorner: Vector3d = Vector3d()

    /** [lowerCorner] of the selected square arena. */
    @JsonProperty("corner2")
    final var lowerCorner: Vector3d = Vector3d()

    /** [center] of the arena */
    val center: Vector3d
        get() {
            return Vector3d(
                this.lowerCorner.world!!,
                (this.lowerCorner.blockX + this.offsetX / 2).toDouble(),
                (this.lowerCorner.blockY + offsetX / 2).toDouble(), (this.lowerCorner.blockZ + offsetZ / 2).toDouble()
            )
        }

    /** Length of the x axe. */
    val offsetX: Int
        get() {
            return this.upperCorner.blockX - this.lowerCorner.blockX + 1
        }

    /** Length of the y axe. */
    val offsetY: Int
        get() {
            return this.upperCorner.blockY - this.lowerCorner.blockY + 1
        }

    /** Length of the z axe. */
    val offsetZ: Int
        get() {
            return this.upperCorner.blockZ - this.lowerCorner.blockZ
        }


    /** Sets the corners between [corner1] and [corner2]. Automatically sets lowerCorner and upperCorner. */
    fun setCorners(corner1: Vector3d, corner2: Vector3d) {
        this.calculateDownLocation(corner1, corner2)
        this.calculateUpLocation(corner1, corner2)
    }

    /**
     * Is location inside of this selection.
     */
    fun isLocationInSelection(location: Vector3d): Boolean {
        if (location.world != null && location.world == this.upperCorner.world) {
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
    fun getRelativeBlockDirectionToLocation(location: Vector3d): BlockDirection {
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

    private fun calculateUpLocation(corner1: Vector3d, corner2: Vector3d) {
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
        this.upperCorner = Vector3d(corner1.world!!, x.toDouble(), y.toDouble(), z.toDouble())
    }

    private fun calculateDownLocation(corner1: Vector3d, corner2: Vector3d) {
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
        this.lowerCorner = Vector3d(corner1.world!!, x.toDouble(), y.toDouble(), z.toDouble())
    }
}
