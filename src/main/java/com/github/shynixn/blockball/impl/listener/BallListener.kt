package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.event.*
import com.github.shynixn.mcutils.common.sound.SoundService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Handles common ball events.
 */
class BallListener (
    private val soccerBallFactory: SoccerBallFactory,
    private val soundService: SoundService
) : Listener {
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
    fun ballDeathEvent(event: BallRemoveEvent) {
        this.soccerBallFactory.removeTrackedBall(event.ball)
    }

    /**
     * Gets called when a player left clicks a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballKickEvent(event: BallLeftClickEvent) {
        this.playEffects(event.ball, BallActionType.ONKICK)
    }

    /**
     * Gets called when a player right clicks a ball.
     */
    @EventHandler
    fun ballPassEvent(event: BallRightClickEvent) {
        this.playEffects(event.ball, BallActionType.ONPASS)
    }

    /**
     * Gets called when a player interacts a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballInteractEvent(event: BallTouchPlayerEvent) {
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
    private fun playEffects(ball: SoccerBall, actionEffect: BallActionType) {
        if (ball.meta.soundEffects.containsKey(actionEffect)) {
            this.soundService.playSound(
                ball.getLocation(),
                ball.getLocation().world!!.players,
                ball.meta.soundEffects[actionEffect]!!,
            )
        }
    }
}
