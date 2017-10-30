package com.github.shynixn.blockball.api.persistence.entity;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public interface AreaSelection<T extends AreaSelection> extends Persistenceable<T> {
    /**
     * Returns the upper corner depending on the coordinates.
     *
     * @return location
     */
    Object getUpperCorner();

    /**
     * Returns the lower corner depending on the coordinates.
     *
     * @return location
     */
    Object getLowerCorner();

    /**
     * Sets the upper and lower corner depending on the given 2 corners.
     *
     * @param corner1 corner1
     * @param corner2 corner2
     */
    void setCorners(Object corner1, Object corner2);

    /**
     * Returns the center of the selected area.
     *
     * @return location;
     */
    Object getCenter();

    /**
     * Returns if the given location is inside of this area selection.
     *
     * @param location location
     * @return isInside
     */
    boolean isLocationInSelection(Object location);

    /**
     * Returns the length of the x axe.
     *
     * @return width
     */
    int getOffsetX();

    /**
     * Returns the length of the y axe.
     *
     * @return width
     */
    int getOffsetY();

    /**
     * Returns the length of the z axe.
     *
     * @return width
     */
    int getOffsetZ();
}
