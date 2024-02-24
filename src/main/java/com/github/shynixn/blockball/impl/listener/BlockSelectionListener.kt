package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.BlockSelectionService
import com.google.inject.Inject
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent

class BlockSelectionListener @Inject constructor(private val blockSelectionService: BlockSelectionService) : Listener {
    /**
     * Gets called when the player interacts with a block.
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action == Action.LEFT_CLICK_BLOCK) {
            val shouldBeCancelled =
                blockSelectionService.selectLeftLocation(event.player, event.clickedBlock!!.location)

            @Suppress("DEPRECATION")
            if (!event.isCancelled) {
                event.isCancelled = shouldBeCancelled
            }
        } else if (event.action == Action.RIGHT_CLICK_BLOCK) {
            val shouldBeCancelled =
                blockSelectionService.selectRightLocation(event.player, event.clickedBlock!!.location)

            @Suppress("DEPRECATION")
            if (!event.isCancelled) {
                event.isCancelled = shouldBeCancelled
            }
        }
    }

    /**
     * Gets called when a player leaves the server.
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        blockSelectionService.cleanResources(event.player)
    }
}
