package com.github.shynixn.blockball.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection

@JsonIgnoreProperties("center", "offsetX", "offsetY", "offsetZ")
open class Selection {

    /** [upperCorner] of the selected square soccerArena. */
    @JsonProperty("corner1")
    var upperCorner: Vector3d? = null

    /** [lowerCorner] of the selected square soccerArena. */
    @JsonProperty("corner2")
    var lowerCorner: Vector3d? = null

    /** [center] of the soccerArena */
    val center: Vector3d
        get() {
            return Vector3d(
                this.lowerCorner!!.world!!,
                (this.lowerCorner!!.blockX + this.offsetX / 2).toDouble(),
                (this.lowerCorner!!.blockY + offsetX / 2).toDouble(),
                (this.lowerCorner!!.blockZ + offsetZ / 2).toDouble()
            )
        }

    /** Length of the x axe. */
    val offsetX: Int
        get() {
            return this.upperCorner!!.blockX - this.lowerCorner!!.blockX + 1
        }

    /** Length of the y axe. */
    val offsetY: Int
        get() {
            return this.upperCorner!!.blockY - this.lowerCorner!!.blockY + 1
        }

    /** Length of the z axe. */
    val offsetZ: Int
        get() {
            return this.upperCorner!!.blockZ - this.lowerCorner!!.blockZ
        }

    /**
     * Is location inside of this selection.
     */

    fun isLocationIn2dSelection(location: Vector3d): Boolean {
        if (location.world != null && location.world == this.upperCorner!!.world) {
            if (this.upperCorner!!.x >= location.x && this.lowerCorner!!.x <= location.x) {
                if (this.upperCorner!!.z >= location.z && this.lowerCorner!!.z <= location.z) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Is location inside of this selection.
     */
    fun isLocationInSelection(location: Vector3d): Boolean {
        if (location.world != null && location.world == this.upperCorner!!.world) {
            if (this.upperCorner!!.x >= location.x && this.lowerCorner!!.x <= location.x) {
                if (this.upperCorner!!.y >= location.y + 1 && this.lowerCorner!!.y <= location.y + 1) {
                    if (this.upperCorner!!.z >= location.z && this.lowerCorner!!.z <= location.z) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * If the given location is outside the soccerArena it returns the block direction
     * in which the soccerArena can be reached.
     */
    fun getRelativeBlockDirectionToLocation(location: Vector3d): BlockDirection {
        if (location.blockX >= upperCorner!!.blockX && this.upperCorner!!.z >= location.z && this.lowerCorner!!.z <= location.z) {
            return BlockDirection.WEST
        }

        if (location.blockX <= lowerCorner!!.blockX && this.upperCorner!!.z >= location.z && this.lowerCorner!!.z <= location.z) {
            return BlockDirection.EAST
        }

        if (location.blockZ >= upperCorner!!.blockZ && this.upperCorner!!.x >= location.x && this.lowerCorner!!.x <= location.x) {
            return BlockDirection.NORTH
        }

        if (location.blockZ <= lowerCorner!!.blockZ && this.upperCorner!!.x >= location.x && this.lowerCorner!!.x <= location.x) {
            return BlockDirection.SOUTH
        }

        return BlockDirection.DOWN
    }
}
