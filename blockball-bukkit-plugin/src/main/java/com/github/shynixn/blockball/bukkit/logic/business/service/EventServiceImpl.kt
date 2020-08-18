package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.service.EventService
import com.github.shynixn.blockball.core.logic.persistence.entity.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event

class EventServiceImpl : EventService {
    /**
     * Sends a custom event.
     */
    override fun sendEvent(event: Any) {
        val internalEvent: Event = when (event) {
            is GameEndEventEntity -> {
                GameEndEvent(event.winningTeam, event.game)
            }
            is GameJoinEventEntity -> {
                GameJoinEvent(event.player as Player, event.game)
            }
            is GameLeaveEventEntity -> {
                GameLeaveEvent(event.player as Player, event.game)
            }
            is GameGoalEventEntity -> GameGoalEvent(event.player as Player?, event.team, event.game)
            else -> {
                throw IllegalArgumentException("This event type $event does not exist!")
            }
        }

        Bukkit.getServer().pluginManager.callEvent(internalEvent)

        if (event is GameCancelableEventEntity) {
            require(internalEvent is GameCancelableEvent)
            event.isCancelled = internalEvent.isCancelled
        }
    }
}