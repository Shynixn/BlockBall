package com.github.shynixn.blockball.api.persistence.entity;

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
public interface IPosition extends Persistenceable<IPosition>{
    /**
     * Sets the coordinates x, y, z
     *
     * @param x x
     * @param y y
     * @param z z
     * @return builder
     */
    IPosition setCoordinates(double x, double y, double z);

    /**
     * Sets the rotation yaw, pitch
     *
     * @param yaw   yaw
     * @param pitch pitch
     * @return builder
     */
    IPosition setRotation(double yaw, double pitch);

    /**
     * Adds or Subs the given coordinates x, y, z to or from the builder
     *
     * @param x x
     * @param y y
     * @param z z
     * @return builder
     */
    IPosition addCoordinates(double x, double y, double z);

    /**
     * Sets the worldName of the builder
     *
     * @param worldName worldName
     * @return builder
     */
    IPosition setWorldName(String worldName);

    /**
     * Sets the yaw of the builder
     *
     * @param yaw yaw
     * @return builder
     */
    IPosition setYaw(double yaw);

    /**
     * Sets the pitch of the builder
     *
     * @param pitch pitch
     * @return builder
     */
    IPosition setPitch(double pitch);

    /**
     * Sets the x coordinate of the builder
     *
     * @param x x
     * @return builder
     */
    IPosition setX(double x);

    /**
     * Sets the y coordinate of the builder
     *
     * @param y y
     * @return builder
     */
    IPosition setY(double y);

    /**
     * Sets the z coordinate of the builder
     *
     * @param z z
     * @return builder
     */
    IPosition setZ(double z);

    /**
     * Returns the yaw of the builder
     *
     * @return yaw
     */
    double getYaw();

    /**
     * Returns the pitch of the builder
     *
     * @return pitch
     */
    double getPitch();

    /**
     * Returns the x coordinate of the builder
     *
     * @return x
     */
    double getX();

    /**
     * Returns the y coordinate of the builder
     *
     * @return y
     */
    double getY();

    /**
     * Returns the z coordinate of the builder
     *
     * @return z
     */
    double getZ();

    /**
     * Returns the x coordinate as int of the builder
     *
     * @return x
     */
    int getBlockX();

    /**
     * Returns the y coordinate as int of the builder
     *
     * @return y
     */
    int getBlockY();

    /**
     * Returns the z coordinate as int of the builder
     *
     * @return z
     */
    int getBlockZ();

    /**
     * Returns the worldname of the builder
     *
     * @return worldName
     */
    String getWorldName();

    /**
     * Returns the relativePosition to a given direction
     *
     * @param distance  distance
     * @param direction direction
     * @return builder
     */
    IPosition relativePosition(double distance, Direction direction);

    /**
     * Defines different relative positions
     */
    enum Direction {
        FORWARD,
        BACKWARDS,
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}
