@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.extension

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.Permission
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Returns if the given [player] has got this [Permission].
 */
internal fun Permission.hasPermission(player: Player): Boolean {
    return player.hasPermission(this.permission)
}

/**
 * Converts the given Location to a position.
 */
internal fun Location.toPosition(): Position {
    val position = PositionEntity()

    if (this.world != null) {
        position.worldName = this.world!!.name
    }

    position.x = this.x
    position.y = this.y
    position.z = this.z
    position.yaw = this.yaw.toDouble()
    position.pitch = this.pitch.toDouble()

    return position
}

/**
 * Finds the version compatible NMS class.
 */
fun findClazz(name: String): Class<*> {
    return Class.forName(
        name.replace(
            "VERSION",
            BlockBallApi.resolve(PluginProxy::class.java).getServerVersion().bukkitId
        )
    )
}

/**
 * Converts the given position to a bukkit Location.
 */
internal fun Position.toLocation(): Location {
    return Location(Bukkit.getWorld(this.worldName!!), this.x, this.y, this.z, this.yaw.toFloat(), this.pitch.toFloat())
}

/**
 * Converts the given position to a bukkit vector.
 */
internal fun Position.toVector(): Vector {
    return Vector(this.x, this.y, this.z)
}

/**
 * Converts the given bukkit vector to a position.
 */
internal fun Vector.toPosition(): Position {
    return PositionEntity(this.x, this.y, this.z)
}
