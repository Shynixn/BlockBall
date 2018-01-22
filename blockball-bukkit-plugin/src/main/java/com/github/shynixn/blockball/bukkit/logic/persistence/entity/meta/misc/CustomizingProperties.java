package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc;

import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;

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
public class CustomizingProperties extends PersistenceObject<CustomizingMeta> implements CustomizingMeta {

    @YamlSerializer.YamlSerialize(value = "auto-empty-reset", orderNumber = 1)
    private boolean emptyReset;

    @YamlSerializer.YamlSerialize(value = "auto-select-team", orderNumber = 2)
    private boolean autoTeamSelect;

    @YamlSerializer.YamlSerialize(value = "force-even-teams", orderNumber = 3)
    private boolean forceEventTeams;

    @YamlSerializer.YamlSerialize(value = "damage-enabled", orderNumber = 4)
    private boolean enableDamage;

    @YamlSerializer.YamlSerialize(value = "instant-join", orderNumber = 5)
    private boolean fastJoining;

    @YamlSerializer.YamlSerialize(value = "message.team-full", orderNumber = 6)
    private String teamFullMessage;

    @YamlSerializer.YamlSerialize(value = "message.how-to-join", orderNumber = 7)
    private String howtoJoinMessage;

    @YamlSerializer.YamlSerialize(value = "message.leave", orderNumber = 8)
    private String leaveMessage;

    /**
     * Enables or disables auto reset when the arena is empty.
     *
     * @param enable enable
     */
    @Override
    public void setAutoEmptyResetEnabled(boolean enable) {
        this.emptyReset = enable;
    }

    /**
     * Returns if auto reset when the arena is empty is enabled.
     *
     * @return enabled
     */
    @Override
    public boolean isAutoEmptyResetEnabled() {
        return this.emptyReset;
    }

    /**
     * Forces even teams on both sides. Red and blue team amount has to be the same to start.
     *
     * @param enabled enabled
     */
    @Override
    public void setForceEvenTeamsEnabled(boolean enabled) {
        this.forceEventTeams = enabled;
    }

    /**
     * Returns if even teams on both sides is enabled. Red and blue team amount has to be the same to start.
     *
     * @return enabled
     */
    @Override
    public boolean isForceEvenTeamsEnabled() {
        return this.forceEventTeams;
    }

    /**
     * Enables or disables player to be allowed hitting each other.
     *
     * @param enabled enabled
     */
    @Override
    public void setDamagingPlayersEnabled(boolean enabled) {
        this.enableDamage = enabled;
    }

    /**
     * Returns if player are allowed hitting each other.
     *
     * @return enabled
     */
    @Override
    public boolean isDamagingPlayersEnabled() {
        return this.enableDamage;
    }

    /**
     * Enables or disables automatically team choosing depending on the amount of players currently in the match.
     *
     * @param enabled enabled
     */
    @Override
    public void setAutoSelectTeamEnabled(boolean enabled) {
        this.autoTeamSelect = enabled;
    }

    /**
     * Enables or disables automatically team choosing depending on the amount of players currently in the match.
     *
     * @return enabled
     */
    @Override
    public boolean isAutoSelectTeamEnabled() {
        return this.autoTeamSelect;
    }

    /**
     * Enables or disables automatically joining a game by running into a forcefield. Works only on arenas with forcefield.
     *
     * @param enabled enabled
     */
    @Override
    public void setFastJoiningEnabled(boolean enabled) {
        this.fastJoining = enabled;
    }

    /**
     * Returns if fastjoining is enabled.
     *
     * @return fastjoining
     */
    @Override
    public boolean isFastJoiningEnabled() {
        return this.fastJoining;
    }

    /**
     * Returns the leave message which gets played when a player leaves a match.
     *
     * @return message
     */
    @Override
    public Optional<String> getLeaveMessage() {
        return Optional.ofNullable(this.leaveMessage);
    }

    /**
     * Sets the the leave message which gets played when a player leaves a match.
     *
     * @param message message
     */
    @Override
    public void setLeaveMessage(String message) {
        this.leaveMessage = message;
    }

    /**
     * Sets the how to join message which gets played when a player tries to join a match.
     *
     * @param message message
     */
    @Override
    public void setHowToJoinMessage(String message) {
        this.howtoJoinMessage = message;
    }

    /**
     * Returns the how to join message which gets played when a player tries to join a match.
     *
     * @return message
     */
    @Override
    public Optional<String> getHowToJoinMessage() {
        return Optional.ofNullable(this.howtoJoinMessage);
    }

    /**
     * Sets the team full message which gets played when a player tries to join a match but the team is already full.
     *
     * @param message message
     */
    @Override
    public void setTeamFullMessage(String message) {
        this.teamFullMessage = message;
    }

    /**
     * Returns the team full message which gets played when a player tries to join a match but the team is already full.
     *
     * @return message
     */
    @Override
    public Optional<String> getTeamFullMessage() {
        return Optional.ofNullable(this.teamFullMessage);
    }

    @Override
    public List<IPosition> getLeaveSigns() {
        return null;
    }
}
