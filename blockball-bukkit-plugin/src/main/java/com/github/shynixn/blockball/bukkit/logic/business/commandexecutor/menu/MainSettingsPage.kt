package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
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
class MainSettingsPage : Page(MainSettingsPage.ID, MainConfigurationPage.ID) {

    companion object {
        /** Id of the page. */
        val ID = 3
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.MAINSETTING
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player?, command: BlockBallCommand?, cache: Array<out Any>?, args: Array<out String>?): CommandResult {
        if (command == BlockBallCommand.SETTINGS_OPEN && args!!.size == 3) {
            var arena = cache!![0] as BukkitArena
            arena.gameType = GameType.values()[args[2] as Int];
        }
        return super.execute(player, command, cache, args)
    }

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    override fun buildPage(cache: Array<out Any>?): ChatBuilder {
        val arena = cache!![0] as BukkitArena
        return ChatBuilder()
                .component("- GameType: " + arena.gameType.name).builder()
                .component(" [select..]")
                .setColor(ChatColor.AQUA)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_GAMETYPES.command)
                .setHoverText("Opens the selectionbox for game modes.")
                .builder().nextLine()
                .component("- Team Red:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_RED_CONFIGURE.command)
                .setHoverText("Opens the settings page for the red team.")
                .builder().nextLine()
                .component("- Team Blue:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_BLUE_CONFIGURE.command)
                .setHoverText("Opens the settings page for the blue team.")
                .builder().nextLine()
                .component("- Effects:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.EFFECTS_OPEN.command)
                .setHoverText("Opens the settings page for effects like scoreboard, bossbar and holograms.").builder()
    }
}