package com.github.shynixn.blockball

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.PluginDependency
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.impl.commandexecutor.*
import com.github.shynixn.blockball.impl.listener.*
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.Version
import com.github.shynixn.mcutils.packet.api.PacketInType
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.impl.service.PacketServiceImpl
import com.google.inject.Guice
import com.google.inject.Injector
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.configuration.MemorySection
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * Plugin Main.
 * @author Shynixn
 */
class BlockBallPlugin : JavaPlugin(), PluginProxy {
    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
    }

    private var injector: Injector? = null
    private var serverVersion: Version? = null
    private val bstatsPluginId = 1317
    private var packetService: PacketService? = null

    /**
     * Gets the installed version of the plugin.
     */
    override val version: String
        get() {
            return description.version
        }

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

        val useLegacy = getResource("plugin.yml")!!.bufferedReader().use { reader ->
            !reader.readText().contains("libraries")
        }

        val versions = if (useLegacy) {
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

        this.packetService = PacketServiceImpl(this)
        this.injector = Guice.createInjector(BlockBallDependencyInjectionBinder(this, packetService!!))
        this.reloadConfig()

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(resolve(GameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(DoubleJumpListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(HubgameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(MinigameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BungeeCordgameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BallListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BlockSelectionListener::class.java), this)

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")

        startPlugin()

        val dependencyService = resolve(DependencyService::class.java)
        val configurationService = resolve(ConfigurationService::class.java)
        val bungeeCordConnectionService = resolve(BungeeCordConnectionService::class.java)

        dependencyService.checkForInstalledDependencies()

        val enableMetrics = configurationService.findValue<Boolean>("metrics")
        val enableBungeeCord = configurationService.findValue<Boolean>("game.allow-server-linking")

        // Register CommandExecutor
        val commandService = resolve(CommandService::class.java)
        commandService.registerCommandExecutor("blockballstop", resolve(StopCommandExecutor::class.java))
        commandService.registerCommandExecutor("blockballreload", resolve(ReloadCommandExecutor::class.java))
        commandService.registerCommandExecutor(
            "blockballbungeecord",
            resolve(BungeeCordSignCommandExecutor::class.java)
        )
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

        if (dependencyService.isInstalled(PluginDependency.PLACEHOLDERAPI)) {
            val placeHolderService = resolve(DependencyPlaceholderApiService::class.java)
            placeHolderService.registerListener()
        }

        if (enableBungeeCord) {
            bungeeCordConnectionService.restartChannelListeners()
            Bukkit.getServer()
                .consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.DARK_GREEN + "Started server linking.")
        }

        packetService!!.registerPacketListening(PacketInType.USEENTITY)

        Bukkit.getServer()
            .consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.description.version + " by Shynixn")
    }

    /**
     * Override on disable.
     */
    override fun onDisable() {
        if (injector == null) {
            return
        }

        packetService!!.close()

        try {
            resolve(GameService::class.java).close()
        } catch (e: Exception) {
            // Ignored.
        }
    }

    /**
     * Starts the game mode.
     */
    private fun startPlugin() {
        try {
            val gameService = resolve(GameService::class.java)
            gameService.restartGames()

            val method = BlockBallApi::class.java.getDeclaredMethod("initializeBlockBall", PluginProxy::class.java)
            method.isAccessible = true
            method.invoke(BlockBallApi, this)
            logger.log(Level.INFO, "Using NMS Connector " + getServerVersion().bukkitId + ".")
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Failed to enable BlockBall.", e)
        }
    }

    /**
     * Gets the server version this plugin is currently running on.
     */
    override fun getServerVersion(): Version {
        if (this.serverVersion != null) {
            return this.serverVersion!!
        }

        try {
            if (Bukkit.getServer() == null || Bukkit.getServer().javaClass.getPackage() == null) {
                this.serverVersion = Version.VERSION_UNKNOWN
                return this.serverVersion!!
            }

            val version = Bukkit.getServer().javaClass.getPackage().name.replace(".", ",").split(",")[3]

            for (versionSupport in Version.values()) {
                if (versionSupport.bukkitId == version) {
                    this.serverVersion = versionSupport
                    return versionSupport
                }
            }

        } catch (e: Exception) {
            // Ignore parsing exceptions.
        }

        this.serverVersion = Version.VERSION_UNKNOWN

        return this.serverVersion!!
    }

    /**
     * Shutdowns the server.
     */
    override fun shutdownServer() {
        Bukkit.getServer().shutdown()
    }

    /**
     * Gets a business logic from the BlockBall plugin.
     * All types in the service package can be accessed.
     * Throws a [IllegalArgumentException] if the service could not be found.
     */
    override fun <S> resolve(service: Class<S>): S {
        try {
            return this.injector!!.getBinding(service).provider.get()
        } catch (e: Exception) {
            throw IllegalArgumentException("Service could not be resolved.", e)
        }
    }

    /**
     * Creates a new entity from the given class.
     * Throws a IllegalArgumentException if not found.
     *
     * @param entity entityClazz
     * @param <E>    type
     * @return entity.
     */
    override fun <E> create(entity: Class<E>): E {
        try {
            val entityName = entity.simpleName + "Entity"
            return Class.forName("com.github.shynixn.blockball.core.logic.persistence.entity.$entityName")
                .getDeclaredConstructor().newInstance() as E
        } catch (e: Exception) {
            throw IllegalArgumentException("Entity could not be created.", e)
        }
    }

    /**
     * Disables the plugin for the given version and prints the supported version.
     */
    private fun disableForVersion(version: Version, supportedVersion: Version): Boolean {
        if (getServerVersion() == version) {
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
