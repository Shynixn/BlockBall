@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.impl.extension

import com.github.shynixn.blockball.contract.CommandExecutor
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.sound.SoundMeta
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.entity.Player

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
