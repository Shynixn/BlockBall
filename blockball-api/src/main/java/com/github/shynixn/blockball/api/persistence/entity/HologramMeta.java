package com.github.shynixn.blockball.api.persistence.entity;

import java.util.List;
import java.util.Optional;

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
public interface HologramMeta extends Persistenceable<HologramMeta> {
    /**
     * Adds a line to the end of a hologram.
     *
     * @param text text
     * @return line
     */
    HologramMeta addLine(String text);

    /**
     * Removes the line at the position.
     *
     * @param position position
     * @return instance
     */
    HologramMeta removeLine(int position);

    /**
     * Removes all matching lines from the hologram.
     *
     * @param text text
     * @return instance
     */
    HologramMeta removeLine(String text);

    /**
     * Sets a text at the given position.
     *
     * @param position position
     * @param text     text
     * @return instance
     */
    HologramMeta setLine(int position, String text);

    /**
     * Returns a line at the given position.
     *
     * @param position position
     * @return line
     */
    Optional<String> getLine(int position);

    /**
     * Returns all lines in an unmodifiable list.
     *
     * @return lines
     */
    List<String> getLines();

    /**
     * Returns if the hologram is enabled
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Sets the hologram enabled
     *
     * @param enabled enabled
     * @return instance
     */
    HologramMeta setEnabled(boolean enabled);

    /**
     * Returns the location of the hologram.
     *
     * @return location
     */
    Optional<Object> getLocation();

    /**
     * Sets the location of the hologram
     *
     * @param location location
     * @return instance
     */
    HologramMeta setLocation(Object location);
}
