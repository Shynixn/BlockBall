package com.github.shynixn.blockball.lib;

import java.util.HashMap;

import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public final class SChatMenuManager extends SimpleListener {
    private static SChatMenuManager instance;
    private final HashMap<Player, SChatpage> pages = new HashMap<>();

    public SChatMenuManager() {
        super(JavaPlugin.getPlugin(BlockBallPlugin.class));
    }

    public static SChatMenuManager getInstance() {
        if (instance == null)
            instance = new SChatMenuManager();
        return instance;
    }

    public void open(Player player, SChatpage chatPage) {
        this.pages.put(player, chatPage);
        this.pages.get(player).show();
    }

    public boolean isUsing(Player player) {
        return this.pages.containsKey(player);
    }

    public void handleChatMessage(Player player, String message) {
        if(message == null || !this.pages.containsKey(player))
            return;
        final boolean wasFalse = this.pages.get(player).playerPreChatEnter(message);
        this.pages.get(player).lastNumber = -1;
        if (SMathUtils.tryPInt(message) && wasFalse) {
            this.pages.get(player).setLastNumber(Integer.parseInt(message));
            this.pages.get(player).onPlayerSelect(Integer.parseInt(message));
        } else if (message.equalsIgnoreCase("e")) {
            this.pages.remove(player);
        } else if (message.equalsIgnoreCase("b") && this.pages.get(player).getLastInstance() != null) {
            this.open(player, this.pages.get(player).getLastInstance());
        } else {
            this.pages.get(player).show();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAsyncChatEvent2(PlayerChatEvent event) {
        if (!Config.getInstance().isAsyncChat() && !Config.getInstance().isHighpriority()) {
            if (this.pages.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                final String message = ChatColor.stripColor(event.getMessage());
                this.handleChatMessage(event.getPlayer(), message);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerChatEvent2(AsyncPlayerChatEvent event) {
        if (Config.getInstance().isAsyncChat() && !Config.getInstance().isHighpriority()) {
            if (this.pages.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                final String message = ChatColor.stripColor(event.getMessage());
                final Player player = event.getPlayer();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> SChatMenuManager.this.handleChatMessage(player, message), 1L);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncChatEvent(PlayerChatEvent event) {
        if (!Config.getInstance().isAsyncChat() && Config.getInstance().isHighpriority()) {
            if (this.pages.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                final String message = ChatColor.stripColor(event.getMessage());
                this.handleChatMessage(event.getPlayer(), message);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerChatEvent(AsyncPlayerChatEvent event) {
        if (Config.getInstance().isAsyncChat() && Config.getInstance().isHighpriority()) {
            if (this.pages.containsKey(event.getPlayer())) {
                event.setCancelled(true);
                final String message = ChatColor.stripColor(event.getMessage());
                final Player player = event.getPlayer();
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> SChatMenuManager.this.handleChatMessage(player, message), 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerHitEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.pages.containsKey(event.getPlayer())) {
            this.pages.get(event.getPlayer()).hitBlockEvent(event.getClickedBlock());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (this.pages.containsKey(event.getPlayer())) {
            this.pages.remove(event.getPlayer());
        }
    }

    void openPage(SChatpage instance, Player player, SChatpage page) {
        page.setLastInstance(instance);
        this.pages.put(player, page);
        this.pages.get(player).show();
    }
}
