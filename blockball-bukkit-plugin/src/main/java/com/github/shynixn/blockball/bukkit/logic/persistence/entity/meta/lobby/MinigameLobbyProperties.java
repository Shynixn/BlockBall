package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.MinigameLobbyMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
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
public class MinigameLobbyProperties extends HubLobbyProperties implements MinigameLobbyMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "join-spawnpoint")
    private IPosition joinSpawnpoint;

    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "match-duration")
    private int matchDuration;

    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "signs.join")
    private final List<IPosition> joinSigns = new ArrayList<>();

    /**
     * Returns the positions of each sign.
     *
     * @return positions
     */
    @Override
    public List<IPosition> getJoinSignPositions() {
        return Collections.unmodifiableList(this.joinSigns);
    }

    /**
     * Removes the sign-position.
     *
     * @param position position
     */
    @Override
    public void removeJoinSignPosition(IPosition position) {
        if (this.joinSigns.contains(position)) {
            this.joinSigns.remove(position);
        }
    }

    /**
     * Adds a new join sign position.
     *
     * @param position position
     */
    @Override
    public void addJoinSignPosition(IPosition position) {
        if (!this.joinSigns.contains(position)) {
            this.joinSigns.add(position);
        }
    }

    /**
     * Sets the match duration in seconds.
     *
     * @param amountOfSeconds amountOfSeconds
     */
    @Override
    public void setMatchDuration(int amountOfSeconds) {
        this.matchDuration = amountOfSeconds;
    }

    /**
     * Returns the match duration in seconds.
     *
     * @return matchDuration
     */
    @Override
    public int getMatchDuration() {
        return this.matchDuration;
    }

    /**
     * Sets the spawnpoint in the lobby
     *
     * @param location location
     */
    @Override
    public void setLobbySpawnpoint(Object location) {
        if (location != null) {
            this.joinSpawnpoint = new LocationBuilder((Location) location);
        }
    }

    /**
     * Returns the spawnpoint in the lobby.
     *
     * @return location
     */
    @Override
    public Optional<Object> getLobbySpawnpoint() {
        if (this.joinSpawnpoint == null) {
            return Optional.empty();
        }
        return Optional.of(((LocationBuilder) this.joinSpawnpoint).toLocation());
    }
}
