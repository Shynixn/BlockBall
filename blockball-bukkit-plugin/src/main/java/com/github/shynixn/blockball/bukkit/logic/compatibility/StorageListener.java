package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.business.proxy.BallProxy;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
public class StorageListener extends SimpleListener {

    private final BallEntityController ballController;
    private final List<UUID> cache = new ArrayList<>();

    /**
     * Initializes a new listener by plugin.
     *
     * @param ballController ballController
     * @param plugin         plugin
     */
    public StorageListener(Plugin plugin, BallEntityController ballController) {
        super(plugin);
        if (ballController == null)
            throw new IllegalArgumentException("BallEntityController cannot be null!");
        this.ballController = ballController;
    }

    /**
     * Removes the ball of a player before he leaves the server.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        final Optional<BallProxy> optBall = this.getBallFromPlayer(event.getPlayer());
        optBall.ifPresent(BallProxy::remove);
    }

    /**
     * Removes the ball of a player before he teleports to another world
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (!event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            final Optional<BallProxy> optBall = this.getBallFromPlayer(event.getPlayer());
            optBall.ifPresent(BallProxy::remove);
        }
    }

    /**
     * Saves the ball asynchronously when it is inside of a chunk which gets unloaded. Replaces the entity with an invisible armorstands which holds the
     * ball information for respawning.
     *
     * @param event event
     */
    @EventHandler
    public void onChunkSaveEvent(ChunkUnloadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            final Optional<BallProxy> ball;
            if (entity instanceof LivingEntity) {
                if ((ball = this.ballController.getBallFromEntity(entity)).isPresent()) {
                    if (ball.get().getPersistent()) {
                        this.pushToCache(ball.get());
                        this.ballController.remove(ball.get());
                        this.ballController.saveAndDestroy(ball.get(), true);
                        final ArmorStand armorStand = (ArmorStand) event.getWorld().spawnEntity(ball.get().getLocation(), EntityType.ARMOR_STAND);
                        armorStand.setCustomNameVisible(false);
                        armorStand.setRemoveWhenFarAway(false);
                        armorStand.setVisible(false);
                        armorStand.setCustomName("balluuid-" + ball.get().getUuid().toString());
                    } else {
                        ball.get().remove();
                    }
                }
            }
        }
    }

    /**
     * Saves all balls from the world similar to chunk unload.
     *
     * @param event event
     */
    @EventHandler
    public void onWorldSaveEvent(WorldSaveEvent event) {
        for (final Entity entity : event.getWorld().getEntities()) {
            final Optional<BallProxy> ball;
            if (entity instanceof LivingEntity) {
                if ((ball = this.ballController.getBallFromEntity(entity)).isPresent()) {
                    if (ball.get().getPersistent()) {
                        this.ballController.saveAndDestroy(ball.get(), false);
                    }
                }
            }
        }
    }

    /**
     * Checks if an ball armorstand is inside of the chunk and spawns a ball at the location-
     *
     * @param event event
     */
    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity.getCustomName() != null) {
                if (entity.getCustomName().equals("ResourceBallsPlugin")) {
                    this.plugin.getLogger().log(Level.INFO, "Removed unknown ball.");
                    entity.remove();
                } else if (entity.getCustomName().startsWith("balluuid-")) {
                    entity.remove();
                    this.ballController.loadAndSpawn(UUID.fromString(entity.getCustomName().replace("balluuid-", "")));
                }
            }
        }
    }

    private Optional<BallProxy> getBallFromPlayer(Player player) {
        for (final BallProxy ball : this.ballController.getAll()) {
            if (ball.getOwner().isPresent() && ball.getOwner().get().equals(player)) {
                return Optional.of(ball);
            }
        }
        return Optional.empty();
    }

    /**
     * Cache for ball saving so the ball gets not saved twice by ridiculous chunk unloading of minecraft.
     *
     * @param ball ball
     * @return success
     */
    private boolean pushToCache(BallProxy ball) {
        final UUID uuid = ball.getUuid();
        if (!this.cache.contains(uuid)) {
            this.cache.add(uuid);
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                this.cache.remove(uuid);
            }, 60L);
            return true;
        }
        return false;
    }
}
