@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.*
import com.github.shynixn.blockball.bukkit.logic.business.listener.*
import com.github.shynixn.blockball.bukkit.logic.business.nms.VersionSupport
import com.google.inject.Guice
import com.google.inject.Injector
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * Plugin Main Type.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
        private const val PLUGIN_NAME = "BlockBall"
    }

    private var injector: Injector? = null

    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        Bukkit.getServer().consoleSender.sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
        this.saveDefaultConfig()
        this.injector = Guice.createInjector(BlockBallDependencyInjectionBinder(this))

        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            Bukkit.getPluginManager().disablePlugin(this)
        } else {
            this.reloadConfig()
            // Register Listeners
            Bukkit.getPluginManager().registerEvents(resolve(GameListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(DoubleJumpListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(HubgameListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(MinigameListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(BungeeCordgameListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(StatsListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(BallListener::class.java), this)
            Bukkit.getPluginManager().registerEvents(resolve(BlockSelectionListener::class.java), this)

            // Register CommandExecutor
            resolve(ArenaCommandExecutor::class.java)
            resolve(BungeeCordSignCommandExecutor::class.java)
            resolve(JoinCommandExecutor::class.java)
            resolve(LeaveCommandExecutor::class.java)
            resolve(ReloadCommandExecutor::class.java)
            resolve(SpectateCommandExecutor::class.java)
            resolve(StopCommandExecutor::class.java)

            val updateCheker = resolve(UpdateCheckService::class.java)
            val dependencyChecker = resolve(DependencyService::class.java)
            val configurationService = resolve(ConfigurationService::class.java)
            val ballEntitySerivice = resolve(BallEntityService::class.java)

            Bukkit.getWorlds().forEach { world ->
                ballEntitySerivice.cleanUpInvalidEntities(world.entities)
            }

            updateCheker.checkForUpdates()
            dependencyChecker.checkForInstalledDependencies()

            val enableMetrics = configurationService.findValue<Boolean>("metrics")

            if (enableMetrics) {
                Metrics(this)
            }

            startPlugin()
            Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.description.version + " by Shynixn")
        }
    }

    /**
     * Override on disable.
     */
    override fun onDisable() {
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
            logger.log(Level.INFO, "Using NMS Connector " + VersionSupport.getServerVersion().versionText + ".")
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Failed to enable BlockBall.", e)
        }
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
            return Class.forName("com.github.shynixn.blockball.bukkit.logic.persistence.entity.$entityName").newInstance() as E
        } catch (e: Exception) {
            throw IllegalArgumentException("Entity could not be created.", e)
        }
    }
}