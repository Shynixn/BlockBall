package com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.LobbyMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;

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
public class LobbyProperties extends PersistenceObject<LobbyMeta> implements LobbyMeta {
    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getSignPositions() {
        return null;
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeSignPosition(IPosition position) {

    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getRedTeamSignPositions() {
        return null;
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeRedTeamSignPosition(IPosition position) {

    }

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    @Override
    public void addRedTeamSignLocation(IPosition position) {

    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getBlueTeamSignPositions() {
        return null;
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeBlueTeamSignPosition(IPosition position) {

    }

    /**
     * Sets the min amount of players required for a match to start.
     *
     * @param amount amount
     */
    @Override
    public void setMinAmountOfPlayers(int amount) {

    }

    /**
     * Returns the min amount of players required for a match to start.
     *
     * @return amount
     */
    @Override
    public int getMinAmountOfPlayers() {
        return 0;
    }

    /**
     * Sets the match duration in seconds.
     *
     * @param amountOfSeconds amountOfSeconds
     */
    @Override
    public void setMatchDuration(int amountOfSeconds) {

    }

    /**
     * Returns the match duration in seconds.
     *
     * @return matchDuration
     */
    @Override
    public int getMatchDuration() {
        return 0;
    }

    /**
     * Sets the spawnpoint in the lobby
     *
     * @param location location
     */
    @Override
    public void setLobbySpawnpoint(Object location) {

    }

    /**
     * Returns the spawnpoint in the lobby.
     *
     * @return location
     */
    @Override
    public Optional<Object> getLobbySpawnpoint() {
        return null;
    }

    /**
     * Sets the spawnpoint when someone leaves the lobby.
     *
     * @param location location
     */
    @Override
    public void setLobbyLeftSpawnpoint(Object location) {

    }

    /**
     * Returns the spawnpoint when someone leaves the lobby.
     *
     * @return location
     */
    @Override
    public Optional<Object> getLobbyLeftSpawnpoint() {
        return null;
    }

    /**
     * Sets the amount of seconds the lobby countdown lasts.
     *
     * @param amountOfSeconds amountofSeconds
     */
    @Override
    public void setLobbyDuration(int amountOfSeconds) {

    }

    /**
     * Returns the amount of seconds the lobby countdown lasts.
     *
     * @return amountOfSeconds
     */
    @Override
    public int getAmountOfSeconds() {
        return 0;
    }
}
