@file:Suppress("UNCHECKED_CAST", "unused", "UNUSED_PARAMETER")

package com.github.shynixn.blockball.sponge

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.core.logic.business.commandexecutor.*
import com.github.shynixn.blockball.sponge.logic.business.dependency.Metrics2
import com.github.shynixn.blockball.sponge.logic.business.extension.sendMessage
import com.google.inject.Inject
import com.google.inject.Injector
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.game.state.GameStoppingServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer

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
@Plugin(
    id = "blockball",
    name = "BlockBall",
    description = "BlockBall is a spigot and also a sponge plugin to play soccer games in Minecraft."
)
class BlockBallPlugin : PluginProxy {
    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
    }

    private var injector: Injector? = null
    private var serverVersion: Version? = null

    @Inject
    private lateinit var plugin: PluginContainer

    @Inject
    private lateinit var metrics: Metrics2

    @Inject
    private lateinit var logger: Logger

    @Inject
    private lateinit var spongeInjector: Injector

    /**
     * Gets the installed version of the plugin.
     */
    override val version: String
        get() {
            return plugin.version.get()
        }

    /**
     * Enables the plugin BlockBall.
     */
    @Listener
    fun onEnable(event: GameInitializationEvent) {
        Sponge.getServer().console.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
        this.injector = spongeInjector.createChildInjector(BlockBallDependencyInjectionBinder(plugin, this))

        if (!getServerVersion().isCompatible(Version.VERSION_1_12_R1)) {
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")
            sendConsoleMessage(ChatColor.RED.toString() + "BlockBall does not support your server version")
            sendConsoleMessage(ChatColor.RED.toString() + "Install v" + Version.VERSION_1_12_R1.id + " - v" + Version.VERSION_1_12_R1.id)
            sendConsoleMessage(ChatColor.RED.toString() + "Plugin gets now disabled!")
            sendConsoleMessage(ChatColor.RED.toString() + "================================================")

            disablePlugin()

            return
        }

        // Register Listeners
        /* TODO: Enable Listeners.
        Sponge.getEventManager().registerListeners(resolve(GameListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(DoubleJumpListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(HubgameListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(MinigameListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(BungeeCordgameListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(StatsListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(BallListener::class.java), this)
        Sponge.getEventManager().registerListeners(resolve(BlockSelectionListener::class.java), this)*/

        // Register CommandExecutor
        val configurationService = resolve(ConfigurationService::class.java)
        val commandService = resolve(CommandService::class.java)

        commandService.registerCommandExecutor("blockballstop", resolve(StopCommandExecutor::class.java))
        commandService.registerCommandExecutor("blockballreload", resolve(ReloadCommandExecutor::class.java))
        commandService.registerCommandExecutor(
            "blockballbungeecord",
            resolve(BungeeCordSignCommandExecutor::class.java)
        )
        commandService.registerCommandExecutor("blockball", resolve(ArenaCommandExecutor::class.java))
        commandService.registerCommandExecutor(
            configurationService.findValue<Map<String, String>>("global-spectate"),
            resolve(SpectateCommandExecutor::class.java)
        )
        commandService.registerCommandExecutor(
            configurationService.findValue<Map<String, String>>("global-leave"),
            resolve(LeaveCommandExecutor::class.java)
        )
        commandService.registerCommandExecutor(
            configurationService.findValue<Map<String, String>>("global-join"),
            resolve(JoinCommandExecutor::class.java)
        )

        // TODO: server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")

        val updateCheker = resolve(UpdateCheckService::class.java)
        val dependencyChecker = resolve(DependencyService::class.java)
        val ballEntitySerivice = resolve(BallEntityService::class.java)
        val bungeeCordConnectionService = resolve(BungeeCordConnectionService::class.java)

        updateCheker.checkForUpdates()
        dependencyChecker.checkForInstalledDependencies()

        val enableBungeeCord = configurationService.findValue<Boolean>("game.allow-server-linking")

        startPlugin()

        if (enableBungeeCord) {
            bungeeCordConnectionService.restartChannelListeners()
            Sponge.getServer().console.sendMessage(PREFIX_CONSOLE + ChatColor.DARK_GREEN + "Started server linking.")
        }

        Sponge.getServer().console.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.plugin.version.get() + " by Shynixn")
    }

    /**
     * OnDisable.
     */
    @Listener
    fun onDisable(event: GameStoppingServerEvent) {
        try {
            resolve(GameService::class.java).close()
        } catch (e: Exception) {
            plugin.logger.error("Failed to close plugin.", e)
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
            plugin.logger.info("Using NMS Connector " + getServerVersion().bukkitId + ".")
        } catch (e: Exception) {
            plugin.logger.error("Failed to enable BlockBall.", e)
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
            val version = Sponge.getPluginManager().getPlugin("sponge").get().version.get().split("-")[0]

            for (versionSupport in Version.values()) {
                if (versionSupport.id == version) {
                    this.serverVersion = versionSupport
                    return versionSupport
                }
            }

        } catch (e: Exception) {
        }

        this.serverVersion = Version.VERSION_UNKNOWN
        return this.serverVersion!!
    }

    /**
     * Sends a console message from this plugin.
     */
    override fun sendConsoleMessage(message: String) {
        Sponge.getServer().console.sendMessage(PREFIX_CONSOLE + message)
    }

    /**
     * Sets the motd of the server.
     */
    override fun setMotd(message: String) {
        sendConsoleMessage("BlockBall does currently not support changing the server motd.")
    }

    /**
     * Shutdowns the server.
     */
    override fun shutdownServer() {
        Sponge.getServer().shutdown()
    }

    /**
     * Is the plugin enabled?
     */
    override fun isEnabled(): Boolean {
        return true
    }

    /**
     * Tries to find a version compatible class.
     */
    override fun findClazz(name: String): Class<*> {
        return Class.forName(name)
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
            return Class.forName("com.github.shynixn.blockball.bukkit.logic.persistence.entity.$entityName")
                .getDeclaredConstructor()
                .newInstance() as E
        } catch (e: Exception) {
            throw IllegalArgumentException("Entity could not be created.", e)
        }
    }

    /**
     * Disables the plugin.
     */
    private fun disablePlugin() {
        Sponge.getGame().eventManager.unregisterPluginListeners(this)
        Sponge.getGame().commandManager.getOwnedBy(this).forEach { Sponge.getGame().commandManager.removeMapping(it) }
        Sponge.getGame().scheduler.getScheduledTasks(this).forEach { it.cancel() }
    }
}
