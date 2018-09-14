@file:Suppress("unused", "DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.extension

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.proxy.HighlightArmorstandProxy
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.nms.MaterialCompatibility13
import com.github.shynixn.blockball.bukkit.logic.business.nms.VersionSupport
import com.github.shynixn.blockball.bukkit.logic.business.proxy.HighlightArmorstandProxyImpl
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PositionEntity
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException

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
 * Executes the given [f] for the given [plugin] on main thread.
 */
inline fun Any.sync(plugin: Plugin, delayTicks: Long = 0L, repeatingTicks: Long = 0L, crossinline f: () -> Unit) {
    if (repeatingTicks > 0) {
        plugin.server.scheduler.runTaskTimer(plugin, {
            f.invoke()
        }, delayTicks, repeatingTicks)
    } else {
        plugin.server.scheduler.runTaskLater(plugin, {
            f.invoke()
        }, delayTicks)
    }
}

/**
 * Convers the type into bukkit material
 */
fun MaterialType.toBukkitMaterial(): Material {
    return MaterialCompatibility13.getBukkitMaterial(this)
}

/**
 * Finds the corresponding version.
 */
fun VersionSupport.toVersion(): Version {
    return Version.values().find { v -> this.simpleVersionText == v.id }!!
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
    meta.displayName = displayName.convertChatColors()
    itemMeta = meta
    return this
}

/**
 * Executes the given [f] for the given [plugin] asynchronly.
 */
inline fun Any.async(plugin: Plugin, delayTicks: Long = 0L, repeatingTicks: Long = 0L, crossinline f: () -> Unit) {
    if (repeatingTicks > 0) {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, {
            f.invoke()
        }, delayTicks, repeatingTicks)
    } else {
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, {
            f.invoke()
        }, delayTicks)
    }
}

/**
 * Sets the [Server] modt with the given [text].
 */
internal fun Server.setModt(text: String) {
    val builder = java.lang.StringBuilder("[")
    builder.append((text.replace("[", "").replace("]", "")))
    builder.append(ChatColor.RESET.toString())
    builder.append("]")

    val minecraftServerClazz = Class.forName("net.minecraft.server.VERSION.MinecraftServer".replace("VERSION", VersionSupport.getServerVersion().versionText))
    val craftServerClazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", VersionSupport.getServerVersion().versionText))

    val setModtMethod = minecraftServerClazz.getDeclaredMethod("setMotd", String::class.java)
    val getServerConsoleMethod = craftServerClazz.getDeclaredMethod("getServer")

    val console = getServerConsoleMethod!!.invoke(Bukkit.getServer())
    setModtMethod!!.invoke(console, builder.toString().convertChatColors())
}

/**
 * Refactors a list to a single line.
 */
internal fun List<String>.toSingleLine(): String {
    val builder = StringBuilder()
    this.forEachIndexed { index, p ->
        builder.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', p))
        if (index + 1 != this.size)
            builder.append('\n')
        builder.append(org.bukkit.ChatColor.RESET)
    }
    return builder.toString()
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

    val stateSignEnabled = plugin.config.getString("messages.state-sign-enabled").convertChatColors()
    val stateSignDisabled = plugin.config.getString("messages.state-sign-disabled").convertChatColors()
    val stateSignRunning = plugin.config.getString("messages.state-sign-running").convertChatColors()

    when {
        game.status == GameStatus.RUNNING -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignRunning)
        game.status == GameStatus.ENABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignEnabled)
        game.status == GameStatus.DISABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, stateSignDisabled)
    }

    if (game.arena.gameType == GameType.HUBGAME) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, "∞")
    } else if (game is MiniGame) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, game.gameCountdown.toString())
                .replace(PlaceHolder.REMAINING_PLAYERS_TO_START.placeHolder, (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingamePlayersStorage.size).toString())
    }

    if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
        cache = cache.replace(PlaceHolder.LASTHITBALL.placeHolder, (game.lastInteractedEntity as Player).name)
    }

    return cache.convertChatColors()
}

/**
 * Sets the color of the itemstack if it has a leather meta.
 */
internal fun ItemStack.setColor(color: Color): ItemStack {
    if (this.itemMeta is LeatherArmorMeta) {
        val leatherMeta = this.itemMeta as LeatherArmorMeta
        leatherMeta.color = color
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
    if (location.world.name == this.upperCorner.worldName) {
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
@Throws(ClassNotFoundException::class, IllegalAccessException::class, NoSuchMethodException::class, InvocationTargetException::class, NoSuchFieldException::class)
fun Player.sendPacket(packet: Any) {
    val version = VersionSupport.getServerVersion()
    val craftPlayer = Class.forName("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer".replace("VERSION", version.versionText)).cast(player)
    val methodHandle = craftPlayer.javaClass.getDeclaredMethod("getHandle")
    val entityPlayer = methodHandle.invoke(craftPlayer)

    val field = Class.forName("net.minecraft.server.VERSION.EntityPlayer".replace("VERSION", version.versionText)).getDeclaredField("playerConnection")
    field.isAccessible = true
    val connection = field.get(entityPlayer)

    val sendMethod = connection.javaClass.getDeclaredMethod("sendPacket", packet.javaClass.interfaces[0])
    sendMethod.invoke(connection, packet)
}

/**
 * Spawns an armorstand proxy entity for the given [player] at the given [location].
 */
fun World.spawnVirtualArmorstand(player: Player, location: Location): HighlightArmorstandProxy {
    val highlightArmorstandProxy = HighlightArmorstandProxyImpl(player.uniqueId, location)
    highlightArmorstandProxy.spawn()
    return highlightArmorstandProxy
}

/**
 * Converts the chatcolors of this string.
 */
internal fun String.convertChatColors(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

/**
 * Removes the chatColors.
 */
internal fun String.stripChatColors(): String {
    return ChatColor.stripColor(this)
}

/**
 * Tries to return the [ParticleType] from the given name.
 */
fun String.toParticleType(): ParticleType {
    ParticleType.values().forEach { p ->
        if (p.gameId_18.equals(this, true) || p.gameId_113.equals(this, true) || p.name.equals(this, true) || p.minecraftId_112.equals(this, true)) {
            return p
        }
    }

    throw IllegalArgumentException("ParticleType cannot be parsed from '" + this + "'.")
}

/**
 * Converts the given Location to a position.
 */
internal fun Location.toPosition(): Position {
    val position = PositionEntity()
    position.worldName = this.world.name
    position.x = this.x
    position.y = this.y
    position.z = this.z
    position.yaw = this.yaw.toDouble()
    position.pitch = this.pitch.toDouble()

    return position
}

/**
 * Converts the given position to a bukkit Location.
 */
internal fun Position.toLocation(): Location {
    return Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z, this.yaw.toFloat(), this.pitch.toFloat())
}