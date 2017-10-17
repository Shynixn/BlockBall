package com.github.shynixn.blockball.bukkit;

import com.github.shynixn.blockball.bukkit.metrics.Metrics;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.bukkit.nms.VersionSupport;
import com.github.shynixn.blockball.lib.ReflectionUtils;
import com.github.shynixn.blockball.lib.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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
    private static final String PLUGIN_NAME = "BlockBall";
    private static final long SPIGOT_RESOURCEID = 15320;
    public static final String PREFIX_CONSOLE = ChatColor.BLUE + "[BlockBall] ";
    private static final long TICK_TIME = 20L;
    private boolean enabled = true;
    private static Logger logger;

    /**
     * Enables the BlockBall plugin
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
     * Disables the BlockBall plugin
     */
    @Override
    public void onDisable() {
        if (this.enabled) {
            BlockBallApi.closeGames();
            NMSRegistry.unregisterAll();
        }
    }

    /**
     * Reloads the BlockBall plugin
     */
    private void reload() {
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.enabled = false;
            Bukkit.getPluginManager().disablePlugin(BlockBallPlugin.this);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...");
            this.saveDefaultConfig();
            ConfigOld.getInstance().reload();
            if (ConfigOld.getInstance().isMetrics()) {
                new Metrics(this);
            }
            this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
                try {
                    UpdateUtils.checkPluginUpToDateAndPrintMessage(SPIGOT_RESOURCEID, PREFIX_CONSOLE, PLUGIN_NAME, BlockBallPlugin.this);
                } catch (final IOException e) {
                   Bukkit.getLogger().log(Level.WARNING, "Failed to check for updates.");
                }
            });
            BungeeCord.reload(this, PREFIX_CONSOLE, "bbbungee", "[BlockBall]");
            if (BungeeCord.isSignModeEnabled()) {
                this.enabled = false;
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBallBungeeConnector " + this.getDescription().getVersion() + " by Shynixn");
            } else {
                try {
                    ReflectionUtils.invokeMethodByClass(BlockBallApi.class, "initialize", new Class[0], new Object[0]);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to initializeHook BlockBall.", e);
                }

                NMSRegistry.registerAll();
                SLanguage.reload(Language.class);
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.getDescription().getVersion() + " by Shynixn");
            }
        }
    }


    /**
     * Returns the logger of the petblocks plugin
     *
     * @return logger
     */
    public static Logger logger() {
        return logger;
    }
}
