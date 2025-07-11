package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.enumeration.PlaceHolder
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.bukkit.CoroutineTimings
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.di.DependencyInjectionModule
import com.github.shynixn.mcutils.common.language.reloadTranslation
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderService
import com.github.shynixn.mcutils.common.placeholder.PlaceHolderServiceImpl
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.worldguard.WorldGuardServiceImpl
import com.github.shynixn.shybossbar.ShyBossBarDependencyInjectionModule
import com.github.shynixn.shybossbar.contract.BossBarService
import com.github.shynixn.shybossbar.contract.ShyBossBarLanguage
import com.github.shynixn.shybossbar.entity.ShyBossBarSettings
import com.github.shynixn.shybossbar.impl.commandexecutor.ShyBossBarCommandExecutor
import com.github.shynixn.shybossbar.impl.listener.ShyBossBarListener
import com.github.shynixn.shycommandsigns.ShyCommandSignsDependencyInjectionModule
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignService
import com.github.shynixn.shycommandsigns.contract.ShyCommandSignsLanguage
import com.github.shynixn.shycommandsigns.entity.ShyCommandSignSettings
import com.github.shynixn.shycommandsigns.impl.commandexecutor.ShyCommandSignCommandExecutor
import com.github.shynixn.shycommandsigns.impl.listener.ShyCommandSignListener
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
    private lateinit var scoreboardModule: DependencyInjectionModule
    private lateinit var bossBarModule: DependencyInjectionModule
    private lateinit var signModule : DependencyInjectionModule
    private var immediateDisable = false

    companion object {
        val playerDataKey = "playerData"
        var leaderBoardKey = "leaderBoard"
        var indexKey = "[index]"
        var gameKey = "[game]"
        val patreonOnly = "PatreonOnly"
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
                Version.VERSION_1_21_R4,
                Version.VERSION_1_21_R5,
            )
        } else {
            arrayOf(
                Version.VERSION_1_21_R5,
            )
        }

        if (!Version.serverVersion.isCompatible(*versions)) {
            immediateDisable = true
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
        val placeHolderService = PlaceHolderServiceImpl(this)
        this.scoreboardModule = loadShyScoreboardModule(language, placeHolderService)
        this.bossBarModule = loadShyBossBarModule(language, placeHolderService)
        this.signModule = loadShyCommandSignsModule(language, placeHolderService)
        this.module = BlockBallDependencyInjectionModule(this, language, placeHolderService).build()

        // Connect to database
        try {
            val playerDataRepository = module.getService<PlayerDataRepository<PlayerInformation>>()
            playerDataRepository.createIfNotExist()
        } catch (e: Exception) {
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

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
        plugin.launch(object : CoroutineTimings() {}) {
            // Load Games
            val gameService = module.getService<GameService>()
            try {
                gameService.reloadAll()
            } catch (e: SoccerGameException) {
                plugin.logger.log(Level.WARNING, " Cannot start game of soccerArena ${e.arena.name}.", e)
            }

            // Enable stats
            module.getService<StatsService>().register()
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
        if (immediateDisable) {
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
        scoreboardModule.close()
        bossBarModule.close()
    }

    private fun loadShyBossBarModule(
        language: ShyBossBarLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyBossBarSettings({ s ->
            s.addPermission = "blockball.shybossbar.add"
            s.baseCommand = "blockballbossbar"
            s.checkForChangeChangeSeconds = config.getInt("bossbar.checkForChangeChangeSeconds")
            s.commandAliases = config.getStringList("commands.blockballbossbar.aliases")
            s.commandPermission = "blockball.shybossbar.command"
            s.defaultBossBars = listOf(
                "bossbar/blockball_bossbar.yml" to "blockball_bossbar.yml"
            )
            s.dynBossBarPermission = "blockball.shybossbar.bossbar."
            s.joinDelaySeconds = config.getInt("bossbar.joinDelaySeconds")
            s.reloadPermission = "blockball.shybossbar.reload"
            s.removePermission = "blockball.shybossbar.remove"
            s.setPermission = "blockball.shybossbar.set"
            s.updatePermission = "blockball.shybossbar.update"
            s.worldGuardFlag = "blockballshybossbar"
        })
        settings.reload()
        val module = ShyBossBarDependencyInjectionModule(
            this,
            settings,
            language,
            WorldGuardServiceImpl(this),
            placeHolderService
        ).build()

        // Register PlaceHolders
        com.github.shynixn.shybossbar.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService,
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyBossBarListener>(), this)

        // Register CommandExecutor
        module.getService<ShyBossBarCommandExecutor>()
        val bossBarService = module.getService<BossBarService>()
        launch {
            bossBarService.reload()
        }

        return module
    }

    private fun loadShyScoreboardModule(
        language: ShyScoreboardLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyScoreboardSettings({ s ->
            s.addPermission = "blockball.shyscoreboard.add"
            s.baseCommand = "blockballscoreboard"
            s.checkForChangeChangeSeconds = config.getInt("scoreboard.checkForChangeChangeSeconds")
            s.commandAliases = config.getStringList("commands.blockballscoreboard.aliases")
            s.commandPermission = "blockball.shyscoreboard.command"
            s.defaultScoreboards = listOf(
                "scoreboard/blockball_scoreboard.yml" to "blockball_scoreboard.yml"
            )
            s.dynScoreboardPermission = "blockball.shyscoreboard.scoreboard."
            s.joinDelaySeconds = config.getInt("scoreboard.joinDelaySeconds")
            s.reloadPermission = "blockball.shyscoreboard.reload"
            s.removePermission = "blockball.shyscoreboard.remove"
            s.setPermission = "blockball.shyscoreboard.set"
            s.updatePermission = "blockball.shyscoreboard.update"
            s.worldGuardFlag = "blockballscoreboard"
        })
        settings.reload()
        val module =
            ShyScoreboardDependencyInjectionModule(
                this,
                settings,
                language,
                WorldGuardServiceImpl(this),
                placeHolderService
            ).build()

        // Register PlaceHolders
        com.github.shynixn.shyscoreboard.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService
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

    private fun loadShyCommandSignsModule(
        language: ShyCommandSignsLanguage,
        placeHolderService: PlaceHolderService
    ): DependencyInjectionModule {
        val settings = ShyCommandSignSettings({ s ->
            s.addPermission = "blockball.shycommandsigns.add"
            s.baseCommand = "blockballsign"
            s.commandAliases = config.getStringList("commands.blockballsign.aliases")
            s.commandPermission = "blockball.shycommandsigns.command"
            s.coolDownTicks = config.getInt("sign.clickCooldownTicks")
            s.defaultSigns = listOf(
                "sign/blockball_join_sign.yml" to "blockball_join_sign.yml",
                "sign/blockball_join_red_sign.yml" to "blockball_join_red_sign.yml",
                "sign/blockball_join_blue_sign.yml" to "blockball_join_blue_sign.yml",
                "sign/blockball_leave_sign.yml" to "blockball_leave_sign.yml"
            )
            s.reloadPermission = "blockball.shycommandsigns.reload"
        })
        settings.reload()
        val module =
            ShyCommandSignsDependencyInjectionModule(
                this,
                settings,
                language,
                placeHolderService
            ).build()

        // Register PlaceHolders
        com.github.shynixn.shycommandsigns.enumeration.PlaceHolder.registerAll(
            this,
            placeHolderService
        )

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(module.getService<ShyCommandSignListener>(), this)

        // Register CommandExecutor
        module.getService<ShyCommandSignCommandExecutor>()
        val signService = module.getService<ShyCommandSignService>()
        launch {
            signService.reload()
        }
        return module
    }
}
