package com.github.shynixn.blockball.business.bukkit;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.bukkit.nms.VersionSupport;
import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import com.github.shynixn.blockball.business.metrics.Metrics;
import com.github.shynixn.blockball.lib.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

/**
 * @author Shynixn
 */
public final class BlockBallPlugin extends JavaPlugin {
    public static final String PLUGIN_NAME = "BlockBall";
    public static final String PREFIX_CONSOLE = ChatColor.BLUE + "[BlockBall] ";
    private static final long TICK_TIME = 20L;
    private boolean enabled = true;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        if (this.getConfig().getBoolean("plugin-start-delay.enabled")) {
            this.getServer().getScheduler().runTaskLater(this, BlockBallPlugin.this::reload, TICK_TIME * this.getConfig().getInt("plugin-start-delay.time-seconds"));
        } else {
            this.reload();
        }
    }

    @Override
    public void onDisable() {
        if (this.enabled) {
            BlockBallApi.closeGames();
            NMSRegistry.unregisterAll();
        }
    }

    private void reload() {
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.enabled = false;
            Bukkit.getPluginManager().disablePlugin(BlockBallPlugin.this);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...");
            this.saveDefaultConfig();
            Config.getInstance().reload();
            if (Config.getInstance().isMetrics()) {
                new Metrics(this);
            }
            BungeeCord.reload(this, PREFIX_CONSOLE, "bbbungee", "[BlockBall]");
            if (BungeeCord.isSignModeEnabled()) {
                this.enabled = false;
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBallBungeeConnector " + this.getDescription().getVersion() + " by Shynixn");
            } else {
                try {
                    ReflectionUtils.invokeMethodByClass(BlockBallApi.class, "initialize", new Class[0], new Object[0]);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to init BlockBall.", e);
                }

                NMSRegistry.registerAll();
                SLanguage.reload(Language.class);
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled BlockBall " + this.getDescription().getVersion() + " by Shynixn");
            }
        }
    }
}
