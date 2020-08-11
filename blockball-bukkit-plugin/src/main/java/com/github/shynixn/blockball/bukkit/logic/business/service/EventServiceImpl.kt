package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.GameCancelableEvent
import com.github.shynixn.blockball.api.bukkit.event.GameEndEvent
import com.github.shynixn.blockball.api.bukkit.event.GameGoalEvent
import com.github.shynixn.blockball.api.business.service.EventService
import com.github.shynixn.blockball.core.logic.persistence.entity.GameCancelableEventEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameEndEventEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameGoalEventEntity
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