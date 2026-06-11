package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.enumeration.BallExecuteActionType
import com.github.shynixn.blockball.enumeration.BallTriggerActionType
import org.bukkit.entity.Player

/**
 * Event which gets sent after an action has triggered and been translated to an actual action.
 */
class BallActionEvent(ball: SoccerBall, val player: Player, val executeActionType: BallExecuteActionType, val triggerActionType: BallTriggerActionType) :
    BallEvent(ball) {
}