package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ChatBuilder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.extension.toSingleLine
import org.bukkit.ChatColor
import org.bukkit.GameMode
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
class GameSettingsPage : Page(GameSettingsPage.ID, MainSettingsPage.ID) {

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
        } else if (command == BlockBallCommand.GAMESETTINGS_LOBBYSPAWNPOINT) {
            arena.meta.minigameMeta.lobbySpawnpoint = player.location.toPosition()
        } else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_EVENTEAMS) {
            arena.meta.lobbyMeta.onlyAllowEventTeams = !arena.meta.lobbyMeta.onlyAllowEventTeams
        } else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD) {
            arena.meta.hubLobbyMeta.instantForcefieldJoin = !arena.meta.hubLobbyMeta.instantForcefieldJoin
        } else if (command == BlockBallCommand.GAMESETTINGS_TOGGLE_RESETEMPTY) {
            arena.meta.hubLobbyMeta.resetArenaOnEmpty = !arena.meta.hubLobbyMeta.resetArenaOnEmpty
        } else if (command == BlockBallCommand.GAMESETTINGS_BUNGEEKICKMESSAGE && args.size >= 3) {
            arena.meta.bungeeCordMeta.kickMessage = mergeArgs(2, args)
        } else if (command == BlockBallCommand.GAMESETTINGS_MAXSCORE && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.lobbyMeta.maxScore = args[2].toInt()
        } else if (command == BlockBallCommand.GAMESETTINGS_MAXDURATION && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.minigameMeta.matchDuration = args[2].toInt()
        } else if (command == BlockBallCommand.GAMESETTINGS_LOBBYDURATION && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.minigameMeta.lobbyDuration = args[2].toInt()
        } else if (command == BlockBallCommand.GAMESETTINGS_CALLBACK_BUKKITGAMEMODES && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.lobbyMeta.gamemode = GameMode.values()[args[2].toInt()]
        } else if (command == BlockBallCommand.GAMESETTINGS_REMAININGPLAYERSMESSAGE && args.size > 3) {
            arena.meta.minigameMeta.playersRequiredToStartMessage = mergeArgs(2, args)
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

        var leaveSpawnpoint = "none"
        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            leaveSpawnpoint = printLocation(arena.meta.lobbyMeta.leaveSpawnpoint!!)
        }
        var lobbySpawnpoint = "none"
        if (arena.meta.minigameMeta.lobbySpawnpoint != null) {
            lobbySpawnpoint = printLocation(arena.meta.minigameMeta.lobbySpawnpoint!!)
        }
        val builder = ChatBuilder()
                .component("- Max Score: " + arena.meta.lobbyMeta.maxScore).builder()
                .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMESETTINGS_MAXSCORE.command)
                .setHoverText("Amount of goals a team has to score in order to win.")
                .builder().nextLine()
                .component("- Leave Spawnpoint: $leaveSpawnpoint").builder()
                .component(ClickableComponent.LOCATION.text).setColor(ClickableComponent.LOCATION.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_LEAVESPAWNPOINT.command)
                .setHoverText("Sets the spawnpoint for people who leave the match.")
                .builder().nextLine()
                .component("- Gamemode: " + (arena.meta.lobbyMeta.gamemode as GameMode).name).builder()
                .component(ClickableComponent.SELECT.text).setColor(ClickableComponent.SELECT.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.LIST_BUKKITGAMESMODES.command)
                .setHoverText("Minecraft gamemode (Survival, Adventure, Creative) the players will be inside of a game.")
                .builder().nextLine()
                .component("- Even teams enabled: " + arena.meta.lobbyMeta.onlyAllowEventTeams).builder()
                .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_EVENTEAMS.command)
                .setHoverText("Forces players to join the other team regardless of their choice to have the same amount of players on both teams.")
                .builder().nextLine()

        if (arena.gameType == GameType.MINIGAME || arena.gameType == GameType.BUNGEE) {
            builder.component("- Match Duration: " + arena.meta.minigameMeta.matchDuration).builder()
                    .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                    .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMESETTINGS_MAXDURATION.command)
                    .setHoverText("Amount of seconds a game is going to last until it ends. The team with the highest score wins the match.")
                    .builder().nextLine()
            builder.component("- Lobby Duration: " + arena.meta.minigameMeta.lobbyDuration).builder()
                    .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                    .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMESETTINGS_LOBBYDURATION.command)
                    .setHoverText("Amount of seconds until a lobby is going to start when it reached its min amount of red and blue players.")
                    .builder().nextLine()
                    .component("- Lobby Spawnpoint: $lobbySpawnpoint").builder()
                    .component(ClickableComponent.LOCATION.text).setColor(ClickableComponent.LOCATION.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_LOBBYSPAWNPOINT.command)
                    .setHoverText("Sets the spawnpoint for people who join the lobby.")
                    .builder().nextLine()
            builder.component("- Remaining Players Message:").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color).setHoverText(arena.meta.minigameMeta.playersRequiredToStartMessage).builder()
                    .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                    .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMESETTINGS_REMAININGPLAYERSMESSAGE.command)
                    .setHoverText(ChatColor.UNDERLINE.toString() + "Minigame exclusive\n" + ChatColor.RESET + "Message being send to the action bar of players who wait for more players in the lobby.")
                    .builder().nextLine()
        }
        if (arena.gameType == GameType.BUNGEE) {
            builder.component("- Kick Message: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color).setHoverText(arena.meta.bungeeCordMeta.kickMessage).builder()
                    .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                    .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.GAMESETTINGS_BUNGEEKICKMESSAGE.command)
                    .setHoverText(ChatColor.UNDERLINE.toString() + "BungeeCord exclusive\n" + ChatColor.RESET + "Message being send to players who try to join a running or a full server.")
                    .builder().nextLine()
        } else if (arena.gameType == GameType.HUBGAME) {
            builder.component("- Join Message: ").builder()
                    .component(ClickableComponent.PREVIEW.text).setColor(ClickableComponent.PREVIEW.color).setHoverText(arena.meta.hubLobbyMeta.joinMessage.toSingleLine()).builder()
                    .component(ClickableComponent.EDIT.text).setColor(ClickableComponent.EDIT.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.MULTILINES_HUBGAMEJOINMESSAGE.command)
                    .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Message being send to players who touch the forcefield.")
                    .builder().nextLine()
                    .component("- Reset on empty: " + arena.meta.hubLobbyMeta.resetArenaOnEmpty).builder()
                    .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_RESETEMPTY.command)
                    .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Should the HubGame game be reset to it's starting stage when everyone has left the game?")
                    .builder().nextLine()
                    .component("- Instant forcefield join: " + arena.meta.hubLobbyMeta.instantForcefieldJoin).builder()
                    .component(ClickableComponent.TOGGLE.text).setColor(ClickableComponent.TOGGLE.color)
                    .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD.command)
                    .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Should players join the game immediately after running into the forcefield? Teams get automatically selected.")
                    .builder().nextLine()
        }
        return builder
    }
}