package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby;

import com.github.shynixn.blockball.api.persistence.entity.EventMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;

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
public class EventLobbyProperties extends PersistenceObject<EventMeta> implements EventMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "referee")
    private String referee;
    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "team.red")
    private final List<String> red = new ArrayList<>();
    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "team.blue")
    private final List<String> blue = new ArrayList<>();

    /**
     * Adds the name of a player for the redTeam
     *
     * @param name name
     */
    @Override
    public void addRedName(String name) {
        if (!this.red.contains(name)) {
            this.red.add(name);
        }
    }

    /**
     * Removes the name of a player of the redTeam
     *
     * @param name name
     */
    @Override
    public void removeRedName(String name) {
        if (this.red.contains(name)) {
            this.red.remove(name);
        }
    }

    /**
     * Returns all names of the redPlayers
     *
     * @return names
     */
    @Override
    public List<String> getRedPlayerNames() {
        return Collections.unmodifiableList(this.red);
    }

    /**
     * Adds the name of a player for the blueTeam
     *
     * @param name name
     */
    @Override
    public void addBlueName(String name) {
        if (!this.blue.contains(name)) {
            this.blue.add(name);
        }
    }

    /**
     * Removes the name of a player of the blueTeam
     *
     * @param name name
     */
    @Override
    public void removeBlueName(String name) {
        if (this.blue.contains(name)) {
            this.blue.remove(name);
        }
    }

    /**
     * Returns all names of the bluePlayers
     *
     * @return names
     */
    @Override
    public List<String> getBluePlayerNames() {
        return Collections.unmodifiableList(this.blue);
    }

    /**
     * Returns the name of the referee if present
     *
     * @return referee
     */
    @Override
    public Optional<String> getRefereeName() {
        return Optional.ofNullable(referee);
    }

    /**
     * Sets the name of the referee
     *
     * @param name name
     */
    @Override
    public void setRefereeName(String name) {
        this.referee = name;
    }
}
