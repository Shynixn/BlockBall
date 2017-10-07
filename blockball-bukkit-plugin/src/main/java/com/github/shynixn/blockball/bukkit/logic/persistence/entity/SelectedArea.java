package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.LocationBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
class SelectedArea {
    private LocationBuilder downCorner;
    private LocationBuilder upCorner;

    /**
     * Returns the upCorner
     *
     * @return upCorner
     */
    IPosition getUpCornerLocation() {
        return this.upCorner;
    }

    /**
     * Returns the downCorner
     *
     * @return downCorner
     */
    IPosition getDownCornerLocation() {
        return this.downCorner;
    }

    /**
     * Returns the width of the x axe
     *
     * @return width
     */
    int getXWidth() {
        return this.upCorner.getBlockX() - this.downCorner.getBlockX() + 1;
    }

    /**
     * Returns the width of the y axe
     *
     * @return width
     */
    int getYWidth() {
        return this.upCorner.getBlockY() - this.downCorner.getBlockY() + 1;
    }

    /**
     * Returns the width of the z axe
     *
     * @return width
     */
    int getZWidth() {
        return this.upCorner.getBlockZ() - this.downCorner.getBlockZ();
    }

    /**
     * Sets the corner locations
     *
     * @param corner1 corner1
     * @param corner2 corner2
     */
    void setCornerLocations(Location corner1, Location corner2) {
        this.calculateDownLocation(corner1, corner2);
        this.calculateUpLocation(corner1, corner2);
    }

    /**
     * Returns the center of the area
     *
     * @return center
     */
    public Location getCenter() {
        if (this.getDownCornerLocation() != null) {
            return new Location(Bukkit.getWorld(this.getDownCornerLocation().getWorldName()), this.getDownCornerLocation().getBlockX() + this.getXWidth() / 2, this.getDownCornerLocation().getBlockY() + this.getYWidth() / 2, this.getDownCornerLocation().getBlockZ() + this.getZWidth() / 2);
        }
        return null;
    }

    /**
     * Returns if the given location is in this area
     *
     * @param location location
     * @return isThere
     */
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

    /**
     * Calculates the upLocation
     *
     * @param corner1 corner1
     * @param corner2 corner2
     */
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
        this.upCorner = new LocationBuilder(new Location(corner1.getWorld(), x, y, z));
    }

    /**
     * Calculates the downLocation
     *
     * @param corner1 corner1
     * @param corner2 corner2
     */
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
        this.downCorner = new LocationBuilder(new Location(corner1.getWorld(), x, y, z));
    }
}
