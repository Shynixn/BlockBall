package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.area;

import com.github.shynixn.blockball.api.persistence.entity.AreaSelection;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.lib.YamlSerializer;
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
class SelectedArea<T extends AreaSelection> extends PersistenceObject<T> implements AreaSelection<T> {
    @YamlSerializer.YamlSerialize(orderNumber = 5,value = "down-corner-location")
    private LocationBuilder downCorner;

    @YamlSerializer.YamlSerialize(orderNumber = 6,value = "up-corner-location")
    private LocationBuilder upCorner;

    /**
     * Default constructor
     */
    public SelectedArea() {
    }

    /**
     * Initializes a new selected area
     * @param corner1 corner1
     * @param corner2 corner2
     */
    SelectedArea(Location corner1, Location corner2) {
        super();
        this.setCorners(corner1, corner2);
    }

    /**
     * Returns the upper corner depending on the coordinates.
     *
     * @return location
     */
    @Override
    public Object getUpperCorner() {
        return this.upCorner.toLocation();
    }

    /**
     * Returns the lower corner depending on the coordinates.
     *
     * @return location
     */
    @Override
    public Object getLowerCorner() {
        return this.downCorner.toLocation();
    }

    /**
     * Sets the upper and lower corner depending on the given 2 corners.
     *
     * @param corner1 corner1
     * @param corner2 corner2
     */
    @Override
    public void setCorners(Object corner1, Object corner2) {
        this.calculateDownLocation((Location)corner1, (Location)corner2);
        this.calculateUpLocation((Location)corner1, (Location)corner2);
    }

    /**
     * Returns the center of the area
     *
     * @return center
     */
    @Override
    public Location getCenter() {
        return new Location(Bukkit.getWorld(this.downCorner.getWorldName()), this.downCorner.getBlockX()
                + this.getOffsetX() / 2, this.downCorner.getBlockY()
                + this.getOffsetY() / 2, this.downCorner.getBlockZ()
                + this.getOffsetZ() / 2);
    }

    /**
     * Returns if the given location is inside of this area selection.
     *
     * @param mLocation location
     * @return isInside
     */
    @Override
    public boolean isLocationInSelection(Object mLocation) {
        final Location location = (Location) mLocation;
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
     * Returns the length of the x axe.
     *
     * @return width
     */
    @Override
    public int getOffsetX() {
        return this.upCorner.getBlockX() - this.downCorner.getBlockX() + 1;
    }

    /**
     * Returns the length of the y axe.
     *
     * @return width
     */
    @Override
    public int getOffsetY() {
        return this.upCorner.getBlockY() - this.downCorner.getBlockY() + 1;
    }

    /**
     * Returns the length of the z axe.
     *
     * @return width
     */
    @Override
    public int getOffsetZ() {
        return this.upCorner.getBlockZ() - this.downCorner.getBlockZ();
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
