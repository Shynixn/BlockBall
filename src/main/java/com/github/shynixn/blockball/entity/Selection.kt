package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection

open class Selection {

    var corner1: Vector3d? = null

    var corner2: Vector3d? = null

    /** [center] of the soccerArena */
    val center: Vector3d
        get() {
            return Vector3d(
                this.corner2!!.world!!,
                (this.corner2!!.blockX + this.offsetX / 2).toDouble(),
                (this.corner2!!.blockY + offsetX / 2).toDouble(),
                (this.corner2!!.blockZ + offsetZ / 2).toDouble()
            )
        }

    /** Length of the x axe. */
    val offsetX: Int
        get() {
            return this.corner1!!.blockX - this.corner2!!.blockX + 1
        }

    /** Length of the y axe. */
    val offsetY: Int
        get() {
            return this.corner1!!.blockY - this.corner2!!.blockY + 1
        }

    /** Length of the z axe. */
    val offsetZ: Int
        get() {
            return this.corner1!!.blockZ - this.corner2!!.blockZ
        }

    /**
     * Is location inside of this selection.
     */

    fun isLocationIn2dSelection(location: Vector3d): Boolean {
        if (location.world != null && location.world == this.corner1!!.world) {
            if (this.corner1!!.x >= location.x && this.corner2!!.x <= location.x) {
                if (this.corner1!!.z >= location.z && this.corner2!!.z <= location.z) {
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
        if (location.world != null && location.world == this.corner1!!.world) {
            if (this.corner1!!.x >= location.x && this.corner2!!.x <= location.x) {
                if (this.corner1!!.y >= location.y + 1 && this.corner2!!.y <= location.y + 1) {
                    if (this.corner1!!.z >= location.z && this.corner2!!.z <= location.z) {
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
        if (location.blockX >= corner1!!.blockX && this.corner1!!.z >= location.z && this.corner2!!.z <= location.z) {
            return BlockDirection.WEST
        }

        if (location.blockX <= corner2!!.blockX && this.corner1!!.z >= location.z && this.corner2!!.z <= location.z) {
            return BlockDirection.EAST
        }

        if (location.blockZ >= corner1!!.blockZ && this.corner1!!.x >= location.x && this.corner2!!.x <= location.x) {
            return BlockDirection.NORTH
        }

        if (location.blockZ <= corner2!!.blockZ && this.corner1!!.x >= location.x && this.corner2!!.x <= location.x) {
            return BlockDirection.SOUTH
        }

        return BlockDirection.DOWN
    }
}
