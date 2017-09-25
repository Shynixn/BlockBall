package com.github.shynixn.blockball.bukkit.logic.ball;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.events.BallDeathEvent;
import com.github.shynixn.blockball.bukkit.Config;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.lib.SimpleListener;
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
import org.bukkit.plugin.java.JavaPlugin;

class BallListener extends SimpleListener {
    private final BallRepository manager;

    BallListener(BallRepository manager) {
        super(JavaPlugin.getPlugin(BlockBallPlugin.class));
        this.manager = manager;
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new RemoveOldBalls(), 0L, 20L * 10);
    }

    @EventHandler
    public void entityRightClickEvent(final PlayerInteractAtEntityEvent event) {
        if (this.isBall(event.getRightClicked())) {
            if (Config.getInstance().isUseEngineV2()) {
                final Ball ball = this.getBall(event.getRightClicked());
                ball.pass(event.getPlayer());
            }
            event.setCancelled(true);
        } else if (this.isDeadBall(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBallDeathEvent(BallDeathEvent event) {
        this.manager.remove(event.getBall());
    }

    @EventHandler
    public void onPlayerDamageBallEvent(EntityDamageByEntityEvent event) {
        if (Config.getInstance().isUseEngineV2()) {
            if (this.isBall(event.getEntity())) {
                final Ball ball = this.getBall(event.getEntity());
                ball.kick(event.getDamager());
            }
        }
    }

    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        if (this.isBall(event.getEntity())) {
            if (event.getCause() != null && event.getCause() != DamageCause.FALL)
                this.getBall(event.getEntity()).damage();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void entityLeashEvent(PlayerLeashEntityEvent event) {
        if (this.isBall(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    private Ball getBall(Entity entity) {
        for (final Ball ball : this.manager.getAll()) {
            if (ball.isSameEntity(entity))
                return ball;
        }
        return null;
    }

    private boolean isDeadBall(Entity entity) {
        if (!this.isBall(entity)) {
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

    private boolean isBall(Entity entity) {
        return this.getBall(entity) != null;
    }

    private class RemoveOldBalls implements Runnable {
        @Override
        public void run() {
            for (final World world : Bukkit.getWorlds()) {
                for (final Entity entity : world.getEntities()) {
                    if (BallListener.this.isDeadBall(entity)) {
                        entity.remove();
                    }
                }
            }
        }
    }
}
