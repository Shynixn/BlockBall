package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Arena
import com.github.shynixn.blockball.entity.GameStorage
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.JoinResult
import com.github.shynixn.blockball.enumeration.LeaveResult
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.entity.Player

interface BlockBallGame {
    /**
     * Player who was the last one to hit the ball.
     */
    var lastHitPlayer: Player?

    /**
     * Gets the arena.
     */
    val arena: Arena

    /**
     * RedScore.
     */
    var redScore: Int

    /**
     * Blue Score.
     */
    var blueScore: Int

    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    var closing: Boolean

    /**
     * Ingame scoreboard.
     */
    var scoreboard: Any?

    /**
     * Ingame bossbar.
     */
    var bossBar: Any?

    /**
     * Ingame holograms.
     */
    val holograms: MutableList<HologramProxy>

    /**
     * All players which are already fix in team red.
     */
    val redTeam: List<Player>

    /**
     * All players which are already fix in team blue.
     */
    val blueTeam: List<Player>

    /**
     * Is the game closed.
     */
    var closed: Boolean

    /**
     * Status.
     */
    var status: GameState

    /**
     * Ball.
     */
    var ball: Ball?

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
   var lastInteractedEntity: Any?

    /**
     * Contains players which are in cooldown by doublejump.
     */
    val doubleJumpCoolDownPlayers: MutableMap<Player, Int>

    /**
     * Storage.
     */
    val ingamePlayersStorage: MutableMap<Player, GameStorage>

    /**
     * Ball bumper counter
     */
    var ballBumperCounter: Int

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
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
     * Closes the given game and all underlying resources.
     */
    fun close()
}
