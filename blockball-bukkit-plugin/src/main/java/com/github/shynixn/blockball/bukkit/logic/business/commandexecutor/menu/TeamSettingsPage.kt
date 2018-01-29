package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.event.entity.BukkitArena
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toPosition
import org.bukkit.ChatColor
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
class TeamSettingsPage : Page(TeamSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        val ID = 5
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.TEAMMETA
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player?, command: BlockBallCommand?, cache: Array<Any>?, args: Array<out String>?): CommandResult {
        if (command == BlockBallCommand.TEAM_RED_CONFIGURE) {
            cache!![2] = 0
        }
        if (command == BlockBallCommand.TEAM_SPAWNPOINT) {
            val teamMeta = getTeamMeta(cache);
            teamMeta.spawnpoint = player!!.location.toPosition()
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
        var spawnpoint = "none"
        val arena = cache!![0] as BukkitArena
        val teamMeta = getTeamMeta(cache);
        if (teamMeta.spawnpoint != null) {
            spawnpoint = this.printLocation(teamMeta.spawnpoint)
        }
        return ChatBuilder()
                .component("- Name: " + teamMeta.displayName).builder()
                .addComponent(ClickableComponent.EDIT.component)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.ARENA_SETDISPLAYNAME.command)
                .setHoverText("Edit the name of the arena.")
                .builder().nextLine()
                .component("- Spawnpoint: " + spawnpoint).builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_SPAWNPOINT.command)
                .setHoverText("Uses your current location to set the spawnpoint of the ball.")
                .builder().nextLine()
                .component("- Settings:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.SETTINGS_OPEN.command)
                .setHoverText("Opens the settings page.").builder()
    }

    private fun getTeamMeta(cache: Array<Any>?): TeamMeta<Location, ItemStack> {
        val arena = cache!![0] as BukkitArena
        val type = cache[2] as Int
        return if (type == 0) {
            arena.meta.redTeamMeta
        } else {
            arena.meta.blueTeamMeta
        }
    }

}