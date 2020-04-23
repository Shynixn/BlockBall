package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import com.github.shynixn.blockball.api.persistence.entity.Position

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
open class GameEntity(
    /**
     *  Arena of the game.
     */
    override val arena: Arena
) : Game {
    /**
     * Cached block position for the ball forcefield.
     */
    override var ballForceFieldBlockPosition: Position? = null
    /**
     * Cached in arena position for the ball forcefield.
     */
    override var ballForceFieldArenaPosition: Position? = null

    /**
     * Status.
     */
    override var status: GameStatus = GameStatus.ENABLED
    /**
     * Ingame scoreboard.
     */
    override var scoreboard: Any? = null
    /**
     * Ingame bossbar.
     */
    override var bossBar: Any? = null
    /**
     * Ingame holograms.
     */
    override val holograms: MutableList<HologramProxy> = ArrayList()
    /**
     * Contains players which are in cooldown by doublejump.
     */
    override val doubleJumpCoolDownPlayers: MutableMap<Any, Int> = HashMap()
    /**
     * Ball.
     */
    override var ball: BallProxy? = null
    /**
     * Last location of the ball.
     */
    override var lastBallLocation: Any? = null
    /**
     * Is the ball spawning?
     */
    override var ballSpawning: Boolean = false

    /**
     * Ball spawn counter.
     */
    override var ballSpawnCounter: Int = 0
    /**
     * Ball bumper counter
     */
    override var ballBumperCounter: Int = 0
    /**
     * Is the ball currently enabled to spawn?
     */
    override var ballEnabled: Boolean = true
    /**
     * Are the goals mirrored?
     */
    override var mirroredGoals: Boolean = false
    /**
     * Ball bumper.
     */
    override var ballBumper: Int = 20
    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    override var closing: Boolean = false
    /**
     * RedScore.
     */
    override var redScore: Int = 0
    /**
     * Blue Score.
     */
    override var blueScore: Int = 0

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
    override var lastInteractedEntity: Any? = null

    /**
     * Storag.
     */
    override val ingamePlayersStorage: MutableMap<Any, GameStorage> = HashMap()
    /**
     * List of players which are already in the [redTeam] or [blueTeam].
     */
    override val inTeamPlayers: List<Any>
        get() {
            val players = ArrayList(redTeam)
            players.addAll(blueTeam)
            return players
        }

    /**
     * All players which are already fix in team red.
     */
    override val redTeam: List<Any>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.RED }
                .keys.toList()
        }
    /**
     * All players which are already fix in team blue.
     */
    override val blueTeam: List<Any>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.BLUE }
                .keys.toList()
        }

    /**
     * Are currently players actively playing in this game?
     */
    override var playing: Boolean = false
    /**
     * Is the game closed.
     */
    override var closed: Boolean = false
}