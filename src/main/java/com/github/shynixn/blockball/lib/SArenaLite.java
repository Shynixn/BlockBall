package com.github.shynixn.blockball.lib;

import java.io.Serializable;

import org.bukkit.Location;

public class SArenaLite implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private SLocation downCorner;
    private SLocation upCorner;

    public SArenaLite(String name, Location corner1, Location corner2) {
        super();
        this.name = name;
        this.setCornerLocations(corner1, corner2);
    }

    public void setCornerLocations(Location corner1, Location corner2) {
        this.calculateDownLocation(corner1, corner2);
        this.calculateUpLocation(corner1, corner2);
    }

    protected SArenaLite() {
        super();
    }

    public Location getCenter() {
        if (this.getDownCornerLocation() != null) {
            return new Location(this.getDownCornerLocation().getWorld(), this.getDownCornerLocation().getBlockX() + this.getXWidth() / 2, this.getDownCornerLocation().getBlockY() + this.getYWidth() / 2, this.getDownCornerLocation().getBlockZ() + this.getZWidth() / 2);
        }
        return null;
    }

    public boolean isLocationInArea(Location location) {
        if (location.getWorld().getName().equals(this.upCorner.getWorldName())) {
            if ((this.upCorner.getX() >= location.getX()) && (this.downCorner.getX() <= location.getX())) {
                if ((this.upCorner.getY() >= location.getY() + 1) && (this.downCorner.getY() <= location.getY() + 1)) {
                    if ((this.upCorner.getZ() >= location.getZ()) && (this.downCorner.getZ() <= location.getZ())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public int getXWidth() {
        return this.upCorner.getBlockX() - this.downCorner.getBlockX() + 1;
    }

    public int getYWidth() {
        return this.upCorner.getBlockY() - this.downCorner.getBlockY() + 1;
    }

    public int getZWidth() {
        return this.upCorner.getBlockZ() - this.downCorner.getBlockZ();
    }

    public SLocation getUpCornerLocation() {
        return this.upCorner;
    }

    public SLocation getDownCornerLocation() {
        return this.downCorner;
    }

    private void calculateUpLocation(Location corner1, Location corner2) {
        final int x;
        if (corner1.getBlockX() > corner2.getBlockX()) {
            x = corner1.getBlockX();
        } else {
            x = corner2.getBlockX();
        }
        final int y;
        if (corner1.getBlockY() > corner2.getBlockY()) {
            y = corner1.getBlockY();
        } else {
            y = corner2.getBlockY();
        }
        final int z;
        if (corner1.getBlockZ() > corner2.getBlockZ()) {
            z = corner1.getBlockZ();
        } else {
            z = corner2.getBlockZ();
        }
        this.upCorner = new SLocation(new Location(corner1.getWorld(), x, y, z));
    }

    private void calculateDownLocation(Location corner1, Location corner2) {
        final int x;
        if (corner1.getBlockX() < corner2.getBlockX()) {
            x = corner1.getBlockX();
        } else {
            x = corner2.getBlockX();
        }
        final int y;
        if (corner1.getBlockY() < corner2.getBlockY()) {
            y = corner1.getBlockY();
        } else {
            y = corner2.getBlockY();
        }
        final int z;
        if (corner1.getBlockZ() < corner2.getBlockZ()) {
            z = corner1.getBlockZ();
        } else {
            z = corner2.getBlockZ();
        }
        this.downCorner = new SLocation(new Location(corner1.getWorld(), x, y, z));
    }
}
