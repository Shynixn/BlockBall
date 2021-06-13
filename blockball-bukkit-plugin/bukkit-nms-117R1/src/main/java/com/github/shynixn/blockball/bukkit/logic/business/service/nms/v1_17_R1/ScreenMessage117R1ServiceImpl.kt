package com.github.shynixn.blockball.bukkit.logic.business.service.nms.v1_17_R1

import com.github.shynixn.blockball.api.business.service.ScreenMessageService
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

class ScreenMessage117R1ServiceImpl : ScreenMessageService {
    /**
     * Sets the [title] of the given [player] [P] for the amount of [stay] ticks. Optionally shows a [subTitle] and displays
     * a [fadeIn] and [fadeOut] effect in ticks.
     */
    override fun <P> setTitle(player: P, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        require(player is Player)
        player.sendTitle(title.translateChatColors(), subTitle.translateChatColors(), fadeIn, stay, fadeIn)
    }

    /**
     * Sets the [message] for the given [player] at the actionbar.
     */
    override fun <P> setActionBar(player: P, message: String) {
        require(player is Player)
        player.spigot()
            .sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message.translateChatColors()))
    }
}
