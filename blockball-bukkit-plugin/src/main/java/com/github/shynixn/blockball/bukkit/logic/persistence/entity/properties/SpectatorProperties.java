package com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.SpectatorMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.LocationBuilder;
import com.github.shynixn.blockball.lib.YamlSerializer;

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
public class SpectatorProperties extends PersistenceObject<SpectatorMeta> implements SpectatorMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "gamemode")
    private int gameMode;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "spawnpoint")
    private IPosition spawnpoint;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "left-spawnpoint")
    private IPosition leavepoint;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "notifications.nearby-players")
    private boolean notifynearbyplayers;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "notifications.radius")
    private int notifyRadius;

    /**
     * Sets the minecraft gamemode when being in the spectator mode
     *
     * @param gameMode gameMode
     */
    @Override
    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * Sets the minecraft gamemode when being in the spectator mode
     *
     * @return gameMode
     */
    @Override
    public int getGameMode() {
        return this.gameMode;
    }

    /**
     * Sets the location the spectators get teleported when enabling the spectator mode.
     *
     * @param location location
     */
    @Override
    public void setSpawnpoint(Object location) {
        if (location != null) {
            this.spawnpoint = new LocationBuilder((org.bukkit.Location) location);
        } else {
            this.spawnpoint = null;
        }
    }

    /**
     * Returns he location the spectators get teleported when enabling the spectator mode.
     *
     * @return location
     */
    @Override
    public Optional<Object> getSpawnpoint() {
        if (this.spawnpoint == null) {
            return Optional.empty();
        } else {
            return Optional.of(((LocationBuilder) this.spawnpoint).toLocation());
        }
    }

    /**
     * Sets the location the spectators get teleported when disabling the spectator mode.
     *
     * @param location location
     */
    @Override
    public void setLeftSpawnpoint(Object location) {
        if (location != null) {
            this.leavepoint = new LocationBuilder((org.bukkit.Location) location);
        } else {
            this.leavepoint = null;
        }
    }

    /**
     * Returns the location the spectators get teleported when disabling the spectator mode.
     *
     * @return location
     */
    @Override
    public Optional<Object> getLeftSpawnpoint() {
        if (this.leavepoint == null) {
            return Optional.empty();
        } else {
            return Optional.of(((LocationBuilder) this.leavepoint).toLocation());
        }
    }

    /**
     * Enables or disables if nearby players should be notified by scorring messages even if they are not in specator mode.
     *
     * @param enabled enabled
     */
    @Override
    public void setNotifyNearbyPlayersEnabled(boolean enabled) {
        this.notifynearbyplayers = enabled;
    }

    /**
     * Returns if nearby players should be notified by scorring messages even if they are not in specator mode.
     *
     * @return notify
     */
    @Override
    public boolean isNotifyNearbyPlayersEnabled() {
        return this.notifynearbyplayers;
    }

    /**
     * Sets the radius in which players get notified when enabling nearby player notifications.
     *
     * @param amountOfBlocks radius
     */
    @Override
    public void setNotifyNearbyPlayersRadius(int amountOfBlocks) {
        this.notifyRadius = amountOfBlocks;
    }

    /**
     * Returns the radius in which players get notified when enabling nearby player notifications.
     *
     * @return radius
     */
    @Override
    public int getNotifyNearbyPlayersRadius() {
        return this.notifyRadius;
    }
}
