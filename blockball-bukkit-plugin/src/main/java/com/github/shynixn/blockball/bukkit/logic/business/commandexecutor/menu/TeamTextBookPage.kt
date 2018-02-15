package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toSingleLine
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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
    override fun getCommandKey(): PageKey {
        return PageKey.TEAMTEXTBOOK
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        if (command == BlockBallCommand.TEXTBOOK_OPEN) {
            cache[5] = cache[2]
            val teamMeta = getTeamMeta(cache)
            cache[2] = teamMeta.signLines
        } else if (command == BlockBallCommand.TEXTBOOK_JOINMESSAGE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.joinMessage = mergeArgs(2, args)
        }
        else if (command == BlockBallCommand.TEXTBOOK_LEAVEMESSAGE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.leaveMessage = mergeArgs(2, args)
        }
        else if (command == BlockBallCommand.TEXTBOOK_SCORETIELE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.scoreMessageTitle = mergeArgs(2, args)
        }
        else if (command == BlockBallCommand.TEXTBOOK_SCORESUBTITLE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.scoreMessageSubTitle = mergeArgs(2, args)
        }
        else if (command == BlockBallCommand.TEXTBOOK_WINTIELE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.winMessageTitle = mergeArgs(2, args)
        }
        else if (command == BlockBallCommand.TEXTBOOK_WINSUBTITLE && args.size >= 3) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.winMessageSubTitle = mergeArgs(2, args)
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
        val teamMeta = getTeamMeta(cache)
        return ChatBuilder()
                .component("- Join Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.joinMessage).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_JOINMESSAGE.command)
                .setHoverText("Changes the message being played when a player joins this team.")
                .builder().nextLine()
                .component("- Leave Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.leaveMessage).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_LEAVEMESSAGE.command)
                .setHoverText("Changes the message being played when a player leaves this team.")
                .builder().nextLine()
                .component("- ScoreTitle Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.scoreMessageTitle).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_SCORETIELE.command)
                .setHoverText("Changes the title message getting played when a player scores a goal.")
                .builder().nextLine()
                .component("- ScoreSubTitle Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.scoreMessageSubTitle).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_SCORESUBTITLE.command)
                .setHoverText("Changes the subtitle message getting played when a player scores a goal.")
                .builder().nextLine()
                .component("- WinTitle Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.winMessageTitle).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_WINTIELE.command)
                .setHoverText("Changes the title message getting played when the team wins the match.")
                .builder().nextLine()
                .component("- WinSubTitle Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.winMessageSubTitle).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEXTBOOK_WINSUBTITLE.command)
                .setHoverText("Changes the subtitle message getting played when the team wins the match.")
                .builder().nextLine()
                .component("- Sign Template: ").builder().component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color)
                .setHoverText(teamMeta.signLines.toList().toSingleLine()).builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_TEAMSIGNTEMPLATE.command)
                .setHoverText("Opens the page to change the template on signs to join this team.")
                .builder().nextLine()
    }

    private fun getTeamMeta(cache: Array<Any?>?): TeamMeta<Location, ItemStack> {
        val arena = cache!![0] as BukkitArena
        val type = cache[5] as Int
        return if (type == 0) {
            arena.meta.redTeamMeta
        } else {
            arena.meta.blueTeamMeta
        }
    }

}