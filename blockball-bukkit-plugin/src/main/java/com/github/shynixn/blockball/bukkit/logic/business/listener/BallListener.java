package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.bukkit.event.ball.BallDeathEvent;
import com.github.shynixn.blockball.api.bukkit.event.ball.BallHitWallEvent;
import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

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
public class BallListener extends SimpleListener {
    private final BlockBallManager manager;

    /**
     * Initializes a new ball listener
     *
     * @param manager manager
     * @param plugin  plugin
     */
    public BallListener(BlockBallManager manager, Plugin plugin) {
        super(plugin);
        this.manager = manager;
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            for (final World world : Bukkit.getWorlds()) {
                for (final Entity entity : world.getEntities()) {
                    if (BallListener.this.isDeadBall(entity)) {
                        entity.remove();
                    }
                }
            }
        }, 0L, 20L * 10);
    }

    /**
     * Gets called when a player rightClicks on a ball and cancels it or launches a pass
     *
     * @param event event
     */
    @EventHandler
    public void entityRightClickEvent(final PlayerInteractAtEntityEvent event) {
        final Optional<Ball> optBall = this.manager.getBallController().findByBallByEntity(event.getRightClicked());
        if (optBall.isPresent()) {
            if (Config.getInstance().isEngineV2Enabled()) {
                final Ball ball = optBall.get();
                ball.pass(event.getPlayer());
            }
            event.setCancelled(true);
        } else if (this.isDeadBall(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    /**
     * Gets called when a ball entity dies
     *
     * @param event event
     */
    @EventHandler
    public void onBallDeathEvent(BallDeathEvent event) {
        this.manager.getBallController().remove(event.getBall());
    }

    /**
     * Gets called when a player hits the ball and kicks the ball if engine v2 is selected
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerDamageBallEvent(EntityDamageByEntityEvent event) {
        final Optional<Ball> optBall = this.manager.getBallController().findByBallByEntity(event.getEntity());
        if (Config.getInstance().isEngineV2Enabled()) {
            if (optBall.isPresent()) {
                final Ball ball = optBall.get();
                ball.kick(event.getDamager());
            }
        }
    }

    /**
     * Gets called when the ball takes damage and cancels all of it
     *
     * @param event event
     */
    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        final Optional<Ball> optBall = this.manager.getBallController().findByBallByEntity(event.getEntity());
        if (optBall.isPresent()) {
            if (event.getCause() != null && event.getCause() != DamageCause.FALL) {
                optBall.get().damage(event.getFinalDamage());
            }
            event.setCancelled(true);
        }
    }

    /**
     * Gets called when a player tries to leah a ball and cancels all of it
     *
     * @param event event
     */
    @EventHandler
    public void entityLeashEvent(PlayerLeashEntityEvent event) {
        final Optional<Ball> optBall = this.manager.getBallController().findByBallByEntity(event.getEntity());
        if (optBall.isPresent()) {
            event.setCancelled(true);
        }
    }

    /**
     * Lets the ball bounce back when he hits a wall.
     *
     * @param event event
     */
    @EventHandler
    public void onBallHitWallEvent(BallHitWallEvent event) {
        for (final Integer id : event.getBall().getMeta().getBounceMaterials().keySet()) {
            if (event.getBlock() != null) {
                if (event.getBlock().getType().getId() == id
                        && event.getBlock().getData() == event.getBall().getMeta().getBounceMaterials().get(id)) {
                    event.getBall().bounceBack();
                }
            }
        }
    }

    /**
     * Checks if the ball is a dead ball
     *
     * @param entity entity
     * @return isDead
     */
    private boolean isDeadBall(Entity entity) {
        final Optional<Ball> optBall = this.manager.getBallController().findByBallByEntity(entity);
        if (!optBall.isPresent()) {
            if (entity instanceof ArmorStand) {
                final ArmorStand stand = (ArmorStand) entity;
                final int xidentifier = (int) stand.getBodyPose().getZ();
                final int identifier = (int) stand.getRightArmPose().getX();
                if (xidentifier == 2777 && identifier == 2777) {
                    return true;
                }
            } else if (entity instanceof Rabbit && entity.getCustomName() != null && entity.getCustomName().equals("MyBallsIdentifier")) {
                return true;
            }
        }
        return false;
    }
}
