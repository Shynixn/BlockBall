package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.PlaceHolderService
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.StatsService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.entity.SoccerArena
import com.github.shynixn.blockball.impl.commandexecutor.BlockBallCommandExecutor
import com.github.shynixn.blockball.impl.exception.SoccerGameException
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.reloadTranslation
import com.github.shynixn.mcutils.common.repository.Repository
import com.github.shynixn.mcutils.common.selection.AreaSelectionService
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.sign.SignService
import kotlinx.coroutines.runBlocking
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*
import java.util.logging.Level

/**
 * Plugin Main.
 * @author Shynixn
 */
class BlockBallPlugin : JavaPlugin() {
    private val prefix: String = ChatColor.BLUE.toString() + "[BlockBall] "
    private val bstatsPluginId = 1317
    private lateinit var module: DependencyInjectionModule
    private var immidiateDisable = false

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
            )
        } else {
            arrayOf(
                Version.VERSION_1_21_R1,
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

        // Guice
        this.module = BlockBallDependencyInjectionModule(this).build()
        this.reloadConfig()

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
            val configurationService = module.getService<ConfigurationService>()

            // Enable Metrics
            val enableMetrics = configurationService.findValue<Boolean>("metrics")

            if (enableMetrics) {
                Metrics(plugin, bstatsPluginId)
            }

            // Load Language
            val language = configurationService.findValue<String>("language")
            try {
                plugin.reloadTranslation(language, BlockBallLanguageImpl::class.java, "en_us", "es_es")
                logger.log(Level.INFO, "Loaded language file $language.properties.")
            } catch (e: Exception) {
                // Compatibility to < 6.46.3
                Files.move(
                    plugin.dataFolder.toPath().resolve("lang").resolve("en_us.properties"),
                    plugin.dataFolder.toPath().resolve("lang")
                        .resolve("old_" + UUID.randomUUID().toString() + ".properties"),
                    StandardCopyOption.REPLACE_EXISTING
                )
                plugin.reloadTranslation(language, BlockBallLanguageImpl::class.java, "en_us", "es_es")
                logger.log(
                    Level.WARNING,
                    "Your language file is not compatible. Your existing file has been renamed and the original file has been reset."
                )
            }


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
                        resolvedText = placeHolderService.replacePlaceHolders(text, null, game)
                    }
                }

                if (resolvedText == null) {
                    resolvedText = placeHolderService.replacePlaceHolders(text)
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
    }
}
