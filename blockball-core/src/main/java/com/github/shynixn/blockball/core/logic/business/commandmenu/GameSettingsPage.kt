package com.github.shynixn.blockball.core.logic.business.commandmenu

import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.core.logic.persistence.entity.ChatBuilderEntity
import com.google.inject.Inject

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
class GameSettingsPage @Inject constructor(private val proxyService: ProxyService) :
    Page(ID, MainSettingsPage.ID) {

    companion object {
        /** Id of the page. */
        const val ID = 29
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    override fun getCommandKey(): MenuPageKey {
        return MenuPageKey.GAMESETTINGS
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
        val arena = cache[0] as Arena

        if (command == MenuCommand.GAMESETTINGS_LEAVESPAWNPOINT) {
            arena.meta.lobbyMeta.leaveSpawnpoint =
                proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        } else if (command == MenuCommand.GAMESETTINGS_LOBBYSPAWNPOINT) {
            arena.meta.minigameMeta.lobbySpawnpoint =
                proxyService.toPosition(proxyService.getEntityLocation<Any, P>(player))
        } else if (command == MenuCommand.GAMESETTINGS_TOGGLE_EVENTEAMS) {
            arena.meta.lobbyMeta.onlyAllowEventTeams = !arena.meta.lobbyMeta.onlyAllowEventTeams
        } else if (command == MenuCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD) {
            arena.meta.hubLobbyMeta.instantForcefieldJoin = !arena.meta.hubLobbyMeta.instantForcefieldJoin
        } else if (command == MenuCommand.GAMESETTINGS_TOGGLE_RESETEMPTY) {
            arena.meta.hubLobbyMeta.resetArenaOnEmpty = !arena.meta.hubLobbyMeta.resetArenaOnEmpty
        } else if (command == MenuCommand.GAMESETTINGS_BUNGEEKICKMESSAGE && args.size >= 3) {
            arena.meta.bungeeCordMeta.kickMessage = mergeArgs(2, args)
        } else if (command == MenuCommand.GAMESETTINGS_BUNGEELEAVEKICKMESSAGE && args.size >= 3) {
            arena.meta.bungeeCordMeta.leaveKickMessage = mergeArgs(2, args)
        } else if (command == MenuCommand.GAMESETTINGS_BUNGEEFALLBACKSERVER && args.size >= 3) {
            arena.meta.bungeeCordMeta.fallbackServer = mergeArgs(2, args)
        } else if (command == MenuCommand.GAMESETTINGS_MAXSCORE && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.lobbyMeta.maxScore = args[2].toInt()
        } else if (command == MenuCommand.GAMESETTINGS_LOBBYDURATION && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.minigameMeta.lobbyDuration = args[2].toInt()
        } else if (command == MenuCommand.GAMESETTINGS_CALLBACK_BUKKITGAMEMODES && args.size == 3 && args[2].toIntOrNull() != null) {
            arena.meta.lobbyMeta.gamemode = GameMode.values()[args[2].toInt()]
        } else if (command == MenuCommand.GAMESETTINGS_REMAININGPLAYERSMESSAGE && args.size > 3) {
            arena.meta.minigameMeta.playersRequiredToStartMessage = mergeArgs(2, args)
        } else if (command == MenuCommand.GAMESETTINGS_TOGGLE_TELEPORTONJOIN) {
            arena.meta.hubLobbyMeta.teleportOnJoin = !arena.meta.hubLobbyMeta.teleportOnJoin

            if (!arena.meta.hubLobbyMeta.teleportOnJoin) {
                arena.meta.hubLobbyMeta.instantForcefieldJoin = true
            }
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

        var leaveSpawnpoint = "none"
        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            leaveSpawnpoint = arena.meta.lobbyMeta.leaveSpawnpoint!!.toString()
        }
        var lobbySpawnpoint = "none"
        if (arena.meta.minigameMeta.lobbySpawnpoint != null) {
            lobbySpawnpoint = arena.meta.minigameMeta.lobbySpawnpoint!!.toString()
        }
        val builder = ChatBuilderEntity()
            .component("- Max Score: " + arena.meta.lobbyMeta.maxScore).builder()
            .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
            .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.GAMESETTINGS_MAXSCORE.command)
            .setHoverText("Amount of goals a team has to score in order to win.")
            .builder().nextLine()
            .component("- Leave Spawnpoint: $leaveSpawnpoint").builder()
            .component(MenuClickableItem.LOCATION.text).setColor(MenuClickableItem.LOCATION.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_LEAVESPAWNPOINT.command)
            .setHoverText("Sets the spawnpoint for people who leave the match.")
            .builder().nextLine()
            .component("- Gamemode: " + arena.meta.lobbyMeta.gamemode.name).builder()
            .component(MenuClickableItem.SELECT.text).setColor(MenuClickableItem.SELECT.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.LIST_BUKKITGAMESMODES.command)
            .setHoverText("Minecraft gamemode (Survival, Adventure, Creative) the players will be inside of a game.")
            .builder().nextLine()
            .component("- Even teams enabled: " + arena.meta.lobbyMeta.onlyAllowEventTeams).builder()
            .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
            .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_TOGGLE_EVENTEAMS.command)
            .setHoverText("Forces players to join the other team regardless of their choice to have the same amount of players on both teams.")
            .builder().nextLine()

        if (arena.gameType == GameType.MINIGAME || arena.gameType == GameType.BUNGEE) {
            builder.component("- Match Times:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MATCHTIMES_OPEN.command)
                .setHoverText("Opens the match times page.").builder().nextLine()
            builder.component("- Lobby Duration: " + arena.meta.minigameMeta.lobbyDuration).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.GAMESETTINGS_LOBBYDURATION.command)
                .setHoverText("Amount of seconds until a lobby is going to start when it reached its min amount of red and blue players.")
                .builder().nextLine()
                .component("- Lobby Spawnpoint: $lobbySpawnpoint").builder()
                .component(MenuClickableItem.LOCATION.text).setColor(MenuClickableItem.LOCATION.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_LOBBYSPAWNPOINT.command)
                .setHoverText("Sets the spawnpoint for people who join the lobby.")
                .builder().nextLine()
            builder.component("- Remaining Players Message:").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.minigameMeta.playersRequiredToStartMessage).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(
                    ChatClickAction.SUGGEST_COMMAND,
                    MenuCommand.GAMESETTINGS_REMAININGPLAYERSMESSAGE.command
                )
                .setHoverText(ChatColor.UNDERLINE.toString() + "Minigame exclusive\n" + ChatColor.RESET + "Message being send to the action bar of players who wait for more players in the lobby.")
                .builder().nextLine()
        }
        if (arena.gameType == GameType.BUNGEE) {
            builder.component("- Full Server Kick Message: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.bungeeCordMeta.kickMessage)
                .builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.GAMESETTINGS_BUNGEEKICKMESSAGE.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "BungeeCord exclusive\n" + ChatColor.RESET + "Message being send to players who try to join a running or a full server.")
                .builder().nextLine()
            builder.component("- Leave Server Kick Message: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.bungeeCordMeta.leaveKickMessage)
                .builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(
                    ChatClickAction.SUGGEST_COMMAND,
                    MenuCommand.GAMESETTINGS_BUNGEELEAVEKICKMESSAGE.command
                )
                .setHoverText(ChatColor.UNDERLINE.toString() + "BungeeCord exclusive\n" + ChatColor.RESET + "Message being send to players who leave a game. Can be used with third party BungeeCord plugins to determine the fallback servers.")
                .builder().nextLine()
            builder.component("- Fallback Server: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.bungeeCordMeta.fallbackServer)
                .builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.SUGGEST_COMMAND, MenuCommand.GAMESETTINGS_BUNGEEFALLBACKSERVER.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "BungeeCord exclusive\n" + ChatColor.RESET + "Fallback server when a player leaves the match.")
                .builder().nextLine()
        } else if (arena.gameType == GameType.HUBGAME) {
            builder.component("- Join Message: ").builder()
                .component(MenuClickableItem.PREVIEW.text).setColor(MenuClickableItem.PREVIEW.color)
                .setHoverText(arena.meta.hubLobbyMeta.joinMessage.toSingleLine()).builder()
                .component(MenuClickableItem.EDIT.text).setColor(MenuClickableItem.EDIT.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.MULTILINES_HUBGAMEJOINMESSAGE.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Message being send to players who touch the forcefield.")
                .builder().nextLine()
                .component("- Reset on empty: " + arena.meta.hubLobbyMeta.resetArenaOnEmpty).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_TOGGLE_RESETEMPTY.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Should the HubGame game be reset to it's starting stage when everyone has left the game?")
                .builder().nextLine()
                .component("- Teleport on join: " + arena.meta.hubLobbyMeta.teleportOnJoin).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_TOGGLE_TELEPORTONJOIN.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Should players be teleported to the spawnpoint on join? Automatically enables instant forcefield join when being disabled.")
                .builder().nextLine()
                .component("- Instant forcefield join: " + arena.meta.hubLobbyMeta.instantForcefieldJoin).builder()
                .component(MenuClickableItem.TOGGLE.text).setColor(MenuClickableItem.TOGGLE.color)
                .setClickAction(ChatClickAction.RUN_COMMAND, MenuCommand.GAMESETTINGS_TOGGLE_INSTATFORCEFIELD.command)
                .setHoverText(ChatColor.UNDERLINE.toString() + "HubGame exclusive\n" + ChatColor.RESET + "Should players join the game immediately after running into the forcefield? Teams get automatically selected.")
                .builder().nextLine()
        }
        return builder
    }
}