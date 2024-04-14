package com.github.shynixn.blockball

import com.github.shynixn.blockball.contract.CommandService
import com.github.shynixn.blockball.contract.DependencyService
import com.github.shynixn.blockball.contract.GameActionService
import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.impl.commandexecutor.*
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.ConfigurationService
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.common.reloadTranslation
import com.github.shynixn.mcutils.database.api.CachePlayerRepository
import com.github.shynixn.mcutils.database.api.PlayerDataRepository
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.google.inject.Guice
import com.google.inject.Injector
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

    private var injector: Injector? = null
    private val bstatsPluginId = 1317
    private var packetService: PacketService? = null

    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
        this.saveDefaultConfig()

        if (disableForVersion(Version.VERSION_1_8_R1, Version.VERSION_1_8_R3)) {
            return
        }

        if (disableForVersion(Version.VERSION_1_8_R2, Version.VERSION_1_8_R3)) {
            return
        }

        if (disableForVersion(Version.VERSION_1_9_R1, Version.VERSION_1_9_R2)) {
            return
        }

        if (disableForVersion(Version.VERSION_1_13_R1, Version.VERSION_1_13_R2)) {
            return
        }
        val versions = if (BlockBallDependencyInjectionBinder.areLegacyVersionsIncluded) {
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
            )
        } else {
            arrayOf(
                Version.VERSION_1_20_R3,
            )
        }

        if (!Version.serverVersion.isCompatible(*versions)) {
            logger.log(Level.SEVERE, "================================================")
            logger.log(Level.SEVERE, "BlockBall does not support your server version")
            logger.log(Level.SEVERE, "Install v" + versions[0].id + " - v" + versions[versions.size - 1].id)
            logger.log(Level.SEVERE, "Need support for a particular version? Go to https://www.patreon.com/Shynixn")
            logger.log(Level.SEVERE, "Plugin gets now disabled!")
            logger.log(Level.SEVERE, "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        logger.log(Level.INFO, "Loaded NMS version ${Version.serverVersion.bukkitId}.")

        this.packetService = PacketServiceImpl(this)
        this.injector = Guice.createInjector(BlockBallDependencyInjectionBinder(this, packetService!!))
        resolve(GameActionService::class.java).gameService = resolve(GameService::class.java)
        this.reloadConfig()

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(resolve(GameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(DoubleJumpListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(HubgameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(MinigameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BungeeCordgameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BallListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BlockSelectionListener::class.java), this)

        val dependencyService = resolve(DependencyService::class.java)
        val configurationService = resolve(ConfigurationService::class.java)

        dependencyService.checkForInstalledDependencies()

        val enableMetrics = configurationService.findValue<Boolean>("metrics")

        // Register CommandExecutor
        val commandService = resolve(CommandService::class.java)
        commandService.registerCommandExecutor("blockballstop", resolve(StopCommandExecutor::class.java))
        commandService.registerCommandExecutor("blockballreload", resolve(ReloadCommandExecutor::class.java))
        commandService.registerCommandExecutor("blockball", resolve(ArenaCommandExecutor::class.java))
        commandService.registerCommandExecutor(
            (config.get("global-spectate") as MemorySection).getValues(false) as Map<String, String>,
            resolve(SpectateCommandExecutor::class.java)
        )
        commandService.registerCommandExecutor(
            (config.get("global-leave") as MemorySection).getValues(false) as Map<String, String>,
            resolve(LeaveCommandExecutor::class.java)
        )
        commandService.registerCommandExecutor(
            (config.get("global-join") as MemorySection).getValues(false) as Map<String, String>,
            resolve(JoinCommandExecutor::class.java)
        )

        if (enableMetrics) {
            Metrics(this, bstatsPluginId)
        }

        packetService!!.registerPacketListening(PacketInType.USEENTITY)

        val plugin = this
        plugin.launch {
            val language = configurationService.findValue<String>("language")
            plugin.reloadTranslation(language, BlockBallLanguage::class.java, "en_us")
            logger.log(Level.INFO, "Loaded language file $language.properties.")

            // Load Games
            val gameService = resolve(GameService::class.java)
            gameService.reloadAll()

            // Connect to PlayerData Repository.
            try {
                val playerDataRepository = resolve(PlayerDataRepository::class.java)
                playerDataRepository.createIfNotExist()
            } catch (e: Exception) {
                e.printStackTrace()
                injector = null
                Bukkit.getPluginManager().disablePlugin(plugin)
                return@launch
            }

            val playerDataRepository = resolve(PlayerDataRepository::class.java)
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
        if (injector == null) {
            return
        }

        packetService!!.close()

        val playerDataRepository = resolve(CachePlayerRepository::class.java)
        runBlocking {
            playerDataRepository.saveAll()
            playerDataRepository.clearAll()
            playerDataRepository.close()
        }

        try {
            resolve(GameService::class.java).close()
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

    /**
     * Gets a business logic from the BlockBall plugin.
     * All types in the service package can be accessed.
     * Throws a [IllegalArgumentException] if the service could not be found.
     */
    private fun <S> resolve(service: Class<S>): S {
        try {
            return this.injector!!.getBinding(service).provider.get()
        } catch (e: Exception) {
            throw IllegalArgumentException("Service could not be resolved.", e)
        }
    }

    /**
     * Disables the plugin for the given version and prints the supported version.
     */
    private fun disableForVersion(version: Version, supportedVersion: Version): Boolean {
        if (Version.serverVersion == version) {
            this.logger.log(Level.SEVERE, "================================================")
            this.logger.log(Level.SEVERE, "BlockBall does not support this subversion")
            this.logger.log(Level.SEVERE, "Please upgrade from v" + version.id + " to v" + supportedVersion.id)
            this.logger.log(Level.SEVERE, "Plugin gets now disabled!")
            this.logger.log(Level.SEVERE, "================================================")
            return true
        }

        return false
    }
}
