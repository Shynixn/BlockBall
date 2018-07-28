package com.github.shynixn.blockball.bukkit

import com.github.shynixn.ball.bukkit.core.logic.business.CoreManager
import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.bukkit.dependencies.RegisterHelper
import com.github.shynixn.blockball.bukkit.dependencies.placeholderapi.PlaceHolderApiConnection
import com.github.shynixn.blockball.bukkit.logic.business.controller.BungeeCordPingManager
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.helper.GoogleGuiceBinder
import com.github.shynixn.blockball.bukkit.logic.business.helper.ReflectionUtils
import com.github.shynixn.blockball.bukkit.logic.business.helper.UpdateUtils
import com.github.shynixn.blockball.bukkit.logic.business.helper.async
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.google.inject.Guice
import com.google.inject.Inject
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
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

    //region Static Fields
    companion object {
        /** Final Prefix of BlockBall in the console */
        val PREFIX_CONSOLE: String = ChatColor.BLUE.toString() + "[BlockBall] "
        private const val PLUGIN_NAME = "BlockBall"
        private const val SPIGOT_RESOURCEID: Long = 15320
    }
    //endregion

    //region Private Fields
    private var isStartedUp: Boolean = true
    private var coreManager: CoreManager? = null
    //endregion

    //region Private Dependency Fields
    @Inject
    private var bungeeCordController: BungeeCordPingManager? = null

    @Inject
    private var gameController: GameRepository? = null
    //endregion

    //region Public Methods
    /**
     * Enables the plugin BlockBall.
     */
    override fun onEnable() {
        this.saveDefaultConfig()
        Guice.createInjector(GoogleGuiceBinder(this))
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.isStartedUp = false
            Bukkit.getPluginManager().disablePlugin(this)
        } else {
            Config.reload()
            if (Config.metrics!!) {
                Metrics(this)
            }
            checkForUpdates()
            startPlugin()
            Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.description.version + " by Shynixn")
        }
    }

    /**
     * Disables the plugin BlockBall.
     */
    override fun onDisable() {
        super.onDisable()
        gameController!!.close()
    }
    //endregion

    //region Private Methods
    /**
     * Starts the game mode.
     */
    private fun startPlugin() {
        try {
            RegisterHelper.PREFIX = BlockBallPlugin.PREFIX_CONSOLE
            RegisterHelper.register("Vault")
            RegisterHelper.register("WorldEdit")
            RegisterHelper.register("BossBarAPI")
            if (RegisterHelper.register("PlaceholderAPI")) {
                PlaceHolderApiConnection.initializeHook(Bukkit.getPluginManager().getPlugin("BlockBall"))
            }
            gameController!!.reload()
            ReflectionUtils.invokeMethodByKotlinClass<Void>(BlockBallApi::class.java, "initializeBlockBall", arrayOf(Any::class.java, Any::class.java), arrayOf(this.gameController, bungeeCordController))
            coreManager = CoreManager(this, "storage.yml", "ball.yml")
            logger.log(Level.INFO, "Using NMS Connector " + VersionSupport.getServerVersion().versionText + ".")
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Failed to enable BlockBall.", e)
        }
    }

    /**
     * Checks if new updates are available on spigotmc.org.
     */
    private fun checkForUpdates() {
        async(this) {
            try {
                UpdateUtils.checkPluginUpToDateAndPrintMessage(SPIGOT_RESOURCEID, PREFIX_CONSOLE, PLUGIN_NAME, this)
            } catch (e: IOException) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to check for updates.")
            }
        }
    }
    //endregion
}