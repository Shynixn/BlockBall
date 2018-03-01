package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
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
class ScoreboardPage : Page(ScoreboardPage.ID, EffectsSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 8
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.SCOREBOARD
    }


    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as BukkitArena
        val scoreboard = arena.meta.scoreboardMeta
        cache[2] = scoreboard.lines
        if (command == BlockBallCommand.SCOREBOARD_TITLE) {
            scoreboard.title = this.mergeArgs(2, args)
        } else if (command == BlockBallCommand.SCOREBOARD_TOGGLE) {
            scoreboard.enabled = !scoreboard.enabled
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
        val arena = cache[0] as BukkitArena
        val scoreboard = arena.meta.scoreboardMeta
        return ChatBuilder()
                .component("- Title: " + scoreboard.title).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.SCOREBOARD_TITLE.command)
                .setHoverText("Edit the title of the scoreboard.")
                .builder().nextLine()
                .component("- Enabled: " + scoreboard.enabled).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SCOREBOARD_TOGGLE.command)
                .setHoverText("Toggle the scoreboard.")
                .builder().nextLine()
                .component("- Lines:").builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_SCOREBOARD.command)
                .setHoverText("Configure the lines of the scoreboard.")
                .builder().nextLine()
    }
}