@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.extension

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import org.bukkit.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.Vector

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

/**
 * Updates this inventory.
 */
fun PlayerInventory.updateInventory() {
    (this.holder as Player).updateInventory()
}

/**
 * Is the player touching the ground?
 */
fun Player.isTouchingGround(): Boolean {
    return this.isOnGround
}

/**
 * Set displayname.
 */
fun ItemStack.setDisplayName(displayName: String): ItemStack {
    val meta = itemMeta

    if (meta != null) {
        @Suppress("UsePropertyAccessSyntax")
        meta.setDisplayName(displayName.translateChatColors())
        itemMeta = meta
    }

    return this
}


/**
 * Returns if the given [player] has got this [Permission].
 */
internal fun Permission.hasPermission(player: Player): Boolean {
    return player.hasPermission(this.permission)
}

/**
 * Sends the given [packet] to this player.
 */
fun Player.sendPacket(packet: Any) {
    BlockBallApi.resolve(PackageService::class.java).sendPacket(this, packet)
}

/**
 * Removes the chatColors.
 */
internal fun String.stripChatColors(): String {
    return ChatColor.stripColor(this)!!
}

/**
 * Sets the skin of an itemstack.
 */
internal fun ItemStack.setSkin(skin: String) {
    BlockBallApi.resolve(ItemService::class.java).setSkin(this, skin)
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
    return Class.forName(name.replace("VERSION", BlockBallApi.resolve(PluginProxy::class.java).getServerVersion().bukkitId))
}

/**
 * Converts the given gamemode to a bukkit gamemode.
 */
internal fun GameMode.toGameMode(): org.bukkit.GameMode {
    return org.bukkit.GameMode.valueOf(this.name)
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