package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.business.service.HubGameForcefieldService
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

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
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class HubgameListener @Inject constructor(private val hubGameForcefieldService: HubGameForcefieldService) : Listener {
    /** Handles the forcefield of hubGames. */
    @EventHandler
    fun onPlayerMoveAgainstHubForceField(event: PlayerMoveEvent) {
        if (event.to == null || event.to!!.distance(event.from) <= 0) {
            return
        }

        hubGameForcefieldService.checkForForcefieldInteractions(event.player, event.to)
    }

    /**
     * Gets called when the player teleports into the hubfield directly.
     */
    @EventHandler
    fun onPlayerTeleportEvent(event: PlayerTeleportEvent) {
        if (event.to == null) {
            return
        }

        hubGameForcefieldService.checkForForcefieldInteractions(event.player, event.to)
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