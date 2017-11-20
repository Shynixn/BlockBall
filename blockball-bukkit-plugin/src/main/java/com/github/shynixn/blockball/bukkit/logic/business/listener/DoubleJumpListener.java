package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.persistence.entity.meta.gadget.DoubleJumpMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.lib.SimpleListener;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
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
public class DoubleJumpListener extends SimpleListener {

    private GameController gameController;

    /**
     * Initializes a new listener by plugin.
     *
     * @param plugin plugin
     */
    public DoubleJumpListener(Plugin plugin, GameController gameController) {
        super(plugin);
        if (gameController == null) {
            throw new IllegalArgumentException("Gamecontroller cannot be null!");
        }
        this.gameController = gameController;
    }

    /**
     * Allows double jump for players in games.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (event.getPlayer().isOnGround()) {
            final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
            if (optGame.isPresent()) {
                event.getPlayer().setAllowFlight(true);
            }
        }
    }

    /**
     * Handles double jump pressing in games.
     *
     * @param event event
     */
    @EventHandler
    public void onToggleFlightEvent(PlayerToggleFlightEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
            return;
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        if (optGame.isPresent()) {
            final Player player = event.getPlayer();
            final Game game = optGame.get();
            player.setAllowFlight(false);
            player.setFlying(false);
            event.setCancelled(true);
            final DoubleJumpMeta doubleJumpMeta = game.getArena().getMeta().find(DoubleJumpMeta.class).get();
            if (doubleJumpMeta.isEnabled()) {
                player.setVelocity(player.getLocation().getDirection()
                        .multiply(doubleJumpMeta.getHorizontalStrength())
                        .setY(doubleJumpMeta.getVerticalStrength()));
                try {
                    doubleJumpMeta.getSoundEffect().applyToLocation(player.getLocation());
                    doubleJumpMeta.getParticleEffect().apply(player.getLocation());
                } catch (final Exception e) {
                    BlockBallPlugin.logger().log(Level.WARNING, "Invalid 1.8/1.9 sound. [DoubleJumpSound]", e);
                }
            }
        }
    }
}
