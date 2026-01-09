package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallDependencyInjectionModule
import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.CloudService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerRefereeGame
import com.github.shynixn.blockball.entity.CloudGame
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.fasterxml.jackson.core.type.TypeReference
import com.github.shynixn.fasterxml.jackson.databind.ObjectMapper
import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.LanguageItem
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.selection.AreaHighlight
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.awt.Color
import java.util.*
import java.util.logging.Level
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class BlockBallCommandExecutor(
    private val arenaRepository: CacheRepository<SoccerArena>,
    private val gameService: GameService,
    private val plugin: CoroutinePlugin,
    private val language: BlockBallLanguage,
    private val selectionService: AreaSelectionService,
    private val placeHolderService: PlaceHolderService,
    private val itemService: ItemService,
    private val chatMessageService: ChatMessageService,
    private val cloudService: CloudService
) {
    private val arenaTabs: (s: CommandSender) -> List<String> = {
        var cache = arenaRepository.getCache()
        if (cache == null) {
            cache = runBlocking {
                arenaRepository.getAll()
            }
        }
        cache.map { e -> e.name }
    }
    private val onlinePlayerTabs: ((CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }
    private val worldTabs: ((CommandSender) -> List<String>) = {
        Bukkit.getWorlds().map { e -> e.name }
    }
    private val playerMustExist = object : Validator<Player> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): Player? {
            try {
                val playerId = openArgs[0]
                val player = Bukkit.getPlayer(playerId)

                if (player != null) {
                    return player
                }
                return Bukkit.getPlayer(UUID.fromString(playerId))
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.playerNotFoundMessage.text, null, mapOf("0" to openArgs[0])
            )
        }
    }

    private val teamTabs: (s: CommandSender) -> List<String> = {
        val tabs = ArrayList<Team>()
        tabs.add(Team.RED)
        tabs.add(Team.BLUE)

        if (it.hasPermission(Permission.REFEREE_JOIN.permission)) {
            tabs.add(Team.REFEREE)
        }

        tabs.map { e -> e.name.lowercase(Locale.ENGLISH) }
    }

    private val remainingStringValidator = object : Validator<String> {
        override suspend fun transform(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return openArgs.joinToString(" ")
        }
    }

    private val arenaNameRegex = object : Validator<String> {
        private val regex = Regex("^[a-zA-Z0-9-]+$")

        override suspend fun validate(
            sender: CommandSender, prevArgs: List<Any>, argument: String, openArgs: List<String>
        ): Boolean {
            return argument.matches(regex)
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.arenaNameHasToBeFormat.text
        }
    }

    private val maxLengthValidator = object : Validator<String> {
        override suspend fun validate(
            sender: CommandSender, prevArgs: List<Any>, argument: String, openArgs: List<String>
        ): Boolean {
            return argument.length < 20
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.maxLength20Characters.text
        }
    }
    private val gameMustNotExistValidator = object : Validator<String> {
        override suspend fun validate(
            sender: CommandSender, prevArgs: List<Any>, argument: String, openArgs: List<String>
        ): Boolean {
            val existingArenas = arenaRepository.getAll()
            return existingArenas.firstOrNull { e -> e.name.equals(argument, true) } == null
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.gameAlreadyExistsMessage.text, null, mapOf("0" to openArgs[0])
            )
        }
    }
    private val gameMustExistValidator = object : Validator<SoccerArena> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): SoccerArena? {
            val existingArenas = arenaRepository.getAll()
            return existingArenas.firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.gameDoesNotExistMessage.text, null, mapOf("0" to openArgs[0])
            )
        }
    }
    private val teamValidator = object : Validator<Team> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): Team? {
            return try {
                Team.valueOf(openArgs[0].uppercase(Locale.ENGLISH))
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.teamDoesNotExistMessage.text, null, mapOf("0" to openArgs[0])
            )
        }
    }

    private val teamMetaValidator = object : Validator<TeamMeta> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): TeamMeta? {
            val team: Team = try {
                Team.valueOf(openArgs[0].uppercase(Locale.ENGLISH))
            } catch (e: Exception) {
                return null
            }
            val arena = prevArgs[prevArgs.size - 1] as SoccerArena
            val teamMeta = if (team == Team.RED) {
                arena.meta.redTeamMeta
            } else if (team == Team.BLUE) {
                arena.meta.blueTeamMeta
            } else {
                arena.meta.refereeTeamMeta
            }
            return teamMeta
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return placeHolderService.resolvePlaceHolder(
                language.teamDoesNotExistMessage.text, null, mapOf("0" to openArgs[0])
            )
        }
    }

    private val selectionTypeValidator = object : Validator<SelectionType> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): SelectionType? {
            return SelectionType.values().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.selectionTypeDoesNotExistMessage.text
        }
    }

    private val gameTypeValidator = object : Validator<GameType> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): GameType? {
            return GameType.values().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.gameTypeNotExistMessage.text
        }
    }

    private val locationTypeValidator = object : Validator<LocationType> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): LocationType? {
            return LocationType.values().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.selectionTypeDoesNotExistMessage.text
        }
    }

    private val doubleValidator = object : Validator<Double> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): Double? {
            return openArgs[0].toDoubleOrNull()
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.cannotParseNumberMessage.text
        }
    }

    private val worldValidator = object : Validator<World> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): World? {
            try {
                return Bukkit.getWorld(openArgs[0])
            } catch (e: Exception) {
                return null
            }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.cannotParseWorldMessage.text
        }
    }

    init {
        val mcCart = CommandBuilder(plugin, "blockball", chatMessageService) {
            usage(language.commandUsage.text.translateChatColors())
            description(language.commandDescription.text)
            aliases(plugin.config.getStringList("commands.blockball.aliases"))
            permission(Permission.COMMAND)
            permissionMessage(language.noPermissionMessage.text.translateChatColors())
            subCommand("create") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandCreateToolTip.text }
                builder().argument("name").validator(maxLengthValidator).validator(arenaNameRegex)
                    .validator(gameMustNotExistValidator).tabs { listOf("<name>") }.argument("displayName")
                    .validator(remainingStringValidator).tabs { listOf("<displayName>") }
                    .execute { sender, name, displayName -> createArena(sender, name, displayName) }
            }
            subCommand("copy") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandCopyToolTip.text }
                builder()
                    .argument("source").validator(gameMustExistValidator).tabs(arenaTabs)
                    .argument("name").validator(maxLengthValidator).validator(arenaNameRegex)
                    .validator(gameMustNotExistValidator).tabs { listOf("<name>") }.argument("displayName")
                    .validator(remainingStringValidator).tabs { listOf("<displayName>") }
                    .execute { sender, source, name, displayName -> copyArena(sender, source, name, displayName) }
            }
            subCommand("delete") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandDeleteToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs)
                    .execute { sender, arena -> deleteArena(sender, arena) }
            }
            subCommand("list") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandListToolTip.text }
                builder().execute { sender -> listArena(sender) }
            }
            subCommand("toggle") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandToggleToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs)
                    .execute { sender, arena -> toggleGame(sender, arena) }
            }
            subCommand("join") {
                noPermission()
                toolTip { language.commandJoinToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs)
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { sender, arena ->
                        joinGame(
                            sender, sender, arena.name
                        )
                    }.argument("team").validator(teamValidator).tabs(teamTabs)
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { sender, arena, team ->
                        joinGame(sender, sender, arena.name, team)
                    }.argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { Permission.EDIT_GAME.permission }
                    .permissionMessage { language.noPermissionMessage.text }.execute { sender, arena, team, player ->
                        joinGame(sender, player, arena.name, team)
                    }
            }
            subCommand("leave") {
                noPermission()
                toolTip { language.commandLeaveToolTip.text }
                builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { sender -> leaveGame(sender) }
                    .argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                    .permission { Permission.EDIT_GAME.permission }
                    .permissionMessage { language.noPermissionMessage.text }.execute { _, player ->
                        leaveGame(player)
                    }
            }
            helpCommand()
            subCommand("axe") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandAxeToolTip.text }
                builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                    selectionService.addSelectionItemToInventory(player)
                    player.sendLanguageMessage(language.axeReceivedMessage)
                }
            }
            subCommand("select") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandSelectToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("type")
                    .validator(selectionTypeValidator).tabs {
                        SelectionType.values().map { e ->
                            e.name.lowercase(
                                Locale.ENGLISH
                            )
                        }
                    }.executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena, locationType ->
                        setSelection(player, arena, locationType)
                    }
            }
            subCommand("location") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandSelectToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("type")
                    .validator(locationTypeValidator).tabs {
                        LocationType.values().map { e ->
                            e.name.lowercase(
                                Locale.ENGLISH
                            )
                        }
                    }.executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena, locationType ->
                        setLocation(player, arena, locationType)
                    }
            }
            subCommand("gamerule") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandGameRuleToolTip.text }
                subCommand("gameType") {
                    toolTip { language.commandGameRuleToolTip.text }
                    permission(Permission.EDIT_GAME)
                    builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("value")
                        .validator(gameTypeValidator).tabs {
                            GameType.values().map { e ->
                                e.name.lowercase(
                                    Locale.ENGLISH
                                )
                            }
                        }.execute { sender, arena, gameType ->
                            if (gameType == GameType.REFEREEGAME && !BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
                                sender.sendLanguageMessage(language.gameTypeRefereeOnlyForPatreons)
                                return@execute
                            }

                            arena.gameType = gameType
                            arenaRepository.save(arena)
                            sender.sendLanguageMessage(language.gameRuleChangedMessage)
                            reloadArena(sender, arena)
                        }
                }
            }
            subCommand("highlight") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandHighlightToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs)
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena ->
                        setHighlights(player, arena)
                        player.sendLanguageMessage(language.toggleHighlightMessage)
                    }
            }
            subCommand("inventory") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandInventoryToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("team")
                    .validator(teamMetaValidator).tabs(teamTabs)
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena, meta ->
                        setInventory(player, arena, meta)
                    }
            }
            subCommand("armor") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandArmorToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("team")
                    .validator(teamMetaValidator).tabs(teamTabs)
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena, meta ->
                        setArmor(player, arena, meta)
                    }
            }
            subCommand("referee") {
                permission(Permission.REFEREE_JOIN)
                subCommand("startgame") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeStartGameToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        startGameReferee(player)
                    }
                }
                subCommand("stopgame") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeStopGameToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        stopGameReferee(player)
                    }
                }
                subCommand("setball") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeSetBallToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        setBallToPlayerLocation(player)
                    }.argument("x").validator(doubleValidator).tabs { listOf("<x>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x ->
                            setBallToPlayerLocation(player, x)
                        }
                        .argument("y").validator(doubleValidator).tabs { listOf("<y>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x, y ->
                            setBallToPlayerLocation(player, x, y)
                        }
                        .argument("z").validator(doubleValidator).tabs { listOf("<z>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x, y, z ->
                            setBallToPlayerLocation(player, x, y, z)
                        }
                        .argument("yaw").validator(doubleValidator).tabs { listOf("<yaw>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x, y, z, yaw ->
                            setBallToPlayerLocation(player, x, y, z, yaw)
                        }
                        .argument("pitch").validator(doubleValidator).tabs { listOf("<pitch>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x, y, z, yaw, pitch ->
                            setBallToPlayerLocation(player, x, y, z, yaw, pitch)
                        }
                        .argument("world").validator(worldValidator).tabs(worldTabs)
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, x, y, z, yaw, pitch, wordl ->
                            setBallToPlayerLocation(player, x, y, z, yaw, pitch, wordl)
                        }
                }
                subCommand("setballrel") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeSetBallToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        setBallToPlayerLocation(player)
                    }.argument("forward").validator(doubleValidator).tabs { listOf("<forward>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, forward ->
                            setBallRelativeToPlayerLocation(player, forward, 0.0)
                        }
                        .argument("sideward").validator(doubleValidator).tabs { listOf("<sideward>") }
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, forward, sideward ->
                            setBallRelativeToPlayerLocation(player, forward, sideward)
                        }
                }
                subCommand("whistleresume") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeWhistleResumeToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        whistleRefereeResume(player)
                    }
                }
                subCommand("whistlestop") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeWhistleStopToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        whistleRefereeStop(player)
                    }
                }
                subCommand("freezetime") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeFreezeTimeToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        freezeTimeReferee(player)
                    }
                }
                subCommand("nextperiod") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeNextPeriodToolTip.text }
                    builder().executePlayer({ language.commandSenderHasToBePlayer.text }) { player ->
                        nextPeriodReferee(player)
                    }
                }
                subCommand("kickplayer") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeKickPlayerToolTip.text }
                    builder().argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, playerToKick ->
                            kickPlayerFromGame(player, playerToKick)
                        }
                }
                subCommand("yellowcard") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeYellowCardToolTip.text }
                    builder().argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, playerToAssign ->
                            setYellowCardToPlayer(player, playerToAssign)
                        }
                }
                subCommand("redcard") {
                    permission(Permission.REFEREE_JOIN)
                    toolTip { language.commandRefereeRedCardToolTip.text }
                    builder().argument("player").validator(playerMustExist).tabs(onlinePlayerTabs)
                        .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, playerToAssign ->
                            setRedCardToPlayer(player, playerToAssign)
                        }
                }
            }
            subCommand("cloud") {
                permission(Permission.CLOUD)
                subCommand("login") {
                    permission(Permission.CLOUD)
                    toolTip { language.cloudLoginToolTip.text }
                    builder().execute { sender ->
                        try {
                            cloudService.performLoginFlow(sender)
                            sender.sendLanguageMessage(language.cloudLoginComplete)
                        } catch (e: Exception) {
                            sender.sendLanguageMessage(language.commonErrorMessage)
                            plugin.logger.log(Level.WARNING, "An error occurred during cloud login", e)
                        }
                    }
                }
                subCommand("logout") {
                    permission(Permission.CLOUD)
                    toolTip { language.cloudLogoutToolTip.text }
                    builder().execute { sender ->
                        try {
                            cloudService.performLogout(sender)
                            sender.sendLanguageMessage(language.cloudLogoutSuccess)
                        } catch (e: Exception) {
                            sender.sendLanguageMessage(language.cloudLogoutSuccess)
                        }
                    }
                }
                subCommand("demo") {
                    permission(Permission.CLOUD)
                    toolTip { language.cloudDemoDataToolTip.text }
                    builder().execute { sender ->
                        try {
                            sender.sendLanguageMessage(language.cloudDemoDataStart)
                            val cloudGames = withContext(Dispatchers.IO) {
                                val rawData = plugin.getResource("cloud/cloud_demo_data.json")!!.readBytes()
                                    .toString(Charsets.UTF_8)
                                val objectMapper = ObjectMapper()
                                objectMapper.readValue(rawData, object : TypeReference<List<CloudGame>>() {})
                            }

                            for (cloudGame in cloudGames) {
                                cloudService.publishGameStats(cloudGame)
                            }

                            sender.sendLanguageMessage(language.cloudDemoDataCompleted)
                        } catch (e: Exception) {
                            sender.sendLanguageMessage(language.commonErrorMessage)
                            plugin.logger.log(Level.WARNING, "An error occurred during cloud upload", e)
                        }
                    }
                }
            }
            subCommand("placeholder") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandPlaceHolderToolTip.text }
                builder().argument("placeholder").tabs { listOf("<>") }.execute { sender, placeHolder ->
                    val evaluatedValue = placeHolderService.resolvePlaceHolder(placeHolder, null)
                    sender.sendLanguageMessage(language.commandPlaceHolderMessage, evaluatedValue)
                }.executePlayer({ language.commandSenderHasToBePlayer.text }) { player, placeHolder ->
                    val evaluatedValue = placeHolderService.resolvePlaceHolder(placeHolder, player)
                    player.sendLanguageMessage(language.commandPlaceHolderMessage, evaluatedValue)
                }
            }
            subCommand("reload") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandReloadToolTip.text }
                builder().execute { sender ->
                    reloadArena(sender, null)
                }.argument("name").validator(gameMustExistValidator).tabs(arenaTabs).execute { sender, arena ->
                    reloadArena(sender, arena)
                }
            }

        }
        mcCart.build()
    }

    private fun setYellowCardToPlayer(referee: Player, playerToAssign: Player) {
        val game = gameService.getByPlayer(referee) ?: return
        val storage = game.ingamePlayersStorage[playerToAssign]
        if (storage == null) {
            referee.sendLanguageMessage(language.commandRefereeYellowCardErrorMessage, playerToAssign.name)
            return
        }

        storage.yellowCards++

        for (player in game.getPlayers()) {
            player.sendLanguageMessage(language.commandRefereeYellowCardSuccessMessage, playerToAssign.name)
        }
    }

    private fun setRedCardToPlayer(referee: Player, playerToAssign: Player) {
        val game = gameService.getByPlayer(referee) ?: return
        val storage = game.ingamePlayersStorage[playerToAssign]
        if (storage == null) {
            referee.sendLanguageMessage(language.commandRefereeRedCardErrorMessage, playerToAssign.name)
            return
        }

        storage.redCards++

        for (player in game.getPlayers()) {
            player.sendLanguageMessage(language.commandRefereeRedCardSuccessMessage, playerToAssign.name)
        }
    }

    private fun kickPlayerFromGame(referee: Player, playerToKick: Player) {
        val game = gameService.getByPlayer(referee) ?: return
        if (gameService.getByPlayer(playerToKick) != game) {
            referee.sendLanguageMessage(language.commandRefereeKickPlayerErrorMessage, playerToKick.name)
            return
        }
        for (player in game.getPlayers()) {
            player.sendLanguageMessage(language.commandRefereeKickPlayerSuccessMessage, playerToKick.name)
        }
        game.leave(playerToKick)
    }

    private fun freezeTimeReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return
        val ball = game.ball

        if (game is SoccerRefereeGame) {
            game.isTimerBlockerEnabled = true
        }

        if (ball != null) {
            ball.isInteractable = false
        }

        player.sendLanguageMessage(language.refereeBallDisabled)
    }

    private fun whistleRefereeStop(player: Player) {
        val game = gameService.getByPlayer(player) ?: return
        val ball = game.ball

        if (ball != null) {
            ball.isInteractable = false
        }

        player.sendLanguageMessage(language.refereeBallDisabled)
    }

    private fun whistleRefereeResume(player: Player) {
        val game = gameService.getByPlayer(player) ?: return
        val ball = game.ball

        if (game is SoccerRefereeGame) {
            game.isTimerBlockerEnabled = false
        }

        if (ball != null) {
            ball.isInteractable = true
        }

        player.sendLanguageMessage(language.refereeBallEnabled)
    }

    private fun nextPeriodReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return

        if (game is SoccerRefereeGame) {
            game.switchToNextMatchTime()
        }
    }

    private suspend fun setBallRelativeToPlayerLocation(player: Player, forward: Double, sideWard: Double) {
        val game = gameService.getByPlayer(player) ?: return
        val target = withContext(plugin.entityDispatcher(player)) {
            player.location.toVector3d().addRelativeFront(forward).addRelativeLeft(sideWard).toLocation()
        }
        game.setBallToLocation(target)
    }

    private suspend fun setBallToPlayerLocation(
        player: Player,
        x: Double? = null,
        y: Double? = null,
        z: Double? = null,
        yaw: Double? = null,
        pitch: Double? = null,
        world: World? = null
    ) {
        val game = gameService.getByPlayer(player) ?: return
        val location = withContext(plugin.entityDispatcher(player)) {
            player.location.clone()
        }

        if (x != null) {
            location.x = x
        }

        if (y != null) {
            location.y = y
        }

        if (z != null) {
            location.z = z
        }

        if (yaw != null) {
            location.yaw = yaw.toFloat()
        }

        if (pitch != null) {
            location.pitch = pitch.toFloat()
        }

        if (world != null) {
            location.world = world
        }

        game.setBallToLocation(location)
    }

    private fun stopGameReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return

        if (game is SoccerRefereeGame) {
            game.stopGame()
            player.sendLanguageMessage(language.refereeStoppedGame)
        }
    }

    private fun startGameReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return

        if (game is SoccerRefereeGame) {
            game.setLobbyCountdownActive(true)
            game.isTimerBlockerEnabled = true
            player.sendLanguageMessage(language.refereeStartedGame)
        }
    }

    private suspend fun createArena(sender: CommandSender, name: String, displayName: String) {
        val arena = SoccerArena()
        arena.name = name
        arena.displayName = displayName

        val items = arrayOf(
            Item("minecraft:leather_boots,301"),
            Item("minecraft:leather_leggings,300"),
            Item("minecraft:leather_chestplate,299"),
            null
        )

        arena.meta.redTeamMeta.armor = mapItemsToColoredSerializedItems(items, "16711680")
        arena.meta.blueTeamMeta.armor = mapItemsToColoredSerializedItems(items, "255")
        arena.meta.refereeTeamMeta.armor = mapItemsToColoredSerializedItems(items, "16777215")

        arenaRepository.save(arena)
        sender.sendLanguageMessage(language.gameCreatedMessage, name)
    }


    private suspend fun copyArena(sender: CommandSender, source: SoccerArena, name: String, displayName: String) {
        arenaRepository.clearCache()
        arenaRepository.getAll()
        source.name = name
        source.displayName = displayName
        source.enabled = false
        source.ballSpawnPoint = null
        source.corner1 = null
        source.corner2 = null
        source.meta.redTeamMeta.goal.corner1 = null
        source.meta.redTeamMeta.goal.corner2 = null
        source.meta.blueTeamMeta.goal.corner1 = null
        source.meta.blueTeamMeta.goal.corner2 = null

        arenaRepository.save(source)
        sender.sendLanguageMessage(language.gameCreatedMessage, name)
    }

    private fun mapItemsToColoredSerializedItems(items: Array<Item?>, color: String): Array<String?> {
        return items.map { e ->
            if (e != null) {
                itemService.toItemStack(
                    e.copy(
                        nbt = "{display:{color:$color}}", component = "{\"minecraft:dyed_color\":$color}"
                    )
                )
            } else {
                null
            }
        }.map { itemService.serializeItemStack(it) }.toTypedArray()
    }

    private suspend fun deleteArena(sender: CommandSender, arena: SoccerArena) {
        arenaRepository.delete(arena)
        arenaRepository.clearCache()
        gameService.reloadAll()

        withContext(Dispatchers.IO) {
            val htmlFile = plugin.dataFolder.resolve("stats/templates/${arena.name}_summary.html")
            if (htmlFile.exists()) {
                htmlFile.delete()
            }
            val yamlFile = plugin.dataFolder.resolve("stats/templates/${arena.name}_summary.yml")
            if (yamlFile.exists()) {
                yamlFile.delete()
            }
        }

        sender.sendLanguageMessage(language.deletedGameMessage, arena.name)
    }

    private suspend fun toggleGame(sender: CommandSender, arena: SoccerArena) {
        try {
            arena.enabled = !arena.enabled
            gameService.reload(arena)
            sender.sendLanguageMessage(language.enabledArenaMessage, arena.enabled.toString())
        } catch (e: SoccerGameException) {
            arena.enabled = false
            sender.sendLanguageMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
            return
        }
        arenaRepository.save(arena)
        sender.sendLanguageMessage(language.reloadedGameMessage, arena.name)
    }

    private suspend fun setInventory(player: Player, arena: SoccerArena, teamMetadata: TeamMeta) {
        teamMetadata.inventory = withContext(plugin.entityDispatcher(player)) {
            player.inventory.contents.clone().map { e -> itemService.serializeItemStack(e) }.toTypedArray()
        }
        arenaRepository.save(arena)
        player.sendLanguageMessage(language.updatedInventoryMessage)
    }

    private suspend fun setArmor(player: Player, arena: SoccerArena, teamMeta: TeamMeta) {
        teamMeta.armor = withContext(plugin.entityDispatcher(player)) {
            player.inventory.armorContents.clone().map { e -> itemService.serializeItemStack(e) }.toTypedArray()
        }
        arenaRepository.save(arena)
        player.sendLanguageMessage(language.updatedArmorMessage)
    }

    private fun CommandBuilder.permission(permission: Permission) {
        this.permission(permission.permission)
    }

    private suspend fun listArena(sender: CommandSender) {
        val existingArenas = arenaRepository.getAll()

        val headerBuilder = StringBuilder()
        headerBuilder.append(org.bukkit.ChatColor.GRAY)
        headerBuilder.append(org.bukkit.ChatColor.STRIKETHROUGH)
        for (i in 0 until (30 - plugin.name.length) / 2) {
            headerBuilder.append(" ")
        }
        headerBuilder.append(org.bukkit.ChatColor.RESET)
        headerBuilder.append(org.bukkit.ChatColor.WHITE)
        headerBuilder.append(org.bukkit.ChatColor.BOLD)
        headerBuilder.append(plugin.name)
        headerBuilder.append(org.bukkit.ChatColor.RESET)
        headerBuilder.append(org.bukkit.ChatColor.GRAY)
        headerBuilder.append(org.bukkit.ChatColor.STRIKETHROUGH)
        for (i in 0 until (30 - plugin.name.length) / 2) {
            headerBuilder.append(" ")
        }
        sender.sendMessage(headerBuilder.toString())
        for (arena in existingArenas) {
            if (arena.enabled) {
                sender.sendMessage(
                    ChatColor.YELLOW.toString() + arena.name + " [${arena.displayName.translateChatColors()}]" + ChatColor.GOLD.toString() + " [" + arena.gameType.name.lowercase(
                        Locale.ENGLISH
                    ) + "] " + ChatColor.GREEN + "[enabled]"
                )
            } else {
                sender.sendMessage(
                    ChatColor.YELLOW.toString() + arena.name + " [${arena.displayName.translateChatColors()}]" + ChatColor.GOLD.toString() + " [" + arena.gameType.name.lowercase(
                        Locale.ENGLISH
                    ) + "] " + ChatColor.RED + "[disabled]"
                )
            }

            sender.sendMessage()
        }

        val footerBuilder = java.lang.StringBuilder()
        footerBuilder.append(org.bukkit.ChatColor.GRAY)
        footerBuilder.append(org.bukkit.ChatColor.STRIKETHROUGH)
        footerBuilder.append("               ")
        footerBuilder.append(org.bukkit.ChatColor.RESET)
        footerBuilder.append(org.bukkit.ChatColor.WHITE)
        footerBuilder.append(org.bukkit.ChatColor.BOLD)
        footerBuilder.append("1/1")
        footerBuilder.append(org.bukkit.ChatColor.RESET)
        footerBuilder.append(org.bukkit.ChatColor.GRAY)
        footerBuilder.append(org.bukkit.ChatColor.STRIKETHROUGH)
        footerBuilder.append("               ")
        sender.sendMessage(footerBuilder.toString())
    }

    private fun joinGame(
        sender: CommandSender,
        player: Player,
        name: String,
        team: Team? = null,
        retry: Boolean = true
    ): Boolean {
        for (game in gameService.getAll()) {
            if (game.getPlayers().contains(player)) {
                if (game.arena.name.equals(name, true)) {
                    // It is the same game.
                    val previousTeam = game.ingamePlayersStorage[player]!!.team

                    if (game.status == GameState.JOINABLE && team != null && previousTeam != team) {
                        // Switching teams.
                        game.leave(player)

                        val joinResult = if (retry) {
                            joinGame(sender, player, name, team, false)
                        } else {
                            false
                        }
                        if (!joinResult) {
                            game.join(player, previousTeam)
                            return false
                        }
                        return true
                    }

                    return false
                }

                game.leave(player)
            }
        }

        val game = gameService.getByName(name)

        if (game == null) {
            sender.sendLanguageMessage(language.gameDoesNotExistMessage, name)
            return false
        }

        if (!sender.hasPermission(
                Permission.JOIN.permission.replace(
                    "[name]", game.arena.name
                )
            ) && !sender.hasPermission(Permission.JOIN.permission.replace("[name]", "*"))
        ) {
            sender.sendLanguageMessage(language.noPermissionForGameMessage, game.arena.name)
            return false
        }

        if (team != null && team == Team.REFEREE) {
            if (game !is SoccerRefereeGame) {
                sender.sendLanguageMessage(language.gameIsNotARefereeGame)
                return false
            }

            if (!sender.hasPermission(Permission.REFEREE_JOIN.permission)) {
                sender.sendLanguageMessage(language.noPermissionForGameMessage, game.arena.name)
                return false
            }
        }

        val joinResult = game.join(player, team)

        if (team != null && joinResult == JoinResult.TEAM_FULL && retry) {
            if (team == Team.BLUE) {
                return joinGame(sender, player, name, Team.RED, false)
            } else {
                return joinGame(sender, player, name, Team.BLUE, false)
            }
        }

        if (joinResult == JoinResult.TEAM_FULL || joinResult == JoinResult.GAME_ALREADY_RUNNING) {
            sender.sendLanguageMessage(language.gameIsFullMessage)
            return false
        }

        if (joinResult == JoinResult.SUCCESS_BLUE) {
            player.sendLanguageMessage(language.joinTeamBlueMessage)
        } else if (joinResult == JoinResult.SUCCESS_RED) {
            player.sendLanguageMessage(language.joinTeamRedMessage)
        } else if (joinResult == JoinResult.SUCCESS_REFEREE) {
            player.sendLanguageMessage(language.joinTeamRefereeMessage)
        }
        return true
    }

    private fun leaveGame(player: Player) {
        var leftGame = false

        for (game in gameService.getAll()) {
            if (game.getPlayers().contains(player)) {
                game.leave(player)
                leftGame = true
            }
        }

        if (leftGame) {
            player.sendLanguageMessage(language.leftGameMessage)
        }
    }

    private suspend fun setLocation(player: Player, arena: SoccerArena, locationType: LocationType) {
        val playerLocation = withContext(plugin.entityDispatcher(player)) {
            player.location.toVector3d()
        }

        if (locationType == LocationType.BALL) {
            arena.ballSpawnPoint = playerLocation
        } else if (locationType == LocationType.LEAVE_SPAWNPOINT) {
            arena.meta.lobbyMeta.leaveSpawnpoint = playerLocation
        } else if (locationType == LocationType.BLUE_SPAWNPOINT) {
            arena.meta.blueTeamMeta.spawnpoint = playerLocation
        } else if (locationType == LocationType.RED_SPAWNPOINT) {
            arena.meta.redTeamMeta.spawnpoint = playerLocation
        } else if (locationType == LocationType.REFEREE_SPAWNPOINT) {
            arena.meta.refereeTeamMeta.spawnpoint = playerLocation
        } else if (locationType == LocationType.RED_LOBBY) {
            arena.meta.redTeamMeta.lobbySpawnpoint = playerLocation
        } else if (locationType == LocationType.BLUE_LOBBY) {
            arena.meta.blueTeamMeta.lobbySpawnpoint = playerLocation
        } else if (locationType == LocationType.REFEREE_LOBBY) {
            arena.meta.refereeTeamMeta.lobbySpawnpoint = playerLocation
        }

        arenaRepository.save(arena)
        player.sendLanguageMessage(language.selectionSetMessage, locationType.name.lowercase())
    }

    private suspend fun setSelection(player: Player, arena: SoccerArena, selectionType: SelectionType) {
        val selectionLeft = selectionService.getLeftClickLocation(player)
        val selectionRight = selectionService.getRightClickLocation(player)

        if (selectionType == SelectionType.FIELD || selectionType == SelectionType.RED_GOAL || selectionType == SelectionType.BLUE_GOAL) {
            if (selectionLeft == null) {
                player.sendLanguageMessage(language.noLeftClickSelectionMessage)
                return
            }
            if (selectionRight == null) {
                player.sendLanguageMessage(language.noRightClickSelectionMessage)
                return
            }

            if (selectionType == SelectionType.FIELD) {
                arena.corner2 = convertToOutercorner2(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.corner1 = convertToOutercorner1(selectionLeft.toVector3d(), selectionRight.toVector3d())
            } else if (selectionType == SelectionType.RED_GOAL) {
                arena.meta.redTeamMeta.goal.corner2 =
                    convertToOutercorner2(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.meta.redTeamMeta.goal.corner1 =
                    convertToOutercorner1(selectionLeft.toVector3d(), selectionRight.toVector3d())
            } else if (selectionType == SelectionType.BLUE_GOAL) {
                arena.meta.blueTeamMeta.goal.corner2 =
                    convertToOutercorner2(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.meta.blueTeamMeta.goal.corner1 =
                    convertToOutercorner1(selectionLeft.toVector3d(), selectionRight.toVector3d())
            }
        }

        arenaRepository.save(arena)
        player.sendLanguageMessage(language.selectionSetMessage, selectionType.name.lowercase())
    }

    /**
     * The block selection is not precise enough, we want to exact corner location.
     */
    private fun convertToOutercorner1(selection1: Vector3d, selection2: Vector3d): Vector3d {
        return Vector3d(
            selection1.world,
            max(selection1.x + 0.99, selection2.x + 0.99),
            max(selection1.y + 0.99, selection2.y + 0.99),
            max(selection1.z + 0.99, selection2.z + 0.99)
        )
    }

    /**
     * The block selection is not precise enough, we want to exact corner location.
     */
    private fun convertToOutercorner2(selection1: Vector3d, selection2: Vector3d): Vector3d {
        return Vector3d(
            selection1.world,
            min(selection1.x, selection2.x),
            min(selection1.y, selection2.y),
            min(selection1.z, selection2.z)
        )
    }

    private suspend fun reloadArena(sender: CommandSender, arena: SoccerArena?) {
        try {
            arenaRepository.clearCache()
        } catch (e: SoccerGameException) {
            e.arena.enabled = false
            sender.sendLanguageMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
            return
        }

        if (arena == null) {
            plugin.reloadConfig()
            plugin.reloadTranslation(language)
            plugin.logger.log(Level.INFO, "Loaded language file.")

            try {
                arenaRepository.clearCache()
                gameService.reloadAll()
            } catch (e: SoccerGameException) {
                e.arena.enabled = false
                sender.sendLanguageMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
                return
            }

            sender.sendLanguageMessage(language.reloadedAllGamesMessage)
            return
        }

        try {
            arenaRepository.clearCache()
            gameService.reload(arena)
        } catch (e: SoccerGameException) {
            sender.sendLanguageMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
            return
        }
        sender.sendLanguageMessage(language.reloadedGameMessage, arena.name)
        return
    }

    private fun setHighlights(player: Player, arena: SoccerArena) {
        if (selectionService.isHighlighting(player)) {
            selectionService.removePlayer(player)
        } else {
            selectionService.setPlayer(player) {
                val arenaHighlighted = runBlocking {
                    arenaRepository.getAll().firstOrNull { e -> e.name == arena.name }
                }

                if (arenaHighlighted == null) {
                    return@setPlayer emptyList()
                }

                val highLights = ArrayList<AreaHighlight>()
                if (arenaHighlighted.corner2 != null && arenaHighlighted.corner1 != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arenaHighlighted.corner2!!),
                            roundLocation(arenaHighlighted.corner1!!),
                            Color.BLACK.rgb,
                            "Field",
                            true
                        )
                    )
                }
                if (arenaHighlighted.meta.redTeamMeta.goal.corner2 != null && arenaHighlighted.meta.redTeamMeta.goal.corner1 != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arenaHighlighted.meta.redTeamMeta.goal.corner2!!),
                            roundLocation(arenaHighlighted.meta.redTeamMeta.goal.corner1!!),
                            Color.RED.rgb,
                            "Red"
                        )
                    )
                }
                if (arenaHighlighted.meta.blueTeamMeta.goal.corner2 != null && arenaHighlighted.meta.blueTeamMeta.goal.corner1 != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arenaHighlighted.meta.blueTeamMeta.goal.corner2!!),
                            roundLocation(arenaHighlighted.meta.blueTeamMeta.goal.corner1!!),
                            Color.BLUE.rgb,
                            "Blue"
                        )
                    )
                }
                if (arenaHighlighted.ballSpawnPoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.ballSpawnPoint!!, null, Color.pink.rgb, "Ball"
                        )
                    )
                }

                if (arenaHighlighted.meta.lobbyMeta.leaveSpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.meta.lobbyMeta.leaveSpawnpoint!!, null, Color.ORANGE.rgb, "Leave"
                        )
                    )
                }

                if (arenaHighlighted.meta.redTeamMeta.spawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.meta.redTeamMeta.spawnpoint!!, null, Color.RED.rgb, "Red Spawn"
                        )
                    )
                }

                if (arenaHighlighted.meta.blueTeamMeta.spawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.meta.blueTeamMeta.spawnpoint!!, null, Color.BLUE.rgb, "Blue Spawn"
                        )
                    )
                }

                if (arenaHighlighted.meta.redTeamMeta.lobbySpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.meta.redTeamMeta.lobbySpawnpoint!!, null, Color.RED.rgb, "Red Lobby"
                        )
                    )
                }

                if (arenaHighlighted.meta.blueTeamMeta.lobbySpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arenaHighlighted.meta.blueTeamMeta.lobbySpawnpoint!!, null, Color.BLUE.rgb, "Blue Lobby"
                        )
                    )
                }

                highLights
            }
        }
    }

    private fun roundLocation(vector3d: Vector3d): Vector3d {
        return Vector3d(
            vector3d.world, floor(vector3d.x), floor(vector3d.y), floor(vector3d.z)
        )
    }

    private fun CommandSender.sendLanguageMessage(languageItem: LanguageItem, vararg args: String) {
        val sender = this
        chatMessageService.sendLanguageMessage(sender, languageItem, *args)
    }
}
