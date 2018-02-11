package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.helper.toSingleLine
import org.bukkit.ChatColor
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
class MultipleLinesPage : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {
    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.MULTIPLELINES
    }

    override fun getPreviousIdFrom(cache: Array<Any?>): Int {
        return cache!![4] as Int
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val dataList = cache!![2] as ArrayList<String>
        if (command == BlockBallCommand.MULTILINES_SCOREBOARD) {
            cache[4] = ScoreboardPage.ID
            cache[3] = 0
        }
        else if (command == BlockBallCommand.MULTILINES_HOLOGRAM) {
            cache[4] = HologramPage.ID
            cache[3] = 0
        }
        else if (command == BlockBallCommand.MULTILINES_ADD) {
            dataList.add(this.mergeArgs(2, args))
        } else if (command == BlockBallCommand.MULTILINES_ANY) {
            if (args!!.size >= 3) {
                cache[3] = args[2].toInt()
            } else {
                cache[3] = 0
            }
        } else if (command == BlockBallCommand.MULTILINES_SET) {
            val index = cache[3] as Int;
            if (index < dataList.size) {
                dataList[index] = mergeArgs(2, args)
            } else {
                cache[3] = 0;
            }
        } else if (command == BlockBallCommand.MULTILINES_REMOVE) {
            val index = cache[3] as Int;
            if (index < dataList.size) {
                dataList.removeAt(cache[3] as Int)
            } else {
                cache[3] = 0;
            }
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder {
        val infoList = cache!![2] as ArrayList<String>
        var selectedLine = "none"
        val index = cache[3] as Int
        if (index < infoList.size) {
            selectedLine = infoList[index]
        }
        return ChatBuilder()
                .component("- Preview:").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(infoList.toSingleLine())
                .builder().nextLine()
                .component("- Add line:").builder()
                .component(ClickableComponent.ADD.text).setColor(ClickableComponent.ADD.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.MULTILINES_ADD.command)
                .setHoverText("Adds a new line to the list.")
                .builder().nextLine()
                .component("- Edit a line:").builder()
                .component(" [selected line..]").setColor(ChatColor.GRAY)
                .setHoverText((index + 1).toString() + ": " + selectedLine.convertChatColors()).builder().nextLine()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_LINES.command)
                .setHoverText("Select a different line.")
                .builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.MULTILINES_SET.command)
                .setHoverText("Changes the selected line.")
                .builder()
                .component(ClickableComponent.DELETE.text).setColor(ClickableComponent.DELETE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_REMOVE.command)
                .setHoverText("Removes the selected line.").builder()
    }
}