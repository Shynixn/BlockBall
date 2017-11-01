package com.github.shynixn.blockball.bukkit;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectController;
import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallBungeeCordManager;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallManager;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.metrics.Metrics;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.bukkit.nms.VersionSupport;
import com.github.shynixn.blockball.lib.ReflectionUtils;
import com.github.shynixn.blockball.lib.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public final class BlockBallPlugin extends JavaPlugin {
    public static final String PREFIX_CONSOLE = ChatColor.BLUE + "[BlockBall] ";
    private static final String PLUGIN_NAME = "BlockBall";
    private static final long SPIGOT_RESOURCEID = 15320;
    private static final long TICK_TIME = 20L;
    private boolean enabled = true;
    private static Logger logger;

    private BlockBallManager blockBallManager;
    private BlockBallBungeeCordManager blockBallBungeeCordManager;

    /**
     * Enables the BlockBall plugin.
     */
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        logger = this.getLogger();
        if (this.getConfig().getBoolean("plugin-start-delay.enabled")) {
            this.getServer().getScheduler().runTaskLater(this, BlockBallPlugin.this::reload, TICK_TIME * this.getConfig().getInt("plugin-start-delay.time-seconds"));
        } else {
            this.reload();
        }
    }

    /**
     * Disables the BlockBall plugin.
     */
    @Override
    public void onDisable() {
        if (this.enabled) {
            NMSRegistry.unregisterAll();
            try {
                if (this.blockBallBungeeCordManager != null) {
                    this.blockBallBungeeCordManager.close();
                }
                if (this.blockBallManager != null) {
                    this.blockBallManager.close();
                }
            } catch (final Exception e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to disable BlockBall.", e);
            }
        }
    }

    /**
     * Reloads the BlockBall plugin.
     */
    private void reload() {
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.enabled = false;
            Bukkit.getPluginManager().disablePlugin(BlockBallPlugin.this);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...");
            this.saveDefaultConfig();
            Config.getInstance().reload();
            if (Config.getInstance().isMetricsEnabled()) {
                new Metrics(this);
            }
            this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    UpdateUtils.checkPluginUpToDateAndPrintMessage(SPIGOT_RESOURCEID, PREFIX_CONSOLE, PLUGIN_NAME, BlockBallPlugin.this);
                } catch (final IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to check for updates.");
                }
            });
            if (Config.getInstance().isBungeeCordLinkingEnabled()) {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + "Starting BungeeCord linking....");
                try {
                    this.blockBallBungeeCordManager = new BlockBallBungeeCordManager(this);
                    ReflectionUtils.invokeMethodByClass(BlockBallApi.class, "initializeBungeeCord"
                            , new Class[]{BungeeCordSignController.class, BungeeCordConnectController.class}
                            , new Object[]{this.blockBallBungeeCordManager.getBungeeCordSignController()
                                    , this.blockBallBungeeCordManager.getBungeeCordConnectController()});
                    Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + "Server [" + Bukkit.getServer().getServerName() + " is now available via BlockBall-Bungeecord.");
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    logger().log(Level.WARNING, "Failed to enable plugin.", e);
                }
            }
            if (!Config.getInstance().isOnlyBungeeCordLinkingEnabled()) {
                NMSRegistry.registerAll();
                try {
                    this.blockBallManager = new BlockBallManager(this);
                    ReflectionUtils.invokeMethodByClass(BlockBallApi.class, "initializeBlockBall"
                            , new Class[]{BallController.class, GameController.class}
                            , new Object[]{this.blockBallManager.getBallController()
                                    , this.blockBallManager.getGameController()});
                    Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.getDescription().getVersion() + " by Shynixn");
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    logger().log(Level.WARNING, "Failed to enable plugin.", e);
                }
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.getDescription().getVersion() + " by Shynixn");
            }
        }
    }

    /**
     * Returns the logger of the blockball plugin.
     *
     * @return logger
     */
    public static Logger logger() {
        return logger;
    }
}
