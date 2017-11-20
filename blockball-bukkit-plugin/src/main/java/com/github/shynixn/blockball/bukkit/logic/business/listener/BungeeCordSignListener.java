package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController;
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordSign;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallBungeeCordManager;
import com.github.shynixn.blockball.lib.SimpleListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class BungeeCordSignListener extends SimpleListener {

    private final BlockBallBungeeCordManager manager;

    /**
     * Initializes a new listener by plugin.
     *
     * @param plugin plugin
     */
    public BungeeCordSignListener(Plugin plugin, BlockBallBungeeCordManager manager) {
        super(plugin);
        if (manager == null)
            throw new IllegalArgumentException("Manager cannot be null!");
        this.manager = manager;
    }

    /**
     * Handles click on signs to create new server signs or to connect players to the server
     * written on the sign.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (!(event.getClickedBlock().getState() instanceof Sign))
            return;
        if (this.manager.signPlacementCache.containsKey(event.getPlayer())) {
            final String server = this.manager.signPlacementCache.get(event.getPlayer());
            this.manager.signPlacementCache.remove(event.getPlayer());
            final BungeeCordSignController signController = this.manager.getBungeeCordSignController();
            final BungeeCordSign sign = signController.create(server, event.getClickedBlock().getLocation());
            signController.store(sign);
            this.manager.getBungeeCordConnectController().pingServers();
        } else {
            final Sign sign = (Sign) event.getClickedBlock().getState();
            try {
                final BungeeCordSign signInfo;
                if ((signInfo = this.getBungeeCordSignFromLocation(sign.getLocation())) != null) {
                    this.manager.getBungeeCordConnectController().connectToServer(event.getPlayer(), signInfo.getServer());
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot connect player to server.", ex);
            }
        }
    }

    private BungeeCordSign getBungeeCordSignFromLocation(Location signLocation) {
        for (final BungeeCordSign sign : this.manager.getBungeeCordSignController().getAll()) {
            final Location l = (Location) sign.getLocation();
            if (signLocation.getBlockX() == l.getBlockX()) {
                if (signLocation.getBlockY() == l.getBlockY()) {
                    if (signLocation.getBlockZ() == l.getBlockZ()) {
                        return sign;
                    }
                }
            }
        }
        return null;
    }
}
