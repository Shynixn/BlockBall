package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.sign.SignService
import com.github.shynixn.shyscoreboard.ShyScoreboardDependencyInjectionModule
import com.github.shynixn.shyscoreboard.contract.ScoreboardService
import com.github.shynixn.shyscoreboard.contract.ShyScoreboardLanguage
import com.github.shynixn.shyscoreboard.entity.ShyScoreboardSettings
import com.github.shynixn.shyscoreboard.impl.commandexecutor.ShyScoreboardCommandExecutor
import com.github.shynixn.shyscoreboard.impl.listener.ShyScoreboardListener
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * Plugin Main.
 * @author Shynixn
 */
class BlockBallPlugin : JavaPlugin() {
    private val prefix: String = ChatColor.BLUE.toString() + "[BlockBall] "
    private lateinit var module: DependencyInjectionModule
    private lateinit var scoreboardModule : DependencyInjectionModule
    private var immidiateDisable = false

    companion object {
        val playerDataKey = "playerData"
        var leaderBoardKey = "leaderBoard"
        var indexKey = "[index]"
        var gameKey = "[game]"
    }

    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Loading BlockBall ...")
        this.saveDefaultConfig()
        val versions = if (BlockBallDependencyInjectionModule.areLegacyVersionsIncluded) {
            arrayOf(
                Version.VERSION_1_8_R3,
                Version.VERSION_1_9_R2,
                Version.VERSION_1_10_R1,
                Version.VERSION_1_11_R1,
                Version.VERSION_1_12_R1,
                Version.VERSION_1_13_R2,
                Version.VERSION_1_14_R1,
                Version.VERSION_1_15_R1,
                Version.VERSION_1_16_R1,
                Version.VERSION_1_16_R2,
                Version.VERSION_1_16_R3,
                Version.VERSION_1_17_R1,
                Version.VERSION_1_18_R1,
                Version.VERSION_1_18_R2,
                Version.VERSION_1_19_R1,
                Version.VERSION_1_19_R2,
                Version.VERSION_1_19_R3,
                Version.VERSION_1_20_R1,
                Version.VERSION_1_20_R2,
                Version.VERSION_1_20_R3,
                Version.VERSION_1_20_R4,
                Version.VERSION_1_21_R1,
                Version.VERSION_1_21_R2,
                Version.VERSION_1_21_R3,
            )
        } else {
            arrayOf(
                Version.VERSION_1_21_R3,
            )
        }

        if (!Version.serverVersion.isCompatible(*versions)) {
            immidiateDisable = true
            logger.log(Level.SEVERE, "================================================")
            logger.log(Level.SEVERE, "BlockBall does not support your server version")
            logger.log(Level.SEVERE, "Install v" + versions[0].from + " - v" + versions[versions.size - 1].to)
            logger.log(Level.SEVERE, "Need support for a particular version? Go to https://www.patreon.com/Shynixn")
            logger.log(Level.SEVERE, "Plugin gets now disabled!")
            logger.log(Level.SEVERE, "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        logger.log(Level.INFO, "Loaded NMS version ${Version.serverVersion}.")

        // Load BlockBallLanguage
        val language = BlockBallLanguageImpl()
        reloadTranslation(language)
        logger.log(Level.INFO, "Loaded language file.")

        // Module
        this.scoreboardModule = loadShyScoreboardModule(language)
        this.module = BlockBallDependencyInjectionModule(this, language, this.scoreboardModule.getService()).build()

        // Register PlaceHolder
        PlaceHolder.registerAll(
            module.getService(), module.getService(), module.getService(), module.getService()
        )

        // Register Packet
        module.getService<PacketService>().registerPacketListening(PacketInType.USEENTITY)

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<GameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<DoubleJumpListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<HubgameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<MinigameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<BallListener>(), this)

        // Register CommandExecutor
        module.getService<BlockBallCommandExecutor>()

        // Service dependencies
        Bukkit.getServicesManager().register(
            SoccerBallFactory::class.java, module.getService<SoccerBallFactory>(), this, ServicePriority.Normal
        )
        Bukkit.getServicesManager()
            .register(GameService::class.java, module.getService<GameService>(), this, ServicePriority.Normal)

        val plugin = this
        plugin.launch {
            // Load Games
            val gameService = module.getService<GameService>()
            try {
                gameService.reloadAll()
            } catch (e: SoccerGameException) {
                plugin.logger.log(Level.WARNING, "Cannot start game of soccerArena ${e.arena.name}.", e)
            }

            // Connect to PlayerData Repository.
            try {
                val playerDataRepository = module.getService<PlayerDataRepository<PlayerInformation>>()
                playerDataRepository.createIfNotExist()
            } catch (e: Exception) {
                e.printStackTrace()
                immidiateDisable = true
                Bukkit.getPluginManager().disablePlugin(plugin)
                return@launch
            }

            // Enable stats
            module.getService<StatsService>().register()

            // Load Signs
            val placeHolderService = module.getService<PlaceHolderService>()
            val signService = module.getService<SignService>()
            val arenaService = module.getService<Repository<SoccerArena>>()
            signService.onSignDestroy = { signMeta ->
                plugin.launch {
                    val arenas = arenaService.getAll()
                    for (arena in arenas) {
                        for (signToRemove in arena.meta.lobbyMeta.joinSigns.filter { e -> e.isSameSign(signMeta) }) {
                            arena.meta.lobbyMeta.joinSigns.remove(signToRemove)
                            arenaService.save(arena)
                        }
                        for (signToRemove in arena.meta.lobbyMeta.leaveSigns.filter { e -> e.isSameSign(signMeta) }) {
                            arena.meta.lobbyMeta.leaveSigns.remove(signToRemove)
                            arenaService.save(arena)
                        }
                        for (signToRemove in arena.meta.redTeamMeta.teamSigns.filter { e -> e.isSameSign(signMeta) }) {
                            arena.meta.lobbyMeta.joinSigns.remove(signToRemove)
                            arenaService.save(arena)
                        }
                        for (signToRemove in arena.meta.blueTeamMeta.teamSigns.filter { e -> e.isSameSign(signMeta) }) {
                            arena.meta.lobbyMeta.leaveSigns.remove(signToRemove)
                            arenaService.save(arena)
                        }
                    }
                }
            }
            signService.onPlaceHolderResolve = { signMeta, text ->
                var resolvedText: String? = null

                if (signMeta.tag != null) {
                    val game = gameService.getByName(signMeta.tag!!)
                    if (game != null) {
                        resolvedText =
                            placeHolderService.resolvePlaceHolder(text, null, mapOf(gameKey to game.arena.name))
                    }
                }

                if (resolvedText == null) {
                    resolvedText = placeHolderService.resolvePlaceHolder(text, null)
                }

                resolvedText
            }

            val playerDataRepository = module.getService<PlayerDataRepository<PlayerInformation>>()
            for (player in Bukkit.getOnlinePlayers()) {
                playerDataRepository.getByPlayer(player)
            }

            Bukkit.getServer().consoleSender.sendMessage(prefix + ChatColor.GREEN + "Enabled BlockBall " + plugin.description.version + " by Shynixn")
        }
    }

    /**
     * Override on disable.
     */
    override fun onDisable() {
        if (immidiateDisable) {
            return
        }

        module.getService<PacketService>().close()
        module.getService<AreaSelectionService>().close()
        module.getService<StatsService>().close()

        val playerDataRepository = module.getService<CachePlayerRepository<PlayerInformation>>()
        runBlocking {
            playerDataRepository.saveAll()
            playerDataRepository.clearAll()
            playerDataRepository.close()
        }

        try {
            module.getService<GameService>().close()
        } catch (e: Exception) {
            // Ignored.
        }

        module.close()
    }

    private fun loadShyScoreboardModule(language: ShyScoreboardLanguage): DependencyInjectionModule {
        val settings = ShyScoreboardSettings({ s ->
            s.joinDelaySeconds = config.getInt("scoreboard.joinDelaySeconds")
            s.checkForPermissionChangeSeconds = config.getInt("scoreboard.checkForPermissionChangeSeconds")
            s.baseCommand = "blockballscoreboard"
            s.commandAliases = config.getStringList("commands.blockballscoreboard.aliases")
            s.commandPermission = "blockball.shyscoreboard.command"
            s.reloadPermission = "blockball.shyscoreboard.reload"
            s.dynScoreboardPermission = "blockball.shyscoreboard.scoreboard."
            s.addPermission = "blockball.shyscoreboard.add"
            s.removePermission = "blockball.shyscoreboard.remove"
            s.updatePermission = "blockball.shyscoreboard.remove"
            s.defaultScoreboards = listOf(
                "scoreboard/blockball_scoreboard.yml" to "blockball_scoreboard.yml"
            )
        })
        settings.reload()
        val module = ShyScoreboardDependencyInjectionModule(this, settings, language).build()

        // Register PlaceHolders
        com.github.shynixn.shyscoreboard.enumeration.PlaceHolder.registerAll(
            this,
            module.getService<PlaceHolderService>(),
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyScoreboardListener>(), this)

        // Register CommandExecutor
        module.getService<ShyScoreboardCommandExecutor>()
        val scoreboardService = module.getService<ScoreboardService>()
        launch {
            scoreboardService.reload()
        }

        return module
    }
}
