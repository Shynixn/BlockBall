package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.HubLobbyMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.lib.YamlSerializer;
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
public class HubLobbyProperties extends PersistenceObject<HubLobbyMeta> implements HubLobbyMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "leave-spawnpoint")
    private IPosition leaveSpawnpoint;

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "signs.redteam")
    private final List<IPosition> redTeamSigns = new ArrayList<>();

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "signs.blueteam")
    private final List<IPosition> blueTeamSigns = new ArrayList<>();

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "signs.leave")
    private final List<IPosition> leaveSigns = new ArrayList<>();

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getRedTeamSignPositions() {
        return Collections.unmodifiableList(this.redTeamSigns);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeRedTeamSignPosition(IPosition position) {
        if (this.redTeamSigns.contains(position)) {
            this.redTeamSigns.remove(position);
        }
    }

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    @Override
    public void addRedTeamSignLocation(IPosition position) {
        if (!this.redTeamSigns.contains(position)) {
            this.redTeamSigns.add(position);
        }
    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getBlueTeamSignPositions() {
        return Collections.unmodifiableList(this.blueTeamSigns);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeBlueTeamSignPosition(IPosition position) {
        if (this.blueTeamSigns.contains(position)) {
            this.blueTeamSigns.remove(position);
        }
    }

    /**
     * Adds a blueTeamSignLocation
     *
     * @param position position
     */
    @Override
    public void addBlueTeamSignLocation(IPosition position) {
        if (!this.blueTeamSigns.contains(position)) {
            this.blueTeamSigns.add(position);
        }
    }

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    @Override
    public List<IPosition> getLeaveSignPositions() {
        return Collections.unmodifiableList(this.leaveSigns);
    }

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    @Override
    public void removeLeaveSignPosition(IPosition position) {
        if (this.leaveSigns.contains(position)) {
            this.leaveSigns.remove(position);
        }
    }

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    @Override
    public void addLeaveSignLocation(IPosition position) {
        if (!this.leaveSigns.contains(position)) {
            this.leaveSigns.add(position);
        }
    }

    /**
     * Sets the spawnpoint when someone leaves the match.
     *
     * @param location location
     */
    @Override
    public void setLeaveSpawnpoint(Object location) {
        if (location != null) {
            this.leaveSpawnpoint = new LocationBuilder((Location) location);
        }
    }

    /**
     * Returns the spawnpoint when someone leaves thematch.
     *
     * @return location
     */
    @Override
    public Optional<Object> getLeaveSpawnpoint() {
        if (this.leaveSpawnpoint == null) {
            return Optional.empty();
        }
        return Optional.of(((LocationBuilder) this.leaveSpawnpoint).toLocation());
    }
}
