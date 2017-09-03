package com.github.shynixn.blockball.api.entities;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.io.Serializable;

public interface IPosition extends ConfigurationSerializable, Serializable {
    IPosition setCoordinates(double x, double y, double z);

    IPosition setRotation(double yaw, double pitch);

    IPosition addCoordinates(double x, double y, double z);

    Location toLocation();

    Vector toVector();

    EulerAngle toAngle();

    void applyVelocity(Entity entity);

    void applyLocation(Entity entity);

    IPosition setWorld(World world);

    IPosition setWorldName(String worldname);

    IPosition setYaw(double yaw);

    IPosition setPitch(double pitch);

    IPosition setX(double x);

    IPosition setY(double y);

    IPosition setZ(double z);

    double getYaw();

    double getPitch();

    World getWorld();

    double getX();

    double getY();

    double getZ();

    int getBlockX();

    int getBlockY();

    int getBlockZ();

    String getWorldName();

    String toString();

    boolean equals(Object arg0);

    IPosition copy();
}
