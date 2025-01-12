package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.HubGameForcefieldService
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class HubgameListener (private val hubGameForcefieldService: HubGameForcefieldService) : Listener {
    /** Handles the forcefield of hubGames. */
    @EventHandler
    fun onPlayerMoveAgainstHubForceField(event: PlayerMoveEvent) {
        if (event.to == null || event.to!!.distance(event.from) <= 0) {
            return
        }

        hubGameForcefieldService.checkForForcefieldInteractions(event.player, event.to!!)
    }

    /**
     * Gets called when the player teleports into the hubfield directly.
     */
    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        hubGameForcefieldService.checkForForcefieldInteractions(event.player, event.to!!)
    }

    /**
     * Gets called when a player leaves the server and the game.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        hubGameForcefieldService.cleanResources(event.player)
    }

    /**
     * The [event] gets called when a player toggles flight with double space pressing and
     * disables flying when it's a player in a game that simply used double jump.
     */
    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (event.player.gameMode == GameMode.CREATIVE) {
            return
        }

        val interactionCache = hubGameForcefieldService.getInteractionCache(event.player)

        if (!interactionCache.toggled) {
            return
        }

        event.player.allowFlight = false
        event.player.isFlying = false
        event.isCancelled = true
        interactionCache.toggled = false
    }
}
