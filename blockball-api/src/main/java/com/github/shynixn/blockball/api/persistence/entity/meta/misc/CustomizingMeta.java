package com.github.shynixn.blockball.api.persistence.entity.meta.misc;

import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

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
public interface CustomizingMeta extends Persistenceable {



    /**
     * Enables or disables auto reset when the arena is empty.
     *
     * @param enable enable
     */
    void setAutoEmptyResetEnabled(boolean enable);

    /**
     * Returns if auto reset when the arena is empty is enabled.
     *
     * @return enabled
     */
    boolean isAutoEmptyResetEnabled();

    /**
     * Forces even teams on both sides. Red and blue team amount has to be the same to start.
     *
     * @param enabled enabled
     */
    void setForceEvenTeamsEnabled(boolean enabled);

    /**
     * Returns if even teams on both sides is enabled. Red and blue team amount has to be the same to start.
     *
     * @return enabled
     */
    boolean isForceEvenTeamsEnabled();

    /**
     * Enables or disables player to be allowed hitting each other.
     *
     * @param enabled enabled
     */
    void setDamagingPlayersEnabled(boolean enabled);

    /**
     * Returns if player are allowed hitting each other.
     *
     * @return enabled
     */
    boolean isDamagingPlayersEnabled();

    /**
     * Enables or disables automatically team choosing depending on the amount of players currently in the match.
     *
     * @param enabled enabled
     */
    void setAutoSelectTeamEnabled(boolean enabled);

    /**
     * Enables or disables automatically team choosing depending on the amount of players currently in the match.
     *
     * @return enabled
     */
    boolean isAutoSelectTeamEnabled();

    /**
     * Enables or disables automatically joining a game by running into a forcefield. Works only on arenas with forcefield.
     *
     * @param enabled enabled
     */
    void setFastJoiningEnabled(boolean enabled);

    /**
     * Returns if fastjoining is enabled.
     *
     * @return fastjoining
     */
    boolean isFastJoiningEnabled();

    /**
     * Returns the leave message which gets played when a player leaves a match.
     *
     * @return message
     */
    Optional<String> getLeaveMessage();

    /**
     * Sets the the leave message which gets played when a player leaves a match.
     *
     * @param message message
     */
    void setLeaveMessage(String message);

    /**
     * Sets the how to join message which gets played when a player tries to join a match.
     *
     * @param message message
     */
    void setHowToJoinMessage(String message);

    /**
     * Returns the how to join message which gets played when a player tries to join a match.
     *
     * @return message
     */
    Optional<String> getHowToJoinMessage();

    /**
     * Sets the team full message which gets played when a player tries to join a match but the team is already full.
     *
     * @param message message
     */
    void setTeamFullMessage(String message);

    /**
     * Returns the team full message which gets played when a player tries to join a match but the team is already full.
     *
     * @return message
     */
    Optional<String> getTeamFullMessage();

    List<IPosition> getLeaveSigns();
}
