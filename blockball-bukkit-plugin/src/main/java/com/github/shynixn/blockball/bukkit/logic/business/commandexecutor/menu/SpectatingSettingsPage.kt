package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ChatBuilder

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
class SpectatingSettingsPage : Page(ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 118
    }


    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.SPECTATING
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val arena = cache[0] as BukkitArena

        if (arena.gameType != GameType.HUBGAME) {
            return ChatBuilder()
                    .component("- Notifications:").builder()
                    .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.NOTIFICATIONS_OPEN.command)
                    .setHoverText("Configure the notifications nearby players do receive.")
                    .builder().nextLine()
                    .component("- Spectatormode:").builder()
                    .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SPECTATE_OPEN.command)
                    .setHoverText("Configure the spectator mode.")
                    .builder().nextLine()
        } else {
            return ChatBuilder()
                    .component("- Notifications:").builder()
                    .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.NOTIFICATIONS_OPEN.command)
                    .setHoverText("Configure the notifications nearby players do receive.")
                    .builder().nextLine()
        }
    }
}