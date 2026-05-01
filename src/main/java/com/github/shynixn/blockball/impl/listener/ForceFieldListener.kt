package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.ForceFieldService
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class ForceFieldListener(private val forceFieldService: ForceFieldService) : Listener {
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (event.to == null || event.to?.world != event.from.world || event.to!!.distance(event.from) <= 0) {
            return
        }

        forceFieldService.checkInteractionsWithForceField(event.player, event.to!!)
    }

    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        forceFieldService.checkInteractionsWithForceField(event.player, event.to!!)
    }

    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        val player = event.player

        if (player.gameMode == GameMode.CREATIVE || player.gameMode == GameMode.SPECTATOR) {
            return
        }

        if (!forceFieldService.isInFlightStatus(player)) {
            return
        }

        forceFieldService.resetFlightStatus(player)
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val player = event.player

        if (forceFieldService.isInFlightStatus(player)) {
            forceFieldService.resetFlightStatus(player)
        }

        forceFieldService.clear(player)
    }

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        forceFieldService.checkInteractionsWithForceField(event.player, event.player.location)
    }
}