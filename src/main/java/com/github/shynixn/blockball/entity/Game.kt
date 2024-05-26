package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.contract.Ball
import com.github.shynixn.blockball.contract.HologramProxy
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.entity.Player

open class Game(
    /**
     *  Arena of the game.
     */
    val arena: Arena
) {
    /**
     * Status.
     */
    var status: GameState = GameState.JOINABLE
    /**
     * Ingame scoreboard.
     */
    var scoreboard: Any? = null
    /**
     * Ingame bossbar.
     */
    var bossBar: Any? = null
    /**
     * Ingame holograms.
     */
    val holograms: MutableList<HologramProxy> = ArrayList()
    /**
     * Contains players which are in cooldown by doublejump.
     */
    val doubleJumpCoolDownPlayers: MutableMap<Any, Int> = HashMap()
    /**
     * Ball.
     */
    var ball: Ball? = null
    /**
     * Last location of the ball.
     */
    var lastBallLocation: Any? = null
    /**
     * Is the ball spawning?
     */
    var ballSpawning: Boolean = false

    /**
     * Ball spawn counter.
     */
    var ballSpawnCounter: Int = 0
    /**
     * Ball bumper counter
     */
    var ballBumperCounter: Int = 0
    /**
     * Is the ball currently enabled to spawn?
     */
    var ballEnabled: Boolean = true
    /**
     * Are the goals mirrored?
     */
    var mirroredGoals: Boolean = false
    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    var closing: Boolean = false
    /**
     * RedScore.
     */
    var redScore: Int = 0
    /**
     * Blue Score.
     */
    var blueScore: Int = 0

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
    var lastInteractedEntity: Any? = null

    /**
     * Storag.
     */
    val ingamePlayersStorage: MutableMap<Player, GameStorage> = HashMap()
    /**
     * List of players which are already in the [redTeam] or [blueTeam].
     */
    val inTeamPlayers: List<Player>
        get() {
            val players = ArrayList(redTeam)
            players.addAll(blueTeam)
            return players
        }

    /**
     * All players which are already fix in team red.
     */
    val redTeam: List<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.RED }
                .keys.toList()
        }
    /**
     * All players which are already fix in team blue.
     */
    val blueTeam: List<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.BLUE }
                .keys.toList()
        }

    /**
     * Are currently players actively playing in this game?
     */
    var playing: Boolean = false
    /**
     * Is the game closed.
     */
    var closed: Boolean = false
}
