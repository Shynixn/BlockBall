package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
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
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.SCOREBOARD
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(player: P, command: MenuCommand, cache: Array<Any?>, args: Array<String>): MenuCommandResult {
        val arena = cache[0] as Arena
        val scoreboard = arena.meta.scoreboardMeta
        cache[2] = scoreboard.lines
        if (command == MenuCommand.SCOREBOARD_TITLE) {
            scoreboard.title = this.mergeArgs(2, args)
        } else if (command == MenuCommand.SCOREBOARD_TOGGLE) {
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
        val arena = cache[0] as Arena
        val scoreboard = arena.meta.scoreboardMeta
        return ChatBuilderEntity()
                .component("- Title: " + scoreboard.title).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.SCOREBOARD_TITLE.command)
                .setHoverText("Edit the title of the scoreboard.")
                .builder().nextLine()
                .component("- Enabled: " + scoreboard.enabled).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.SCOREBOARD_TOGGLE.command)
                .setHoverText("Toggle the scoreboard.")
                .builder().nextLine()
                .component("- Lines:").builder()
                .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_SCOREBOARD.command)
                .setHoverText("Configure the lines of the scoreboard.")
                .builder().nextLine()
    }
}