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
public class BungeeCordSignListener  {
  /*  private final BlockBallBungeeCordManager manager;
    private BungeeCordSignController signController;

    /**
     * Initializes a new listener by plugin
     * @param controller controller
     * @param plugin plugin

    public BungeeCordSignListener(BlockBallBungeeCordManager Plugin plugin) {
        super(plugin);
        this.controller = controller;
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                if (this.manager.signPlacementCache.containsKey(event.getPlayer())) {
                    final String server = this.manager.signPlacementCache.get(event.getPlayer());
                    this.manager.signPlacementCache.remove(event.getPlayer());
                    final Sign sign = (Sign) event.getClickedBlock().getState();
                    final BungeeCordSign bungeeCordSign = this.signController.create(server, sign.getLocation());
                    this.signController.store(bungeeCordSign);

                    this.signController.add(server, sign.getLocation());
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
    }*/
}
