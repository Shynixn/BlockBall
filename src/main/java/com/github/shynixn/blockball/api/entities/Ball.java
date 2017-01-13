package com.github.shynixn.blockball.api.entities;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Rabbit;
import org.bukkit.util.Vector;

public interface Ball {
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
