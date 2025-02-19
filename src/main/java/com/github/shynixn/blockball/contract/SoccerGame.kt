package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.entity.GameStorage
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.JoinResult
import com.github.shynixn.blockball.enumeration.LeaveResult
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.Location
import org.bukkit.entity.Player

interface SoccerGame {
    /**
     * Player who was the last one to hit the ball.
     */
    var lastHitPlayer: Player?

    /**
     * Gets the soccerArena.
     */
    val arena: SoccerArena

    /**
     * RedScore.
     */
    var redScore: Int

    /**
     * Blue Score.
     */
    var blueScore: Int

    /**
     * Ingame scoreboard.
     */
    var scoreboard: Any?

    /**
     * Ingame bossbar.
     */
    var bossBar: Any?

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
     * Status.
     */
    var status: GameState

    /**
     * SoccerBall.
     */
    var ball: SoccerBall?

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
    var lastInteractedEntity: Player?

    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    var closing: Boolean

    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    var closed: Boolean

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
    val language : BlockBallLanguage

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
    fun handle(ticks: Int)

    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    fun respawn(player: Player)

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
}
