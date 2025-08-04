package com.github.shynixn.blockball.impl.listener

import checkForPluginMainThread
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerMiniGame
import com.github.shynixn.blockball.contract.SoccerRefereeGame
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.event.GameGoalEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

class MinigameListener(
    private val gameService: GameService,
    private val plugin: Plugin
) : Listener {
    private val commandMessages by lazy {
        val list = ArrayList<String>()
        list.add("/blockball")
        list.addAll(plugin.config.getStringList("commands.blockball.aliases").map { e -> "/${e}" })
        list
    }

    /**
     * Cancels actions in minigame and bungeecord games to restrict destroying the soccerArena.
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        val game = gameService.getByPlayer(event.player)

        if (game != null && game.arena.enabled && (game.arena.gameType == GameType.MINIGAME)) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player scores a goal.
     */
    @EventHandler
    fun onPlayerGoalEvent(event: GameGoalEvent) {
        checkForPluginMainThread()
        val game = event.game

        if (game is SoccerRefereeGame) {
            // Do not automatically switch in referee games.
            return
        }

        if (game !is SoccerMiniGame) {
            return
        }

        val matchTimes = game.arena.meta.minigameMeta.matchTimes

        if (game.matchTimeIndex < 0 || game.matchTimeIndex >= matchTimes.size) {
            return
        }

        val matchTime = matchTimes[game.matchTimeIndex]

        if (matchTime.closeType == MatchTimeCloseType.NEXT_GOAL) {
            game.switchToNextMatchTime()
        }
    }

    /**
     * Cancels commands in minigame and bungeecord games to restrict destroying the soccerArena.
     */
    @EventHandler
    fun onPlayerExecuteCommand(event: PlayerCommandPreprocessEvent) {
        if (commandMessages.firstOrNull { e -> event.message.startsWith(e) } != null) {
            return
        }

        if (event.player.hasPermission(Permission.OBSOLETE_STAFF.permission) || event.player.hasPermission(Permission.EDIT_GAME.permission) || event.player.isOp) {
            return
        }

        val game = gameService.getByPlayer(event.player)

        if (game != null && game.arena.enabled && (game.arena.gameType == GameType.MINIGAME)) {
            event.isCancelled = true
        }
    }
}
