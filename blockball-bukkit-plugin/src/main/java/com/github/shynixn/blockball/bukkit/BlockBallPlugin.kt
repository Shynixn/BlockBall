package com.github.shynixn.blockball.bukkit

import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.business.controller.BallController
import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectController
import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController
import com.github.shynixn.blockball.api.business.controller.GameController
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallBungeeCordManager
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.BlockBallReloadCommandExecutor
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.NewArenaCommandExecutor
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.helper.GoogleGuiceBinder
import com.github.shynixn.blockball.bukkit.logic.business.helper.ReflectionUtils
import com.github.shynixn.blockball.bukkit.logic.business.helper.UpdateUtils
import com.github.shynixn.blockball.bukkit.logic.business.listener.StatsListener
import com.google.inject.Guice
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.logging.Level

/**
 * Created by Shynixn 2018.
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
class BlockBallPlugin : JavaPlugin() {

    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
        private val PLUGIN_NAME = "BlockBall"
        private val SPIGOT_RESOURCEID: Long = 15320
    }

    private var isnEnabled: Boolean = true
    private var success: Boolean = false

    @Inject
    private var blockBallBungeeCordManager: BlockBallBungeeCordManager? = null

    @Inject
    private var gameController: GameRepository? = null;

    @Inject
    private var arenaCommandexecutor: NewArenaCommandExecutor? = null

    @Inject
    private var blockBallReloadCommandExecutor: BlockBallReloadCommandExecutor? = null

    @Inject
    private var statsListener: StatsListener? = null

    override fun onEnable() {
        this.saveDefaultConfig()
        Config.getInstance().reload()
        Guice.createInjector(GoogleGuiceBinder(this))
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.isnEnabled = false
            Bukkit.getPluginManager().disablePlugin(this)
        } else {
            Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
            Config.getInstance().reload()
            if (Config.getInstance().isMetricsEnabled) {
                //  new Metrics(this);
            }
            checkForUpdates()
            startBungeecordLinking()
            startPlugin()
        }
    }

    private fun startPlugin() {
        success = false
        if (!Config.getInstance().isOnlyBungeeCordLinkingEnabled) {
            try {
                ReflectionUtils.invokeMethodByClass<Any>(BlockBallApi::class.java, "initializeBlockBall"
                        , arrayOf(BallController::class.java, GameController::class.java)
                        , arrayOf(this.blockBallBungeeCordManager!!.bungeeCordSignController, this.gameController))
                success = true
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Failed to enable plugin.", e)
            }
        } else {
            success = true
        }
        if (success) {
            Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.description.version + " by Shynixn")
        }
    }

    private fun startBungeecordLinking() {
        if (Config.getInstance().isBungeeCordLinkingEnabled) {
            Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + "Starting BungeeCord linking....")
            try {
                ReflectionUtils.invokeMethodByClass<Any>(BlockBallApi::class.java, "initializeBungeeCord"
                        , arrayOf(BungeeCordSignController::class.java, BungeeCordConnectController::class.java)
                        , arrayOf(this.blockBallBungeeCordManager!!.bungeeCordSignController, this.blockBallBungeeCordManager!!.bungeeCordConnectController))
                Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + "Server [" + Bukkit.getServer().serverName + " is now available via BlockBall-Bungeecord.")
            } catch (e: NoSuchMethodException) {
                logger.log(Level.WARNING, "Failed to enable plugin.", e)
            } catch (e: InvocationTargetException) {
                logger.log(Level.WARNING, "Failed to enable plugin.", e)
            } catch (e: IllegalAccessException) {
                logger.log(Level.WARNING, "Failed to enable plugin.", e)
            }

        }
    }

    private fun checkForUpdates() {
        this.server.scheduler.runTaskAsynchronously(this) {
            try {
                UpdateUtils.checkPluginUpToDateAndPrintMessage(SPIGOT_RESOURCEID, PREFIX_CONSOLE, PLUGIN_NAME, this)
            } catch (e: IOException) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to check for updates.")
            }
        }
    }
}