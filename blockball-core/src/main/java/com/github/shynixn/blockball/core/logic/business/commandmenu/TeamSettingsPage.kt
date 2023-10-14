@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.google.inject.Inject

class TeamSettingsPage @Inject constructor(private val proxyService: ProxyService) :
    Page(TeamSettingsPage.ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 5
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.TEAMMETA
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun <P> execute(
        player: P,
        command: MenuCommand,
        cache: Array<Any?>,
        args: Array<String>
    ): MenuCommandResult {
        if (command == MenuCommand.TEAM_RED_CONFIGURE) {
            cache[2] = 0
        }
        if (command == MenuCommand.TEAM_BLUE_CONFIGURE) {
            cache[2] = 1
        } else if (command == MenuCommand.TEAM_SPAWNPOINT) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.spawnpoint = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        } else if (command == MenuCommand.TEAM_LOBBY) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.lobbySpawnpoint = proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        } else if (command == MenuCommand.TEAM_NAME) {
            val teamMeta = getTeamMeta(cache)
            val name = mergeArgs(2, args)
            teamMeta.displayName = name
        } else if (command == MenuCommand.TEAM_PREFIX) {
            val teamMeta = getTeamMeta(cache)
            val name = mergeArgs(2, args)
            teamMeta.prefix = name
        } else if (command == MenuCommand.TEAM_MINAMOUNT) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                if (amount > teamMeta.maxAmount) {
                    return MenuCommandResult.MAX_PLAYERS
                }
                teamMeta.minAmount = amount
            }
        } else if (command == MenuCommand.TEAM_MAXAMOUNT) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                if (amount < teamMeta.minAmount) {
                    return MenuCommandResult.MINPLAYERS
                }
                teamMeta.maxAmount = amount
            }
        } else if (command == MenuCommand.TEAM_POINTSGOAL) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                teamMeta.pointsPerGoal = amount
            }
        } else if (command == MenuCommand.TEAM_POINTSDEATH) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toIntOrNull()
            if (amount != null) {
                teamMeta.pointsPerEnemyDeath = amount
            }
        } else if (command == MenuCommand.TEAM_WALKSPEED) {
            val teamMeta = getTeamMeta(cache)
            val amount = args[2].toDoubleOrNull()
            if (amount != null) {
                teamMeta.walkingSpeed = amount
            }
        } else if (command == MenuCommand.TEAM_ARMOR) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.armorContents = proxyService.getPlayerInventoryArmorCopy(player)
        } else if (command == MenuCommand.TEAM_INVENTORY) {
            val teamMeta = getTeamMeta(cache)
            teamMeta.inventoryContents = proxyService.getPlayerInventoryCopy(player)
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
            spawnpoint = teamMeta.spawnpoint!!.toString()
        }
        var lobby = "none"
        if(teamMeta.lobbySpawnpoint != null){
            lobby = teamMeta.lobbySpawnpoint!!.toString()
        }

        return ChatBuilderEntity()
            .component("- Name: " + teamMeta.displayName).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_NAME.command)
            .setHoverText("Edit the name of the team.")
            .builder().nextLine()
            .component("- Color: " + teamMeta.prefix + "Color").builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_PREFIX.command)
            .setHoverText("Edit the prefix of the team.")
            .builder().nextLine()
            .component("- Min amount: " + teamMeta.minAmount).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_MINAMOUNT.command)
            .setHoverText("Edit the min amount of players required to start a match.")
            .builder().nextLine()
            .component("- Max amount: " + teamMeta.maxAmount).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_MAXAMOUNT.command)
            .setHoverText("Edit the max amount of players which can join this team.")
            .builder().nextLine()
            .component("- Armor").builder()
            .component(MenuClickableItem.COPY_ARMOR.text).setColor(MenuClickableItem.COPY_ARMOR.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_ARMOR.command)
            .setHoverText("Copies your current equipped armor to the team's armor.")
            .builder().nextLine()
            .component("- Inventory").builder()
            .component(MenuClickableItem.COPY_INVENTORY.text).setColor(MenuClickableItem.COPY_INVENTORY.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_INVENTORY.command)
            .setHoverText("Copies your current your inventory to the team's inventory.")
            .builder().nextLine()
            .component("- Walking Speed: " + teamMeta.walkingSpeed).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_WALKSPEED.command)
            .setHoverText("Edit the speed each player of this team is going to walk. (default: 0.2)")
            .builder().nextLine()
            .component("- Points per goal: " + teamMeta.pointsPerGoal).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_POINTSGOAL.command)
            .setHoverText("Edit the amount of points this team gets when a goal gets scored. (default: 1)")
            .builder().nextLine()
            .component("- Points per opponent death: " + teamMeta.pointsPerEnemyDeath).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.TEAM_POINTSDEATH.command)
            .setHoverText("Edit the amount of points this team gets when a player of the opponent team dies. (default: 0)")
            .builder().nextLine()
            .component("- Spawnpoint: $spawnpoint").builder()
            .component(" [location..]").setColor(ChatColor.BLUE)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_SPAWNPOINT.command)
            .setHoverText("If this spawnpoint is set the team will spawn at this location instead of the spawning location of the ball.")
            .builder().nextLine()
            .component("- Lobby: $lobby").builder()
            .component(" [location..]").setColor(ChatColor.BLUE)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEAM_LOBBY.command)
            .setHoverText("If this spawnpoint is set the team will spawn in its own lobby.")
            .builder().nextLine()
            .component("- Textbook: ").builder()
            .component(MenuClickableItem.PAGE.text).setColor(MenuClickableItem.PAGE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.TEXTBOOK_OPEN.command)
            .setHoverText("Opens the messages and texts specific for this team.")
            .builder().nextLine()
    }

    private fun getTeamMeta(cache: Array<Any?>?): TeamMeta {
        val arena = cache!![0] as Arena
        val type = cache[2] as Int
        return if (type == 0) {
            arena.meta.redTeamMeta
        } else {
            arena.meta.blueTeamMeta
        }
    }

}
