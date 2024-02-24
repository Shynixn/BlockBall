package com.github.shynixn.blockball.impl.service.nms.v1_13_R2

import com.github.shynixn.blockball.contract.ScreenMessageService
import com.github.shynixn.mcutils.common.translateChatColors
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player

class ScreenMessage113R1ServiceImpl : ScreenMessageService {
    /**
     * Sets the [title] of the given [player] [P] for the amount of [stay] ticks. Optionally shows a [subTitle] and displays
     * a [fadeIn] and [fadeOut] effect in ticks.
     */
    override fun setTitle(player: Player, title: String, subTitle: String, fadeIn: Int, stay: Int, fadeOut: Int) {
        player.sendTitle(title.translateChatColors(), subTitle.translateChatColors(), fadeIn, stay, fadeIn)
    }

    /**
     * Sets the [message] for the given [player] at the actionbar.
     */
    override fun setActionBar(player: Player, message: String) {
        player.spigot()
            .sendMessage(ChatMessageType.ACTION_BAR, *TextComponent.fromLegacyText(message.translateChatColors()))
    }
}
