package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.api.persistence.entity.*;
import com.github.shynixn.blockball.api.persistence.entity.meta.MetaDataTransaction;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.BlockBallMetaCollection;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.area.SelectedArea;
import com.github.shynixn.blockball.lib.YamlSerializer;
import org.bukkit.Location;

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
public class BlockBallArena extends SelectedArea<Arena> implements Arena {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "name")
    private String name;

    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "displayname")
    private String displayName;

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "enabled")
    private boolean enabled;

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "gamemode")
    private GameType gameType = GameType.LOBBY;

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "ball-spawnpoint")
    private LocationBuilder ballSpawnLocation;

    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "meta")
    private final BlockBallMetaCollection transaction = new BlockBallMetaCollection();

    /**
     * Default constructor
     */
    public BlockBallArena() {
    }

    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return Long.parseLong(this.name);
    }

    /**
     * Sets the id of the object
     *
     * @param id id
     */
    @Override
    public void setId(long id) {
        super.setId(id);
        this.name = String.valueOf(id);
        this.displayName = "Arena " + id;
    }

    /**
     * Returns the meta data transaction for finding meta data of blockball.
     *
     * @return metaData
     */
    @Override
    public MetaDataTransaction getMeta() {
        return this.transaction;
    }

    /**
     * Returns the unique name of the arena
     *
     * @return name
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Returns if the arena is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Sets the arena enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns the displayName of the arena if present
     *
     * @return displayName
     */
    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(this.displayName);
    }

    /**
     * Sets the displayName of the arena
     *
     * @param displayName displayName
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the gameType of the arena
     *
     * @return gameType
     */
    @Override
    public GameType getGameType() {
        return this.gameType;
    }

    /**
     * Sets the gameType of the arena
     *
     * @param type type
     */
    @Override
    public void setGameType(GameType type) {
        this.gameType = type;
    }

    /**
     * Returns the spawnpoint of the ball
     *
     * @return ballSpawnpoint
     */
    @Override
    public Object getBallSpawnLocation() {
        if (this.ballSpawnLocation == null)
            return null;
        return this.ballSpawnLocation.toLocation();
    }

    /**
     * Sets the spawnpoint of the ball
     *
     * @param location location
     */
    @Override
    public void setBallSpawnLocation(Object location) {
        if (location != null) {
            this.ballSpawnLocation = new LocationBuilder((Location) location);
        }
    }
}
