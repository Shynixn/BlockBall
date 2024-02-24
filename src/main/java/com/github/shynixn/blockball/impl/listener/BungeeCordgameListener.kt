package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.RightclickManageService
import com.github.shynixn.blockball.entity.BungeeCordGame
import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.plugin.Plugin

class BungeeCordgameListener @Inject constructor(
    private val gameService: GameService,
    private val rightClickManageService: RightclickManageService,
    private val gameActionService: GameActionService,
    private val plugin: Plugin
) : Listener {
    /**
     * Joins the game for a bungeecord player.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val game = gameService.getAllGames().firstOrNull { p -> p.arena.gameType == GameType.BUNGEE } ?: return
        val player = event.player

        plugin.launch {
            delay(40.ticks)
            if (player.isOnline) {
                val success = gameActionService.joinGame(game, player)

                if (!success) {
                    player.kickPlayer(BlockBallLanguage.bungeeCordKickMessage)
                }
            }
        }
    }

    @EventHandler
    fun onPingServerEven(event: ServerListPingEvent) {
        val game = gameService.getAllGames().find { p -> p.arena.gameType == GameType.BUNGEE }

        if (game != null && game is BungeeCordGame) {
            if (game.modt.isNotBlank()) {
                event.motd = game.modt
            }
        }
    }

    /**
     * Handles click on signs to create new server signs or to connect players to the server
     * written on the sign.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.clickedBlock != null && event.clickedBlock!!.state !is Sign) {
            return
        }

        rightClickManageService.executeWatchers(event.player, event.clickedBlock!!.location)
    }
}
