package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.event.GameEndEvent
import com.github.shynixn.blockball.event.GameGoalEvent
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.sendPluginMessage
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

abstract class SoccerGameImpl(
    /**
     * Gets the soccerArena.
     */
    override val arena: SoccerArena,
    private val placeHolderService: PlaceHolderService,
    private val packetService: PacketService,
    private val plugin: Plugin,
    private val soccerBallFactory: SoccerBallFactory,
    private val commandService: CommandService,
    override val language: BlockBallLanguage,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val itemService: ItemService
) : SoccerGame {

    /**
     * Generated game id.
     */
    override val id: String = UUID.randomUUID().toString().replace("-", "").substring(0, 8)

    /**
     * Is the ball spawning?
     */
    private var ballSpawning: Boolean = false

    /**
     * SoccerBall spawn counter.
     */
    private var ballSpawnCounter: Int = 0

    /**
     * Is the ball currently enabled to spawn?
     */
    var ballEnabled: Boolean = true

    /**
     * Storage.
     */
    override val ingamePlayersStorage: MutableMap<Player, GameStorage> = HashMap()

    /**
     * Player who was the last one to hit the ball.
     */
    override var lastHitPlayer: Player? = null

    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    override var closing: Boolean = false

    override var closed: Boolean = false

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
    override val doubleJumpCoolDownPlayers: MutableMap<Player, Int> = HashMap()

    /**
     * Are currently players actively playing in this game?
     */
    var playing: Boolean = false

    /**
     * SoccerBall bumper counter
     */
    override var ballBumperCounter: Int = 0

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
    override var lastInteractedEntity: Player? = null

    var mirroredGoals: Boolean = false

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
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
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
            storeTemporaryPlayerData(player, joiningTeam)
            setPlayerToArena(player, joiningTeam)
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.redTeamMeta.joinCommands)
            JoinResult.SUCCESS_RED
        } else if (joiningTeam == Team.BLUE && blueTeam.size < arena.meta.blueTeamMeta.maxAmount) {
            storeTemporaryPlayerData(player, joiningTeam)
            setPlayerToArena(player, joiningTeam)
            executeCommandsWithPlaceHolder(setOf(player), arena.meta.blueTeamMeta.joinCommands)
            JoinResult.SUCCESS_BLUE
        } else if (joiningTeam == Team.REFEREE && refereeTeam.size < arena.meta.refereeTeamMeta.maxAmount) {
            storeTemporaryPlayerData(player, joiningTeam)
            setPlayerToArena(player, joiningTeam)
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

        plugin.launch {
            val playerData = playerDataRepository.getByPlayer(player)
            if (playerData != null) {
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
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
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

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
        }

        return LeaveResult.SUCCESS
    }

    /**
     * Tick handle.
     */
    fun handleMiniGameEssentials(ticks: Int) {
        if (ticks >= 20) {
            if (closing) {
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

        plugin.launch {
            for (playerPair in participatingPlayers) {
                val player = playerPair.first
                val data = playerPair.second
                val playerData = playerDataRepository.getByPlayer(player)

                if (playerData != null) {
                    playerData.statsMeta.playedGames++
                    playerData.statsMeta.scoredGoalsFull += data.scoredGoals
                    playerData.statsMeta.scoredOwnGoalsFull += data.scoredOwnGoals
                    playerData.playerName = player.name
                    playerData.statsMeta.drawsAmount += drawCounter
                    val lastGameIds = ArrayList(playerData.statsMeta.lastGames)
                    lastGameIds.add(0, StatsGame().also {
                        it.id = id
                        it.name = arena.name
                        it.displayName = ChatColor.stripChatColors(arena.displayName)
                    })
                    playerData.statsMeta.lastGames = lastGameIds.take(6).toList()
                    if (winningPlayers.contains(player)) {
                        playerData.statsMeta.winsAmount++
                    }
                }
            }
        }
    }

    /**
     * Gets called when the given [game] gets win by the given [team].
     */
    fun onWin(team: Team) {
        val event = GameEndEvent(team, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        val players = HashSet(inTeamPlayers)

        for (player in players) {
            if (team == Team.RED) {
                player.sendPluginMessage(language.winRed)
            } else if (team == Team.BLUE) {
                player.sendPluginMessage(language.winBlue)
            }
        }

        setGameClosing()
    }

    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    override fun respawn(player: Player) {
        if (!ingamePlayersStorage.containsKey(player)) {
            return
        }

        val team = ingamePlayersStorage[player]!!.goalTeam

        val teamMeta = if (team == Team.RED) {
            arena.meta.redTeamMeta
        } else if (team == Team.BLUE) {
            arena.meta.blueTeamMeta
        } else if (team == Team.REFEREE) {
            arena.meta.refereeTeamMeta
        } else {
            return
        }

        if (teamMeta.spawnpoint == null) {
            player.teleport(arena.ballSpawnPoint!!.toLocation())
        } else {
            player.teleport(teamMeta.spawnpoint!!.toLocation())
        }
    }

    protected fun handleBallSpawning() {
        if (ballSpawning && ballEnabled) {
            ballSpawnCounter--
            if (ballSpawnCounter <= 0) {
                destroyBall()
                ball = soccerBallFactory.createSoccerBallForGame(
                    arena.ballSpawnPoint!!.toLocation(), arena.ball, this
                )
                ballSpawning = false
                ballSpawnCounter = 0

                // Dirty Hack, maybe use a different method?
                if (this is SoccerRefereeGame) {
                    ball?.isInteractable = false
                }
            }

            return
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
            player.sendPluginMessage(language.winDraw)
        }
    }

    /**
     * Notifies that the ball is inside of the goal of the given team.
     * This team has to be the default goal of the team. Mirroring
     * is handled inside of the method.
     */
    override fun notifyBallInGoal(team: Team) {
        if (ballSpawning) {
            return
        }

        var teamOfGoal = team

        if (teamOfGoal == Team.BLUE && mirroredGoals) {
            teamOfGoal = Team.RED
        } else if (teamOfGoal == Team.RED && mirroredGoals) {
            teamOfGoal = Team.BLUE
        }

        if (teamOfGoal == Team.RED) {
            blueScore += arena.meta.blueTeamMeta.pointsPerGoal
            onScore(Team.BLUE)
            onScoreReward(Team.BLUE, blueTeam)
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
            onScoreReward(Team.RED, redTeam)
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
        var interactionEntity: Player? = null

        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            interactionEntity = lastInteractedEntity!!
        }

        if (interactionEntity == null) {
            if (ingamePlayersStorage.isEmpty()) {
                return
            }

            interactionEntity = ingamePlayersStorage.keys.toTypedArray()[0]
            lastInteractedEntity = interactionEntity
        }

        val gameGoalEntityEvent = GameGoalEvent(interactionEntity as Player?, team, this)
        Bukkit.getPluginManager().callEvent(gameGoalEntityEvent)

        if (gameGoalEntityEvent.isCancelled) {
            return
        }

        val players = ArrayList(inTeamPlayers)

        if (team == Team.RED) {
            for (player in players) {
                player.sendPluginMessage(language.scoreRed, interactionEntity.name)
            }
        } else {
            for (player in players) {
                player.sendPluginMessage(language.scoreBlue, interactionEntity.name)
            }
        }

        val isOwnGoal =
            !((blueTeam.contains(interactionEntity) && team == Team.BLUE) || (redTeam.contains(interactionEntity) && team == Team.RED))
        val scorerStorage = if (ingamePlayersStorage.containsKey(interactionEntity)) {
            ingamePlayersStorage[interactionEntity]!!
        } else {
            null
        }

        plugin.launch {
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

    private fun onScoreReward(team: Team, players: Set<Player>) {
        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            if (players.contains(lastInteractedEntity!!)) {
                val teamMeta = getTeamMetaFromTeam(team)
                executeCommandsWithPlaceHolder(setOf(lastInteractedEntity!!), teamMeta.goalCommands)
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
        respawnBall(arena.meta.customizingMeta.subsequentBallSpawnDelayTicks)

        if (!arena.meta.customizingMeta.backTeleport) {
            return
        }

        val tickDelay = 20 * arena.meta.customizingMeta.backTeleportDelay
        plugin.launch {
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

            ingamePlayersStorage.forEach { i ->
                if (i.value.goalTeam == Team.RED) {
                    i.key.teleport(redTeamSpawnpoint.toLocation())
                } else if (i.value.goalTeam == Team.BLUE) {
                    i.key.teleport(blueTeamSpawnpoint.toLocation())
                } else if (i.value.team == Team.REFEREE) {
                    i.key.teleport(refereeSpawnpoint.toLocation())
                }
            }
        }
    }

    /**
     * Stores health, food, etc.
     * TODO: Migrate to minigame essentials.
     */
    protected fun storeTemporaryPlayerData(player: Player, team: Team) {
        // Store
        val stats = GameStorage()
        ingamePlayersStorage[player] = stats
        stats.team = team
        stats.goalTeam = team
        stats.gameMode = player.gameMode
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
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
            player.inventory.contents =
                teamMeta.inventory.map { e -> itemService.deserializeItemStack(e) }.toTypedArray()
            player.inventory.setArmorContents(teamMeta.armor.map { e -> itemService.deserializeItemStack(e) }
                .toTypedArray())
            player.updateInventory()
        }
    }

    private fun restoreFromTemporaryPlayerData(player: Player) {
        val stats = ingamePlayersStorage[player]!!
        player.gameMode = stats.gameMode
        player.allowFlight = stats.gameMode == GameMode.CREATIVE
        player.isFlying = false
        player.level = stats.level
        player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        player.exp = stats.exp.toFloat()
        player.foodLevel = stats.hunger

        if (!arena.meta.customizingMeta.keepHealthEnabled) {
            player.maxHealth = stats.maxHealth
            player.health = stats.health
        }

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents.clone()
            player.inventory.setArmorContents(stats.armorContents.clone())
            player.updateInventory()
        }
    }

    abstract fun setPlayerToArena(player: Player, team: Team)

    protected fun respawnBall(delayInTicks: Int) {
        if (ballSpawning) {
            return
        }

        destroyBall()
        ballSpawning = true
        ballSpawnCounter = delayInTicks
    }

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

    // region Referee
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
        ballSpawning = false
        ballSpawnCounter = 0
    }


    protected fun destroyBall() {
        ball?.remove()
        ball = null
    }

    protected fun setGameClosing() {
        if (closing) {
            return
        }

        closing = true
    }

    // endregion
}
