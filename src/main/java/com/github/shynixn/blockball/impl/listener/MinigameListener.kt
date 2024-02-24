package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.GameMiniGameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.MiniGame
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.enumeration.Permission
import com.github.shynixn.blockball.event.GameGoalEvent
import com.github.shynixn.blockball.impl.extension.hasPermission
import com.github.shynixn.mcutils.common.ConfigurationService
import com.google.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEvent

class MinigameListener @Inject constructor(
    private val gameService: GameService,
    private val miniGameActionService: GameMiniGameActionService,
    private val configurationService: ConfigurationService
) : Listener {

    /**
     * Cancels actions in minigame and bungeecord games to restrict destroying the arena.
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        var game = gameService.getGameFromPlayer(event.player)

        if (!game.isPresent) {
            game = gameService.getGameFromSpectatingPlayer(event.player)
        }

        if (game.isPresent && game.get().arena.enabled && (game.get().arena.gameType == GameType.MINIGAME || game.get().arena.gameType == GameType.BUNGEE)) {
            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player scores a goal.
     */
    @EventHandler
    fun onPlayerGoalEvent(event: GameGoalEvent) {
        if (event.game !is MiniGame) {
            return
        }

        val miniGame = event.game as MiniGame
        val matchTimes = miniGame.arena.meta.minigameMeta.matchTimes

        if (miniGame.matchTimeIndex < 0 || miniGame.matchTimeIndex >= matchTimes.size) {
            return
        }

        val matchTime = matchTimes[miniGame.matchTimeIndex]

        if (matchTime.closeType == MatchTimeCloseType.NEXT_GOAL) {
            miniGameActionService.switchToNextMatchTime(miniGame)
        }
    }

    /**
     * Cancels commands in minigame and bungeecord games to restrict destroying the arena.
     */
    @EventHandler
    fun onPlayerExecuteCommand(event: PlayerCommandPreprocessEvent) {
        if (event.message.startsWith("/blockball")
            || event.message.startsWith("/" + configurationService.findValue<String>("global-leave.command"))
            || Permission.STAFF.hasPermission(event.player) || Permission.ADMIN.hasPermission(event.player)
            || event.player.isOp
        ) {

            return
        }

        var game = gameService.getGameFromPlayer(event.player)
        if (game.isPresent) {
            game = gameService.getGameFromSpectatingPlayer(event.player)
        }

        if (game.isPresent && game.get().arena.enabled && (game.get().arena.gameType == GameType.MINIGAME || game.get().arena.gameType == GameType.BUNGEE)) {
            event.isCancelled = true
        }
    }
}
