package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.GameStorage
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.shyguild.entity.Guild
import org.bukkit.Location
import org.bukkit.entity.Player

interface SoccerGame {
    /**
     * Global Game State.
     */
    var status: GameState

    /**
     * If set, then the game is already disposed and must not be used.
     */
    val isDisposed: Boolean

    /**
     * The substate of the game.
     * BlockBall uses a state machine to determine the next action for modern actions.
     * Old actions are still being used but being refactored over time.
     */
    val subState: GameSubState

    /**
     * When this substate has ended.
     */
    val subStateEndTimeStamp: Long

    /**
     * The next substate in the game.
     */
    val subStateNext: GameSubState

    /**
     * If the next substate requires a location then this is the parameter.
     */
    val subStateLocationParam: Location?

    /**
     * If the next substate requires a player then this is the parameter.
     */
    val subStatePlayerParam: Player?

    /**
     * Generated game id.
     */
    val id: String

    /**
     * Gets the soccerArena.
     */
    val arena: SoccerArena

    /**
     * Red club in club mode.
     */
    var redClub: Guild?

    /**
     * Blue club in club mode.
     */
    var blueClub: Guild?

    /**
     * RedScore.
     */
    var redScore: Int

    /**
     * Are the goals mirrored?
     */
    var mirroredGoals: Boolean

    /**
     * Blue Score.
     */
    var blueScore: Int

    /**
     * All players which are already fix in team red.
     */
    val redTeam: Set<Player>

    /**
     * All players which are already fix in team blue.
     */
    val blueTeam: Set<Player>

    /**
     * All players which are referees.
     */
    val refereeTeam: Set<Player>

    /**
     * SoccerBall.
     */
    var ball: SoccerBall?

    /**
     * Contains the players which have touched the ball from the same team.
     * Once another team touches the ball this stack is cleared.
     */
    var interactedWithBall: MutableList<Player>

    /**
     * Contains players which are in cooldown by doublejump.
     */
    val doubleJumpCoolDownPlayers: MutableMap<Player, Int>

    /**
     * Storage.
     */
    val ingamePlayersStorage: MutableMap<Player, GameStorage>

    /**
     * SoccerBall bumper counter
     */
    var ballBumperCounter: Int

    /**
     * Compatibility reference.
     */
    val language: BlockBallLanguage

    /**
     * Performs all state Machine actions.
     */
    fun runStateMachine()

    /**
     * Is called when the substate of the game changes. This is used to perform actions when the substate changes.
     */
    fun onStateMachineSubStateChange(oldSubState: GameSubState, newSubState: GameSubState)

    /**
     * Checks if the game is in club mode and clubs are playing.
     */
    fun areClubsPlaying(): Boolean

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of soccerArena settings.
     * Does nothing if the player is already in a Game.
     */
    fun join(player: Player, team: Team? = null): JoinResult

    /**
     * Leaves the given player.
     */
    fun leave(player: Player): LeaveResult

    /**
     * Tick handle.
     */
    fun handle(hasSecondPassed: Boolean)

    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    fun respawn(player: Player, team: Team? = null)

    /**
     * Applies death points.
     */
    fun applyDeathPoints(player: Player)

    /**
     * Notifies that the ball is inside of the goal of the given team.
     * This team has to be the default goal of the team. Mirroring
     * is handled inside of the method.
     */
    fun notifyBallInGoal(team: Team)

    /**
     * Cancels the game.
     */
    fun close()

    /**
     * Gets all players.
     */
    fun getPlayers(): Set<Player>

    /**
     * Respawns the ball and sets it to the given location.
     */
    fun setBallToLocation(location: Location)

    /**
     * Sets the next substate of the game. The substate is used to determine the next action of the game.
     */
    fun setNextGameSubState(subState: GameSubState, timeFromNowMilliSeconds: Long = 0)
}
