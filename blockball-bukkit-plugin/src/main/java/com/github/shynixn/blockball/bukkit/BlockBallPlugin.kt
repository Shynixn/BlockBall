@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.enumeration.PluginDependency
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.context.SqlDbContext
import com.github.shynixn.blockball.bukkit.logic.business.listener.*
import com.github.shynixn.blockball.core.logic.business.commandexecutor.*
import com.github.shynixn.blockball.core.logic.business.extension.cast
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.mccoroutine.registerSuspendingEvents
import com.google.inject.Guice
import com.google.inject.Injector
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.IOUtils
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.configuration.MemorySection
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.FileOutputStream
import java.util.logging.Level

/**
 * Plugin Main Type.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018-2019 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class BlockBallPlugin : JavaPlugin(), PluginProxy {
    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
    }

    private var injector: Injector? = null
    private var serverVersion: Version? = null
    private val bstatsPluginId = 1317

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

        if (!getServerVersion().isCompatible(
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
                Version.VERSION_1_16_R3
            )
        ) {
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")
            sendConsoleMessage(ChatColor.RED.toString() + "BlockBall does not support your server version")
            sendConsoleMessage(ChatColor.RED.toString() + "Install v" + Version.VERSION_1_8_R3.id + " - v" + Version.VERSION_1_16_R3.id)
            sendConsoleMessage(ChatColor.RED.toString() + "Plugin gets now disabled!")
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")

            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        this.injector = Guice.createInjector(BlockBallDependencyInjectionBinder(this))
        this.reloadConfig()

        // Register Listeners
        Bukkit.getPluginManager().registerEvents(resolve(GameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(DoubleJumpListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(HubgameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(MinigameListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BungeeCordgameListener::class.java), this)
        Bukkit.getPluginManager().registerSuspendingEvents(resolve(StatsListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BallListener::class.java), this)
        Bukkit.getPluginManager().registerEvents(resolve(BlockSelectionListener::class.java), this)

        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")

        startPlugin()

        val updateCheckService = resolve(UpdateCheckService::class.java)
        val dependencyService = resolve(DependencyService::class.java)
        val configurationService = resolve(ConfigurationService::class.java)
        val bungeeCordConnectionService = resolve(BungeeCordConnectionService::class.java)

        updateCheckService.checkForUpdates()
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

        val protocolService = resolve(ProtocolService::class.java)

        for (world in Bukkit.getWorlds()) {
            for (player in world.players) {
                protocolService.register(player)
            }
        }
        protocolService.registerPackets(listOf(findClazz("net.minecraft.server.VERSION.PacketPlayInUseEntity")))

        Bukkit.getServer()
            .consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.description.version + " by Shynixn, LazoYoung")
    }

    /**
     * Override on disable.
     */
    override fun onDisable() {
        if (injector == null) {
            return
        }

        runBlocking {
            resolve(PersistenceStatsService::class.java).close()
        }

        resolve(SqlDbContext::class.java).close()
        resolve(ProtocolService::class.java).dispose()

        try {
            resolve(GameService::class.java).close()
        } catch (e: Exception) {
            // Ignored.
        }
    }

    /**
     * Loads the default config and saves it to the plugin folder.
     */
    override fun saveDefaultConfig() {
        this.getResource("assets/blockball/config.yml").use { inputStream ->
            if (!this.dataFolder.exists()) {
                this.dataFolder.mkdir()
            }

            val configFile = File(this.dataFolder, "config.yml")
            if (configFile.exists()) {
                return
            }

            FileOutputStream(configFile).use { outStream ->
                IOUtils.copy(inputStream, outStream)
            }
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
            if (Bukkit.getServer().cast<Server?>() == null || Bukkit.getServer().javaClass.getPackage() == null) {
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
     * Sets the motd of the server.
     */
    override fun setMotd(message: String) {
        val builder = java.lang.StringBuilder("[")
        builder.append((message.replace("[", "").replace("]", "")))
        builder.append(ChatColor.RESET.toString())
        builder.append("]")

        val minecraftServerClazz = findClazz("net.minecraft.server.VERSION.MinecraftServer")
        val craftServerClazz = findClazz("org.bukkit.craftbukkit.VERSION.CraftServer")
        val setModtMethod = minecraftServerClazz.getDeclaredMethod("setMotd", String::class.java)
        val getServerConsoleMethod = craftServerClazz.getDeclaredMethod("getServer")

        val console = getServerConsoleMethod.invoke(Bukkit.getServer())
        setModtMethod.invoke(console, builder.toString().translateChatColors())
    }

    /**
     * Shutdowns the server.
     */
    override fun shutdownServer() {
        Bukkit.getServer().shutdown()
    }

    /**
     * Tries to find a version compatible class.
     */
    override fun findClazz(name: String): Class<*> {
        return Class.forName(
            name.replace(
                "VERSION",
                BlockBallApi.resolve(PluginProxy::class.java).getServerVersion().bukkitId
            )
        )
    }

    /**
     * Sends a console message from this plugin.
     */
    override fun sendConsoleMessage(message: String) {
        Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + message)
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
    </E> */
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
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")
            sendConsoleMessage(ChatColor.RED.toString() + "BlockBall does not support this subversion")
            sendConsoleMessage(ChatColor.RED.toString() + "Please upgrade from v" + version.id + " to v" + supportedVersion.id)
            sendConsoleMessage(ChatColor.RED.toString() + "Plugin gets now disabled!")
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")
            Bukkit.getPluginManager().disablePlugin(this)
            return true
        }

        return false
    }
}
