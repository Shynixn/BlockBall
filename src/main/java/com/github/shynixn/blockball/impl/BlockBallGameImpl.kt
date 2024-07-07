package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.event.GameEndEvent
import com.github.shynixn.blockball.event.GameGoalEvent
import com.github.shynixn.blockball.impl.extension.setSignLines
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.command.CommandService
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard
import java.util.logging.Level

abstract class BlockBallGameImpl(
    /**
     * Gets the arena.
     */
    override val arena: Arena,
    val gameService: GameService,
    private val placeHolderService: PlaceHolderService,
    private val packetService: PacketService,
    private val plugin: Plugin,
    private val bossBarService: BossBarService,
    private val scoreboardService: ScoreboardService,
    private val ballEntityService: BallEntityService,
    private val chatMessageService: ChatMessageService,
    private val commandService: CommandService,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>
) : BlockBallGame {
    /**
     * Is the ball spawning?
     */
    private var ballSpawning: Boolean = false

    /**
     * Ball spawn counter.
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
     * RedScore.
     */
    override var redScore: Int = 0

    /**
     * Blue Score.
     */
    override var blueScore: Int = 0

    /**
     * Marks the game for being closed and will automatically
     * switch to close state once the resources are cleard.
     */
    override var closing: Boolean = false

    /**
     * Is the game closed.
     */
    override var closed: Boolean = false

    /**
     * Status.
     */
    override var status: GameState = GameState.JOINABLE

    /**
     * Ball.
     */
    override var ball: Ball? = null

    /**
     * Contains players which are in cooldown by doublejump.
     */
    override val doubleJumpCoolDownPlayers: MutableMap<Player, Int> = HashMap()

    /**
     * Are currently players actively playing in this game?
     */
    var playing: Boolean = false

    /**
     * Ball bumper counter
     */
    override var ballBumperCounter: Int = 0

    /**
     * The last interacted entity with the ball. Can also be a non player.
     */
    override var lastInteractedEntity: Any? = null

    var mirroredGoals: Boolean = false

    /**
     * All players which are already fix in team red.
     */
    override val redTeam: List<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.RED }.keys.toList()
        }

    /**
     * All players which are already fix in team blue.
     */
    override val blueTeam: List<Player>
        get() {
            return this.ingamePlayersStorage.filter { p -> p.value.team != null && p.value.team!! == Team.BLUE }.keys.toList()
        }

    /**
     * List of players which are already in the [redTeam] or [blueTeam].
     */
    private val inTeamPlayers: List<Player>
        get() {
            val players = ArrayList(redTeam)
            players.addAll(blueTeam)
            return players
        }

    /**
     * Tick handle.
     */
    override fun handle(ticks: Int) {
        if (ticks >= 20) {
            if (closing) {
                return
            }

            this.kickUnwantedEntitiesOutOfForcefield()
            this.onUpdateSigns()
            this.updateScoreboard()
            this.updateBossBar()
            this.updateDoubleJumpCooldown()
            this.updateHolograms()
            this.updateDoubleJumpCooldown()
        }
    }


    fun onMatchEnd(team: Team? = null) {
        val winningPlayers = HashSet<Player>()

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
            }
        }

        // Store playing stats.
        val participatingPlayers = inTeamPlayers.toTypedArray()

        plugin.launch {
            for (player in participatingPlayers) {
                val playerData = playerDataRepository.getByPlayer(player)

                if (playerData != null) {
                    playerData.statsMeta.playedGames++
                    playerData.playerName = player.name

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
    fun onWin(team: Team, teamMeta: TeamMeta) {
        val event = GameEndEvent(team, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return
        }

        val winMessageTitle = teamMeta.winMessageTitle
        val winMessageSubTitle = teamMeta.winMessageSubTitle

        val players = ArrayList(inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers()
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first as Player })

        players.forEach { p ->
            require(p is Player)
            chatMessageService.sendTitleMessage(
                p,
                placeHolderService.replacePlaceHolders(winMessageTitle, p, this),
                placeHolderService.replacePlaceHolders(winMessageSubTitle, p, this),
                teamMeta.winMessageFadeIn,
                teamMeta.winMessageStay,
                teamMeta.winMessageFadeOut,
            )
        }

        closing = true
    }

    /**
     * Returns if the given [player] is allowed to join the match.
     */
    protected fun isAllowedToJoinWithPermissions(player: Player): Boolean {
        if (player.hasPermission(Permission.JOIN.permission + ".all") || player.hasPermission(Permission.JOIN.permission + "." + arena.name)) {
            return true
        }

        player.sendMessage(BlockBallLanguage.joinNoPermission)
        return false
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
        } else {
            arena.meta.blueTeamMeta
        }

        if (teamMeta.spawnpoint == null) {
            player.teleport(arena.meta.ballMeta.spawnpoint!!.toLocation())
        } else {
            player.teleport(teamMeta.spawnpoint!!.toLocation())
        }
    }

    protected fun fixBallPositionSpawn() {
        if (ball == null || ball!!.isDead) {
            return
        }
        if (ingamePlayersStorage.isEmpty()) {
            ball!!.remove()
        }
    }


    protected fun handleBallSpawning() {
        if (ballSpawning && ballEnabled) {
            ballSpawnCounter--
            if (ballSpawnCounter <= 0) {
                if (ball != null && !ball!!.isDead) {
                    ball!!.remove()
                }

                ball = ballEntityService.spawnTemporaryBall(
                    arena.meta.ballMeta.spawnpoint!!.toLocation(), arena.meta.ballMeta
                )
                ballSpawning = false
                ballSpawnCounter = 0
            }
        } else if ((ball == null || ball!!.isDead) && (redTeam.isNotEmpty() || blueTeam.isNotEmpty())) {
            if (arena.gameType != GameType.HUBGAME || redTeam.size >= arena.meta.redTeamMeta.minAmount && blueTeam.size >= arena.meta.blueTeamMeta.minAmount) {
                ballSpawning = true
                ballSpawnCounter = arena.meta.ballMeta.delayInTicks
            }
        }
    }

    /**
     * Gets called when the signs should be updated in the game lifecycle.
     */
    protected fun onUpdateSigns() {
        try {
            for (i in arena.meta.redTeamMeta.signs.indices) {
                val position = arena.meta.redTeamMeta.signs[i]

                if (!replaceTextOnSign(
                        position, arena.meta.redTeamMeta.signLines, arena.meta.redTeamMeta
                    )
                ) {
                    arena.meta.redTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in arena.meta.blueTeamMeta.signs.indices) {
                val position = arena.meta.blueTeamMeta.signs[i]

                if (!replaceTextOnSign(
                        position, arena.meta.blueTeamMeta.signLines, arena.meta.blueTeamMeta
                    )
                ) {
                    arena.meta.blueTeamMeta.signs.removeAt(i)
                    return
                }
            }
            for (i in arena.meta.lobbyMeta.joinSigns.indices) {
                val position = arena.meta.lobbyMeta.joinSigns[i]

                if (!replaceTextOnSign(position, arena.meta.lobbyMeta.joinSignLines, null)) {
                    arena.meta.lobbyMeta.joinSigns.removeAt(i)
                    return
                }
            }
            for (i in arena.meta.lobbyMeta.leaveSigns.indices) {
                val position = arena.meta.lobbyMeta.leaveSigns[i]

                if (!replaceTextOnSign(position, arena.meta.lobbyMeta.leaveSignLines, null)) {
                    arena.meta.lobbyMeta.leaveSigns.removeAt(i)
                    return
                }
            }
        } catch (e: Exception) { // Removing sign task could clash with updating signs.
            plugin.logger.log(Level.SEVERE, "Sign update was cached.", e)
        }
    }

    /**
     * Replaces the text on the sign.
     */
    private fun replaceTextOnSign(
        signPosition: Vector3d, lines: List<String>, teamMeta: TeamMeta?
    ): Boolean {
        var players = redTeam

        if (arena.meta.blueTeamMeta == teamMeta) {
            players = blueTeam
        }

        val location = signPosition.toLocation()
        val placeHolderReplacedLines =
            lines.map { l -> placeHolderService.replacePlaceHolders(l, null, this, teamMeta, players.size) }

        return location.setSignLines(placeHolderReplacedLines)
    }

    /**
     * Kicks entities out of the arena.
     */
    private fun kickUnwantedEntitiesOutOfForcefield() {
        if (!arena.meta.protectionMeta.entityProtectionEnabled) {
            return
        }

        val ballSpawnpointLocation = arena.meta.ballMeta.spawnpoint!!.toLocation()

        for (entity in ballSpawnpointLocation.world!!.entities) {
            if (entity is Player || entity is ItemFrame) {
                continue
            }

            if (arena.isLocationInSelection(entity.location.toVector3d())) {
                val vector = arena.meta.protectionMeta.entityProtection
                entity.location.setDirection(vector.toVector())
                entity.velocity = vector.toVector()
            }
        }
    }

    /**
     * Updates the hologram for the current game.
     */
    private fun updateHolograms() {
        if (holograms.size != arena.meta.hologramMetas.size) {
            holograms.forEach { h -> h.remove() }
            holograms.clear()

            arena.meta.hologramMetas.forEach { meta ->
                val hologram = PacketHologram()
                hologram.packetService = packetService

                hologram.lines = meta.lines
                hologram.location = meta.position!!.toLocation()
                holograms.add(hologram)
            }
        }

        holograms.forEachIndexed { i, holo ->
            val players = ArrayList(inTeamPlayers)
            val additionalPlayers = getAdditionalNotificationPlayers()
            players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first as Player }
                .toList())

            holo.players.addAll(players as Collection<Player>)

            additionalPlayers.filter { p -> !p.second && holo.players.contains(p.first) }.forEach { p ->
                holo.players.remove(p.first)
            }

            val lines = ArrayList(arena.meta.hologramMetas[i].lines)

            for (k in lines.indices) {
                lines[k] = placeHolderService.replacePlaceHolders(lines[k], null, this)
            }

            holo.lines = lines
            holo.update()
        }
    }

    /**
     * Updates the bossbar for the current game.
     */
    private fun updateBossBar() {
        val meta = arena.meta.bossBarMeta
        if (Version.serverVersion.isVersionSameOrGreaterThan(Version.VERSION_1_9_R1)) {
            if (bossBar == null && arena.meta.bossBarMeta.enabled) {
                bossBar = bossBarService.createNewBossBar<Any>(arena.meta.bossBarMeta)
            }

            if (bossBar != null) {
                bossBarService.changeConfiguration(
                    bossBar, placeHolderService.replacePlaceHolders(meta.message, null, this), meta, null
                )

                val players = ArrayList(inTeamPlayers)
                val additionalPlayers = getAdditionalNotificationPlayers()
                players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }
                    .map { p -> p.first as Player }.toList())

                val bossbarPlayers = bossBarService.getPlayers<Any, Any>(bossBar!!)

                additionalPlayers.filter { p -> !p.second }.forEach { p ->
                    if (bossbarPlayers.contains(p.first)) {
                        bossBarService.removePlayer(bossBar!!, p.first)
                    }
                }

                players.forEach { p ->
                    bossBarService.addPlayer(bossBar, p)
                }
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
     * Updates the scoreboard for all players when enabled.
     */
    private fun updateScoreboard() {
        if (!arena.meta.scoreboardMeta.enabled) {
            return
        }

        if (scoreboard == null) {
            scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard

            scoreboardService.setConfiguration(
                scoreboard as Scoreboard, ScoreboardDisplaySlot.SIDEBAR, arena.meta.scoreboardMeta.title
            )
        }

        val players = ArrayList(inTeamPlayers)
        val additionalPlayers = getAdditionalNotificationPlayers()
        players.addAll(additionalPlayers.asSequence().filter { pair -> pair.second }.map { p -> p.first as Player }
            .toList())

        additionalPlayers.filter { p -> !p.second }.forEach { p ->
            if ((p.first as Player).scoreboard == scoreboard) {
                (p.first as Player).scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
            }
        }

        players.forEach { p ->
            if (scoreboard != null) {
                if (p.scoreboard != scoreboard) {
                    p.scoreboard = scoreboard as Scoreboard
                }

                val lines = arena.meta.scoreboardMeta.lines

                var j = lines.size
                for (i in 0 until lines.size) {
                    val line = placeHolderService.replacePlaceHolders(lines[i], p as Player, this)
                    scoreboardService.setLine(scoreboard as Scoreboard, j, line)
                    j--
                }
            }
        }
    }

    /**
     * Returns a list of players which can be also notified
     */
    private fun getAdditionalNotificationPlayers(): MutableList<Pair<Any, Boolean>> {
        if (!arena.meta.spectatorMeta.notifyNearbyPlayers) {
            return ArrayList()
        }

        val players = ArrayList<Pair<Any, Boolean>>()
        val center = arena.center


        center.toLocation().world!!.players.filter { p -> !ingamePlayersStorage.containsKey(p) }.forEach { p ->
            val playerPosition = p.location.toVector3d()
            val distanceToCenter = playerPosition.distance(center)

            if (distanceToCenter <= arena.meta.spectatorMeta.notificationRadius) {
                players.add(Pair(p, true))
            } else {
                players.add(Pair(p, false))
            }
        }

        return players
    }

    /**
     * Get nofified players.
     */
    private fun getNofifiedPlayers(): List<Pair<Any, Boolean>> {
        val players = ArrayList<Pair<Any, Boolean>>()

        if (arena.meta.spectatorMeta.notifyNearbyPlayers) {
            for (player in arena.center.toLocation().world!!.players) {
                val playerPosition = player.location.toVector3d()

                if (playerPosition.distance(arena.center) <= arena.meta.spectatorMeta.notificationRadius) {
                    players.add(Pair(player, true))
                } else {
                    players.add(Pair(player, false))
                }
            }
        }

        return players
    }

    /**
     * Gets called when the given [game] ends with a draw.
     */
    fun onDraw() {
        val additionalPlayers = getNofifiedPlayers().filter { pair -> pair.second }.map { p -> p.first as Player }
        additionalPlayers.forEach { p ->
            chatMessageService.sendTitleMessage(
                p,
                placeHolderService.replacePlaceHolders(arena.meta.redTeamMeta.drawMessageTitle, p, this),
                placeHolderService.replacePlaceHolders(arena.meta.redTeamMeta.drawMessageSubTitle, p, this),
                arena.meta.redTeamMeta.drawMessageFadeIn,
                arena.meta.redTeamMeta.drawMessageStay,
                arena.meta.redTeamMeta.drawMessageFadeOut,
            )
        }

        redTeam.forEach { p ->
            chatMessageService.sendTitleMessage(
                p,
                placeHolderService.replacePlaceHolders(arena.meta.redTeamMeta.drawMessageTitle, p, this),
                placeHolderService.replacePlaceHolders(arena.meta.redTeamMeta.drawMessageSubTitle, p, this),
                arena.meta.redTeamMeta.drawMessageFadeIn,
                arena.meta.redTeamMeta.drawMessageStay,
                arena.meta.redTeamMeta.drawMessageFadeOut,
            )
        }
        blueTeam.forEach { p ->
            chatMessageService.sendTitleMessage(
                p,
                placeHolderService.replacePlaceHolders(arena.meta.blueTeamMeta.drawMessageTitle, p, this),
                placeHolderService.replacePlaceHolders(arena.meta.blueTeamMeta.drawMessageSubTitle, p, this),
                arena.meta.blueTeamMeta.drawMessageFadeIn,
                arena.meta.blueTeamMeta.drawMessageStay,
                arena.meta.blueTeamMeta.drawMessageFadeOut,
            )
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
            onScore(Team.BLUE, arena.meta.blueTeamMeta)
            onScoreReward(Team.BLUE, blueTeam)
            relocatePlayersAndBall()

            if (blueScore >= arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(Team.BLUE)
                onWin(Team.BLUE, arena.meta.blueTeamMeta)
            }

            return
        }

        if (teamOfGoal == Team.BLUE) {
            redScore += arena.meta.redTeamMeta.pointsPerGoal
            onScore(Team.RED, arena.meta.redTeamMeta)
            onScoreReward(Team.BLUE, redTeam)
            relocatePlayersAndBall()

            if (redScore >= arena.meta.lobbyMeta.maxScore) {
                onMatchEnd(Team.RED)
                onWin(Team.RED, arena.meta.redTeamMeta)
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
        } else {
            redScore += arena.meta.redTeamMeta.pointsPerEnemyDeath
        }
    }


    /**
     * Gets called when a goal gets scored on the given [game] by the given [team].
     */
    private fun onScore(team: Team, teamMeta: TeamMeta) {
        var interactionEntity: Player? = null

        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            interactionEntity = lastInteractedEntity!! as Player
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

        val scoreMessageTitle = teamMeta.scoreMessageTitle
        val scoreMessageSubTitle = teamMeta.scoreMessageSubTitle

        val players = ArrayList(inTeamPlayers)
        val additionalPlayers = getNofifiedPlayers()
        players.addAll(additionalPlayers.filter { pair -> pair.second }.map { p -> p.first as Player })

        val scoreTeamMeta = if (ingamePlayersStorage.containsKey(interactionEntity)) {
            val scorerGameStory = ingamePlayersStorage[interactionEntity]!!

            if (scorerGameStory.team == Team.RED) {
                arena.meta.redTeamMeta
            } else {
                arena.meta.blueTeamMeta
            }
        } else {
            null
        }

        players.forEach { p ->
            require(p is Player)
            chatMessageService.sendTitleMessage(
                p,
                placeHolderService.replacePlaceHolders(scoreMessageTitle, p, this, scoreTeamMeta),
                placeHolderService.replacePlaceHolders(scoreMessageSubTitle, p, this, scoreTeamMeta),
                teamMeta.scoreMessageFadeIn,
                teamMeta.scoreMessageStay,
                teamMeta.scoreMessageFadeOut
            )
        }

        plugin.launch {
            val playerData = playerDataRepository.getByPlayer(interactionEntity)

            if (playerData != null) {
                playerData.statsMeta.scoredGoals++
            }
        }
    }

    private fun onScoreReward(team: Team, players: List<Player>) {
        if (lastInteractedEntity != null && lastInteractedEntity is Player) {
            if (players.contains(lastInteractedEntity!!)) {
                val teamMeta = getTeamMetaFromTeam(team)
                executeCommandsWithPlaceHolder(listOf(lastInteractedEntity!! as Player), teamMeta.goalCommands)
            }
        }
    }

    fun executeCommandsWithPlaceHolder(players: List<Player>, commands: List<CommandMeta>) {
        commandService.executeCommands(players, commands) { c, p ->
            placeHolderService.replacePlaceHolders(
                c, p, this
            )
        }
    }

    /**
     * Teleports all players and ball back to their spawnpoint if [game] has got back teleport enabled.
     */
    private fun relocatePlayersAndBall() {
        if (!arena.meta.customizingMeta.backTeleport) {
            respawnBall()
            return
        }

        val tickDelay = 20 * arena.meta.customizingMeta.backTeleportDelay

        respawnBall(tickDelay)
        plugin.launch {
            delay(tickDelay.ticks)
            var redTeamSpawnpoint = arena.meta.redTeamMeta.spawnpoint

            if (redTeamSpawnpoint == null) {
                redTeamSpawnpoint = arena.meta.ballMeta.spawnpoint!!
            }

            var blueTeamSpawnpoint = arena.meta.blueTeamMeta.spawnpoint

            if (blueTeamSpawnpoint == null) {
                blueTeamSpawnpoint = arena.meta.ballMeta.spawnpoint!!
            }

            ingamePlayersStorage.forEach { i ->
                if (i.value.goalTeam == Team.RED) {
                    i.key.teleport(redTeamSpawnpoint.toLocation())
                } else if (i.value.goalTeam == Team.BLUE) {
                    i.key.teleport(blueTeamSpawnpoint.toLocation())
                }
            }
        }
    }

    private fun respawnBall(delayInTicks: Int = arena.meta.ballMeta.delayInTicks) {
        if (ballSpawning) {
            return
        }

        ballSpawning = true
        ballSpawnCounter = delayInTicks / 20
    }

    fun getTeamMetaFromTeam(team: Team): TeamMeta {
        if (team == Team.RED) {
            return arena.meta.redTeamMeta
        }

        return arena.meta.blueTeamMeta
    }
}
