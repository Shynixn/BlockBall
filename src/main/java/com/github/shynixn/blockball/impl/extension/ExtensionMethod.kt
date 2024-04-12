@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.impl.extension

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.entity.Position
import com.github.shynixn.blockball.entity.Sound
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.sound.SoundMeta
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.util.Vector

/**
 * Returns if the given [player] has got this [Permission].
 */
internal fun Permission.hasPermission(player: Player): Boolean {
    return player.hasPermission(this.permission)
}


/**
 * Strips the chat colors from the string.
 */
fun String.stripChatColors(): String {
    return ChatColor.stripChatColors(this)
}

fun Location.setSignLines(lines: List<String>): Boolean {
    val location = this

    if (!location.world!!.isChunkLoaded(location.blockX shr 4, location.blockZ shr 4)) {
        return true
    }

    if (location.block.state !is Sign) {
        return false
    }

    val sign = location.block.state as Sign

    for (i in lines.indices) {
        val text = lines[i]
        sign.setLine(i, text)
    }

    sign.update(true)
    return true
}

fun Sound.toSoundMeta(): SoundMeta {
    val input = this
    return SoundMeta().also {
        it.name = input.name
        it.pitch = input.pitch
        it.volume = input.volume
        it.effectType = input.effectingType
    }
}

/**
 * Merges arguments starting from [starting] to [amount] from the given [args].
 */
fun CommandExecutor.mergeArgs(starting: Int, amount: Int, args: Array<out String>): String {
    val builder = StringBuilder()
    var counter = 0
    var i = starting

    while (counter < amount) {
        if (builder.isNotEmpty()) {
            builder.append(' ')
        }

        if (i < args.size) {
            builder.append(args[i].stripChatColors())
        }

        counter++
        i++
    }

    return builder.toString()
}


/**
 * Converts the given Location to a position.
 */
internal fun Location.toPosition(): Position {
    val position = Position()

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
    return Version.findClass(name)
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
    return Position(this.x, this.y, this.z)
}
