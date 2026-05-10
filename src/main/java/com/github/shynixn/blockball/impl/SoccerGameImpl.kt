package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.entity.cloud.CloudGame
import com.github.shynixn.blockball.entity.cloud.CloudPlayer
import com.github.shynixn.blockball.enumeration.GameState
import com.github.shynixn.blockball.enumeration.GameSubState
import com.github.shynixn.blockball.enumeration.JoinResult
import com.github.shynixn.blockball.enumeration.LeaveResult
import com.github.shynixn.blockball.enumeration.Team
import com.github.shynixn.blockball.event.GameEndEvent
import com.github.shynixn.blockball.event.GameGoalEvent
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.LanguageType
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.common.Vector3d
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.meta.enumeration.BlockDirection
import com.github.shynixn.shyguild.entity.Guild
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level


abstract class SoccerGameImpl(
    /**
     * Gets the soccerArena.
     */
    override val arena: SoccerArena,
    val placeHolderService: PlaceHolderService,
    private val plugin: Plugin,
    val soccerBallFactory: SoccerBallFactory,
    private val commandService: CommandService,
    override val language: BlockBallLanguage,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val itemService: ItemService,
    val chatMessageService: ChatMessageService,
    private val cloudService: CloudService,
    private val coroutineHandler: CoroutineHandler,
    private val server: Server,
    val forceFieldService: ForceFieldService
) : SoccerGame {
    protected var startDateUtc = Instant.now()

    /**
     * If set, then the game is already disposed and must not be used.
     */
    override var isDisposed: Boolean = false

    /**
     * The substate of the game.
     * BlockBall uses a state machine to determine the next action for modern actions.
     * Old actions are still being used but being refactored over time.
     */
    override var subState: GameSubState = GameSubState.FREE

    /**
     * When this substate has ended.
     */
    override var subStateEndTimeStamp: Long = Long.MAX_VALUE

    /**
     * The next substate in the game.
     */
    override var subStateNext: GameSubState = GameSubState.FREE

    /**
     * If the next substate requires a location then this is the parameter.
     */
    override var subStateLocationParam: Location? = null

    /**
     * If the next substate requires a player then this is the parameter.
     */
    override var subStatePlayerParam: Player? = null

    /**
     * Generated game id.
     */
    override val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

    /**
     * Red club in club mode.
     */
    override var redClub: Guild? = null

    /**
     * Blue club in club mode.
     */
    override var blueClub: Guild? = null

    /**
     * Is the ball currently enabled to spawn?
     */
    var ballEnabled: Boolean = true

    /**
     * Storage.
     */
    override val ingamePlayersStorage: MutableMap<Player, GameStorage> = ConcurrentHashMap()

    /**
     * RedScore.
     */
    override var redScore: Int = 0

    /**
     * Blue Score.
     */
    override var blueScore: Int = 0

    /**
     * Status.
     */
    override var status: GameState = GameState.JOINABLE

    /**
     * SoccerBall.
     */
    override var ball: SoccerBall? = null

    /**
     * Contains players which are in cooldown by doublejump.
     */
    override val doubleJumpCoolDownPlayers: MutableMap<Player, Int> = ConcurrentHashMap()

    /**
     * Are currently players actively playing in this game?
     */
    var playing: Boolean = false

    /**
     * SoccerBall bumper counter
     */
    override var ballBumperCounter: Int = 0

    /**
     * Contains the players which have touched the ball from the same team.
     * Once another team touches the ball this stack is cleared.
     */
    override var interactedWithBall: MutableList<Player> = ArrayList()
    override var mirroredGoals: Boolean = false

    /**
     * The temporary force field placed around the throw-in / corner kick / goal kick position.
     * Cleared after the designated player gains ball control.
     */
    private var playerForceField: ForceField? = null

    /**
     * Creates and registers a 2x2 force field centered on [location] that knocks back
     * all players except [designatedPlayer].
     */
    private fun createPlayerForceField(location: Location, designatedPlayer: Player) {
        removePlayerForceField()
        val center = location.toVector3d()
        val ff = ForceField(center, arena.ballOutOfBounds.actionForceFieldSize)
        ff.on2dInside = { player ->
            if (player != designatedPlayer) {
                forceFieldService.knockBackOutside(ff, player)
            }
        }
        playerForceField = ff
        forceFieldService.addForceField(ff)
    }

    /**
     * Removes and unregisters the current [playerForceField] if present.
     */
    fun removePlayerForceField() {
        val ff = playerForceField ?: return
        forceFieldService.removeForceField(ff)
        playerForceField = null
    }

    override fun areClubsPlaying(): Boolean {
        return redClub != null || blueClub != null
    }

    /**
     * All players which are already fix in team red.
     */
    override val redTeam: Set<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.RED }.keys.toHashSet()
        }

    /**
     * All players which are already fix in team blue.
     */
    override val blueTeam: Set<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.BLUE }.keys.toHashSet()
        }

    /**
     * All players which are referees.
     */
    override val refereeTeam: Set<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.REFEREE }.keys.toHashSet()
        }

    /**
     * List of players which are already in the teams.
     */
    private val inTeamPlayers: List<Player>
        get() {
            val players = ArrayList(redTeam)
            players.addAll(blueTeam)
            players.addAll(refereeTeam)
            return players
        }

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of soccerArena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun join(player: Player, team: Team?): JoinResult {
        val event = GameJoinEvent(player, this)
        server.pluginManager.callEvent(event)

        if (event.isCancelled()) {
            return JoinResult.EVENT_CANCELLED
        }

        var joiningTeam = team

        if (arena.meta.lobbyMeta.onlyAllowEventTeams) {
            if (joiningTeam == Team.RED && redTeam.size > blueTeam.size) {
                joiningTeam = null
            } else if (joiningTeam == Team.BLUE && blueTeam.size > redTeam.size) {
                joiningTeam = null
            }
        }

        if (joiningTeam == null) {
            joiningTeam = Team.BLUE
            if (redTeam.size < blueTeam.size) {
                joiningTeam = Team.RED
            }
        }

        val result = if (joiningTeam == Team.RED && redTeam.size < arena.meta.redTeamMeta.maxAmount) {
            setPlayerToArena(player, joiningTeam)
            storeTemporaryPlayerData(player, joiningTeam)
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.redTeamMeta.joinCommands)
            JoinResult.SUCCESS_RED
        } else if (joiningTeam == Team.BLUE && blueTeam.size < arena.meta.blueTeamMeta.maxAmount) {
            setPlayerToArena(player, joiningTeam)
            storeTemporaryPlayerData(player, joiningTeam)
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.blueTeamMeta.joinCommands)
            JoinResult.SUCCESS_BLUE
        } else if (joiningTeam == Team.REFEREE && refereeTeam.size < arena.meta.refereeTeamMeta.maxAmount) {
            setPlayerToArena(player, joiningTeam)
            storeTemporaryPlayerData(player, joiningTeam)
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.refereeTeamMeta.joinCommands)
            JoinResult.SUCCESS_REFEREE
        } else {
            JoinResult.TEAM_FULL
        }

        if (result == JoinResult.TEAM_FULL) {
            return result
        }

        if (joiningTeam == Team.REFEREE) {
            // Do not track stats as referee.
            return result
        }

        coroutineHandler.execute {
            val playerData = playerDataRepository.getByPlayer(player)
            if (playerData != null) {
                playerData.cachedStorage = ingamePlayersStorage[player]
                playerData.statsMeta.joinedGames++
            }
        }

        return result
    }

    /**
     * Leaves the given player.
     */
    override fun leave(player: Player): LeaveResult {
        if (!ingamePlayersStorage.containsKey(player)) {
            return LeaveResult.NOT_IN_MATCH
        }

        val event = GameLeaveEvent(player, this)
        server.pluginManager.callEvent(event)

        if (event.isCancelled()) {
            return LeaveResult.EVENT_CANCELLED
        }

        val playerData = ingamePlayersStorage[player]!!
        restoreFromTemporaryPlayerData(player)

        if (playerData.team == Team.RED) {
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.redTeamMeta.leaveCommands)
        } else if (playerData.team == Team.BLUE) {
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.blueTeamMeta.leaveCommands)
        } else if (playerData.team == Team.REFEREE) {
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.refereeTeamMeta.leaveCommands)
        }

        ingamePlayersStorage.remove(player)

        coroutineHandler.execute {
            val playerData = playerDataRepository.getByPlayer(player)

            if (playerData != null) {
                coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
                    if (player.isOnline) {
                        playerData.cachedStorage = null
                    }
                }
            }
        }

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            val teleportTarget = arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation()

            coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
                player.teleportCompat(plugin, teleportTarget)
            }
        }

        return LeaveResult.SUCCESS
    }

    /**
     * Tick handle.
     */
    fun handleMiniGameEssentials(hasSecondPassed: Boolean) {
        if (hasSecondPassed) {
            if (isDisposed) {
                return
            }

            this.updateDoubleJumpCooldown()
        }
    }


    fun onMatchEnd(team: Team? = null) {
        val winningPlayers = HashSet<Player>()
        var drawCounter = 0

        when (team) {
            Team.BLUE -> {
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.looseCommands)
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.winCommands)
                winningPlayers.addAll(blueTeam)
            }

            Team.RED -> {
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.winCommands)
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.looseCommands)
                winningPlayers.addAll(redTeam)
            }

            else -> {
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.drawCommands)
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.drawCommands)
                drawCounter++
            }
        }

        // Store playing stats.
        val participatingPlayers =
            ingamePlayersStorage.filter { e -> e.value.team != Team.REFEREE }.map { e -> Pair(e.key, e.value) }
        val allPlayers = ingamePlayersStorage.keys.toTypedArray()

        coroutineHandler.execute {
            val cloudGame = CloudGame()
            cloudGame.courtName = arena.meta.cloudMeta.name
            cloudGame.startDate = DateTimeFormatter.ISO_INSTANT.format(startDateUtc.truncatedTo(ChronoUnit.MILLIS))
            cloudGame.endDate = DateTimeFormatter.ISO_INSTANT.format(Instant.now().truncatedTo(ChronoUnit.MILLIS))
            cloudGame.teamRed.name = arena.meta.cloudMeta.redTeamName
            cloudGame.teamRed.score = redScore
            cloudGame.teamBlue.name = arena.meta.cloudMeta.blueTeamName
            cloudGame.teamBlue.score = blueScore

            for (playerPair in participatingPlayers) {
                val player = playerPair.first
                val data = playerPair.second
                val playerData = playerDataRepository.getByPlayer(player) ?: continue

                playerData.statsMeta.playedGames++
                playerData.statsMeta.scoredGoalsFull += data.scoredGoals
                playerData.statsMeta.scoredOwnGoalsFull += data.scoredOwnGoals
                playerData.playerName = player.name
                playerData.statsMeta.drawsAmount += drawCounter

                if (winningPlayers.contains(player)) {
                    playerData.statsMeta.winsAmount++
                }

                val cloudPlayer = CloudPlayer().also {
                    it.id = player.uniqueId.toString()
                    it.name = player.name
                    it.goalsPerGameRate =
                        playerData.statsMeta.scoredGoals.toDouble() / playerData.statsMeta.playedGames.toDouble()
                    it.goalsScoredAmount = playerData.statsMeta.scoredGoalsFull
                    it.gamesAmount = playerData.statsMeta.playedGames
                    it.winsAmount = playerData.statsMeta.winsAmount
                    it.drawsAmount = playerData.statsMeta.drawsAmount
                    it.lossesAmount =
                        playerData.statsMeta.playedGames - playerData.statsMeta.winsAmount - playerData.statsMeta.drawsAmount
                    it.winRate =
                        playerData.statsMeta.winsAmount.toDouble() / playerData.statsMeta.playedGames.toDouble()
                }

                if (data.team == Team.BLUE) {
                    cloudGame.teamBlue.players.add(cloudPlayer)
                } else if (data.team == Team.RED) {
                    cloudGame.teamRed.players.add(cloudPlayer)
                }
            }

            if (arena.meta.cloudMeta.enabled) {
                try {
                    val gameUrl = cloudService.publishGameStats(cloudGame)
                    for (player in allPlayers) {
                        if (player.isOnline && language.cloudPublishGameMessage.type != LanguageType.HIDDEN) {
                            chatMessageService.sendLanguageMessage(player, language.cloudPublishGameMessage)
                            player.sendMessage(ChatColor.GRAY.toString() + gameUrl)
                        }
                    }
                } catch (e: Exception) {
                    plugin.logger.log(Level.WARNING, "Cannot publish game to BlockBall Hub.", e)
                }
            }
        }
    }

    /**
     * Gets called when the given [game] gets win by the given [team].
     */
    fun onWin(team: Team) {
        val event = GameEndEvent(team, this)
        server.pluginManager.callEvent(event)

        if (event.isCancelled()) {
            return
        }

        val players = HashSet(inTeamPlayers)

        for (player in players) {
            if (team == Team.RED) {
                chatMessageService.sendLanguageMessage(player, language.winRed)
            } else if (team == Team.BLUE) {
                chatMessageService.sendLanguageMessage(player, language.winBlue)
            }
        }

        setNextGameSubState(GameSubState.CLOSED, 1000 * 3L)
    }

    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    override fun respawn(player: Player, team: Team?) {
        val actualTeam = if (ingamePlayersStorage.containsKey(player)) {
            ingamePlayersStorage[player]!!.goalTeam
        } else {
            team
        }

        if (actualTeam == null) {
            return
        }

        val teamMeta = if (actualTeam == Team.RED) {
            arena.meta.redTeamMeta
        } else if (actualTeam == Team.BLUE) {
            arena.meta.blueTeamMeta
        } else if (actualTeam == Team.REFEREE) {
            arena.meta.refereeTeamMeta
        } else {
            return
        }

        val spawnPoint = if (teamMeta.spawnpoint == null) {
            arena.ballSpawnPoint!!.toLocation()
        } else {
            teamMeta.spawnpoint!!.toLocation()
        }

        coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
            player.teleportCompat(plugin, spawnPoint)
        }
    }

    /**
     * Performs all state Machine actions.
     */
    override fun runStateMachine() {
        if (subStateNext == GameSubState.FREE) {
            // The next state of the state machine is not interesting skip.
            return
        }

        val currentMilliseconds = System.currentTimeMillis()

        if (currentMilliseconds < subStateEndTimeStamp) {
            // The current state is still active skip.
            return
        }

        val oldState = subState
        subState = subStateNext
        onStateMachineSubStateChange(oldState, subStateNext)
    }

    /**
     * Is called when the substate of the game changes. This is used to perform actions when the substate changes.
     */
    override fun onStateMachineSubStateChange(
        oldSubState: GameSubState, newSubState: GameSubState
    ) {
        when (newSubState) {
            GameSubState.CLOSED -> {
                close()
            }

            GameSubState.BALL_RESPAWNED -> {
                if (ballEnabled) {
                    destroyBall()
                    ball = soccerBallFactory.createSoccerBallForGame(
                        arena.ballSpawnPoint!!.toLocation(), arena.ball, this
                    )
                    setNextGameSubState(GameSubState.FREE)
                }
            }

            GameSubState.BALL_DESTROYED -> {
                destroyBall()
                setNextGameSubState(GameSubState.FREE)
            }

            GameSubState.BALL_OUT_TELEPORT -> {
                val lastInteractedWithBallPlayer = interactedWithBall.getOrNull(0)

                if (lastInteractedWithBallPlayer == null || ball == null) {
                    destroyBall()
                    setNextGameSubState(
                        GameSubState.BALL_RESPAWNED, arena.meta.customizingMeta.gameStartBallSpawnDelayTicks * 50L
                    )
                    return
                }

                val ballLocation = subStateLocationParam!!.toVector3d()
                ball!!.isInteractable = false
                val exitDirection = arena.getRelativeBlockDirectionToLocation(ballLocation)

                val teamSide = if (arena.meta.redTeamMeta.outArea.isLocationIn2dSelection(ballLocation)) {
                    if (mirroredGoals) {
                        Team.BLUE
                    } else {
                        Team.RED
                    }
                } else if (arena.meta.blueTeamMeta.outArea.isLocationIn2dSelection(ballLocation)) {
                    if (mirroredGoals) {
                        Team.RED
                    } else {
                        Team.BLUE
                    }
                } else {
                    null
                }

                val throwInPlayer = findNearestOpposingPlayer(lastInteractedWithBallPlayer)

                for (player in getPlayers()) {
                    if (player != throwInPlayer) {
                        chatMessageService.sendLanguageMessage(player, language.outMessage)
                    }
                }

                val delaySeconds = arena.ballOutOfBounds.timeToTeleportSec

                if (teamSide == null) {
                    // Throw-in: project the ball's exit position onto the nearest inner-field boundary.
                    val throwInLocation = findThrowInLocation(ballLocation, exitDirection)
                    subStateLocationParam = throwInLocation
                    subStatePlayerParam = throwInPlayer
                    plugin.launch {
                        for (i in delaySeconds downTo 1) {
                            chatMessageService.sendLanguageMessage(
                                subStatePlayerParam!!,
                                language.throwInTeleportMessage,
                                i.toString()
                            )
                            delay(1000)
                        }
                    }
                    setNextGameSubState(GameSubState.BALL_OUT_THROW_IN_PERFORM, delaySeconds * 1000L)
                } else {
                    // Goal-line exit: corner kick or goal kick.
                    val lastTouchedByDefender = (teamSide == Team.RED && redTeam.contains(lastInteractedWithBallPlayer))
                            || (teamSide == Team.BLUE && blueTeam.contains(lastInteractedWithBallPlayer))

                    if (lastTouchedByDefender) {
                        // Corner kick: defender last touched → attacking team kicks from the nearest corner.
                        val attackingTeam = if (teamSide == Team.RED) Team.BLUE else Team.RED
                        val cornerLocation = findCornerKickLocation(ballLocation, exitDirection)
                        val cornerPlayer =
                            findNearestPlayerInTeam(ballLocation, attackingTeam) ?: lastInteractedWithBallPlayer
                        subStateLocationParam = cornerLocation
                        subStatePlayerParam = cornerPlayer
                        plugin.launch {
                            for (i in delaySeconds downTo 1) {
                                chatMessageService.sendLanguageMessage(
                                    subStatePlayerParam!!,
                                    language.cornerKickTeleportMessage,
                                    i.toString()
                                )
                                delay(1000)
                            }
                        }
                        setNextGameSubState(GameSubState.BALL_OUT_CORNER_KICK_PERFORM, delaySeconds * 1000L)
                    } else {
                        // Goal kick: attacker last touched → defending team kicks from their goal area.
                        // When goals are mirrored the defending team physically stands at the opposite goal,
                        // so we read the keeper spawnpoint from the mirrored team's meta.
                        val goalKickTeamMeta = if (mirroredGoals) {
                            if (teamSide == Team.RED) arena.meta.blueTeamMeta else arena.meta.redTeamMeta
                        } else {
                            if (teamSide == Team.RED) arena.meta.redTeamMeta else arena.meta.blueTeamMeta
                        }
                        val goalKickLocation =
                            (goalKickTeamMeta.keeperSpawnpoint ?: arena.ballSpawnPoint!!).toLocation()
                        val goalKickPlayer =
                            findNearestPlayerInTeam(ballLocation, teamSide) ?: lastInteractedWithBallPlayer
                        subStateLocationParam = goalKickLocation
                        subStatePlayerParam = goalKickPlayer
                        plugin.launch {
                            for (i in delaySeconds downTo 1) {
                                chatMessageService.sendLanguageMessage(
                                    subStatePlayerParam!!,
                                    language.goalKickTeleportMessage,
                                    i.toString()
                                )
                                delay(1000)
                            }
                        }
                        setNextGameSubState(GameSubState.BALL_OUT_GOAL_KICK_PERFORM, delaySeconds * 1000L)
                    }
                }

                return
            }

            GameSubState.BALL_OUT_THROW_IN_PERFORM -> {
                val throwInLocation = subStateLocationParam!!
                val throwInPlayer = subStatePlayerParam!!
                coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(throwInPlayer)) {
                    throwInPlayer.teleportCompat(plugin, throwInLocation)
                }
                destroyBall()
                val ballSpawnPoint = throwInLocation.toVector3d().addRelativeFront(1.0).toLocation()
                ball = soccerBallFactory.createSoccerBallForGame(ballSpawnPoint, arena.ball, this)
                ball!!.isInteractable = false
                createPlayerForceField(throwInLocation, throwInPlayer)
                val timeoutDelay = arena.ballOutOfBounds.timeOutStartSec * 1000L
                coroutineHandler.execute {
                    for (i in arena.ballOutOfBounds.timeToStartSec downTo 1) {
                        for (player in getPlayers()) {
                            chatMessageService.sendLanguageMessage(
                                player,
                                language.throwInReadyMessage,
                                i.toString()
                            )
                        }
                        delay(1000)
                    }
                    if (getPlayers().contains(throwInPlayer)) {
                        chatMessageService.sendLanguageMessage(
                            throwInPlayer,
                            language.throwInPerformMessage,
                        )
                    }
                    ball?.lockedPlayer = throwInPlayer // Important, the ball might be null here
                    ball?.isInteractable = true
                    delay(timeoutDelay)
                    if (ball?.lockedPlayer != null) {
                        removePlayerForceField()
                        ball?.lockedPlayer = null
                    }
                }
                setNextGameSubState(GameSubState.FREE)
                return
            }

            GameSubState.BALL_OUT_CORNER_KICK_PERFORM -> {
                val cornerLocation = subStateLocationParam!!
                val cornerPlayer = subStatePlayerParam!!
                coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(cornerPlayer)) {
                    cornerPlayer.teleportCompat(plugin, cornerLocation)
                }
                destroyBall()
                val ballSpawnPoint = cornerLocation.toVector3d().addRelativeFront(1.0).toLocation()
                ball = soccerBallFactory.createSoccerBallForGame(ballSpawnPoint, arena.ball, this)
                ball!!.isInteractable = false
                createPlayerForceField(cornerLocation, cornerPlayer)
                val timeoutDelay = arena.ballOutOfBounds.timeOutStartSec * 1000L
                coroutineHandler.execute {
                    for (i in arena.ballOutOfBounds.timeToStartSec downTo 1) {
                        for (player in getPlayers()) {
                            chatMessageService.sendLanguageMessage(
                                player,
                                language.cornerKickReadyMessage,
                                i.toString()
                            )
                        }
                        delay(1000)
                    }
                    if (getPlayers().contains(cornerPlayer)) {
                        chatMessageService.sendLanguageMessage(cornerPlayer, language.cornerKickPerformMessage)
                    }
                    ball?.lockedPlayer = cornerPlayer
                    ball?.isInteractable = true
                    delay(timeoutDelay)
                    if (ball?.lockedPlayer != null) {
                        removePlayerForceField()
                        ball?.lockedPlayer = null
                    }
                }
                setNextGameSubState(GameSubState.FREE)
                return
            }

            GameSubState.BALL_OUT_GOAL_KICK_PERFORM -> {
                val goalKickLocation = subStateLocationParam!!
                val goalKickPlayer = subStatePlayerParam!!
                coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(goalKickPlayer)) {
                    goalKickPlayer.teleportCompat(plugin, goalKickLocation)
                }
                destroyBall()
                val ballSpawnPoint = goalKickLocation.toVector3d().addRelativeFront(1.0).toLocation()
                ball = soccerBallFactory.createSoccerBallForGame(ballSpawnPoint, arena.ball, this)
                ball!!.isInteractable = false
                createPlayerForceField(goalKickLocation, goalKickPlayer)
                val timeoutDelay = arena.ballOutOfBounds.timeOutStartSec * 1000L
                coroutineHandler.execute {
                    for (i in arena.ballOutOfBounds.timeToStartSec downTo 1) {
                        for (player in getPlayers()) {
                            chatMessageService.sendLanguageMessage(
                                player,
                                language.goalKickReadyMessage,
                                i.toString()
                            )
                        }
                        delay(1000)
                    }
                    if (getPlayers().contains(goalKickPlayer)) {
                        chatMessageService.sendLanguageMessage(goalKickPlayer, language.goalKickPerformMessage)
                    }
                    ball?.lockedPlayer = goalKickPlayer
                    ball?.isInteractable = true
                    delay(timeoutDelay)
                    if (ball?.lockedPlayer != null) {
                        removePlayerForceField()
                        ball?.lockedPlayer = null
                    }
                }
                setNextGameSubState(GameSubState.FREE)
                return
            }

            else -> {
                subStateNext = GameSubState.FREE
            }
        }
    }

    /**
     * Updates the cooldown of the double jump for the given game.
     */
    private fun updateDoubleJumpCooldown() {
        doubleJumpCoolDownPlayers.keys.toTypedArray().forEach { p ->
            var time = doubleJumpCoolDownPlayers[p]!!
            time -= 1

            if (time <= 0) {
                doubleJumpCoolDownPlayers.remove(p)
            } else {
                doubleJumpCoolDownPlayers[p] = time
            }
        }
    }

    /**
     * Gets called when the given [game] ends with a draw.
     */
    fun onDraw() {
        for (player in getPlayers()) {
            chatMessageService.sendLanguageMessage(player, language.winDraw)
        }
    }

    /**
     * Notifies that the ball is inside of the goal of the given team.
     * This team has to be the default goal of the team. Mirroring
     * is handled inside of the method.
     */
    override fun notifyBallInGoal(team: Team) {
        var teamOfGoal = team

        if (teamOfGoal == Team.BLUE && mirroredGoals) {
            teamOfGoal = Team.RED
        } else if (teamOfGoal == Team.RED && mirroredGoals) {
            teamOfGoal = Team.BLUE
        }

        if (teamOfGoal == Team.RED) {
            blueScore += arena.meta.blueTeamMeta.pointsPerGoal
            onScore(Team.BLUE)
            executeGoalCommands(teamOfGoal)
            relocatePlayersAndBall()

            if (blueScore >= arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(Team.BLUE)
                onWin(Team.BLUE)
            }

            return
        }

        if (teamOfGoal == Team.BLUE) {
            redScore += arena.meta.redTeamMeta.pointsPerGoal
            onScore(Team.RED)
            executeGoalCommands(teamOfGoal)
            relocatePlayersAndBall()

            if (redScore >= arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(Team.RED)
                onWin(Team.RED)
            }
        }
    }

    /**
     * Applies points to the belonging teams when the given [player] dies in the given [game].
     */
    override fun applyDeathPoints(player: Player) {
        if (!ingamePlayersStorage.containsKey(player)) {
            return
        }

        val team = ingamePlayersStorage[player]!!.team

        if (team == Team.RED) {
            blueScore += arena.meta.blueTeamMeta.pointsPerEnemyDeath
        } else if (team == Team.BLUE) {
            redScore += arena.meta.redTeamMeta.pointsPerEnemyDeath
        }
    }


    /**
     * Gets called when a goal gets scored on the given [game] by the given [team].
     */
    private fun onScore(team: Team) {
        var interactionEntity = interactedWithBall.getOrNull(0)

        if (interactionEntity == null) {
            if (ingamePlayersStorage.isEmpty()) {
                return
            }

            interactionEntity = ingamePlayersStorage.keys.toTypedArray()[0]
            interactedWithBall.add(0, interactionEntity)
        }

        val gameGoalEntityEvent = GameGoalEvent(interactionEntity as Player?, team, this)
        server.pluginManager.callEvent(gameGoalEntityEvent)

        if (gameGoalEntityEvent.isCancelled()) {
            return
        }

        val players = ArrayList(inTeamPlayers)

        if (team == Team.RED) {
            for (player in players) {
                chatMessageService.sendLanguageMessage(player, language.scoreRed, interactionEntity.name)
            }
        } else {
            for (player in players) {
                chatMessageService.sendLanguageMessage(player, language.scoreBlue, interactionEntity.name)
            }
        }

        val isOwnGoal =
            !((blueTeam.contains(interactionEntity) && team == Team.BLUE) || (redTeam.contains(interactionEntity) && team == Team.RED))
        val scorerStorage = if (ingamePlayersStorage.containsKey(interactionEntity)) {
            ingamePlayersStorage[interactionEntity]!!
        } else {
            null
        }

        coroutineHandler.execute {
            val playerData = playerDataRepository.getByPlayer(interactionEntity)

            if (playerData != null) {
                if (isOwnGoal) {
                    playerData.statsMeta.scoredOwnGoals++
                } else {
                    playerData.statsMeta.scoredGoals++
                }

                if (scorerStorage != null) {
                    if (isOwnGoal) {
                        scorerStorage.scoredOwnGoals++
                    } else {
                        scorerStorage.scoredGoals++
                    }
                }
            }
        }
    }

    /**
     * The goal where the ball has been shot in.
     */
    private fun executeGoalCommands(goalTeam: Team) {
        val lastInteractedEntity = interactedWithBall.getOrNull(0) ?: return
        val goalShooter = lastInteractedEntity

        if (redTeam.contains(goalShooter)) {
            if (goalTeam == Team.BLUE) {
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.goalCommands)
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.enemyGoalCommands)
            } else {
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.ownGoalCommands)
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.enemyOwnGoalCommands)
            }
        } else if (blueTeam.contains(goalShooter)) {
            if (goalTeam == Team.RED) {
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.goalCommands)
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.enemyGoalCommands)
            } else {
                executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.ownGoalCommands)
                executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.enemyOwnGoalCommands)
            }
        }
    }

    fun executeCommandsWithPlaceHolder(players: Set<Player>, commands: List<CommandMeta>) {
        commandService.executeCommands(players.toList(), commands) { c, p ->
            placeHolderService.resolvePlaceHolder(
                c, p
            )
        }
    }

    /**
     * Teleports all players and ball back to their spawnpoint if [game] has got back teleport enabled.
     */
    private fun relocatePlayersAndBall() {
        destroyBall()
        setNextGameSubState(GameSubState.BALL_RESPAWNED, arena.meta.customizingMeta.goalScoredBallSpawnDelayTicks * 50L)

        if (!arena.meta.customizingMeta.backTeleport) {
            return
        }

        val tickDelay = 20 * arena.meta.customizingMeta.backTeleportDelay
        coroutineHandler.execute {
            delay(tickDelay.ticks)
            var redTeamSpawnpoint = arena.meta.redTeamMeta.spawnpoint

            if (redTeamSpawnpoint == null) {
                redTeamSpawnpoint = arena.ballSpawnPoint!!
            }

            var blueTeamSpawnpoint = arena.meta.blueTeamMeta.spawnpoint

            if (blueTeamSpawnpoint == null) {
                blueTeamSpawnpoint = arena.ballSpawnPoint!!
            }

            var refereeSpawnpoint = arena.meta.refereeTeamMeta.spawnpoint

            if (refereeSpawnpoint == null) {
                refereeSpawnpoint = arena.ballSpawnPoint!!
            }

            for (i in ingamePlayersStorage) {
                val player = i.key
                val location = if (i.value.goalTeam == Team.RED) {
                    redTeamSpawnpoint.toLocation()
                } else if (i.value.goalTeam == Team.BLUE) {
                    blueTeamSpawnpoint.toLocation()
                } else if (i.value.team == Team.REFEREE) {
                    refereeSpawnpoint.toLocation()
                } else {
                    null
                }

                if (location != null) {
                    coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
                        player.teleportCompat(plugin, location)
                    }
                }
            }

            executeCommandsWithPlaceHolder(redTeam, arena.meta.redTeamMeta.backTeleportCommands)
            executeCommandsWithPlaceHolder(blueTeam, arena.meta.blueTeamMeta.backTeleportCommands)
            executeCommandsWithPlaceHolder(refereeTeam, arena.meta.refereeTeamMeta.backTeleportCommands)
        }
    }

    /**
     * Stores health, food, etc.
     * TODO: Migrate to minigame essentials.
     */
    private fun storeTemporaryPlayerData(player: Player, team: Team) {
        // Store
        val stats = GameStorage()
        ingamePlayersStorage[player] = stats
        stats.team = team
        stats.goalTeam = team

        coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
            stats.gameMode = player.gameMode
            stats.armorContents =
                player.inventory.armorContents.map { itemService.serializeItemStack(it) }.toTypedArray()
            stats.inventoryContents =
                player.inventory.contents.map { itemService.serializeItemStack(it) }.toTypedArray()
            stats.level = player.level
            stats.exp = player.exp.toDouble()
            stats.maxHealth = player.maxHealth
            stats.health = player.health
            stats.hunger = player.foodLevel

            // Apply
            val teamMeta = getTeamMetaFromTeam(team)
            player.allowFlight = false
            player.isFlying = false
            player.gameMode = arena.meta.lobbyMeta.gamemode
            player.foodLevel = 20
            player.level = 0
            player.exp = 0.0F

            if (!arena.meta.customizingMeta.keepHealthEnabled) {
                player.maxHealth = 20.0
                player.health = 20.0
            }

            if (!arena.meta.customizingMeta.keepInventoryEnabled) {
                player.setInventoryContentsSecure(teamMeta.inventory.map { e -> itemService.deserializeItemStack(e) })
                player.inventory.setArmorContents(teamMeta.armor.map { e -> itemService.deserializeItemStack(e) }
                    .toTypedArray())
                player.updateInventory()
            }
        }
    }

    private fun restoreFromTemporaryPlayerData(player: Player) {
        val stats = ingamePlayersStorage[player]!!

        coroutineHandler.execute(coroutineHandler.fetchEntityDispatcher(player)) {
            player.gameMode = stats.gameMode
            player.allowFlight = stats.gameMode == GameMode.CREATIVE || stats.gameMode == GameMode.SPECTATOR
            player.isFlying = false
            player.level = stats.level
            player.exp = stats.exp.toFloat()
            player.foodLevel = stats.hunger

            if (stats.gameMode == GameMode.SPECTATOR) {
                player.isFlying = true
            }

            if (!arena.meta.customizingMeta.keepHealthEnabled) {
                player.maxHealth = stats.maxHealth
                player.health = stats.health
            }

            if (!arena.meta.customizingMeta.keepInventoryEnabled) {
                player.setInventoryContentsSecure(
                    stats.inventoryContents.map { e -> itemService.deserializeItemStack(e) })
                player.inventory.setArmorContents(stats.armorContents.map { e -> itemService.deserializeItemStack(e) }
                    .toTypedArray())
                player.updateInventory()
            }
        }
    }

    /**
     * Projects [ballLocation] onto the boundary of the inner playing field in the given [exitDirection].
     * This gives the sideline position where a throw-in should be taken.
     * If the exit direction cannot be determined (DOWN), the ball spawn point is used as fallback.
     */
    private fun findThrowInLocation(ballLocation: Vector3d, exitDirection: BlockDirection): Location {
        val c1 = arena.corner1!!   // higher X (WEST edge) / higher Z (NORTH edge)
        val c2 = arena.corner2!!   // lower  X (EAST edge) / lower  Z (SOUTH edge)
        val world = org.bukkit.Bukkit.getWorld(arena.ballSpawnPoint!!.world!!)
        val y = arena.ballSpawnPoint!!.y

        val location = when (exitDirection) {
            BlockDirection.WEST -> Location(world, c1.x, y, ballLocation.z.coerceIn(c2.z, c1.z))
            BlockDirection.EAST -> Location(world, c2.x, y, ballLocation.z.coerceIn(c2.z, c1.z))
            BlockDirection.NORTH -> Location(world, ballLocation.x.coerceIn(c2.x, c1.x), y, c1.z)
            BlockDirection.SOUTH -> Location(world, ballLocation.x.coerceIn(c2.x, c1.x), y, c2.z)
            else -> arena.ballSpawnPoint!!.toLocation()
        }

        // Face the player toward the center of the arena.
        val center = arena.center
        val dx = center.x - location.x
        val dz = center.z - location.z
        location.yaw = Math.toDegrees(Math.atan2(-dx, dz)).toFloat()

        return location
    }

    /**
     * Returns the corner of the playing field nearest to [ballLocation] on the given [exitDirection] goal-line.
     * The player facing direction is set toward the arena center.
     */
    private fun findCornerKickLocation(ballLocation: Vector3d, exitDirection: BlockDirection): Location {
        val c1 = arena.corner1!!
        val c2 = arena.corner2!!
        val world = org.bukkit.Bukkit.getWorld(arena.ballSpawnPoint!!.world!!)
        val y = arena.ballSpawnPoint!!.y
        val centerX = (c1.x + c2.x) / 2.0
        val centerZ = (c1.z + c2.z) / 2.0

        val location = when (exitDirection) {
            BlockDirection.NORTH -> {
                val cornerX = if (ballLocation.x >= centerX) c1.x else c2.x
                Location(world, cornerX, y, c1.z)
            }

            BlockDirection.SOUTH -> {
                val cornerX = if (ballLocation.x >= centerX) c1.x else c2.x
                Location(world, cornerX, y, c2.z)
            }

            BlockDirection.WEST -> {
                val cornerZ = if (ballLocation.z >= centerZ) c1.z else c2.z
                Location(world, c1.x, y, cornerZ)
            }

            BlockDirection.EAST -> {
                val cornerZ = if (ballLocation.z >= centerZ) c1.z else c2.z
                Location(world, c2.x, y, cornerZ)
            }

            else -> arena.ballSpawnPoint!!.toLocation()
        }

        // Face the player toward the center of the arena.
        val center = arena.center
        val dx = center.x - location.x
        val dz = center.z - location.z
        location.yaw = Math.toDegrees(Math.atan2(-dx, dz)).toFloat()

        return location
    }

    /**
     * Finds the nearest player of the team opposing the given [player].
     * Used for throw-ins: the team that did not last touch the ball performs it.
     */
    private fun findNearestOpposingPlayer(player: Player): Player {
        val opposingTeam = if (redTeam.contains(player)) blueTeam else redTeam
        val ballPos: Vector3d? = ball?.getLocation()?.toVector3d() as Vector3d?
        return opposingTeam.minByOrNull { p ->
            if (ballPos != null) p.location.toVector3d().distance(ballPos) else 0.0
        } ?: player
    }

    /**
     * Finds the nearest player in the given [team] to the given [position].
     * Used for goal kicks: the defending team's nearest player performs it.
     */
    private fun findNearestPlayerInTeam(position: Vector3d, team: Team): Player? {
        val players = if (team == Team.RED) redTeam else blueTeam
        return players.minByOrNull { p -> p.location.toVector3d().distance(position) }
    }

    abstract fun setPlayerToArena(player: Player, team: Team)

    fun getTeamMetaFromTeam(team: Team): TeamMeta {
        if (team == Team.RED) {
            return arena.meta.redTeamMeta
        } else if (team == Team.BLUE) {
            return arena.meta.blueTeamMeta
        }

        return arena.meta.refereeTeamMeta
    }

    /**
     * Gets all players.
     */
    override fun getPlayers(): Set<Player> {
        val players = HashSet<Player>()
        players.addAll(redTeam)
        players.addAll(blueTeam)
        players.addAll(refereeTeam)
        return players
    }


    /**
     * Respawns the ball and sets it to the given location.
     * This is only relevant for the referee.
     */
    override fun setBallToLocation(location: Location) {
        destroyBall()
        ball = soccerBallFactory.createSoccerBallForGame(
            location, arena.ball, this
        )
        ball!!.isInteractable = false // We always block interacting with the ball until the referee has started.
        setNextGameSubState(GameSubState.FREE)
    }

    fun destroyBall() {
        ball?.remove()
        ball = null
    }

    /**
     * Sets the next substate of the game. The substate is used to determine the next action of the game.
     */
    override fun setNextGameSubState(
        subState: GameSubState, timeFromNowMilliSeconds: Long
    ) {
        subStateEndTimeStamp = System.currentTimeMillis() + timeFromNowMilliSeconds
        subStateNext = subState
    }
}
