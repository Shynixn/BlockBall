package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import org.bukkit.entity.Player

/**
 * Event which gets sent when a player triggers a ball action.
 * You may cancel this event to disallow player interacting with the ball.
 */
class BallTriggerActionEvent(ball: SoccerBall, val player: Player, val triggerActionType: BallTriggerActionType) :
    BallEvent(ball) {
}