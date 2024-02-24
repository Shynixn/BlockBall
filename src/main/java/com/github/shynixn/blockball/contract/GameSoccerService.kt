package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Game
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.Team

interface GameSoccerService {
    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    fun handle(game: Game, ticks: Int)

    /**
     * Notifies that the ball is inside of the goal of the given team.
     * This team has to be the default goal of the team. Mirroring
     * is handled inside of the method.
     */
    fun notifyBallInGoal(game : Game, team: Team)

    /**
     * Gets called when the match ends. The [winningPlayers] and [loosingPlayers] parameter
     * can be both null when the match ends in a draw.
     */
    fun <P> onMatchEnd(game: Game, winningPlayers: List<P>?, loosingPlayers: List<P>?)

    /**
     * Gets called when the match gets won by the given team.
     */
    fun onWin(game: Game, team: Team, teamMeta: TeamMeta)
}
