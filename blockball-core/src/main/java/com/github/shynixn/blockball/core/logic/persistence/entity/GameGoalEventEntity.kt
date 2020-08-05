package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.Game

class GameGoalEventEntity(
    /**
     *  Player last touching the ball. Can be null in very rare cases, mostly when entities interact with the ball.
     */
    val player: Any?,
    /**
     * Team scoring the goal.
     */
    val team: Team, val game: Game
) : GameCancelableEventEntity()