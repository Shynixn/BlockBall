package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.SoccerBallService
import com.github.shynixn.blockball.enumeration.ClickType
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mcutils.packet.api.event.PacketAsyncEvent
import com.github.shynixn.mcutils.packet.api.meta.enumeration.InteractionType
import com.github.shynixn.mcutils.packet.api.packet.PacketInInteractEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

class BallListener(private val plugin: Plugin, private val soccerBallService: SoccerBallService) : Listener {
    /**
     * Gets called when a packet arrives.
     */
    @EventHandler
    fun onPacketEvent(event: PacketAsyncEvent) {
        val packet = event.packet

        if (packet !is PacketInInteractEntity) {
            return
        }

        val ball = soccerBallService.getByEntityId(packet.entityId) ?: return
        plugin.launch(plugin.regionDispatcher(ball.getLocation())) {
            if (packet.actionType == InteractionType.ATTACK) {
                ball.applyInteraction(event.player, ClickType.LEFT)
            } else {
                ball.applyInteraction(event.player, ClickType.RIGHT)
            }
        }
    }
}