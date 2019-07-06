package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
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
class TeamTextBookPage : Page(TeamTextBookPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 27
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.TEAMTEXTBOOK
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        if (command == MenuCommand.TEXTBOOK_OPEN) {
            cache[5] = cache[2]
            val teamMeta = getTeamMeta(cache)
            cache[2] = teamMeta.signLines
        } else if (command == MenuCommand.TEXTBOOK_JOINMESSAGE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.joinMessage = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_LEAVEMESSAGE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.leaveMessage = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_SCORETIELE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.scoreMessageTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_SCORESUBTITLE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.scoreMessageSubTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_WINTIELE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.winMessageTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_WINSUBTITLE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.winMessageSubTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_DRAWTIELE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.drawMessageTitle = mergeArgs(2, args)
        } else if (command == MenuCommand.TEXTBOOK_DRAWSUBTITLE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.drawMessageSubTitle = mergeArgs(2, args)
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<Any?>): ChatBuilder? {
        val teamMeta = getTeamMeta(cache)
        return ChatBuilderEntity()
            .component("- Join Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.joinMessage).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_JOINMESSAGE.command)
            .setHoverText("Changes the message being played when a player joins this team.")
            .builder().nextLine()
            .component("- Leave Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.leaveMessage).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_LEAVEMESSAGE.command)
            .setHoverText("Changes the message being played when a player leaves this team.")
            .builder().nextLine()
            .component("- ScoreTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.scoreMessageTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_SCORETIELE.command)
            .setHoverText("Changes the title message getting played when a player scores a goal.")
            .builder().nextLine()
            .component("- ScoreSubTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.scoreMessageSubTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_SCORESUBTITLE.command)
            .setHoverText("Changes the subtitle message getting played when a player scores a goal.")
            .builder().nextLine()
            .component("- WinTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.winMessageTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_WINTIELE.command)
            .setHoverText("Changes the title message getting played when the team wins the match.")
            .builder().nextLine()
            .component("- WinSubTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.winMessageSubTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_WINSUBTITLE.command)
            .setHoverText("Changes the subtitle message getting played when the team wins the match.")
            .builder().nextLine()
            .component("- DrawTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.drawMessageTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_DRAWTIELE.command)
            .setHoverText("Changes the title message getting played when the match ends in a draw.")
            .builder().nextLine()
            .component("- DrawSubTitle Message: ").builder()
            .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
            .setHoverText(teamMeta.drawMessageSubTitle).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEXTBOOK_DRAWSUBTITLE.command)
            .setHoverText("Changes the subtitle message getting played when the match ends in a draw.")
            .builder()
    }

    private fun getTeamMeta(cache: Array<Any?>?): TeamMeta {
        val arena = cache!![0] as Arena
        val type = cache[5] as Int
        return if (type == 0) {
            arena.meta.redTeamMeta
        } else {
            arena.meta.blueTeamMeta
        }
    }

}