package com.github.shynixn.blockball.api.entities;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.util.Vector;

public interface Ball {

    /**
     * Kicks the ball with the given strength parameters
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    void kick(Entity entity, double horizontalStrength, double verticalStrength);

    /**
     * Kicks the ball with the default strength values
     *
     * @param entity entity
     */
    void kick(Entity entity);

    /**
     * Passes the ball with the given strength parameters
     *
     * @param entity             entity
     * @param horizontalStrength horizontalStrength
     * @param verticalStrength   verticalStrength
     */
    void pass(Entity entity, double horizontalStrength, double verticalStrength);

    /**
     * Passes the ball with the default strength values
     *
     * @param entity entity
     */
    void pass(Entity entity);

    void spawn(Location location);

    void despawn();

    void setKickStrengthHorizontal(double strength);

    void setKickStrengthVertical(double strength);

    void setSmall(boolean isSmall);

    int getEntityId();

    void damage();

    void setSkin(String skin);

    ArmorStand getDesignEntity();

    Rabbit getMovementEntity();

    String getSkin();

    Vector getVelocity();

    void resetSkin();

    void teleport(Location location);

    Location getLocation();

    boolean isDead();

    void setVelocity(Vector vector);

    boolean isRotating();

    void setRotating(boolean isRotating);
}
