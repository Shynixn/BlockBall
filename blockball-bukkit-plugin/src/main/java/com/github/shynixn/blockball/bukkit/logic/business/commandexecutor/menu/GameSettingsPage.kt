package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.helper.toPosition
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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHERwwt
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class GameSettingsPage : Page(GameSettingsPage.ID, MainConfigurationPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 29
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): PageKey {
        return PageKey.GAMESETTINGS
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    override fun execute(player: Player, command: BlockBallCommand, cache: Array<Any?>, args: Array<String>): CommandResult {
        val arena = cache[0] as BukkitArena
        if (command == BlockBallCommand.GAMESETTINGS_LEAVESPAWNPOINT) {
            arena.meta.lobbyMeta.leaveSpawnpoint = player.location.toPosition()
        }
        else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_EVENTEAMS) {
            arena.meta.lobbyMeta.onlyAllowEventTeams = ! arena.meta.lobbyMeta.onlyAllowEventTeams;
        }
        else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD) {
            arena.meta.hubLobbyMeta.instantForcefieldJoin = ! arena.meta.hubLobbyMeta.instantForcefieldJoin;
        }
        else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_RESETEMPTY) {
            arena.meta.hubLobbyMeta.resetArenaOnEmpty= ! arena.meta.hubLobbyMeta.resetArenaOnEmpty;
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

        var leaveSpawnpoint = "none";
        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            leaveSpawnpoint = printLocation(arena.meta.lobbyMeta.leaveSpawnpoint!!);
        }
        return ChatBuilder()
                .component("- Leave Spawnpoint: " + leaveSpawnpoint).builder()
                .component(ClickableComponent.LOCATION.text).setColor(ClickableComponent.LOCATION.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_LEAVESPAWNPOINT.command)
                .setHoverText("Sets the spawnpoint for people who leave the match.")
                .builder().nextLine()
                .component("- Even teams enabled: " + arena.meta.lobbyMeta.onlyAllowEventTeams).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_EVENTEAMS.command)
                .setHoverText("Forces players to join the other team regardless of their choice to have the same amount of players on both teamsa.")
                .builder().nextLine()
                .component("- Join Message: ").builder()
                .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color).setHoverText(arena.meta.hubLobbyMeta.joinMessage.toSingleLine()).builder()
                .component(ClickableComponent.PAGE.text).setColor(ClickableComponent.PAGE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_HUBGAMEJOINMESSAGE.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n"  + ChatColor.RESET+ "Message being send to players who touch the forcefield.")
                .builder().nextLine()
                .component("- Reset on empty: " + arena.meta.hubLobbyMeta.resetArenaOnEmpty).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_RESETEMPTY.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n"  + ChatColor.RESET+ "Should the HubGame game be reset to it's starting stage when everyone has left the game?")
                .builder().nextLine()
                .component("- Instant forcefield join: " + arena.meta.hubLobbyMeta.instantForcefieldJoin).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET+ "Should players join the game immediately after running into the forcefield? Teams get automatically selected.")
                .builder().nextLine()
    }
}