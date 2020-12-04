package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.BallSpawnEvent
import com.github.shynixn.blockball.api.business.service.EventService
import com.github.shynixn.blockball.core.logic.persistence.event.*
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class EventServiceImpl : EventService {
    /**
     * Sends a custom event.
     */
    override fun sendEvent(event: Any) {
        require(event is BlockBallEventEntity)

        when (event) {
            is BallDeathEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallDeathEvent(event.ballProxy)
                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
            }
            is BallKickEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallKickEvent(
                    event.ballProxy,
                    event.player as Player,
                    event.velocity as Vector
                )
                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.velocity = bukkitEvent.velocity
            }
            is BallPassEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallPassEvent(
                    event.ballProxy,
                    event.player as Player,
                    event.velocity as Vector
                )
                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.velocity = bukkitEvent.velocity
            }
            is BallRayTraceEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallRayTraceEvent(
                    event.ballProxy, event.hitBlock,
                    event.targetLocation as Location, event.blockDirection
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.blockDirection = bukkitEvent.blockDirection
                event.hitBlock = bukkitEvent.hitBlock
                event.targetLocation = bukkitEvent.targetLocation
            }
            is BallSpawnEventEntity -> {
                val bukkitEvent = BallSpawnEvent(event.ballProxy)
                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
            }
            is BallTeleportEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallTeleportEvent(
                    event.ballProxy,
                    event.targetLocation as Location
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.targetLocation = bukkitEvent.targetLocation
            }
            is BallTouchEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.BallTouchEvent(
                    event.ballProxy,
                    event.player as Player,
                    event.velocity as Vector
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.velocity = bukkitEvent.velocity
            }
            is GameEndEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.GameEndEvent(
                    event.winningTeam,
                    event.game
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.winningTeam = bukkitEvent.winningTeam
            }
            is GameGoalEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.GameGoalEvent(
                    event.player as Player,
                    event.team,
                    event.game
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.team = bukkitEvent.team
                event.player = bukkitEvent.player
            }
            is GameJoinEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.GameJoinEvent(
                    event.player as Player,
                    event.game
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.player = bukkitEvent.player
            }
            is GameLeaveEventEntity -> {
                val bukkitEvent = com.github.shynixn.blockball.api.bukkit.event.GameLeaveEvent(
                    event.player as Player,
                    event.game
                )

                Bukkit.getServer().pluginManager.callEvent(bukkitEvent)
                event.isCancelled = bukkitEvent.isCancelled
                event.player = bukkitEvent.player
            }
            else -> {
                throw IllegalArgumentException("This event type $event does not exist!")
            }
        }
    }
}
