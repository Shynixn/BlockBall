package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.InvocationTargetException
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
class ProxyServiceImpl : ProxyService {
    /**
     * Gets all available gamemodes.
     */
    override val gameModes: List<String>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    /**
     * Performs a player command.
     */
    override fun <P> performPlayerCommand(player: P, command: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets the location of the player.
     */
    override fun <L, P> getPlayerLocation(player: P): L {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets a copy of the player inventory.
     */
    override fun <P> getPlayerInventoryCopy(player: P): Array<Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets a copy of the player armor inventory.
     */
    override fun <P> getPlayerInventoryArmorCopy(player: P): Array<Any?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Converts the given [location] to a [Position].
     */
    override fun <L> toPosition(location: L): Position {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Sends a chat message to the [sender].
     */
    override fun <S> sendMessage(sender: S, chatBuilder: ChatBuilder) {
        try {
            val version = JavaPlugin.getPlugin(BlockBallPlugin::class.java).getServerVersion()

            val clazz: Class<*> = if (Bukkit.getServer().javaClass.getPackage().name.replace(
                    ".",
                    ","
                ).split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3] == "v1_8_R1"
            ) {
                findClass("net.minecraft.server.VERSION.ChatSerializer")
            } else {
                findClass("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer")
            }
            val packetClazz = findClass("net.minecraft.server.VERSION.PacketPlayOutChat")
            val chatBaseComponentClazz = findClass("net.minecraft.server.VERSION.IChatBaseComponent")
            val chatComponent = invokeMethod(null, clazz, "a", arrayOf(String::class.java), arrayOf(chatBuilder.toString()))
            val packet: Any
            if (version.isVersionSameOrGreaterThan(Version.VERSION_1_12_R1)) {
                val chatEnumMessage = findClass("net.minecraft.server.VERSION.ChatMessageType")
                packet = invokeConstructor(
                    packetClazz,
                    arrayOf(chatBaseComponentClazz, chatEnumMessage),
                    arrayOf(chatComponent, chatEnumMessage.enumConstants[0])
                )
            } else {
                packet = invokeConstructor(
                    packetClazz,
                    arrayOf(chatBaseComponentClazz, Byte::class.javaPrimitiveType as Class<*>),
                    arrayOf(chatComponent, 0.toByte())
                )
            }
            sendPacket(sender as Player, packet)
        } catch (e: Exception) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to send packet.", e)
        }
    }

    /**
     * Sends a message to the [sender].
     */
    override fun <S> sendMessage(sender: S, message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**
     * Sends a packet to the client player
     *
     * @param player player
     * @param packet packet
     * @throws ClassNotFoundException    exception
     * @throws IllegalAccessException    exception
     * @throws NoSuchMethodException     exception
     * @throws InvocationTargetException exception
     * @throws NoSuchFieldException      exception
     */

    @Throws(
        ClassNotFoundException::class,
        IllegalAccessException::class,
        NoSuchMethodException::class,
        InvocationTargetException::class,
        NoSuchFieldException::class
    )
    private fun sendPacket(player: Player, packet: Any) {
        val craftPlayer = findClass("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer").cast(player)
        val entityPlayer = invokeMethod(craftPlayer, craftPlayer.javaClass, "getHandle", arrayOf(), arrayOf())
        val field = entityPlayer.javaClass.getDeclaredField("playerConnection")
        field.isAccessible = true
        val connection = field.get(entityPlayer)
        invokeMethod(connection, connection.javaClass, "sendPacket", arrayOf(packet.javaClass.interfaces[0]), arrayOf(packet))
    }

    /**
     * Invokes a constructor by the given parameters
     *
     * @param clazz      clazz
     * @param paramTypes paramTypes
     * @param params     params
     * @return instance
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     * @throws InvocationTargetException exception
     * @throws InstantiationException    exception
     */
    @Throws(NoSuchMethodException::class, IllegalAccessException::class, InvocationTargetException::class, InstantiationException::class)
    private fun invokeConstructor(clazz: Class<*>, paramTypes: Array<Class<*>>, params: Array<Any>): Any {
        val constructor = clazz.getDeclaredConstructor(*paramTypes)
        constructor.isAccessible = true
        return constructor.newInstance(*params)
    }

    /**
     * Invokes a method by the given parameters
     *
     * @param instance   instance
     * @param clazz      clazz
     * @param name       name
     * @param paramTypes paramTypes
     * @param params     params
     * @return returnedObject
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     * @throws NoSuchMethodException     exception
     */
    @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchMethodException::class)
    private fun invokeMethod(instance: Any?, clazz: Class<*>, name: String, paramTypes: Array<Class<*>>, params: Array<Any>): Any {
        val method = clazz.getDeclaredMethod(name, *paramTypes)
        method.isAccessible = true
        return method.invoke(instance, *params)
    }

    /**
     * Finds a class regarding of the server Version
     *
     * @param name name
     * @return clazz
     * @throws ClassNotFoundException exception
     */
    @Throws(ClassNotFoundException::class)
    private fun findClass(name: String): Class<*> {
        return Class.forName(
            name.replace(
                "VERSION",
                Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
            )
        )
    }
}