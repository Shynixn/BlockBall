package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.Vector3d
import org.bukkit.entity.Player
import kotlin.math.max
import kotlin.math.min

class ForceField(corner1: Vector3d, corner2: Vector3d) {
    val lowerCorner: Vector3d =
        Vector3d(corner1.world, min(corner1.x, corner2.x), min(corner1.y, corner2.y), min(corner1.z, corner2.z))
    val upperCorner: Vector3d =
        Vector3d(corner2.world, max(corner1.x, corner2.x), max(corner1.y, corner2.y), max(corner1.z, corner2.z))
    val center: Vector3d = Vector3d(
        lowerCorner.world,
        (lowerCorner.x + upperCorner.x) / 2,
        (lowerCorner.y + upperCorner.y) / 2,
        (lowerCorner.z + upperCorner.z) / 2
    )
    var on3dInside: (Player) -> Unit = { _ -> }
    var on3dOutSide: (Player) -> Unit = { _ -> }
    var on2dInside: (Player) -> Unit = { _ -> }
    var on2dOutSide: (Player) -> Unit = { _ -> }

    constructor(center: Vector3d, radius: Double) : this(
        Vector3d(center.world, center.x - radius, center.y - radius, center.z - radius),
        Vector3d(center.world, center.x + radius, center.y + radius, center.z + radius)
    )
}