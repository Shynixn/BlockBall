@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.bukkit.event.GameEndEvent
import com.github.shynixn.blockball.api.bukkit.event.GameGoalEvent
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.isLocationInSelection
import com.github.shynixn.blockball.bukkit.logic.business.extension.replaceGamePlaceholder
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

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
class GameSoccerServiceImpl @Inject constructor(
    private val concurrencyService: ConcurrencyService,
    private val screenMessageService: ScreenMessageService,
    private val dependencyService: DependencyService,
    private val ballEntityService: BallEntityService
) : GameSoccerService {
    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: Game, ticks: Int) {
        this.fixBallPositionSpawn(game)
        this.checkBallInGoal(game)

        if (ticks >= 20) {
            this.handleBallSpawning(game)
        }
    }

    private fun fixBallPositionSpawn(game: Game) {
        if (game.ball == null || game.ball!!.isDead) {
            return
        }

        if (!game.arena.isLocationInSelection(game.ball!!.getLocation() as Location)
            && !game.arena.meta.redTeamMeta.goal.isLocationInSelection(game.ball!!.getLocation() as Location)
            && !game.arena.meta.blueTeamMeta.goal.isLocationInSelection(game.ball!!.getLocation() as Location)
        ) {
            if (game.ballBumper == 0) {
                rescueBall(game)
            }
        } else {
            game.ballBumperCounter = 0
            game.lastBallLocation = (game.ball!!.getLocation() as Location).clone()
        }

        if (game.ingamePlayersStorage.isEmpty()) {
            game.ball!!.remove()
        }

        if (game.ballBumper > 0) {
            game.ballBumper--
        }
    }

    private fun rescueBall(game: Game) {
        if (game.lastBallLocation != null) {
            val ballLocation = game.ball!!.getLocation() as Location
            val knockback = (game.lastBallLocation!! as Location).toVector().subtract(ballLocation.toVector())
            ballLocation.direction = knockback
            game.ball!!.setVelocity(knockback)
            val direction = game.arena.meta.ballMeta.spawnpoint!!.toLocation().toVector().subtract(ballLocation.toVector())
            game.ball!!.setVelocity(direction.multiply(0.1))
            game.ballBumper = 40
            game.ballBumperCounter++
            if (game.ballBumperCounter == 5) {
                (game.ball as BallProxy).teleport(game.arena.meta.ballMeta.spawnpoint!!.toLocation())
            }
        }
    }

    private fun checkBallInGoal(game: Game) {
        if (game.ball == null || game.ball!!.isDead || game.ballSpawning) {
            return
        }

        if (game.arena.meta.redTeamMeta.goal.isLocationInSelection(game.ball!!.getLocation() as Location)) {
            game.blueScore += game.arena.meta.blueTeamMeta.pointsPerGoal
            onScore(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            onScoreReward(game, game.blueTeam as List<Player>)
            relocatePlayersAndBall(game)
            if (game.blueScore >= game.arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(game, game.blueTeam as List<Player>, game.redTeam as List<Player>)
                onWin(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            }
        } else if (game.arena.meta.blueTeamMeta.goal.isLocationInSelection(game.ball!!.getLocation() as Location)) {
            game.redScore += game.arena.meta.redTeamMeta.pointsPerGoal
            onScore(game, Team.RED, game.arena.meta.redTeamMeta)
            onScoreReward(game, game.redTeam as List<Player>)
            relocatePlayersAndBall(game)
            if (game.redScore >= game.arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(game, game.redTeam as List<Player>, game.blueTeam as List<Player>)
                onWin(game, Team.RED, game.arena.meta.redTeamMeta)
            }
        }
    }

    /**
     * Teleports all players and ball back to their spawnpoint if [game] has got back teleport enabled.
     */
    private fun relocatePlayersAndBall(game: Game) {
        if (!game.arena.meta.customizingMeta.backTeleport) {
            respawnBall(game)
            return
        }

        val delay = 20 * game.arena.meta.customizingMeta.backTeleportDelay

        respawnBall(game, delay)
        sync(concurrencyService, delay.toLong()) {
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

    private fun respawnBall(game: Game, delayInTicks: Int = game.arena.meta.ballMeta.delayInTicks) {
        if (game.ballSpawning) {
            return
        }

        game.ballSpawning = true
        game.ballSpawnCounter = delayInTicks / 20
    }

    private fun handleBallSpawning(game: Game) {
        if (game.ballSpawning) {
            if (game is MiniGame && game.endGameActive) {
                return
            }

            game.ballSpawnCounter--
            if (game.ballSpawnCounter <= 0) {
                if (game.ball != null && !game.ball!!.isDead) {
                    game.ball!!.remove()
                }

                game.ball = ballEntityService.spawnTemporaryBall(game.arena.meta.ballMeta.spawnpoint!!.toLocation(), game.arena.meta.ballMeta)
                game.ballSpawning = false
                game.ballSpawnCounter = 0
            }
        } else if ((game.ball == null || game.ball!!.isDead)
            && (!game.redTeam.isEmpty() || !game.blueTeam.isEmpty())
        ) {

            if (game.arena.gameType != GameType.HUBGAME || game.redTeam.size >= game.arena.meta.redTeamMeta.minAmount && game.blueTeam.size >= game.arena.meta.blueTeamMeta.minAmount) {
                game.ballSpawning = true
                game.ballSpawnCounter = game.arena.meta.ballMeta.delayInTicks
            }
        }
    }

    /**
     * Gets called when a goal gets scored on the given [game] by the given [team].
     */
    private fun onScore(game: Game, team: Team, teamMeta: TeamMeta) {
        var interactionEntity: Player? = null

        if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
            interactionEntity = game.lastInteractedEntity!! as Player
        }

        if (interactionEntity == null) {
            if (game.ingamePlayersStorage.isEmpty()) {
                return
            }

            interactionEntity = game.ingamePlayersStorage.keys.toTypedArray()[0] as Player
            game.lastInteractedEntity = interactionEntity
        }

        val event = GameGoalEvent(interactionEntity, team, game)
        Bukkit.getServer().pluginManager.callEvent(event)

        if (event.isCancelled) {
            return
        }

        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers(game)
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p ->
            screenMessageService.setTitle(
                p,
                scoreMessageTitle.replaceGamePlaceholder(game),
                scoreMessageSubTitle.replaceGamePlaceholder(game)
            )
        }
    }


    private fun onScoreReward(game: Game, players: List<Player>) {
        if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
            if (players.contains(game.lastInteractedEntity!!)) {
                if (dependencyService.isInstalled(PluginDependency.VAULT) && game.arena.meta.rewardMeta.moneyReward.containsKey(RewardType.SHOOT_GOAL)) {
                    val vaultService = BlockBallApi.resolve(DependencyVaultService::class.java)
                    vaultService.addMoney(game.lastInteractedEntity, game.arena.meta.rewardMeta.moneyReward[RewardType.SHOOT_GOAL]!!.toDouble())
                }
                if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.SHOOT_GOAL)) {
                    this.executeCommand(
                        game,
                        game.arena.meta.rewardMeta.commandReward[RewardType.SHOOT_GOAL]!!,
                        kotlin.collections.arrayListOf(game.lastInteractedEntity as Player)
                    )
                }
            }
        }
    }

    override fun <P> onMatchEnd(game: Game, winningPlayers: List<P>?, loosingPlayers: List<P>?) {
        if (dependencyService.isInstalled(PluginDependency.VAULT)) {
            val vaultService = BlockBallApi.resolve(DependencyVaultService::class.java)

            if (game.arena.meta.rewardMeta.moneyReward.containsKey(RewardType.WIN_MATCH) && winningPlayers != null) {
                winningPlayers.forEach { p ->
                    vaultService.addMoney(p, game.arena.meta.rewardMeta.moneyReward[RewardType.WIN_MATCH]!!.toDouble())
                }
            }

            if (game.arena.meta.rewardMeta.moneyReward.containsKey(RewardType.LOOSING_MATCH) && loosingPlayers != null) {
                loosingPlayers.forEach { p ->
                    vaultService.addMoney(p, game.arena.meta.rewardMeta.moneyReward[RewardType.LOOSING_MATCH]!!.toDouble())
                }
            }
            if (game.arena.meta.rewardMeta.moneyReward.containsKey(RewardType.PARTICIPATE_MATCH)) {
                game.inTeamPlayers.forEach { p ->
                    vaultService.addMoney(p, game.arena.meta.rewardMeta.moneyReward[RewardType.PARTICIPATE_MATCH]!!.toDouble())
                }
            }
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.WIN_MATCH) && winningPlayers != null) {
            this.executeCommand(game, game.arena.meta.rewardMeta.commandReward[RewardType.WIN_MATCH]!!, winningPlayers as List<Player>)
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.LOOSING_MATCH) && loosingPlayers != null) {
            this.executeCommand(game, game.arena.meta.rewardMeta.commandReward[RewardType.LOOSING_MATCH]!!, loosingPlayers as List<Player>)
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.PARTICIPATE_MATCH)) {
            this.executeCommand(game, game.arena.meta.rewardMeta.commandReward[RewardType.PARTICIPATE_MATCH]!!, game.inTeamPlayers as List<Player>)
        }
    }

    /**
     * Gets called when the given [game] gets win by the given [team].
     */
    override fun onWin(game: Game, team: Team, teamMeta: TeamMeta) {
        val event = GameEndEvent(team, game)
        Bukkit.getServer().pluginManager.callEvent(event)

        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers(game)
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p -> screenMessageService.setTitle(p, winMessageTitle.replaceGamePlaceholder(game), winMessageSubTitle.replaceGamePlaceholder(game)) }

        game.closing = true
    }

    /**
     * Get nofified players.
     */
    private fun getNofifiedPlayers(game: Game): List<Pair<Any, Boolean>> {
        val players = ArrayList<Pair<Any, Boolean>>()

        if (game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            game.arena.center.toLocation().world!!.players.forEach { p ->
                if (p.location.distance(game.arena.center.toLocation()) <= game.arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(p, true))
                } else {
                    players.add(Pair(p, false))
                }
            }
        }

        return players
    }

    private fun executeCommand(game: Game, meta: CommandMeta, players: List<Player>) {
        var command = meta.command
        if (command!!.startsWith("/")) {
            command = command.substring(1, command.length)
        }
        if (command.equals("none", true)) {
            return
        }
        when {
            meta.mode == CommandMode.PER_PLAYER -> players.forEach { p ->
                p.performCommand(command.replaceGamePlaceholder(game))
            }
            meta.mode == CommandMode.CONSOLE_PER_PLAYER -> players.forEach { p ->
                game.lastInteractedEntity = p
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceGamePlaceholder(game))
            }
            meta.mode == CommandMode.CONSOLE_SINGLE -> Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replaceGamePlaceholder(game))
        }
    }
}