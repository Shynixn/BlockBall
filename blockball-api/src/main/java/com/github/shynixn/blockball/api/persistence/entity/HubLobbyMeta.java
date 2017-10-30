package com.github.shynixn.blockball.api.persistence.entity;

import java.util.List;
import java.util.Optional;

/**
 * Metadata for a match-lobby.
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
public interface HubLobbyMeta extends Persistenceable<HubLobbyMeta> {

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getLeaveSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeLeaveSignPosition(IPosition position);

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    void addLeaveSignLocation(IPosition position);

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getRedTeamSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeRedTeamSignPosition(IPosition position);

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    void addRedTeamSignLocation(IPosition position);

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getBlueTeamSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeBlueTeamSignPosition(IPosition position);

    /**
     * Adds a blueTeamSignLocation
     *
     * @param position position
     */
    void addBlueTeamSignLocation(IPosition position);

    /**
     * Sets the spawnpoint when someone leaves the match.
     *
     * @param location location
     */
    void setLeaveSpawnpoint(Object location);

    /**
     * Returns the spawnpoint when someone leaves thematch.
     *
     * @return location
     */
    Optional<Object> getLeaveSpawnpoint();
}
