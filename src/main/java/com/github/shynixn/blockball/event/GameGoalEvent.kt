package com.github.shynixn.blockball.event

import com.github.shynixn.blockball.entity.Game
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.entity.Player

/**
 * Event when someone scores a goal.
 */
class GameGoalEvent(
    /**
     *  Player last touching the ball. Can be null in very rare cases, mostly when entities interact with the ball.
     */
    val player: Player?,
    /**
     * Team scoring the goal.
     */
    val team: Team, game: Game
) : GameEvent(game)
