package com.github.shynixn.blockball.impl

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.contract.*
import com.github.shynixn.blockball.entity.*
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.event.GameJoinEvent
import com.github.shynixn.blockball.event.GameLeaveEvent
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.sound.SoundMeta
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.mcutils.common.toLocation
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.Scoreboard

class BlockBallMiniGameImpl constructor(
    override val arena: Arena,
    gameService: GameService,
    private val playerDataRepository: PlayerDataRepository<PlayerInformation>,
    private val plugin: Plugin,
    private val placeHolderService: PlaceHolderService,
    private val bossBarService: BossBarService,
    private val chatMessageService: ChatMessageService,
    private val configurationService: ConfigurationService,
    private val soundService: SoundService,
    packetService: PacketService,
    scoreboardService: ScoreboardService,
    ballEntityService: BallEntityService
) : BlockBallGameImpl(
    arena,
    gameService,
    placeHolderService,
    packetService,
    plugin,
    bossBarService,
    scoreboardService,
    ballEntityService,
    chatMessageService,
    playerDataRepository
), BlockBallMiniGame {

    /**
     * Is the lobby countdown active.
     */
    var lobbyCountDownActive: Boolean = false

    /**
     * Actual countdown.
     */
    var lobbyCountdown: Int = 20

    /**
     * Actual game coutndown.
     */
    override var gameCountdown: Int = 20

    /**
     * Index of the current match time.
     */
    override var matchTimeIndex: Int = 0

    /**
     * Returns if the lobby is full.
     */
    val isLobbyFull: Boolean
        get() {
            val amount = arena.meta.redTeamMeta.maxAmount + arena.meta.blueTeamMeta.maxAmount

            if (this.ingamePlayersStorage.size >= amount) {
                return true
            }

            return false
        }

    /**
     * Storage for [spectatorPlayers],
     */
    val spectatorPlayersStorage: MutableMap<Player, GameStorage> = HashMap()

    /**
     * List of players which are spectating the game.
     */
    override val spectatorPlayers: List<Player>
        get() {
            return spectatorPlayersStorage.keys.toList()
        }

    /**
     * Returns the bling sound.
     */
    val blingSound: SoundMeta = SoundMeta().also {
        it.name = "BLOCK_NOTE_BLOCK_PLING,BLOCK_NOTE_PLING,NOTE_PLING"
        it.volume = 10.0
        it.pitch = 2.0
    }

    /**
     * Lets the given [player] leave spectate the given [game].
     * Does nothing if the player is already spectating a Game.
     */
    override fun spectate(player: Player) {
        if (spectatorPlayers.contains(player)) {
            return
        }

        if (!isAllowedToSpectateWithPermissions(player)) {
            return
        }

        val storage = createPlayerStorage(player)
        spectatorPlayersStorage[player] = storage
        player.gameMode = GameMode.SPECTATOR
        player.isFlying = true

        if (arena.meta.spectatorMeta.spectateSpawnpoint != null) {
            player.teleport(arena.meta.spectatorMeta.spectateSpawnpoint!!.toLocation())
        } else {
            player.teleport(arena.meta.ballMeta.spawnpoint!!.toLocation())
        }
    }

    /**
     * Lets the given [player] leave join. Optional can the prefered
     * [team] be specified but the team can still change because of arena settings.
     * Does nothing if the player is already in a Game.
     */
    override fun join(player: Player, team: Team?): JoinResult {
        if (!isAllowedToJoinWithPermissions(player)) {
            return JoinResult.NO_PERMISSION
        }

        val otherGame = gameService.getGameFromPlayer(player)

        if (otherGame != null) {
            leave(player)
        }

        val event = GameJoinEvent(player, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return JoinResult.EVENT_CANCELLED
        }

        if (playing || isLobbyFull) {
            val b = ChatBuilder().text(
                placeHolderService.replacePlaceHolders(
                    arena.meta.spectatorMeta.spectateStartMessage[0], player, this
                )
            ).nextLine().component(
                placeHolderService.replacePlaceHolders(
                    arena.meta.spectatorMeta.spectateStartMessage[1], player, this
                )
            ).setClickAction(
                ChatClickAction.RUN_COMMAND,
                "/" + configurationService.findValue<String>("global-spectate.command") + " " + arena.name
            ).setHoverText(" ").builder()
            chatMessageService.sendChatMessage(player, b.convertToTextComponent())
            return JoinResult.GAME_FULL
        }

        if (ingamePlayersStorage.containsKey(player) && team != null) {
            var targetTeam = team
            val amount = getAmountOfQueuedPlayersInThisTeam(targetTeam)

            if (arena.meta.lobbyMeta.onlyAllowEventTeams) {
                val blueTeamAmount = getAmountOfQueuedPlayersInThisTeam(Team.BLUE)
                val redTeamAmount = getAmountOfQueuedPlayersInThisTeam(Team.RED)

                if (blueTeamAmount > redTeamAmount) {
                    targetTeam = Team.RED
                } else if (blueTeamAmount < redTeamAmount) {
                    targetTeam = Team.BLUE
                }
            }

            if (targetTeam == Team.RED) {
                if (amount >= arena.meta.redTeamMeta.maxAmount) {
                    return JoinResult.TEAM_FULL
                }

                joinTeam(player, Team.RED, arena.meta.redTeamMeta)
            } else if (targetTeam == Team.BLUE) {
                if (amount >= arena.meta.blueTeamMeta.maxAmount) {
                    return JoinResult.TEAM_FULL
                }

                joinTeam(player, Team.BLUE, arena.meta.blueTeamMeta)
            }

            ingamePlayersStorage[player]!!.team = targetTeam
            ingamePlayersStorage[player]!!.goalTeam = targetTeam

            if (targetTeam == Team.BLUE) {
                return JoinResult.SUCCESS_BLUE
            } else {
                return JoinResult.SUCCESS_RED
            }
        }

        val storage = this.createPlayerStorage(player)
        ingamePlayersStorage[player] = storage
        player.teleport(arena.meta.minigameMeta.lobbySpawnpoint!!.toLocation())

        if (team != null) {
            val innerResult = join(player, team)
            if (innerResult != JoinResult.SUCCESS_BLUE && innerResult != JoinResult.SUCCESS_RED) {
                return innerResult
            }
        }

        plugin.launch {
            val playerData = playerDataRepository.getByPlayer(player)
            if (playerData != null) {
                playerData.statsMeta.joinedGames++
            }
        }

        return JoinResult.SUCCESS_QUEUED
    }

    /**
     * Leaves the given player.
     */
    override fun leave(player: Player): LeaveResult {
        if (spectatorPlayers.contains(player)) {
            resetStorage(player, spectatorPlayersStorage[player]!!)
            spectatorPlayersStorage.remove(player)
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
            return LeaveResult.SUCCESS
        }

        if (!ingamePlayersStorage.containsKey(player)) {
            return LeaveResult.NOT_IN_MATCH
        }

        val event = GameLeaveEvent(player, this)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return LeaveResult.EVENT_CANCELLED
        }

        if (scoreboard != null && player.scoreboard == scoreboard) {
            player.scoreboard = Bukkit.getScoreboardManager()!!.newScoreboard
        }

        if (bossBar != null) {
            bossBarService.removePlayer(bossBar, player)
        }

        for (hologram in holograms) {
            if (hologram.players.contains(player)) {
                hologram.players.remove(player)
            }
        }

        val stats = ingamePlayersStorage[player]!!
        resetStorage(player, stats)

        if (ingamePlayersStorage.containsKey(player)) {
            val storage = ingamePlayersStorage[player]!!

            if (storage.team == Team.RED) {
                player.sendMessage(
                    placeHolderService.replacePlaceHolders(
                        arena.meta.redTeamMeta.leaveMessage,
                        player,
                        this,
                        null,
                        null
                    )
                )
            } else if (storage.team == Team.BLUE) {
                player.sendMessage(
                    placeHolderService.replacePlaceHolders(
                        arena.meta.redTeamMeta.leaveMessage,
                        player,
                        this,
                        null,
                        null
                    )
                )
            }

            ingamePlayersStorage.remove(player)
        }

        if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
            player.teleport(arena.meta.lobbyMeta.leaveSpawnpoint!!.toLocation())
        }

        return LeaveResult.SUCCESS
    }

    /**
     * Tick handle.
     */
    override fun handle(ticks: Int) {
        // Handle HubGame ticking.
        if (!arena.enabled || closing) {
            status = GameState.DISABLED
            onUpdateSigns()
            close()
            if (ingamePlayersStorage.isNotEmpty()) {
                closing = true
            }
            return
        }

        if (status == GameState.DISABLED) {
            status = GameState.JOINABLE
        }

        if (Bukkit.getWorld(arena.meta.ballMeta.spawnpoint!!.world!!) == null) {
            return
        }

        if (arena.meta.hubLobbyMeta.resetArenaOnEmpty && ingamePlayersStorage.isEmpty() && (redScore > 0 || blueScore > 0)) {
            closing = true
        }

        if (ticks >= 20) {
            if (lobbyCountDownActive) {
                if (lobbyCountdown > 10) {
                    val amountPlayers = arena.meta.blueTeamMeta.maxAmount + arena.meta.redTeamMeta.maxAmount

                    if (ingamePlayersStorage.size >= amountPlayers) {
                        lobbyCountdown = 10
                    }
                }

                lobbyCountdown--

                ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                    if (lobbyCountdown <= 10) {
                        p.exp = 1.0F - (lobbyCountdown.toFloat() / 10.0F)
                    }

                    p.level = lobbyCountdown
                }

                if (lobbyCountdown < 5) {
                    ingamePlayersStorage.keys.forEach { p ->
                        soundService.playSound(
                            p.location, arrayListOf(p), blingSound
                        )
                    }
                }

                if (lobbyCountdown <= 0) {
                    ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                        if (lobbyCountdown <= 10) {
                            p.exp = 1.0F
                        }

                        p.level = 0
                    }

                    lobbyCountDownActive = false
                    playing = true
                    status = GameState.RUNNING
                    matchTimeIndex = -1

                    ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                        val stats = ingamePlayersStorage[p]

                        if (stats!!.team == null) {
                            if (redTeam.size < blueTeam.size) {
                                stats.team = Team.RED
                                stats.goalTeam = Team.RED
                                joinTeam(p, Team.RED, arena.meta.redTeamMeta)
                            } else {
                                stats.team = Team.BLUE
                                stats.goalTeam = Team.BLUE
                                joinTeam(p, Team.BLUE, arena.meta.blueTeamMeta)
                            }
                        }
                    }

                    switchToNextMatchTime()
                }
            }

            if (!lobbyCountDownActive) {
                if (canStartLobbyCountdown()) {
                    lobbyCountDownActive = true
                    lobbyCountdown = arena.meta.minigameMeta.lobbyDuration
                } else if (!playing) {
                    ingamePlayersStorage.keys.toTypedArray().forEach { p ->
                        chatMessageService.sendActionBarMessage(
                            p, placeHolderService.replacePlaceHolders(
                                arena.meta.minigameMeta.playersRequiredToStartMessage, p, this
                            )
                        )
                    }
                }
            }

            if (playing) {
                gameCountdown--

                ingamePlayersStorage.keys.toTypedArray().asSequence().forEach { p ->
                    if (gameCountdown <= 10) {
                        p.exp = gameCountdown.toFloat() / 10.0F
                    }

                    if (gameCountdown <= 5) {
                        soundService.playSound(
                            p.location, arrayListOf(p), blingSound
                        )
                    }

                    p.level = gameCountdown
                }

                if (gameCountdown <= 0) {
                    switchToNextMatchTime()
                }

                if (ingamePlayersStorage.isEmpty() || redTeam.size < arena.meta.redTeamMeta.minPlayingPlayers || blueTeam.size < arena.meta.blueTeamMeta.minPlayingPlayers) {
                    closing = true
                }
            }
        }

        // Handle Ball.
        this.fixBallPositionSpawn()
        if (ticks >= 20) {
            this.handleBallSpawning()
        }

        // Update signs and protections.
        super.handle(ticks)
    }

    /**
     * Closes the given game and all underlying resources.
     */
    override fun close() {
        if (closed) {
            return
        }

        spectatorPlayers.forEach { p ->
            leave(p)
        }

        status = GameState.DISABLED
        closed = true
        ingamePlayersStorage.keys.toTypedArray().forEach { p ->
            leave(p)
        }
        ingamePlayersStorage.clear()
        ball?.remove()
        doubleJumpCoolDownPlayers.clear()
        holograms.forEach { h -> h.remove() }
        holograms.clear()

        if (bossBar != null) {
            bossBarService.cleanResources(bossBar)
        }
    }

    /**
     * Joins the [player] to the given [teamMeta].
     */
    private fun joinTeam(player: Player, team: Team, teamMeta: TeamMeta) {
        player.walkSpeed = teamMeta.walkingSpeed.toFloat()

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = teamMeta.inventory.map {
                if (it != null) {
                    val configuration = org.bukkit.configuration.file.YamlConfiguration()
                    configuration.loadFromString(it)
                    configuration.getItemStack("item")
                } else {
                    null
                }
            }.toTypedArray()
            player.inventory.setArmorContents(
                teamMeta.armor.map {
                    if (it != null) {
                        val configuration = org.bukkit.configuration.file.YamlConfiguration()
                        configuration.loadFromString(it)
                        configuration.getItemStack("item")
                    } else {
                        null
                    }
                }.toTypedArray()
            )
            player.updateInventory()
        }

        if (team == Team.RED && arena.meta.redTeamMeta.lobbySpawnpoint != null) {
            player.teleport(arena.meta.redTeamMeta.lobbySpawnpoint!!.toLocation())
        } else if (team == Team.BLUE && arena.meta.blueTeamMeta.lobbySpawnpoint != null) {
            player.teleport(arena.meta.blueTeamMeta.lobbySpawnpoint!!.toLocation())
        }

        val players = if (team == Team.RED) {
            redTeam
        } else {
            blueTeam
        }

        player.sendMessage(
            placeHolderService.replacePlaceHolders(
                teamMeta.joinMessage, player, this, teamMeta, players.size
            )
        )
    }


    private fun createPlayerStorage(player: Player): GameStorage {
        val stats = GameStorage()
        stats.gameMode = player.gameMode
        stats.armorContents = player.inventory.armorContents.clone()
        stats.inventoryContents = player.inventory.contents.clone()
        stats.flying = player.isFlying
        stats.allowedFlying = player.allowFlight
        stats.walkingSpeed = player.walkSpeed.toDouble()
        stats.scoreboard = player.scoreboard
        stats.level = player.level
        stats.exp = player.exp.toDouble()
        stats.maxHealth = player.maxHealth
        stats.health = player.health
        stats.hunger = player.foodLevel

        player.allowFlight = false
        player.isFlying = false

        if (!arena.meta.customizingMeta.keepHealthEnabled) {
            player.maxHealth = 20.0
            player.health = 20.0
        }

        player.foodLevel = 20
        player.level = 0
        player.exp = 0.0F
        player.gameMode = arena.meta.lobbyMeta.gamemode

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.clear()
            player.updateInventory()
        }

        return stats
    }

    /**
     * Actives the next match time. Closes the match if no match time is available.
     */
    override fun switchToNextMatchTime() {
        matchTimeIndex++

        val matchTimes = arena.meta.minigameMeta.matchTimes

        if (matchTimeIndex >= matchTimes.size) {
            closing = true
            return
        }

        val matchTime = matchTimes[matchTimeIndex]
        val isLastMatchTimeSwap = (matchTimeIndex + 1) >= matchTimes.size

        if (isLastMatchTimeSwap) {
            timeAlmostUp()
        }

        gameCountdown = matchTime.duration

        if (matchTime.isSwitchGoalsEnabled) {
            mirroredGoals = !mirroredGoals

            ingamePlayersStorage.values.forEach { e ->
                if (e.goalTeam != null) {
                    if (e.goalTeam == Team.RED) {
                        e.goalTeam = Team.BLUE
                    } else {
                        e.goalTeam = Team.RED
                    }
                }
            }
        }

        ballEnabled = matchTime.playAbleBall

        if (!ballEnabled && ball != null && !ball!!.isDead) {
            ball!!.remove()
        }

        for (p in ingamePlayersStorage.keys) {
            if (matchTime.respawnEnabled || matchTimeIndex == 0) {
                respawn(p)
            }

            if (!matchTime.startMessageTitle.isBlank() || !matchTime.startMessageSubTitle.isBlank()) {
                val game = this
                plugin.launch {
                    delay(60.ticks)
                    chatMessageService.sendTitleMessage(
                        p,
                        placeHolderService.replacePlaceHolders(matchTime.startMessageTitle, null, game),
                        placeHolderService.replacePlaceHolders(matchTime.startMessageSubTitle, null, game),
                        matchTime.startMessageFadeIn,
                        matchTime.startMessageStay,
                        matchTime.startMessageFadeOut
                    )
                }
            }

            p.exp = 1.0F
        }
    }

    /**
     * Resets the storage of the given [player].
     */
    private fun resetStorage(player: Player, stats: GameStorage) {
        player.gameMode = stats.gameMode
        player.allowFlight = stats.gameMode == GameMode.CREATIVE
        player.isFlying = false
        player.walkSpeed = stats.walkingSpeed.toFloat()
        player.scoreboard = stats.scoreboard as Scoreboard
        player.level = stats.level
        player.exp = stats.exp.toFloat()

        if (!arena.meta.customizingMeta.keepHealthEnabled) {
            player.maxHealth = stats.maxHealth
            player.health = stats.health
        }

        player.foodLevel = stats.hunger

        if (!arena.meta.customizingMeta.keepInventoryEnabled) {
            player.inventory.contents = stats.inventoryContents.clone()
            player.inventory.setArmorContents(stats.armorContents.clone())
            player.updateInventory()
        }
    }

    /**
     * Returns the amount of queues players.
     */
    private fun getAmountOfQueuedPlayersInThisTeam(team: Team): Int {
        var amount = 0

        ingamePlayersStorage.values.forEach { p ->
            if (p.team != null && p.team == team) {
                amount++
            }
        }

        return amount
    }

    /**
     * Gets called when the game ends.
     */
    private fun timeAlmostUp() {
        when {
            redScore == blueScore -> {
                onMatchEnd(null, null)
                onDraw()
            }

            redScore > blueScore -> {
                onMatchEnd(redTeam, blueTeam)
                onWin(Team.RED, arena.meta.redTeamMeta)
            }

            else -> {
                onMatchEnd(blueTeam, redTeam)
                onWin(Team.BLUE, arena.meta.blueTeamMeta)
            }
        }

        // OnWin sets game.closing to true.
        closing = false
    }


    /**
     * Returns if the lobby countdown can already be started.
     * @return canStart
     */
    private fun canStartLobbyCountdown(): Boolean {
        val amount = arena.meta.redTeamMeta.minAmount + arena.meta.blueTeamMeta.minAmount

        if (!playing && ingamePlayersStorage.size >= amount && ingamePlayersStorage.isNotEmpty()) {
            return true
        }

        return false
    }


    /**
     * Returns if the given [player] is allowed to spectate the match.
     */
    private fun isAllowedToSpectateWithPermissions(player: Player): Boolean {
        val hasSpectatingPermission =
            player.hasPermission(Permission.SPECTATE.permission + ".all") || player.hasPermission(
                Permission.SPECTATE.permission + "." + arena.name
            )

        if (hasSpectatingPermission) {
            return true
        }

        player.sendMessage(BlockBallLanguage.spectateNoPermission)
        return false
    }
}
