package com.github.shynixn.blockball.business.bukkit;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import com.github.shynixn.blockball.business.logic.arena.ArenaController;
import com.github.shynixn.blockball.business.logic.game.GameEntity;
import com.github.shynixn.blockball.lib.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Shynixn
 */
public final class BlockBallPlugin extends JavaPlugin {
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
            SPluginLoader.unload(this, BlockBallApi.class);
        }
    }

    private void reload() {
        if (!NMSRegistry.isVersionValid()) {
            SConsoleUtils.sendColoredMessage("================================================", ChatColor.RED, PREFIX_CONSOLE);
            SConsoleUtils.sendColoredMessage("BlockBall does not support your server version", ChatColor.RED, PREFIX_CONSOLE);
            SConsoleUtils.sendColoredMessage("Install v1.8.0 - v1.12.0", ChatColor.RED, PREFIX_CONSOLE);
            SConsoleUtils.sendColoredMessage("Plugin gets now disabled!", ChatColor.RED, PREFIX_CONSOLE);
            SConsoleUtils.sendColoredMessage("================================================", ChatColor.RED, PREFIX_CONSOLE);
            this.enabled = false;
            Bukkit.getPluginManager().disablePlugin(BlockBallPlugin.this);
        } else {
            SConsoleUtils.sendColoredMessage("Loading BlockBall ...", ChatColor.GREEN, PREFIX_CONSOLE);
            this.saveDefaultConfig();
            BungeeCord.reload(this, PREFIX_CONSOLE, "bbbungee", "[BlockBall]");
            if (BungeeCord.isSignModeEnabled()) {
                this.enabled = false;
                SConsoleUtils.sendColoredMessage("Enabled BlockBallBungeeConnector " + this.getDescription().getVersion() + " by Shynixn", ChatColor.GREEN, PREFIX_CONSOLE);
            } else {
                SPluginLoader.load(BlockBallPlugin.this, SLanguage.class, AsyncRunnable.class, SCommandExecutor.class, SEvents.class, ArenaController.class, Config.class, GameEntity.class, BlockBallApi.class);
                Config.getInstance().reload();
                NMSRegistry.registerAll();
                SLanguage.reload(Language.class);
                SConsoleUtils.sendColoredMessage("Enabled BlockBall " + this.getDescription().getVersion() + " by Shynixn", ChatColor.GREEN, PREFIX_CONSOLE);
            }
        }
    }
}
