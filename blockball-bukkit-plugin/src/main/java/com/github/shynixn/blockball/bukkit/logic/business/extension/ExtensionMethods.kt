@file:Suppress("unused", "DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.extension

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ItemService
import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import org.bukkit.*
import org.bukkit.ChatColor
import org.bukkit.configuration.MemorySection
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.lang.reflect.Method

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
 * Deserializes the configuraiton section path to a map.
 */
fun FileConfiguration.deserializeToMap(path: String): Map<String, Any?> {
    val section = getConfigurationSection(path)!!.getValues(false)
    deserialize(section)
    return section
}

/**
 * Deserializes the given section.
 */
private fun deserialize(section: MutableMap<String, Any?>) {
    section.keys.forEach { key ->
        if (section[key] is MemorySection) {
            val map = (section[key] as MemorySection).getValues(false)
            deserialize(map)
            section[key] = map
        }
    }
}

private val getIdFromMaterialMethod: Method = { Material::class.java.getDeclaredMethod("getId") }.invoke()

/**
 * Lazy convertion.
 */
fun Material.toCompatibilityId(): Int {
    for (material in Material.values()) {
        if (material == this) {
            return getIdFromMaterialMethod(material) as Int
        }
    }

    throw IllegalArgumentException("Material id not found!")
}

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
 * Converts all placeholders.
 */
internal fun String.replaceGamePlaceholder(game: Game, teamMeta: TeamMeta? = null, team: List<Player>? = null): String {
    val plugin = JavaPlugin.getPlugin(BlockBallPlugin::class.java)
    var cache = this.replace(PlaceHolder.TEAM_RED.placeHolder, game.arena.meta.redTeamMeta.displayName)
        .replace(PlaceHolder.ARENA_DISPLAYNAME.placeHolder, game.arena.displayName)
        .replace(PlaceHolder.TEAM_BLUE.placeHolder, game.arena.meta.blueTeamMeta.displayName)
        .replace(PlaceHolder.RED_COLOR.placeHolder, game.arena.meta.redTeamMeta.prefix)
        .replace(PlaceHolder.BLUE_COLOR.placeHolder, game.arena.meta.blueTeamMeta.prefix)
        .replace(PlaceHolder.RED_GOALS.placeHolder, game.redScore.toString())
        .replace(PlaceHolder.BLUE_GOALS.placeHolder, game.blueScore.toString())
        .replace(PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder, game.ingamePlayersStorage.size.toString())
        .replace(PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder, (game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount).toString())


    if (teamMeta != null) {
        cache = cache.replace(PlaceHolder.ARENA_TEAMCOLOR.placeHolder, teamMeta.prefix)
            .replace(PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder, teamMeta.displayName)
            .replace(PlaceHolder.ARENA_MAX_PLAYERS_ON_TEAM.placeHolder, teamMeta.maxAmount.toString())
    }

    if (team != null) {
        cache = cache.replace(PlaceHolder.ARENA_PLAYERS_ON_TEAM.placeHolder, team.size.toString())
    }

    val stateSignEnabled = plugin.config.getString("messages.state-sign-enabled")!!.translateChatColors()
    val stateSignDisabled = plugin.config.getString("messages.state-sign-disabled")!!.translateChatColors()
    val stateSignRunning = plugin.config.getString("messages.state-sign-running")!!.translateChatColors()

    when {
        game.status == GameStatus.RUNNING -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignRunning)
        game.status == GameStatus.ENABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignEnabled)
        game.status == GameStatus.DISABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignDisabled)
    }

    if (game.arena.gameType == GameType.HUBGAME) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, "∞")
    } else if (game is MiniGame) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, game.gameCountdown.toString())
            .replace(
                PlaceHolder.REMAINING_PLAYERS_TO_START.placeHolder,
                (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size).toString()
            )
    }

    if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
        cache = cache.replace(PlaceHolder.LASTHITBALL.placeHolder, (game.lastInteractedEntity as Player).name)
    }

    return cache.translateChatColors()
}

/**
 * Sets the color of the itemstack if it has a leather meta.
 */
internal fun ItemStack.setColor(color: Color): ItemStack {
    if (this.itemMeta is LeatherArmorMeta) {
        val leatherMeta = this.itemMeta as LeatherArmorMeta
        @Suppress("UsePropertyAccessSyntax")
        leatherMeta.setColor(color)
        this.itemMeta = leatherMeta
    }
    return this
}

/**
 * Returns if the given [player] has got this [Permission].
 */
internal fun Permission.hasPermission(player: Player): Boolean {
    return player.hasPermission(this.permission)
}

/** Returns if the given [location] is inside of this area selection. */
fun Selection.isLocationInSelection(location: Location): Boolean {
    if (location.world != null && location.world!!.name == this.upperCorner.worldName) {
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