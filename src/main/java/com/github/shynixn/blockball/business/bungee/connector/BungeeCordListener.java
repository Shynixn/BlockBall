package com.github.shynixn.blockball.business.bungee.connector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

/**
 * Created by Shynixn
 */
class BungeeCordListener implements Listener {
    private final BungeeCordController controller;

    BungeeCordListener(BungeeCordController controller, JavaPlugin plugin) {
        super();
        this.controller = controller;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                if (this.controller.lastServer.containsKey(event.getPlayer())) {
                    final String server = this.controller.lastServer.get(event.getPlayer());
                    this.controller.lastServer.remove(event.getPlayer());
                    final Sign sign = (Sign) event.getClickedBlock().getState();
                    this.controller.add(server, sign.getLocation());
                    this.controller.updateSign(sign, new ServerInfo.Container(server, 0, 0));
                } else {
                    final Sign sign = (Sign) event.getClickedBlock().getState();
                    try {
                        final BungeeCordSignInfo signInfo;
                        if ((signInfo = this.getBungeeCordSignInfo(sign.getLocation())) != null) {
                            this.controller.connect(event.getPlayer(), signInfo.getServer());
                        }
                    } catch (final Exception ex) {
                        Bukkit.getLogger().log(Level.WARNING, "Cannot connect player to server.", ex);
                    }
                }
            }
        }
    }

    private BungeeCordSignInfo getBungeeCordSignInfo(Location location2) {
        for (final BungeeCordSignInfo info : this.controller.signs.toArray(new BungeeCordSignInfo[this.controller.signs.size()])) {
            final Location location1 = info.getLocation();
            if (location1.getBlockX() == location2.getBlockX()) {
                if (location1.getBlockY() == location2.getBlockY()) {
                    if (location1.getBlockZ() == location2.getBlockZ()) {
                        return info;
                    }
                }
            }
        }
        return null;
    }

}
