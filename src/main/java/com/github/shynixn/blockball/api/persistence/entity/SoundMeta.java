package com.github.shynixn.blockball.api.persistence.entity;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.Collection;

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
public interface SoundMeta extends Persistenceable<SoundMeta> {
    /**
     * Plays the sound to all given players at their location
     *
     * @param players players
     * @throws Exception exception
     */
    void apply(Collection<Player> players) throws Exception;

    /**
     * Plays the sound to all given players at their location
     *
     * @param players players
     * @throws Exception exception
     */
    void apply(Player... players) throws Exception;

    /**
     * Plays the sound to all players in the world at the given location. Players to far away cannot hear the sound.
     *
     * @param location location
     * @throws Exception exception
     */
    void apply(Location location) throws Exception;

    /**
     * Plays the sound to the given players at the given location. Given players to far away cannot hear the sound.
     *
     * @param location location
     * @param players  players
     * @throws Exception exception
     */
    void apply(Location location, Collection<Player> players) throws Exception;

    /**
     * Plays the sound to the given players at the given location. Given players to far away cannot hear the sound.
     *
     * @param location location
     * @param players  players
     * @throws Exception exception
     */
    void apply(Location location, Player... players) throws Exception;

    /**
     * Returns the name of the sound
     *
     * @return name
     */
    String getName();

    /**
     * Sets the name of the sound
     *
     * @param name name
     * @return builder
     */
    SoundMeta setName(String name);

    /**
     * Returns the sound and throws exception if the sound does not exist
     *
     * @return sound
     * @throws Exception exception
     */
    Sound getSound() throws Exception;

    /**
     * Sets the bukkit sound of the sound
     *
     * @param sound sound
     * @return builder
     */
    SoundMeta setSound(Sound sound);

    /**
     * Returns the volume of the sound
     *
     * @return volume
     */
    double getVolume();

    /**
     * Sets the volume of the sound
     *
     * @param volume volume
     * @return builder
     */
    SoundMeta setVolume(double volume);

    /**
     * Returns the pitch of the sound
     *
     * @return pitch
     */
    double getPitch();

    /**
     * Sets the pitch of the sound
     *
     * @param pitch pitch
     * @return builder
     */
    SoundMeta setPitch(double pitch);
}
