package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.api.entities.IPosition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@Deprecated
public class SLocation implements IPosition {
    private static final long serialVersionUID = 1L;
    private String world;
    private double x;
    private double y;
    private double z;
    private double yaw;
    private double pitch;

    public SLocation(String worldname, double x, double y, double z, float yaw, float pitch) {
        super();
        this.world = worldname;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public SLocation(EulerAngle eulerAngle) {
        super();
        this.x = eulerAngle.getX();
        this.y = eulerAngle.getY();
        this.z = eulerAngle.getZ();
    }

    public SLocation(Map<String, Object> keys) {
        super();
        this.x = (double) keys.get("x");
        this.y = (double) keys.get("y");
        this.z = (double) keys.get("z");
        this.yaw = (double) keys.get("yaw");
        this.pitch = (double) keys.get("pitch");
        this.world = (String) keys.get("worldname");
    }

    @Override
    public SLocation copy() {
        return new SLocation(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    public SLocation() {
        super();
    }

    public SLocation(String worldname, double x, double y, double z, double yaw, double pitch) {
        this(worldname, x, y, z, (float) yaw, (float) pitch);
    }

    public SLocation(String worldname, double x, double y, double z) {
        this(worldname, x, y, z, 0, 0);
    }

    public SLocation(org.bukkit.Location location) {
        this(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    @Override
    public IPosition setCoordinates(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public IPosition setRotation(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    @Override
    public IPosition addCoordinates(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public Location toLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, (float) this.yaw, (float) this.pitch);
    }

    @Override
    public Vector toVector() {
        return new Vector(this.x, this.y, this.z);
    }

    @Override
    public EulerAngle toAngle() {
        return new EulerAngle(this.x, this.y, this.z);
    }

    @Override
    public void applyVelocity(Entity entity) {
        entity.setVelocity(this.toVector());
    }

    @Override
    public void applyLocation(Entity entity) {
        entity.teleport(this.toLocation());
    }

    @Override
    public IPosition setWorld(World world) {
        this.world = world.getName();
        return this;
    }

    @Override
    public IPosition setWorldName(String worldname) {
        this.world = worldname;
        return this;
    }

    @Override
    public IPosition setYaw(double yaw) {
        this.yaw = yaw;
        return this;
    }

    @Override
    public IPosition setPitch(double pitch) {
        return null;
    }

    @Override
    public IPosition setX(double x) {
        this.x = x;
        return this;
    }

    @Override
    public IPosition setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public IPosition setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public boolean equals(Object arg0) {
        if (arg0 instanceof SLocation) {
            final SLocation location = (SLocation) arg0;
            if (location.getBlockX() == this.getBlockX() && location.getBlockY() == this.getBlockY() && location.getBlockZ() == this.getBlockZ())
                return true;
        }
        return false;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z, (float) this.yaw, (float) this.pitch);
    }

    @Override
    public double getYaw() {
        return this.yaw;
    }

    @Override
    public double getPitch() {
        return this.pitch;
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }

    @Override
    public int getBlockX() {
        return (int) this.x;
    }

    @Override
    public int getBlockY() {
        return (int) this.y;
    }

    @Override
    public int getBlockZ() {
        return (int) this.z;
    }

    @Override
    public String getWorldName() {
        return this.world;
    }

    @Override
    public String toString() {
        if (this.getWorld() == null)
            return "unloaded world " + ' ' + this.getBlockX() + "x " + this.getBlockY() + "y " + this.getBlockZ() + "z.";
        return this.getWorld().getName() + ' ' + this.getBlockX() + "x " + this.getBlockY() + "y " + this.getBlockZ() + "z.";
    }

    public int distance(SLocation to) {
        return (int) Math.sqrt(Math.pow(this.x - to.getX(), 2) + Math.pow(this.y - to.getY(), 2) + Math.pow(this.z - to.getZ(), 2));
    }

    public static boolean compareLocation(Location location1, Location location2) {
        if (location1.getBlockX() == location2.getBlockX()) {
            if (location1.getBlockY() == location2.getBlockY()) {
                if (location1.getBlockZ() == location2.getBlockZ()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> serialize() {
        final HashMap<String, Object> map = new HashMap<>();
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        map.put("yaw", this.yaw);
        map.put("pitch", this.pitch);
        map.put("worldname", this.world);
        return map;
    }
}
