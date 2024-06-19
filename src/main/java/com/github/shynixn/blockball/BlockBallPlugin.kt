package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.CommandService
import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.entity.PlayerInformation
import com.github.shynixn.blockball.impl.commandexecutor.*
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.reloadTranslation
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.guice.DependencyInjectionModule
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import kotlinx.coroutines.runBlocking
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.MemorySection
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * Plugin Main.
 * @author Shynixn
 */
class BlockBallPlugin : JavaPlugin() {
    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
    }

    private val bstatsPluginId = 1317
    private lateinit var module: DependencyInjectionModule
    private var immidiateDisable = false

    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
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

        // Register Listeners
        module.getService<GameActionService>().gameService = module.getService()
        Bukkit.getPluginManager().registerEvents(module.getService<GameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<DoubleJumpListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<HubgameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<MinigameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<BungeeCordgameListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<BallListener>(), this)
        Bukkit.getPluginManager().registerEvents(module.getService<BlockSelectionListener>(), this)

        val configurationService = module.getService<ConfigurationService>()
        val enableMetrics = configurationService.findValue<Boolean>("metrics")

        // Register CommandExecutor
        val commandService = module.getService<CommandService>()
        commandService.registerCommandExecutor("blockballstop", module.getService<StopCommandExecutor>())
        commandService.registerCommandExecutor("blockballreload", module.getService<ReloadCommandExecutor>())
        commandService.registerCommandExecutor("blockball", module.getService<ArenaCommandExecutor>())
        commandService.registerCommandExecutor(
            (config.get("global-spectate") as MemorySection).getValues(false) as Map<String, String>,
            module.getService<SpectateCommandExecutor>()
        )
        commandService.registerCommandExecutor(
            (config.get("global-leave") as MemorySection).getValues(false) as Map<String, String>,
            module.getService<LeaveCommandExecutor>()
        )
        commandService.registerCommandExecutor(
            (config.get("global-join") as MemorySection).getValues(false) as Map<String, String>,
            module.getService<JoinCommandExecutor>()
        )

        if (enableMetrics) {
            Metrics(this, bstatsPluginId)
        }

        module.getService<PacketService>().registerPacketListening(PacketInType.USEENTITY)

        val plugin = this
        plugin.launch {
            val language = configurationService.findValue<String>("language")
            plugin.reloadTranslation(language, BlockBallLanguage::class.java, "en_us")
            logger.log(Level.INFO, "Loaded language file $language.properties.")

            // Load Games
            val gameService = module.getService<GameService>()
            gameService.reloadAll()

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

            val playerDataRepository = module.getService<PlayerDataRepository<PlayerInformation>>()
            for (player in Bukkit.getOnlinePlayers()) {
                playerDataRepository.getByPlayer(player)
            }

            Bukkit.getServer()
                .consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + plugin.description.version + " by Shynixn")
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

    /**
     * Shutdowns the server.
     */
    fun shutdownServer() {
        Bukkit.getServer().shutdown()
    }
}
