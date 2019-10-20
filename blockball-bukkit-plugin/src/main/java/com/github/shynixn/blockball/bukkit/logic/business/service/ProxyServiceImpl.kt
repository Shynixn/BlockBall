@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.bukkit.logic.business.extension.sendPacket
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.logging.Level

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class ProxyServiceImpl @Inject constructor(private val pluginProxy: PluginProxy) : ProxyService {

    /**
     * Gets the name of the World the player is in.
     */
    override fun <P> getWorldName(player: P): String {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.world.name
    }

    /**
     * Gets the name of a player.
     */
    override fun <P> getPlayerName(player: P): String {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.name
    }

    /**
     * Gets the player uuid.
     */
    override fun <P> getPlayerUUID(player: P): String {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.uniqueId.toString()
    }

    /**
     * Gets a list of all online players.
     */
    override fun <P> getOnlinePlayers(): List<P> {
        val players = ArrayList<Player>()

        for (world in Bukkit.getWorlds()) {
            players.addAll(world.players)
        }

        return players as List<P>
    }

    /**
     * Sends a plugin message through the given channel.
     */
    override fun <P> sendPlayerPluginMessage(player: P, channel: String, content: ByteArray) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        player.sendPluginMessage(this.pluginProxy as Plugin, "BungeeCord", content)
    }

    /**
     * Gets all available gamemodes.
     */
    override val gameModes: List<String>
        get() {
            return GameMode.values().map { g -> g.name }
        }

    /**
     * Performs a player command.
     */
    override fun <P> performPlayerCommand(player: P, command: String) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        player.performCommand(command)
    }

    /**
     * Sets the location of the player.
     */
    override fun <L, P> setPlayerLocation(player: P, location: L) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        if (location is Position) {
            player.teleport(location.toLocation())
            return
        }

        if (location is Location) {
            player.teleport(location)
            return
        }

        throw IllegalArgumentException("Location has to be a position or location!")
    }

    /**
     * Gets the location of the player.
     */
    override fun <L, P> getPlayerLocation(player: P): L {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.location as L
    }

    /**
     * Gets a copy of the player inventory.
     */
    override fun <P> getPlayerInventoryCopy(player: P): Array<Any?> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.inventory.contents.clone() as Array<Any?>
    }

    /**
     * Gets a copy of the player armor inventory.
     */
    override fun <P> getPlayerInventoryArmorCopy(player: P): Array<Any?> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return player.inventory.armorContents.clone() as Array<Any?>
    }

    /**
     * Converts the given [location] to a [Position].
     */
    override fun <L> toPosition(location: L): Position {
        if (location !is Location) {
            throw IllegalArgumentException("Location has to be a BukkitLocation!")
        }

        return location.toPosition()
    }

    /**
     * Sends a chat message to the [sender].
     */
    override fun <S> sendMessage(sender: S, chatBuilder: ChatBuilder) {
        if (sender !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        try {
            val clazz: Class<*> = if (pluginProxy.getServerVersion() == Version.VERSION_1_8_R1) {
                findClazz("net.minecraft.server.VERSION.ChatSerializer")
            } else {
                findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer")
            }

            val packetClazz = findClazz("net.minecraft.server.VERSION.PacketPlayOutChat")
            val chatBaseComponentClazz = findClazz("net.minecraft.server.VERSION.IChatBaseComponent")
            val chatComponent = clazz.getDeclaredMethod("a", String::class.java).invoke(null, chatBuilder.toString())

            val packet = if (pluginProxy.getServerVersion().isVersionSameOrGreaterThan(Version.VERSION_1_12_R1)) {
                val chatEnumMessage = findClazz("net.minecraft.server.VERSION.ChatMessageType")
                packetClazz.getDeclaredConstructor(chatBaseComponentClazz, chatEnumMessage).newInstance(chatComponent, chatEnumMessage.enumConstants[0])
            } else {
                packetClazz.getDeclaredConstructor(chatBaseComponentClazz, Byte::class.javaPrimitiveType as Class<*>).newInstance(chatComponent, 0.toByte())
            }

            sender.sendPacket(packet)
        } catch (e: Exception) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to send packet.", e)
        }
    }

    /**
     * Sends a message to the [sender].
     */
    override fun <S> sendMessage(sender: S, message: String) {
        if (sender is CommandSender) {
            sender.sendMessage(message)
            return
        }

        if (sender is Player) {
            sender.sendMessage(message)
        }
    }
}