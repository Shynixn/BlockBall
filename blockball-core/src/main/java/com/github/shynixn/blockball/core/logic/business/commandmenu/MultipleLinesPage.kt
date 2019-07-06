@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity

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
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.MULTIPLELINES
    }

    override fun getPreviousIdFrom(cache: Array<Any?>): Int {
        return cache[4] as Int
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        if (command == MenuCommand.MULTILINES_SCOREBOARD) {
            cache[4] = ScoreboardPage.ID
            cache[3] = 0
        } else if (command == MenuCommand.MULTILINES_HUBGAMEJOINMESSAGE) {
            cache[2] = (cache[0] as Arena).meta.hubLobbyMeta.joinMessage
            cache[4] = GameSettingsPage.ID
            cache[3] = 0
        } else if (command == MenuCommand.MULTILINES_HOLOGRAM) {
            cache[4] = HologramPage.ID
            cache[3] = 0
        } else if (command == MenuCommand.MULTILINES_SPECTATEJOINMESSAGE) {
            cache[2] = (cache[0] as Arena).meta.spectatorMeta.spectateStartMessage
            cache[4] = SpectatePage.ID
            cache[3] = 0
        } else if (command == MenuCommand.MULTILINES_TEAMSIGNTEMPLATE) {
            val arena = cache[0] as Arena
            cache[4] = SignSettingsPage.ID
            cache[3] = 0

            when {
                args[2] == "red" -> cache[2] = arena.meta.redTeamMeta.signLines
                args[2] == "blue" -> cache[2] = arena.meta.blueTeamMeta.signLines
                args[2] == "join" -> cache[2] = arena.meta.lobbyMeta.joinSignLines
                args[2] == "leave" -> cache[2] = arena.meta.lobbyMeta.leaveSignLines
            }
        } else if (command == MenuCommand.MULTILINES_ADD) {
            val dataList = cache[2] as ArrayList<String>
            dataList.add(this.mergeArgs(2, args))
        } else if (command == MenuCommand.MULTILINES_ANY) {
            if (args.size >= 3) {
                cache[3] = args[2].toInt()
            } else {
                cache[3] = 0
            }
        } else if (command == MenuCommand.MULTILINES_SET) {
            val dataList = cache[2] as ArrayList<String>
            val index = cache[3] as Int
            if (index < dataList.size) {
                dataList[index] = mergeArgs(2, args)
            } else {
                cache[3] = 0
            }
        } else if (command == MenuCommand.MULTILINES_REMOVE) {
            val dataList = cache[2] as ArrayList<String>

            val index = cache[3] as Int
            if (index < dataList.size) {
                dataList.removeAt(cache[3] as Int)
            } else {
                cache[3] = 0
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
        val infoList = cache[2] as ArrayList<String>
        var selectedLine = "none"
        val index = cache[3] as Int
        if (index < infoList.size) {
            selectedLine = infoList[index]
        }
        return ChatBuilderEntity()
            .component("- Preview:").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(infoList.toSingleLine())
            .builder().nextLine()
            .component("- Add line:").builder()
            .component(MenuClickableItem.ADD.text).setColor(MenuClickableItem.ADD.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.MULTILINES_ADD.command)
            .setHoverText("Adds a new line to the list.")
            .builder().nextLine()
            .component("- Edit a line:").builder()
            .component(" [selected line..]").setColor(ChatColor.GRAY)
            .setHoverText((index + 1).toString() + ": " + selectedLine.translateChatColors()).builder().nextLine()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_LINES.command)
            .setHoverText("Select a different line.")
            .builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.MULTILINES_SET.command)
            .setHoverText("Changes the selected line.")
            .builder()
            .component(MenuClickableItem.DELETE.text).setColor(MenuClickableItem.DELETE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_REMOVE.command)
            .setHoverText("Removes the selected line.").builder()
    }
}