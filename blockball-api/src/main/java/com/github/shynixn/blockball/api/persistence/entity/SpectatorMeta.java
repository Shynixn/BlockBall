package com.github.shynixn.blockball.api.persistence.entity;

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
public interface SpectatorMeta extends Persistenceable<SpectatorMeta> {

    /**
     * Sets the minecraft gamemode when being in the spectator mode
     *
     * @param gameMode gameMode
     */
    void setGameMode(int gameMode);

    /**
     * Sets the minecraft gamemode when being in the spectator mode
     *
     * @return gameMode
     */
    int getGameMode();

    /**
     * Sets the location the spectators get teleported when enabling the spectator mode.
     *
     * @param location location
     */
    void setSpawnpoint(Object location);

    /**
     * Returns he location the spectators get teleported when enabling the spectator mode.
     *
     * @return location
     */
    Optional<Object> getSpawnpoint();

    /**
     * Sets the location the spectators get teleported when disabling the spectator mode.
     *
     * @param location location
     */
    void setLeftSpawnpoint(Object location);

    /**
     * Returns the location the spectators get teleported when disabling the spectator mode.
     *
     * @return location
     */
    Optional<Object> getLeftSpawnpoint();

    /**
     * Enables or disables if nearby players should be notified by scorring messages even if they are not in specator mode.
     *
     * @param enabled enabled
     */
    void setNotifyNearbyPlayersEnabled(boolean enabled);

    /**
     * Returns if nearby players should be notified by scorring messages even if they are not in specator mode.
     *
     * @return notify
     */
    boolean isNotifyNearbyPlayersEnabled();

    /**
     * Sets the radius in which players get notified when enabling nearby player notifications.
     *
     * @param amountOfBlocks radius
     */
    void setNotifyNearbyPlayersRadius(int amountOfBlocks);

    /**
     * Returns the radius in which players get notified when enabling nearby player notifications.
     *
     * @return radius
     */
    int getNotifyNearbyPlayersRadius();
}
