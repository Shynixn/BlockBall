package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.SoccerMiniGame
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.event.GameGoalEvent
import com.github.shynixn.mcutils.common.ConfigurationService
import com.google.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent

class MinigameListener @Inject constructor(
    private val gameService: GameService,
    private val configurationService: ConfigurationService
) : Listener {

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
        val game = event.game

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
        if (event.message.startsWith("/blockball")
            || event.player.hasPermission(Permission.OBSOLETE_STAFF.permission) || event.player.hasPermission(Permission.EDIT_GAME.permission)
            || event.player.isOp
        ) {

            return
        }

        val game = gameService.getByPlayer(event.player)

        if (game != null && game.arena.enabled && (game.arena.gameType == GameType.MINIGAME)) {
            event.isCancelled = true
        }
    }
}
