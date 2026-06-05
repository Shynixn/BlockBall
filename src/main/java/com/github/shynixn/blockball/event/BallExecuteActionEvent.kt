package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.enumeration.BallExecuteActionType
import org.bukkit.entity.Player

/**
 * Event which gets sent after an action has triggered and been translated to an actual action.
 */
class BallExecuteActionEvent(ball: SoccerBall, val player: Player, val executeAction: BallExecuteActionType) :
    BallEvent(ball) {
}