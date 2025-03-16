package com.github.shynixn.blockball.impl.commandexecutor

import com.github.shynixn.blockball.BlockBallDependencyInjectionModule
import com.github.shynixn.blockball.contract.BlockBallLanguage
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerRefereeGame
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.entity.TeamMeta
import com.github.shynixn.blockball.enumeration.*
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.*
import com.github.shynixn.mcutils.common.chat.ChatMessageService
import com.github.shynixn.mcutils.common.command.CommandBuilder
import com.github.shynixn.mcutils.common.command.CommandMeta
import com.github.shynixn.mcutils.common.command.CommandType
import com.github.shynixn.mcutils.common.command.Validator
import com.github.shynixn.mcutils.common.item.Item
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.language.sendPluginMessage
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.common.selection.AreaHighlight
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.sign.SignService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.awt.Color
import java.util.*
import java.util.logging.Level
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class BlockBallCommandExecutor(
    private val arenaRepository: CacheRepository<SoccerArena>,
    private val gameService: GameService,
    private val plugin: Plugin,
    private val language: BlockBallLanguage,
    private val signService: SignService,
    private val selectionService: AreaSelectionService,
    private val placeHolderService: PlaceHolderService,
    private val itemService: ItemService,
    chatMessageService: ChatMessageService
) {
    private val arenaTabs: suspend (s: CommandSender) -> List<String> = {
        arenaRepository.getAll().map { e -> e.name }
    }
    private val onlinePlayerTabs: (suspend (CommandSender) -> List<String>) = {
        Bukkit.getOnlinePlayers().map { e -> e.name }
    }
    private val worldTabs: (suspend (CommandSender) -> List<String>) = {
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
            return language.playerNotFoundMessage.text.format(openArgs[0])
        }
    }

    private val teamTabs: suspend (s: CommandSender) -> List<String> = {
        val tabs = ArrayList<Team>()
        tabs.add(Team.RED)
        tabs.add(Team.BLUE)

        if (it.hasPermission(Permission.REFEREE_JOIN.permission)) {
            tabs.add(Team.REFEREE)
        }

        tabs.map { e -> e.name.lowercase(Locale.ENGLISH) }
    }
    private val coroutineExecutor = object : CoroutineExecutor {
        override fun execute(f: suspend () -> Unit) {
            plugin.launch {
                f.invoke()
            }
        }
    }

    private val remainingStringValidator = object : Validator<String> {
        override suspend fun transform(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return openArgs.joinToString(" ")
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
            return language.gameAlreadyExistsMessage.text.format(openArgs[0])
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
            return language.gameDoesNotExistMessage.text.format(openArgs[0])
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
            return language.teamDoesNotExistMessage.text.format(openArgs[0])
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
            return language.teamDoesNotExistMessage.text.format(openArgs[0])
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

    private val signTypeValidator = object : Validator<SignType> {
        override suspend fun transform(
            sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>
        ): SignType? {
            return SignType.values().firstOrNull { e -> e.name.equals(openArgs[0], true) }
        }

        override suspend fun message(sender: CommandSender, prevArgs: List<Any>, openArgs: List<String>): String {
            return language.signTypeDoesNotExistMessage.text
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
        val mcCart = CommandBuilder(plugin, coroutineExecutor, "blockball", chatMessageService) {
            usage(language.commandUsage.text.translateChatColors())
            description(language.commandDescription.text)
            aliases(plugin.config.getStringList("commands.blockball.aliases"))
            permission(Permission.COMMAND)
            permissionMessage(language.noPermissionMessage.text.translateChatColors())
            subCommand("create") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandCreateToolTip.text }
                builder().argument("name").validator(maxLengthValidator).validator(maxLengthValidator)
                    .validator(gameMustNotExistValidator).tabs { listOf("<name>") }.argument("displayName")
                    .validator(remainingStringValidator).tabs { listOf("<displayName>") }
                    .execute { sender, name, displayName -> createArena(sender, name, displayName) }
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
                    player.sendPluginMessage(language.axeReceivedMessage)
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
                                sender.sendPluginMessage(language.gameTypeRefereeOnlyForPatreons)
                                return@execute
                            }

                            arena.gameType = gameType
                            arenaRepository.save(arena)
                            sender.sendPluginMessage(language.gameRuleChangedMessage)
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
                        player.sendPluginMessage(language.toggleHighlightMessage)
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
            subCommand("sign") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandSignToolTip.text }
                builder().argument("name").validator(gameMustExistValidator).tabs(arenaTabs).argument("type")
                    .validator(signTypeValidator).tabs { listOf("join", "leave") }
                    .executePlayer({ language.commandSenderHasToBePlayer.text }) { player, arena, signType ->
                        setSign(player, arena, signType)
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
            }
            subCommand("placeholder") {
                permission(Permission.EDIT_GAME)
                toolTip { language.commandPlaceHolderToolTip.text }
                builder().argument("placeholder").tabs { listOf("<>") }.execute { sender, placeHolder ->
                    val evaluatedValue = placeHolderService.resolvePlaceHolder(placeHolder, null)
                    sender.sendPluginMessage(language.commandPlaceHolderMessage, evaluatedValue)
                }.executePlayer({ language.commandSenderHasToBePlayer.text }) { player, placeHolder ->
                    val evaluatedValue = placeHolderService.resolvePlaceHolder(placeHolder, player)
                    player.sendPluginMessage(language.commandPlaceHolderMessage, evaluatedValue)
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

    private fun freezeTimeReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return
        val ball = game.ball

        if (game is SoccerRefereeGame) {
            game.isTimerBlockerEnabled = true
        }

        if (ball != null) {
            ball.isInteractable = false
        }

        player.sendPluginMessage(language.refereeBallDisabled)
    }

    private fun whistleRefereeStop(player: Player) {
        val game = gameService.getByPlayer(player) ?: return
        val ball = game.ball

        if (ball != null) {
            ball.isInteractable = false
        }

        player.sendPluginMessage(language.refereeBallDisabled)
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

        player.sendPluginMessage(language.refereeBallEnabled)
    }

    private fun nextPeriodReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return

        if (game is SoccerRefereeGame) {
            game.switchToNextMatchTime()
        }
    }

    private fun setBallRelativeToPlayerLocation(player: Player, forward: Double, sideWard: Double) {
        val game = gameService.getByPlayer(player) ?: return
        val target = player.location.toVector3d().addRelativeFront(forward).addRelativeLeft(sideWard).toLocation()
        game.setBallToLocation(target)
    }

    private fun setBallToPlayerLocation(
        player: Player,
        x: Double? = null,
        y: Double? = null,
        z: Double? = null,
        yaw: Double? = null,
        pitch: Double? = null,
        world: World? = null
    ) {
        val game = gameService.getByPlayer(player) ?: return
        val location = player.location.clone()

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
            player.sendPluginMessage(language.refereeStoppedGame)
        }
    }

    private fun startGameReferee(player: Player) {
        val game = gameService.getByPlayer(player) ?: return

        if (game is SoccerRefereeGame) {
            game.setLobbyCountdownActive(true)
            game.isTimerBlockerEnabled = true
            player.sendPluginMessage(language.refereeStartedGame)
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
        createStatsFile(name)
        sender.sendPluginMessage(language.gameCreatedMessage, name)
    }

    private suspend fun createStatsFile(name: String) {
        var hasCreatedFile = false
        withContext(Dispatchers.IO) {
            val yamlContent = plugin.getResource("stats/game_page.yml")!!.readBytes().toString(Charsets.UTF_8)
            val yamlFile = plugin.dataFolder.resolve("stats/templates/${name}_page.yml")

            if (!yamlFile.exists()) {
                yamlFile.writeText(replaceStatsPlaceHolders(yamlContent, name))
                hasCreatedFile = true
            }

            val htmlContent = plugin.getResource("stats/game_page.html")!!.readBytes().toString(Charsets.UTF_8)
            val htmlFile = plugin.dataFolder.resolve("stats/templates/${name}_page.html")
            if (!htmlFile.exists()) {
                htmlFile.writeText(replaceStatsPlaceHolders(htmlContent, name))
                hasCreatedFile = true
            }
        }

        if (hasCreatedFile) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "blockballstats reload")
        }
    }

    private fun replaceStatsPlaceHolders(content: String, name: String): String {
        return content.replace("###name###", "${name}_page")
            .replace("###displayName###", "%blockball_game_displayName_$name%")
            .replace("###teamRed###", "%blockball_game_redDisplayName_$name%")
            .replace("###teamRedScore###", "%blockball_game_redScore_$name%")
            .replace("###teamBlue###", "%blockball_game_blueDisplayName_$name%")
            .replace("###teamBlueScore###", "%blockball_game_blueScore_$name%")
            .replace("###publishName###", "blockball_${name}")
            .replace("###teamRedPlayerNames###", "%blockball_game_redPlayerNames_$name%")
            .replace("###teamBluePlayerNames###", "%blockball_game_bluePlayerNames_$name%")
            .replace("###topGoalsCurrentPlayerName###", "%blockball_game_topGoalsCurrentPlayerName_$name%")
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
        }.map {
            val yamlConfiguration = YamlConfiguration()
            yamlConfiguration.set("item", it)
            yamlConfiguration.saveToString()
        }.toTypedArray()
    }

    private suspend fun deleteArena(sender: CommandSender, arena: SoccerArena) {
        arenaRepository.delete(arena)
        arenaRepository.clearCache()
        val runningGame = gameService.getAll().firstOrNull { e -> e.arena.name.equals(arena.name, true) }
        runningGame?.close()

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

        sender.sendPluginMessage(language.deletedGameMessage, arena.name)
    }

    private suspend fun toggleGame(sender: CommandSender, arena: SoccerArena) {
        try {
            arena.enabled = !arena.enabled
            gameService.reload(arena)

            if (arena.enabled) {
                createStatsFile(arena.name)
            }

            sender.sendPluginMessage(language.enabledArenaMessage, arena.enabled.toString())
        } catch (e: SoccerGameException) {
            arena.enabled = false
            sender.sendPluginMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
            return
        }
        arenaRepository.save(arena)
        sender.sendPluginMessage(language.reloadedGameMessage, arena.name)
    }

    private suspend fun setInventory(player: Player, arena: SoccerArena, teamMetadata: TeamMeta) {
        teamMetadata.inventory = player.inventory.contents.clone().map { e ->
            val yamlConfiguration = YamlConfiguration()
            yamlConfiguration.set("item", e)
            yamlConfiguration.saveToString()
        }.toTypedArray()
        arenaRepository.save(arena)
        player.sendPluginMessage(language.updatedInventoryMessage)
    }

    private suspend fun setArmor(player: Player, arena: SoccerArena, teamMeta: TeamMeta) {
        teamMeta.armor = player.inventory.armorContents.clone().map { e ->
            val yamlConfiguration = YamlConfiguration()
            yamlConfiguration.set("item", e)
            yamlConfiguration.saveToString()
        }.toTypedArray()
        arenaRepository.save(arena)
        player.sendPluginMessage(language.updatedArmorMessage)
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

    private fun joinGame(sender: CommandSender, player: Player, name: String, team: Team? = null): Boolean {
        for (game in gameService.getAll()) {
            if (game.getPlayers().contains(player)) {
                if (game.arena.name.equals(name, true)) {
                    // It is the same game.
                    val previousTeam = game.ingamePlayersStorage[player]!!.team

                    if (game.status == GameState.JOINABLE && team != null && previousTeam != team) {
                        // Switching teams.
                        game.leave(player)
                        val joinResult = joinGame(sender, player, name, team)

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
            sender.sendPluginMessage(language.gameDoesNotExistMessage, name)
            return false
        }

        if (!sender.hasPermission(
                Permission.JOIN.permission.replace(
                    "[name]", game.arena.name
                )
            ) && !sender.hasPermission(Permission.JOIN.permission.replace("[name]", "*"))
        ) {
            sender.sendPluginMessage(language.noPermissionForGameMessage, game.arena.name)
            return false
        }

        if (team != null && team == Team.REFEREE) {
            if (game !is SoccerRefereeGame) {
                sender.sendPluginMessage(language.gameIsNotARefereeGame)
                return false
            }

            if (!sender.hasPermission(Permission.REFEREE_JOIN.permission)) {
                sender.sendPluginMessage(language.noPermissionForGameMessage, game.arena.name)
                return false
            }
        }

        val joinResult = game.join(player, team)

        if (team != null && joinResult == JoinResult.TEAM_FULL) {
            if (team == Team.BLUE) {
                return joinGame(sender, player, name, Team.RED)
            } else {
                return joinGame(sender, player, name, Team.BLUE)
            }
        }

        if (joinResult == JoinResult.GAME_FULL || joinResult == JoinResult.GAME_ALREADY_RUNNING) {
            sender.sendPluginMessage(language.gameIsFullMessage)
            return false
        }

        if (joinResult == JoinResult.SUCCESS_BLUE) {
            player.sendPluginMessage(language.joinTeamBlueMessage)
        } else if (joinResult == JoinResult.SUCCESS_RED) {
            player.sendPluginMessage(language.joinTeamRedMessage)
        } else if (joinResult == JoinResult.SUCCESS_REFEREE) {
            player.sendPluginMessage(language.joinTeamRefereeMessage)
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
            player.sendPluginMessage(language.leftGameMessage)
        }
    }

    private suspend fun setLocation(player: Player, arena: SoccerArena, locationType: LocationType) {
        if (locationType == LocationType.BALL) {
            arena.meta.ballMeta.spawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.LEAVE_SPAWNPOINT) {
            arena.meta.lobbyMeta.leaveSpawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.BLUE_SPAWNPOINT) {
            arena.meta.blueTeamMeta.spawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.RED_SPAWNPOINT) {
            arena.meta.redTeamMeta.spawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.REFEREE_SPAWNPOINT) {
            arena.meta.refereeTeamMeta.spawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.RED_LOBBY) {
            arena.meta.redTeamMeta.lobbySpawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.BLUE_LOBBY) {
            arena.meta.blueTeamMeta.lobbySpawnpoint = player.location.toVector3d()
        } else if (locationType == LocationType.REFEREE_LOBBY) {
            arena.meta.refereeTeamMeta.lobbySpawnpoint = player.location.toVector3d()
        }

        arenaRepository.save(arena)
        player.sendPluginMessage(language.selectionSetMessage, locationType.name.lowercase())
    }

    private suspend fun setSelection(player: Player, arena: SoccerArena, selectionType: SelectionType) {
        val selectionLeft = selectionService.getLeftClickLocation(player)
        val selectionRight = selectionService.getRightClickLocation(player)

        if (selectionType == SelectionType.FIELD || selectionType == SelectionType.RED_GOAL || selectionType == SelectionType.BLUE_GOAL) {
            if (selectionLeft == null) {
                player.sendPluginMessage(language.noLeftClickSelectionMessage)
                return
            }
            if (selectionRight == null) {
                player.sendPluginMessage(language.noRightClickSelectionMessage)
                return
            }

            if (selectionType == SelectionType.FIELD) {
                arena.lowerCorner = convertToOuterLowerCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.upperCorner = convertToOuterUpperCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
            } else if (selectionType == SelectionType.RED_GOAL) {
                arena.meta.redTeamMeta.goal.lowerCorner =
                    convertToOuterLowerCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.meta.redTeamMeta.goal.upperCorner =
                    convertToOuterUpperCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
            } else if (selectionType == SelectionType.BLUE_GOAL) {
                arena.meta.blueTeamMeta.goal.lowerCorner =
                    convertToOuterLowerCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
                arena.meta.blueTeamMeta.goal.upperCorner =
                    convertToOuterUpperCorner(selectionLeft.toVector3d(), selectionRight.toVector3d())
            }
        }

        arenaRepository.save(arena)
        player.sendPluginMessage(language.selectionSetMessage, selectionType.name.lowercase())
    }

    /**
     * The block selection is not precise enough, we want to exact corner location.
     */
    private fun convertToOuterUpperCorner(selection1: Vector3d, selection2: Vector3d): Vector3d {
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
    private fun convertToOuterLowerCorner(selection1: Vector3d, selection2: Vector3d): Vector3d {
        return Vector3d(
            selection1.world,
            min(selection1.x, selection2.x),
            min(selection1.y, selection2.y),
            min(selection1.z, selection2.z)
        )
    }

    private suspend fun setSign(sender: Player, arena: SoccerArena, signType: SignType) {
        if (signType == SignType.JOIN) {
            sender.sendPluginMessage(language.rightClickOnSignMessage)
            signService.addSignByRightClick(sender) { sign ->
                sign.let {
                    it.line1 = language.joinSignLine1.text
                    it.line2 = language.joinSignLine2.text
                    it.line3 = language.joinSignLine3.text
                    it.line4 = language.joinSignLine4.text
                    it.cooldown = 20
                    it.update = 40
                    it.commands = mutableListOf(CommandMeta().also {
                        it.command = "/blockball join ${arena.name}"
                        it.type = CommandType.PER_PLAYER
                    })
                }

                if (arena.meta.lobbyMeta.joinSigns.firstOrNull { e -> e.isSameSign(sign) } == null) {
                    arena.meta.lobbyMeta.joinSigns.add(sign)
                }

                plugin.launch {
                    arenaRepository.save(arena)
                    gameService.reload(arena)
                    sender.sendPluginMessage(language.addedSignMessage)
                }
            }
        } else if (signType == SignType.LEAVE) {
            sender.sendPluginMessage(language.rightClickOnSignMessage)
            signService.addSignByRightClick(sender) { sign ->
                sign.let {
                    it.line1 = language.leaveSignLine1.text
                    it.line2 = language.leaveSignLine2.text
                    it.line3 = language.leaveSignLine3.text
                    it.line4 = language.leaveSignLine4.text
                    it.cooldown = 20
                    it.update = 40
                    it.commands = mutableListOf(CommandMeta().also {
                        it.command = "/blockball leave"
                        it.type = CommandType.PER_PLAYER
                    })
                }

                if (arena.meta.lobbyMeta.joinSigns.firstOrNull { e -> e.isSameSign(sign) } == null) {
                    arena.meta.lobbyMeta.joinSigns.add(sign)
                }

                plugin.launch {
                    arenaRepository.save(arena)
                    gameService.reload(arena)
                    sender.sendPluginMessage(language.addedSignMessage)
                }
            }
        }
    }


    private suspend fun reloadArena(sender: CommandSender, arena: SoccerArena?) {
        try {
            arenaRepository.clearCache()
        } catch (e: SoccerGameException) {
            e.arena.enabled = false
            sender.sendPluginMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
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
                sender.sendPluginMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
                return
            }

            sender.sendPluginMessage(language.reloadedAllGamesMessage)
            return
        }

        try {
            arenaRepository.clearCache()
            gameService.reload(arena)
        } catch (e: SoccerGameException) {
            sender.sendPluginMessage(language.failedToReloadMessage, e.arena.name, e.message!!)
            return
        }
        sender.sendPluginMessage(language.reloadedGameMessage, arena.name)
        return
    }

    private fun setHighlights(player: Player, arena: SoccerArena) {
        if (selectionService.isHighlighting(player)) {
            selectionService.removePlayer(player)
        } else {
            selectionService.setPlayer(player) {
                val arena = runBlocking {
                    arenaRepository.getAll().firstOrNull { e -> e.name == arena.name }
                }

                if (arena == null) {
                    return@setPlayer emptyList()
                }

                val highLights = ArrayList<AreaHighlight>()
                if (arena.lowerCorner != null && arena.upperCorner != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arena.lowerCorner!!),
                            roundLocation(arena.upperCorner!!),
                            Color.BLACK.rgb,
                            "Field",
                            true
                        )
                    )
                }
                if (arena.meta.redTeamMeta.goal.lowerCorner != null && arena.meta.redTeamMeta.goal.upperCorner != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arena.meta.redTeamMeta.goal.lowerCorner!!),
                            roundLocation(arena.meta.redTeamMeta.goal.upperCorner!!),
                            Color.RED.rgb,
                            "Red"
                        )
                    )
                }
                if (arena.meta.blueTeamMeta.goal.lowerCorner != null && arena.meta.blueTeamMeta.goal.upperCorner != null) {
                    highLights.add(
                        AreaHighlight(
                            roundLocation(arena.meta.blueTeamMeta.goal.lowerCorner!!),
                            roundLocation(arena.meta.blueTeamMeta.goal.upperCorner!!),
                            Color.BLUE.rgb,
                            "Blue"
                        )
                    )
                }
                if (arena.meta.ballMeta.spawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.ballMeta.spawnpoint!!, null, Color.pink.rgb, "Ball"
                        )
                    )
                }

                if (arena.meta.lobbyMeta.leaveSpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.lobbyMeta.leaveSpawnpoint!!, null, Color.ORANGE.rgb, "Leave"
                        )
                    )
                }

                if (arena.meta.redTeamMeta.spawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.redTeamMeta.spawnpoint!!, null, Color.RED.rgb, "Red Spawn"
                        )
                    )
                }

                if (arena.meta.blueTeamMeta.spawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.blueTeamMeta.spawnpoint!!, null, Color.BLUE.rgb, "Blue Spawn"
                        )
                    )
                }

                if (arena.meta.redTeamMeta.lobbySpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.redTeamMeta.lobbySpawnpoint!!, null, Color.RED.rgb, "Red Lobby"
                        )
                    )
                }

                if (arena.meta.blueTeamMeta.lobbySpawnpoint != null) {
                    highLights.add(
                        AreaHighlight(
                            arena.meta.blueTeamMeta.lobbySpawnpoint!!, null, Color.BLUE.rgb, "Blue Lobby"
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
}
