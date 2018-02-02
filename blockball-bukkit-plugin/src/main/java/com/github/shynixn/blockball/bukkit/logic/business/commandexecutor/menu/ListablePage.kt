package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import org.bukkit.entity.Player

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
class ListablePage : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {
    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.LISTABLE
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player?, command: BlockBallCommand?, cache: Array<Any>?, args: Array<out String>?): CommandResult {
        if (command == BlockBallCommand.LIST_GAMETYPES) {
            cache!![2] = GameType.values().map { p -> p.name }
            cache[3] = BlockBallCommand.SETTINGS_OPEN
        } else if (command == BlockBallCommand.LIST_LINES) {
            cache!![3] = BlockBallCommand.MULTILINES_ANY
        } else if (command == BlockBallCommand.LIST_BOSSBARSTYLES) {
            cache!![2] = BossBarMeta.Style.values().map { p -> p.name }
            cache[3] = BlockBallCommand.BOSSBAR_OPEN
        } else if (command == BlockBallCommand.LIST_BOSSBARFLAGS) {
            cache!![2] = BossBarMeta.Flag.values().map { p -> p.name }
            cache[3] = BlockBallCommand.BOSSBAR_CALLBACKFLAGS
        } else if (command == BlockBallCommand.LIST_BOSSBARCOLORS) {
            cache!![2] = BossBarMeta.Color.values().map { p -> p.name }
            cache[3] = BlockBallCommand.BOSSBAR_CALLBACKCOLORS
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any>?): ChatBuilder {
        var infoList = cache!![2] as ArrayList<String>
        var callBackCommand = cache!![3] as BlockBallCommand
        val builder = ChatBuilder()
        if (infoList.size == 0) {
            builder.text(" No data found.")
        } else {
            infoList!!.forEachIndexed { index, p ->
                builder.component((index + 1).toString() + ": [$p]")
                        .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, callBackCommand!!.command + " " + index)
                        .setHoverText("").builder().nextLine()
            }

        }
        return builder;
    }
}