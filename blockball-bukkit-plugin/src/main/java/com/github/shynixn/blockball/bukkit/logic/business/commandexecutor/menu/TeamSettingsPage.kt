package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
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
        const val ID = 5
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
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        if (command == BlockBallCommand.TEAM_RED_CONFIGURE) {
            cache[2] = 0
        }
        if (command == BlockBallCommand.TEAM_BLUE_CONFIGURE) {
            cache[2] = 1
        } else if (command == BlockBallCommand.TEAM_SPAWNPOINT) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.spawnpoint = player.location.toPosition()
        } else if (command == BlockBallCommand.TEAM_NAME) {
            val teamMeta = getTeamMeta(cache)
            val name = mergeArgs(2, args)
            teamMeta.displayName = name
        } else if (command == BlockBallCommand.TEAM_PREFIX) {
            val teamMeta = getTeamMeta(cache)
            val name = mergeArgs(2, args)
            teamMeta.prefix = name
        } else if (command == BlockBallCommand.TEAM_MINAMOUNT) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                if (amount > teamMeta.maxAmount) {
                    return CommandResult.MAX_PLAYERS
                }
                teamMeta.minAmount = amount
            }
        } else if (command == BlockBallCommand.TEAM_MAXAMOUNT) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                if (amount < teamMeta.minAmount) {
                    return CommandResult.MINPLAYERS
                }
                teamMeta.maxAmount = amount
            }
        } else if (command == BlockBallCommand.TEAM_WALKSPEED) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toDoubleOrNull()
            if (amount != null) {
                teamMeta.walkingSpeed = amount
            }
        } else if (command == BlockBallCommand.TEAM_ARMOR) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.armorContents = player.inventory.armorContents.clone()
        } else if (command == BlockBallCommand.TEAM_INVENTORY) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.inventoryContents = player.inventory.contents.clone()
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
        var spawnpoint = "none"
        val teamMeta = getTeamMeta(cache)
        if (teamMeta.spawnpoint != null) {
            spawnpoint = this.printLocation(teamMeta.spawnpoint!!)
        }
        return ChatBuilder()
                .component("- Name: " + teamMeta.displayName).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEAM_NAME.command)
                .setHoverText("Edit the name of the team.")
                .builder().nextLine()
                .component("- Color: " + teamMeta.prefix + "Color").builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEAM_PREFIX.command)
                .setHoverText("Edit the prefix of the team.")
                .builder().nextLine()
                .component("- Min amount: " + teamMeta.minAmount).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEAM_MINAMOUNT.command)
                .setHoverText("Edit the min amount of players required to start a match.")
                .builder().nextLine()
                .component("- Max amount: " + teamMeta.maxAmount).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEAM_MAXAMOUNT.command)
                .setHoverText("Edit the max amount of players which can join this team.")
                .builder().nextLine()
                .component("- Armor").builder()
                .component(ClickableComponent.COPY_ARMOR.text).setColor(ClickableComponent.COPY_ARMOR.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_ARMOR.command)
                .setHoverText("Copies your current equipped armor to the team's armor.")
                .builder().nextLine()
                .component("- Inventory").builder()
                .component(ClickableComponent.COPY_INVENTORY.text).setColor(ClickableComponent.COPY_INVENTORY.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_INVENTORY.command)
                .setHoverText("Copies your current your inventory to the team's inventory.")
                .builder().nextLine()
                .component("- Walking Speed: " + teamMeta.walkingSpeed).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.TEAM_WALKSPEED.command)
                .setHoverText("Edit the speed each player of this team is going to walk. (default: 0.2)")
                .builder().nextLine()
                .component("- Spawnpoint: $spawnpoint").builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEAM_SPAWNPOINT.command)
                .setHoverText("If this spawnpoint is set the team will spawn at this location instead of the spawning location of the ball.")
                .builder().nextLine()
                .component("- Textbook: ").builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.TEXTBOOK_OPEN.command)
                .setHoverText("Opens the messages and texts specific for this team.")
                .builder().nextLine()
    }

    private fun getTeamMeta(cache: Array<Any?>?): TeamMeta<Location, ItemStack> {
        val arena = cache!![0] as BukkitArena
        val type = cache[2] as Int
        return if (type == 0) {
            arena.meta.redTeamMeta
        } else {
            arena.meta.blueTeamMeta
        }
    }

}