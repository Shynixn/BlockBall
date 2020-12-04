@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.ProtocolService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Handles common ball events.
 */
class BallListener @Inject constructor(
    private val ballEntityService: BallEntityService,
    private val particleService: ParticleService,
    private val soundService: SoundService,
    private val protocolService: ProtocolService
) : Listener {
    /**
     * Registers the player on join.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        protocolService.register(event.player)
    }

    /**
     * Unregisters the player on leave.
     */
    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        protocolService.unRegister(event.player)
    }

    /**
     * Gets called when the ball raytraces in the world.
     */
    @EventHandler
    fun ballRayTraceEvent(event: BallRayTraceEvent) {
        this.playEffects(event.ball, BallActionType.ONMOVE)
    }

    /**
     * Gets called when a ball dies.
     *
     * @param event event
     */
    @EventHandler
    fun ballDeathEvent(event: BallDeathEvent) {
        this.ballEntityService.removeTrackedBall(event.ball)
    }

    /**
     * Gets called when a player left clicks a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballKickEvent(event: BallKickEvent) {
        this.playEffects(event.ball, BallActionType.ONKICK)
    }

    /**
     * Gets called when a player interacts a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballInteractEvent(event: BallTouchEvent) {
        this.playEffects(event.ball, BallActionType.ONINTERACTION)
    }

    /**
     * Gets called when the ball spawns.
     *
     * @param event event
     */
    @EventHandler
    fun ballSpawnEvent(event: BallSpawnEvent) {
        this.playEffects(event.ball, BallActionType.ONSPAWN)
    }

    /**
     * Gets called when a ball gets shot into goal.
     *
     * @param event event
     */
    @EventHandler
    fun gameGoalEvent(event: GameGoalEvent) {
        playEffects(event.game.ball!!, BallActionType.ONGOAL)
    }

    /**
     * Plays effects.
     */
    private fun playEffects(ball: BallProxy, actionEffect: BallActionType) {
        if (ball.meta.particleEffects.containsKey(actionEffect)) {
            this.particleService.playParticle(
                ball.getLocation<Any>(),
                ball.meta.particleEffects[actionEffect]!!,
                ball.getLocation<Location>().world!!.players
            )
        }

        if (ball.meta.soundEffects.containsKey(actionEffect)) {
            this.soundService.playSound(
                ball.getLocation<Any>(),
                ball.meta.soundEffects[actionEffect]!!,
                ball.getLocation<Location>().world!!.players
            )
        }
    }
}
