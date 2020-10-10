package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.ScreenMessageService
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.google.inject.Inject
import org.bukkit.entity.Player
import java.util.*

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
class ScreenMessageServiceImpl @Inject constructor(
    private val plugin: PluginProxy,
    private val proxyService: ProxyService
) : ScreenMessageService {
    /**
     * Sets the [title] of the given [player] [P] for the amount of [stay] ticks. Optionally shows a [subTitle] and displays
     * a [fadeIn] and [fadeOut] effect in ticks.
     */
    override fun <P> setTitle(player: P, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val finalTitle = title.translateChatColors()
        val finalSubTitle = subTitle.translateChatColors()
        val version = plugin.getServerVersion()

        val serializerMethod = if (version.isVersionSameOrGreaterThan(Version.VERSION_1_8_R2)) {
            findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer").getDeclaredMethod(
                "a",
                String::class.java
            )
        } else {
            findClazz("net.minecraft.server.VERSION.ChatSerializer").getDeclaredMethod("a", String::class.java)
        }

        val chatBaseComponentClazz = findClazz("net.minecraft.server.VERSION.IChatBaseComponent")

        val titleActionClazz = if (version.isVersionSameOrGreaterThan(Version.VERSION_1_8_R2)) {
            findClazz("net.minecraft.server.VERSION.PacketPlayOutTitle\$EnumTitleAction")
        } else {
            findClazz("net.minecraft.server.VERSION.EnumTitleAction")
        }

        val packetConstructor = findClazz("net.minecraft.server.VERSION.PacketPlayOutTitle").getDeclaredConstructor(
            titleActionClazz,
            chatBaseComponentClazz,
            Int::class.java,
            Int::class.java,
            Int::class.java
        )

        if (finalTitle.isNotEmpty()) {
            val titleJson = serializerMethod.invoke(null, "{\"text\": \"$finalTitle\"}")

            @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
            val enumTitleValue = java.lang.Enum.valueOf<Any>(titleActionClazz as Class<Any>, "TITLE")

            @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
            val enumTimesValue = java.lang.Enum.valueOf<Any>(titleActionClazz, "TIMES")

            val titlePacket = packetConstructor.newInstance(enumTitleValue, titleJson, fadeIn, stay, fadeOut)
            val lengthPacket = packetConstructor.newInstance(enumTimesValue, titleJson, fadeIn, stay, fadeOut)

            proxyService.sendPacket(player, titlePacket)
            proxyService.sendPacket(player, lengthPacket)
        }

        if (finalSubTitle.isNotEmpty()) {
            val subTitleJson = serializerMethod.invoke(null, "{\"text\": \"$finalSubTitle\"}")

            @Suppress("UPPER_BOUND_VIOLATED", "UNCHECKED_CAST")
            val enumSubTitleValue = java.lang.Enum.valueOf<Any>(titleActionClazz as Class<Any>, "SUBTITLE")

            val subIitlePacket = packetConstructor.newInstance(enumSubTitleValue, subTitleJson, fadeIn, stay, fadeOut)
            proxyService.sendPacket(player, subIitlePacket)
        }
    }

    /**
     * Sets the [message] for the given [player] at the actionbar.
     */
    override fun <P> setActionBar(player: P, message: String) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val finalMessage = message.translateChatColors()
        val version = plugin.getServerVersion()
        val chatBaseComponentClazz = findClazz("net.minecraft.server.VERSION.IChatBaseComponent")
        val packetClazz = findClazz("net.minecraft.server.VERSION.PacketPlayOutChat")

        val packet = when {
            version.isVersionSameOrGreaterThan(Version.VERSION_1_16_R1) -> {
                val serializerMethod =
                    findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer").getDeclaredMethod(
                        "a",
                        String::class.java
                    )
                val messageJSON = serializerMethod.invoke(null, "{\"text\": \"$finalMessage\"}")
                val systemUtilsClazz = findClazz("net.minecraft.server.VERSION.SystemUtils")
                val defaultUUID = systemUtilsClazz.getDeclaredField("b").get(null) as UUID
                val chatEnumMessage = findClazz("net.minecraft.server.VERSION.ChatMessageType")
                val chatMessageClazz = findClazz("net.minecraft.server.VERSION.ChatMessageType")
                val chatMessageType = chatMessageClazz.getDeclaredMethod("a", Byte::class.java).invoke(null, 2.toByte())

                packetClazz.getDeclaredConstructor(chatBaseComponentClazz, chatEnumMessage, UUID::class.java)
                    .newInstance(messageJSON, chatMessageType, defaultUUID)
            }
            version.isVersionSameOrGreaterThan(Version.VERSION_1_12_R1) -> {
                val serializerMethod =
                    findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer").getDeclaredMethod(
                        "a",
                        String::class.java
                    )
                val messageJSON = serializerMethod.invoke(null, "{\"text\": \"$finalMessage\"}")
                val chatEnumMessage = findClazz("net.minecraft.server.VERSION.ChatMessageType")
                val chatMessageClazz = findClazz("net.minecraft.server.VERSION.ChatMessageType")
                val chatMessageType = chatMessageClazz.getDeclaredMethod("a", Byte::class.java).invoke(null, 2.toByte())

                packetClazz.getDeclaredConstructor(chatBaseComponentClazz, chatEnumMessage)
                    .newInstance(messageJSON, chatMessageType)
            }
            else -> {
                val serializerMethod =
                    findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer").getDeclaredMethod(
                        "a",
                        String::class.java
                    )
                val messageJSON = serializerMethod.invoke(null, "{\"text\": \"$finalMessage\"}")
                packetClazz.getDeclaredConstructor(chatBaseComponentClazz, Byte::class.java)
                    .newInstance(messageJSON, 2.toByte())
            }
        }

        proxyService.sendPacket(player, packet)
    }
}