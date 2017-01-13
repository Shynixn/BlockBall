package com.github.shynixn.blockball.business.logic.ball;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.events.BallDeathEvent;
import com.github.shynixn.blockball.lib.SEntityCompareable;
import com.github.shynixn.blockball.lib.SEvents;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

class BallListener extends SEvents {
    private final BallController manager;

    BallListener(BallController manager) {
        super();
        this.manager = manager;
        plugin.getServer().getScheduler().runTaskTimer(plugin, new RemoveOldBalls(), 0L, 20L * 10);
    }

    @EventHandler
    public void entityRightClickEvent(final PlayerInteractAtEntityEvent event) {
        if (this.isBall(event.getRightClicked())) {
            event.setCancelled(true);
        } else if (this.isDeadBall(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBallDeathEvent(BallDeathEvent event) {
        this.manager.removeBall(event.getBall());
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
        for (final Ball ball : this.manager.getBalls()) {
            final SEntityCompareable compareable = (SEntityCompareable) ball;
            if (compareable.isSameEntity(entity))
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
