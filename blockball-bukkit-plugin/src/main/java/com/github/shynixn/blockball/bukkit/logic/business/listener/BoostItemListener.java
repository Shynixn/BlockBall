package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.persistence.entity.meta.effect.SoundEffectMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.BoosItemMeta;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.effect.SoundBuilder;
import com.github.shynixn.blockball.lib.SimpleListener;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;
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
public class BoostItemListener extends SimpleListener {

    private static final SoundEffectMeta itemPickUpSound = new SoundBuilder("NOTE_PLING", 2.0, 2.0);
    private GameController gameController;

    /**
     * Initializes a new listener by plugin
     *
     * @param plugin plugin
     */
    public BoostItemListener(Plugin plugin, GameController gameController) {
        super(plugin);
        if (gameController == null) {
            throw new IllegalArgumentException("Gamecontroller cannot be null!");
        }
        this.gameController = gameController;
    }

    /**
     * Applies a boost effect if a player picks up an item from te ground.
     *
     * @param event event
     */
    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        if (optGame.isPresent()) {
            final Map<Object, BoosItemMeta> groundItems = optGame.get().getGroundItems();
            for (final Object item : groundItems.keySet()) {
                if (item.equals(event.getItem())) {
                    try {
                        itemPickUpSound.applyToPlayers(event.getPlayer());
                    } catch (final Exception e) {
                        BlockBallPlugin.logger().log(Level.WARNING, "Failed to play sound.", e);
                    }
                    final BoosItemMeta boosItemMeta = groundItems.get(item);
                    boosItemMeta.apply(event.getPlayer());
                    event.setCancelled(true);
                    event.getItem().remove();
                }
            }
        }
    }

    /**
     * Checks if the item is a boostItem and removes it from a game.
     *
     * @param event event
     */
    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        for (final Game game : this.gameController.getAll()) {
            if (game.getArena().isLocationInSelection(event.getLocation())) {
                if (game.getGroundItems().containsKey(event.getEntity())) {
                    game.removeGroundItem(event.getEntity());
                }
            }
        }
    }
}
