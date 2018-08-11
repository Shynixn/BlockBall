@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.ball.api.BallApi
import com.github.shynixn.ball.api.business.entity.Ball
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.GameSoccerService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.isLocationInSelection
import com.github.shynixn.blockball.bukkit.logic.business.extension.sync
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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
 * of game software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and game permission notice shall be included in all
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
class GameSoccerServiceImpl<G : Game> @Inject constructor(private val plugin: Plugin) : GameSoccerService<G> {
    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: G, ticks: Int) {
        this.fixBallPositionSpawn(game)
        this.checkBallInGoal(game)
        this.handleBallSpawning(game)
    }

    private fun fixBallPositionSpawn(game: G) {
        if (game.ball == null || game.ball!!.isDead) {
            return
        }

        if (!game.arena.isLocationInSelection(game.ball!!.location as Location)
                && !game.arena.meta.redTeamMeta.goal.isLocationInSelection(game.ball!!.location as Location)
                && !game.arena.meta.blueTeamMeta.goal.isLocationInSelection(game.ball!!.location as Location)) {
            if (game.ballBumper == 0) {
                rescueBall(game)
            }
        } else {
            game.ballBumperCounter = 0
            game.lastBallLocation = (game.ball!!.location as Location).clone()
        }

        if (game.ingamePlayersStorage.isEmpty()) {
            game.ball!!.remove()
        }

        if (game.ballBumper > 0) {
            game.ballBumper--
        }
    }

    private fun rescueBall(game: G) {
        if (game.lastBallLocation != null) {
            val ballLocation = game.ball!!.location
            val knockback = (game.lastBallLocation!! as Location).toVector().subtract((ballLocation as Location).toVector())
            ballLocation.direction = knockback
            (game.ball!!.hitBox as ArmorStand).velocity = knockback
            val direction = game.arena.meta.ballMeta.spawnpoint!!.toLocation().toVector().subtract(ballLocation.toVector())
            (game.ball!!.hitBox as ArmorStand).velocity = direction.multiply(0.1)
            game.ballBumper = 40
            game.ballBumperCounter++
            if (game.ballBumperCounter == 5) {
                (game.ball as Ball<Any, Any, Any>).teleport(game.arena.meta.ballMeta.spawnpoint!!.toLocation())
            }
        }
    }

    private fun checkBallInGoal(game: G) {
        if (game.ball == null || game.ball!!.isDead) {
            return
        }

        if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(game.ball!!.location as Location)) {
            game.blueScore++
            game.ball!!.remove()
            //   game.onScore(Team.BLUE, game.arena.meta.blueTeamMeta)
            //  game.onScoreReward(blueTeam)
            teleportBackToSpawnpoint(game)
            if (game.blueScore >= game.arena.meta.lobbyMeta.maxScore) {
                //    game.onMatchEnd(blueTeam, redTeam)
                //  game.onWin(Team.BLUE, game.arena.meta.blueTeamMeta)
            }
        } else if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(game.ball!!.location as Location)) {
            game.redScore++
            game.ball!!.remove()
            //  game.onScore(Team.RED, game.arena.meta.redTeamMeta)
            //  game.onScoreReward(redTeam)
            teleportBackToSpawnpoint(game)
            if (game.redScore >= game.arena.meta.lobbyMeta.maxScore) {
                //  game.onMatchEnd(redTeam, blueTeam)
                //  game.onWin(Team.RED, game.arena.meta.redTeamMeta)
            }
        }
    }

    /**
     * Teleports all players back to their spawnpoint if [arena] has got back teleport enabled.
     */
    private fun teleportBackToSpawnpoint(game: G) {
        if (!game.arena.meta.customizingMeta.backTeleport) {
            return
        }

        sync(plugin, 20L * game.arena.meta.customizingMeta.backTeleportDelay) {
            var redTeamSpawnpoint = game.arena.meta.redTeamMeta.spawnpoint?.toLocation()
            if (redTeamSpawnpoint == null) {
                redTeamSpawnpoint = game.arena.meta.ballMeta.spawnpoint!!.toLocation()
            }

            var blueTeamSpawnpoint = game.arena.meta.blueTeamMeta.spawnpoint?.toLocation()
            if (blueTeamSpawnpoint == null) {
                blueTeamSpawnpoint = game.arena.meta.ballMeta.spawnpoint!!.toLocation()
            }

            game.ingamePlayersStorage.forEach { i ->
                if (i.value.team == Team.RED) {
                    (i.key as Player).teleport(redTeamSpawnpoint)
                } else if (i.value.team == Team.BLUE) {
                    (i.key as Player).teleport(blueTeamSpawnpoint)
                }
            }
        }
    }

    private fun handleBallSpawning(game: G) {
        if (game.ballSpawning) {
            game.ballSpawnCounter--
            if (game.ballSpawnCounter <= 0) {
                game.ball = BallApi.spawnTemporaryBall(game.arena.meta.ballMeta.spawnpoint!!.toLocation(), game.arena.meta.ballMeta)
                game.ballSpawning = false
                game.ballSpawnCounter = 0
            }
        } else if ((game.ball == null || game.ball!!.isDead)
                && (!game.redTeam.isEmpty() || !game.blueTeam.isEmpty())) {

            if (game.arena.gameType != GameType.HUBGAME || game.redTeam.size >= game.arena.meta.redTeamMeta.minAmount && game.blueTeam.size >= game.arena.meta.blueTeamMeta.minAmount) {
                game.ballSpawning = true
                game.ballSpawnCounter = game.arena.meta.ballMeta.delayInTicks
            }
        }
    }
}