package com.github.shynixn.blockball.api.persistence.entity;

import java.util.List;
import java.util.Optional;

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
public interface EventMeta extends Persistenceable<EventMeta>{

    /**
     * Adds the name of a player for the redTeam
     *
     * @param name name
     */
    void addRedName(String name);

    /**
     * Removes the name of a player of the redTeam
     *
     * @param name name
     */
    void removeRedName(String name);

    /**
     * Returns all names of the redPlayers
     *
     * @return names
     */
    List<String> getRedPlayerNames();

    /**
     * Adds the name of a player for the blueTeam
     *
     * @param name name
     */
    void addBlueName(String name);

    /**
     * Removes the name of a player of the blueTeam
     *
     * @param name name
     */
    void removeBlueName(String name);

    /**
     * Returns all names of the bluePlayers
     *
     * @return names
     */
    List<String> getBluePlayerNames();

    /**
     * Returns the name of the referee if present
     *
     * @return referee
     */
    Optional<String> getRefereeName();

    /**
     * Sets the name of the referee
     *
     * @param name name
     */
    void setRefereeName(String name);
}
