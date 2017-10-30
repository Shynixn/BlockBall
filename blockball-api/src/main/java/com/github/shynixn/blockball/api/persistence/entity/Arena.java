package com.github.shynixn.blockball.api.persistence.entity;

import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;

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
public interface Arena extends Persistenceable<Arena> {
    /**
     * Returns the ball settings for this arena
     *
     * @return ballMeta
     */
    BallMeta getBallMeta();

    /**
     * Returns the meta data for lobbies
     *
     * @return lobbyMeta
     */
    <T extends HubLobbyMeta> T getLobbyMeta();

    /**
     * Returns the meta data for teams
     *
     * @return teamMeta
     */
    TeamMeta getTeamMeta();

    /**
     * Returns the meta data for events
     *
     * @return event
     */
    EventMeta getEventMeta();

    /**
     * Returns the meta data of the red team
     *
     * @return meta
     */
    TeamMeta getRedTeamMeta();

    /**
     * Returns the meta data of the blue team
     *
     * @return meta
     */
    TeamMeta getBlueTeamMeta();

    /**
     * Returns the unique name of the arena
     *
     * @return name
     */
    String getName();

    /**
     * Returns if the arena is enabled
     *
     * @return enabled
     */
    boolean isEnabled();

    /**
     * Sets the arena enabled
     *
     * @param enabled enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Returns the displayName of the arena if present
     *
     * @return displayName
     */
    Optional<String> getDisplayName();

    /**
     * Sets the displayName of the arena
     *
     * @param displayName displayName
     */
    void setDisplayName(String displayName);

    /**
     * Returns the gameType of the arena
     *
     * @return gameType
     */
    GameType getGameType();

    /**
     * Sets the gameType of the arena
     *
     * @param type type
     */
    void setGameType(GameType type);

    /**
     * Returns the center of the arena
     *
     * @return center
     */
    Object getCenter();

    /**
     * Returns the spawnpoint of the ball
     *
     * @return ballSpawnpoint
     */
    Object getBallSpawnLocation();

    /**
     * Sets the spawnpoint of the ball
     *
     * @param location location
     */
    void setBallSpawnLocation(Object location);

    /**
     * Returns if the given location is inside of this arena
     *
     * @param location location
     * @return isInside
     */
    boolean isLocationInArena(Object location);
}
