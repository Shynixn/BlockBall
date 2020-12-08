@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.*
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.CommandMeta
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.TeamMeta
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.persistence.event.GameEndEventEntity
import com.github.shynixn.blockball.core.logic.persistence.event.GameGoalEventEntity
import com.google.inject.Inject

class GameSoccerServiceImpl @Inject constructor(
    private val concurrencyService: ConcurrencyService,
    private val screenMessageService: ScreenMessageService,
    private val dependencyService: DependencyService,
    private val ballEntityService: BallEntityService,
    private val placeholderService: PlaceholderService,
    private val proxyService: ProxyService,
    private val eventService: EventService
) : GameSoccerService {
    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: Game, ticks: Int) {
        this.fixBallPositionSpawn(game)

        if (ticks >= 20) {
            this.handleBallSpawning(game)
        }
    }

    /**
     * Notifies that the ball is inside of the goal of the given team.
     * This team has to be the default goal of the team. Mirroring
     * is handled inside of the method.
     */
    override fun notifyBallInGoal(game: Game, team: Team) {
        if (game.ballSpawning) {
            return
        }

        var teamOfGoal = team

        if (teamOfGoal == Team.BLUE && game.mirroredGoals) {
            teamOfGoal = Team.RED
        } else if (teamOfGoal == Team.RED && game.mirroredGoals) {
            teamOfGoal = Team.BLUE
        }

        if (teamOfGoal == Team.RED) {
            game.blueScore += game.arena.meta.blueTeamMeta.pointsPerGoal
            onScore(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            onScoreReward(game, game.blueTeam)
            relocatePlayersAndBall(game)

            if (game.blueScore >= game.arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(game, game.blueTeam, game.redTeam)
                onWin(game, Team.BLUE, game.arena.meta.blueTeamMeta)
            }

            return
        }

        if (teamOfGoal == Team.BLUE) {
            game.redScore += game.arena.meta.redTeamMeta.pointsPerGoal
            onScore(game, Team.RED, game.arena.meta.redTeamMeta)
            onScoreReward(game, game.redTeam)
            relocatePlayersAndBall(game)

            if (game.redScore >= game.arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(game, game.redTeam, game.blueTeam)
                onWin(game, Team.RED, game.arena.meta.redTeamMeta)
            }
        }
    }

    private fun fixBallPositionSpawn(game: Game) {
        if (game.ball == null || game.ball!!.isDead) {
            return
        }
        if (game.ingamePlayersStorage.isEmpty()) {
            game.ball!!.remove()
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
            var redTeamSpawnpoint = game.arena.meta.redTeamMeta.spawnpoint

            if (redTeamSpawnpoint == null) {
                redTeamSpawnpoint = game.arena.meta.ballMeta.spawnpoint!!
            }

            var blueTeamSpawnpoint = game.arena.meta.blueTeamMeta.spawnpoint

            if (blueTeamSpawnpoint == null) {
                blueTeamSpawnpoint = game.arena.meta.ballMeta.spawnpoint!!
            }

            game.ingamePlayersStorage.forEach { i ->
                if (i.value.team == Team.RED) {
                    proxyService.teleport(i.key, redTeamSpawnpoint)
                } else if (i.value.team == Team.BLUE) {
                    proxyService.teleport(i.key, blueTeamSpawnpoint)
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
        if (game.ballSpawning && game.ballEnabled) {
            game.ballSpawnCounter--
            if (game.ballSpawnCounter <= 0) {
                if (game.ball != null && !game.ball!!.isDead) {
                    game.ball!!.remove()
                }

                game.ball = ballEntityService.spawnTemporaryBall(
                    proxyService.toLocation<Any>(game.arena.meta.ballMeta.spawnpoint!!),
                    game.arena.meta.ballMeta
                )
                game.ballSpawning = false
                game.ballSpawnCounter = 0
            }
        } else if ((game.ball == null || game.ball!!.isDead)
            && (game.redTeam.isNotEmpty() || game.blueTeam.isNotEmpty())
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
        var interactionEntity: Any? = null

        if (game.lastInteractedEntity != null && proxyService.isPlayerInstance(game.lastInteractedEntity)) {
            interactionEntity = game.lastInteractedEntity!!
        }

        if (interactionEntity == null) {
            if (game.ingamePlayersStorage.isEmpty()) {
                return
            }

            interactionEntity = game.ingamePlayersStorage.keys.toTypedArray()[0]
            game.lastInteractedEntity = interactionEntity
        }

        val gameGoalEntityEvent = GameGoalEventEntity(game, interactionEntity, team)
        eventService.sendEvent(gameGoalEntityEvent)

        if (gameGoalEntityEvent.isCancelled) {
            return
        }

        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers(game)
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        val scoreTeamMeta = if (game.ingamePlayersStorage.containsKey(interactionEntity)) {
            val scorerGameStory = game.ingamePlayersStorage[interactionEntity]!!

            if (scorerGameStory.team == Team.RED) {
                game.arena.meta.redTeamMeta
            } else {
                game.arena.meta.blueTeamMeta
            }
        } else {
            null
        }

        players.forEach { p ->
            screenMessageService.setTitle(
                p,
                placeholderService.replacePlaceHolders(scoreMessageTitle, game, scoreTeamMeta),
                placeholderService.replacePlaceHolders(scoreMessageSubTitle, game, scoreTeamMeta)
            )
        }
    }


    private fun onScoreReward(game: Game, players: List<Any>) {
        if (game.lastInteractedEntity != null && proxyService.isPlayerInstance(game.lastInteractedEntity)) {
            if (players.contains(game.lastInteractedEntity!!)) {
                if (dependencyService.isInstalled(PluginDependency.VAULT) && game.arena.meta.rewardMeta.moneyReward.containsKey(
                        RewardType.SHOOT_GOAL
                    )
                ) {
                    val vaultService = BlockBallApi.resolve(DependencyVaultService::class.java)
                    vaultService.addMoney(
                        game.lastInteractedEntity,
                        game.arena.meta.rewardMeta.moneyReward[RewardType.SHOOT_GOAL]!!.toDouble()
                    )
                }
                if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.SHOOT_GOAL)) {
                    this.executeCommand(
                        game,
                        game.arena.meta.rewardMeta.commandReward[RewardType.SHOOT_GOAL]!!,
                        arrayListOf(game.lastInteractedEntity!!)
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
                    vaultService.addMoney(
                        p,
                        game.arena.meta.rewardMeta.moneyReward[RewardType.LOOSING_MATCH]!!.toDouble()
                    )
                }
            }
            if (game.arena.meta.rewardMeta.moneyReward.containsKey(RewardType.PARTICIPATE_MATCH)) {
                game.inTeamPlayers.forEach { p ->
                    vaultService.addMoney(
                        p,
                        game.arena.meta.rewardMeta.moneyReward[RewardType.PARTICIPATE_MATCH]!!.toDouble()
                    )
                }
            }
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.WIN_MATCH) && winningPlayers != null) {
            this.executeCommand(
                game,
                game.arena.meta.rewardMeta.commandReward[RewardType.WIN_MATCH]!!,
                winningPlayers as List<Any>
            )
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.LOOSING_MATCH) && loosingPlayers != null) {
            this.executeCommand(
                game,
                game.arena.meta.rewardMeta.commandReward[RewardType.LOOSING_MATCH]!!,
                loosingPlayers as List<Any>
            )
        }

        if (game.arena.meta.rewardMeta.commandReward.containsKey(RewardType.PARTICIPATE_MATCH)) {
            this.executeCommand(
                game,
                game.arena.meta.rewardMeta.commandReward[RewardType.PARTICIPATE_MATCH]!!,
                game.inTeamPlayers
            )
        }
    }

    /**
     * Gets called when the given [game] gets win by the given [team].
     */
    override fun onWin(game: Game, team: Team, teamMeta: TeamMeta) {
        val event = GameEndEventEntity(game, team)
        eventService.sendEvent(event)

        if (event.isCancelled) {
            return
        }

        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle

        val players = ArrayList(game.inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers(game)
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first })

        players.forEach { p ->
            screenMessageService.setTitle(
                p,
                placeholderService.replacePlaceHolders(winMessageTitle, game),
                placeholderService.replacePlaceHolders(winMessageSubTitle, game)
            )
        }

        game.closing = true
    }

    /**
     * Get nofified players.
     */
    private fun getNofifiedPlayers(game: Game): List<Pair<Any, Boolean>> {
        val players = ArrayList<Pair<Any, Boolean>>()

        if (game.arena.meta.spectatorMeta.notifyNearbyPlayers) {
            for (player in proxyService.getPlayersInWorld<Any, Any>(proxyService.toLocation(game.arena.center))) {
                val playerPosition = proxyService.toPosition(proxyService.getEntityLocation<Any, Any>(player))

                if (playerPosition.distance(game.arena.center) <= game.arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(player, true))
                } else {
                    players.add(Pair(player, false))
                }
            }
        }

        return players
    }

    /**
     * Executes a single command.
     */
    private fun executeCommand(game: Game, meta: CommandMeta, players: List<Any>) {
        var command = meta.command
        if (command!!.startsWith("/")) {
            command = command.substring(1, command.length)
        }
        if (command.equals("none", true)) {
            return
        }
        when {
            meta.mode == CommandMode.PER_PLAYER -> players.forEach { p ->
                proxyService.performPlayerCommand(p, placeholderService.replacePlaceHolders(command, game))
            }
            meta.mode == CommandMode.CONSOLE_PER_PLAYER -> players.forEach { p ->
                game.lastInteractedEntity = p
                proxyService.performServerCommand(placeholderService.replacePlaceHolders(command, game))
            }
            meta.mode == CommandMode.CONSOLE_SINGLE -> proxyService.performServerCommand(
                placeholderService.replacePlaceHolders(
                    command,
                    game
                )
            )
        }
    }
}
