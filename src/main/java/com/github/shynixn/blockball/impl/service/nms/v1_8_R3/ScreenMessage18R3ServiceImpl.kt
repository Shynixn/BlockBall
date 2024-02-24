package com.github.shynixn.blockball.impl.service.nms.v1_8_R3

import com.github.shynixn.blockball.contract.ProxyService
import com.github.shynixn.blockball.contract.ScreenMessageService
import com.github.shynixn.blockball.impl.extension.findClazz
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player
import java.util.*

class ScreenMessage18R3ServiceImpl @Inject constructor(
    private val proxyService: ProxyService
) : ScreenMessageService {
    /**
     * Sets the [title] of the given [player] [P] for the amount of [stay] ticks. Optionally shows a [subTitle] and displays
     * a [fadeIn] and [fadeOut] effect in ticks.
     */
    override fun setTitle(player: Player, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        val finalTitle = title.translateChatColors()
        val finalSubTitle = subTitle.translateChatColors()
        val version = Version.serverVersion

        val serializerMethod =
           if (version.isVersionSameOrGreaterThan(Version.VERSION_1_8_R2)) {
                findClazz("net.minecraft.server.VERSION.IChatBaseComponent\$ChatSerializer").getDeclaredMethod(
                    "a",
                    String::class.java
                )
            } else {
                findClazz("net.minecraft.server.VERSION.ChatSerializer").getDeclaredMethod("a", String::class.java)
            }

        val chatBaseComponentClazz = if (version.isVersionSameOrGreaterThan(Version.VERSION_1_17_R1)) {
            findClazz("net.minecraft.network.chat.IChatBaseComponent")
        } else {
            findClazz("net.minecraft.server.VERSION.IChatBaseComponent")
        }

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
    override fun setActionBar(player: Player, message: String) {
        val finalMessage = message.translateChatColors()
        val version = Version.serverVersion
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
