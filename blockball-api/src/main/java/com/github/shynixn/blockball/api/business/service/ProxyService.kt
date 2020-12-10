package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.RaytraceResult
import java.util.stream.Stream

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
interface ProxyService {
    /**
     * Gets all available gamemodes.
     */
    val gameModes: List<String>

    /**
     * Teleports the player to the given location.
     */
    fun <P, L> teleport(player: P, location: L)

    /**
     * Kicks the given player with the given message.
     */
    fun <P> kickPlayer(player: P, message: String)

    /**
     * Is player online.
     */
    fun <P> isPlayerOnline(player: P): Boolean

    /**
     * Performs a player command.
     */
    fun <P> performPlayerCommand(player: P, command: String)

    /**
     * Performs a server command.
     */
    fun performServerCommand(command: String)

    /**
     * Gets the location of the entity.
     */
    fun <L, P> getEntityLocation(entity: P): L

    /**
     * Gets the player eye location.
     */
    fun <P, L> getPlayerEyeLocation(player: P): L

    /**
     * Gets the name of the World the player is in.
     */
    fun <P> getWorldName(player: P): String

    /**
     * Gets the world from name.
     */
    fun <W> getWorldFromName(name: String): W?

    /**
     * Gets the name of a player.
     */
    fun <P> getPlayerName(player: P): String

    /**
     * Gets the player uuid.
     */
    fun <P> getPlayerUUID(player: P): String

    /**
     * Sets the location of the player.
     */
    fun <L, P> setPlayerLocation(player: P, location: L)

    /**
     * Gets a copy of the player inventory.
     */
    fun <P> getPlayerInventoryCopy(player: P): Array<Any?>

    /**
     * Gets a copy of the player armor inventory.
     */
    fun <P> getPlayerInventoryArmorCopy(player: P): Array<Any?>

    /**
     * Sets the player gameMode.
     */
    fun <P> setGameMode(player: P, gameMode: GameMode)

    /**
     * Gets the player gameMode.
     */
    fun <P> getPlayerGameMode(player: P): GameMode

    /**
     * Sets the player flying.
     */
    fun <P> setPlayerFlying(player: P, enabled: Boolean)

    /**
     * Gets if the player is flying.
     */
    fun <P> getPlayerFlying(player: P): Boolean

    /**
     * Sets the player walkingSpeed.
     */
    fun <P> setPlayerWalkingSpeed(player: P, speed: Double)

    /**
     * Gets the player walkingSpeed.
     */
    fun <P> getPlayerWalkingSpeed(player: P): Double

    /**
     * Generates a new scoreboard.
     */
    fun <S> generateNewScoreboard(): S

    /**
     * Gets if the given instance is a player instance.
     */
    fun <P> isPlayerInstance(player: P): Boolean

    /**
     * Gets if the given instance is an itemFrame instance.
     */
    fun <E> isItemFrameInstance(entity: E): Boolean

    /**
     * Sets the player scoreboard.
     */
    fun <P, S> setPlayerScoreboard(player: P, scoreboard: S)

    /**
     * Sets the player velocity.
     */
    fun <P> setEntityVelocity(entity: P, position: Position)

    /**
     * Gets the player direction.
     */
    fun <P> getPlayerDirection(player: P): Position

    /**
     * Gets the location direction.
     */
    fun <L> getLocationDirection(location: L): Position

    /**
     * Gets the player scoreboard.
     */
    fun <P, S> getPlayerScoreboard(player: P): S

    /**
     * Sets if the player is allowed to fly.
     */
    fun <P> setPlayerAllowFlying(player: P, enabled: Boolean)

    /**
     * Gets if the player is allowed to fly.
     */
    fun <P> getPlayerAllowFlying(player: P): Boolean

    /**
     * Gets the player level.
     */
    fun <P> getPlayerLevel(player: P): Int

    /**
     * Gets the player exp.
     */
    fun <P> getPlayerExp(player: P): Double

    /**
     * Sets the player exp.
     */
    fun <P> setPlayerExp(player: P, exp: Double)

    /**
     * Sets the player level.
     */
    fun <P> setPlayerLevel(player: P, level: Int)

    /**
     * Gets the player max health.
     */
    fun <P> getPlayerMaxHealth(player: P): Double

    /**
     * Gets the player health.
     */
    fun <P> getPlayerHealth(player: P): Double

    /**
     * Gets the player hunger.
     */
    fun <P> getPlayerHunger(player: P): Int

    /**
     * Sets the given inventory items.
     */
    fun <P, I> setInventoryContents(player: P, mainInventory: Array<I>, armorInventory: Array<I>)

    /**
     * Gets a list of all online players.
     */
    fun <P> getOnlinePlayers(): List<P>

    /**
     * Sends a plugin message through the given channel.
     */
    fun <P> sendPlayerPluginMessage(player: P, channel: String, content: ByteArray)

    /**
     * Converts the given [location] to a [Position].
     */
    fun <L> toPosition(location: L): Position

    /**
     * Converts the given [position] to a [Location].
     */
    fun <L> toLocation(position: Position): L

    /**
     * Converts the given [position] to a [Vector].
     */
    fun <V> toVector(position: Position): V

    /**
     * Sets the location direction.
     */
    fun <L> setLocationDirection(location: L, position: Position)

    /**
     * Gets a list of players in the given world of the given location.
     */
    fun <P, L> getPlayersInWorld(location: L): List<P>

    /**
     * Gets a stream of entities in the given world of the given location.
     */
    fun <P, L> getEntitiesInWorld(location: L): Stream<Any>

    /**
     * Has player permission?
     */
    fun <P> hasPermission(player: P, permission: String): Boolean

    /**
     * Gets the custom name from an entity.
     */
    fun <E> getCustomNameFromEntity(entity: E): String?

    /**
     * Sends a chat message to the [sender].
     */
    fun <S> sendMessage(sender: S, chatBuilder: ChatBuilder)

    /**
     * Sends a message to the [sender].
     */
    fun <S> sendMessage(sender: S, message: String)

    /**
     * Sets the player max health.
     */
    fun setPlayerMaxHealth(player: Any, health: Double)

    /**
     * Sets the player health.
     */
    fun setPlayerHealth(player: Any, health: Double)

    /**
     * Sets the player hunger.
     */
    fun setPlayerHunger(player: Any, hunger: Int)

    /**
     * Sets the block type at the given location from the hint.
     */
    fun <L> setBlockType(location: L, hint: Any)

    /**
     * Sets the sign lines at the given location.
     * Return true if the block is valid sign with changed lines.
     */
    fun <L> setSignLines(location: L, lines: List<String>): Boolean

    /**
     * Creates a new entity id.
     */
    fun createNewEntityId(): Int

    /**
     * Sends the given [packet] to the given [player].
     */
    fun <P> sendPacket(player: P, packet: Any)
}
