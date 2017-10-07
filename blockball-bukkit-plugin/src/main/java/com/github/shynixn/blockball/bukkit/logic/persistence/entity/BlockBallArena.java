package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.api.persistence.entity.*;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties.BallProperties;
import com.github.shynixn.blockball.lib.YamlSerializer;

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
public class BlockBallArena extends PersistenceObject<Arena> implements Arena{

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "ball")
    private final BallMeta ballMeta = new BallProperties();


    private GoalEntity redGoal;
    private GoalEntity blueGoal;
    private SLocation ballSpawnLocation;
    private final BallMetaEntity2 properties = new BallMetaEntity2();
    private final TeamMetaEntity properties2 = new TeamMetaEntity();
    private final EventMetaEntity properties3 = new EventMetaEntity();

    private final LobbyMetaEntity lobbyMetaEntity = new LobbyMetaEntity();
    private final GameType gameType = GameType.LOBBY;

    private final boolean isEnabled = true;
    private String alias;

    private BoostItemHandler boostItemHandler;
    private List<String> bounce_types;






    /**
     * Returns the ball settings for this arena
     *
     * @return ballMeta
     */
    @Override
    public BallMeta getBallMeta() {
        return this.ballMeta;
    }

    /**
     * Returns the meta data for lobbies
     *
     * @return lobbyMeta
     */
    @Override
    public LobbyMeta getLobbyMeta() {
        return null;
    }

    /**
     * Returns the meta data for teams
     *
     * @return teamMeta
     */
    @Override
    public TeamMeta getTeamMeta() {
        return null;
    }

    /**
     * Returns the meta data for events
     *
     * @return event
     */
    @Override
    public EventMeta getEventMeta() {
        return null;
    }

    /**
     * Returns the meta data for the bossbar.
     *
     * @return bossbar
     */
    @Override
    public BossBarMeta getBossBarMeta() {
        return null;
    }

    /**
     * Returns the meta data for the scoreboard.
     *
     * @return scoreboard
     */
    @Override
    public ScoreboardMeta getScoreboardMeta() {
        return null;
    }

    /**
     * Returns the meta data for the double jump.
     *
     * @return doubleJumpMeta
     */
    @Override
    public DoubleJumpMeta getDoubleJumpMeta() {
        return null;
    }

    /**
     * Returns the meta data of glowing when scoring.
     *
     * @return glowing
     */
    @Override
    public GlowEffectMeta getScoreGlowingMeta() {
        return null;
    }

    /**
     * Returns the meta data of the red team
     *
     * @return meta
     */
    @Override
    public TeamMeta getRedTeamMeta() {
        return null;
    }

    /**
     * Returns the meta data of the blue team
     *
     * @return meta
     */
    @Override
    public TeamMeta getBlueTeamMeta() {
        return null;
    }

    /**
     * Adds a new hologram to the arena.
     *
     * @param hologramMeta hologram
     */
    @Override
    public void addHologram(HologramMeta hologramMeta) {

    }

    /**
     * Removes a hologram from the arena.
     *
     * @param hologramMeta hologram
     */
    @Override
    public void removeHologram(HologramMeta hologramMeta) {

    }

    /**
     * Returns all holograms from the arena
     *
     * @return holograms
     */
    @Override
    public List<HologramMeta> getHolograms() {
        return null;
    }

    /**
     * Returns the max amount the score can reach on the scoreboard.
     *
     * @return score
     */
    @Override
    public int getMaxScore() {
        return 0;
    }

    /**
     * Sets the max amount the score can reach on the scoreboard.
     *
     * @param amount amoun
     */
    @Override
    public void setMaxScore(int amount) {

    }

    /**
     * Returns the unique name of the arena
     *
     * @return name
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Enables or disables auto reset when the arena is empty.
     *
     * @param enable enable
     */
    @Override
    public void setAutoEmptyResetEnabled(boolean enable) {

    }

    /**
     * Returns if auto reset when the arena is empty is enabled.
     *
     * @return enabled
     */
    @Override
    public boolean isAutoEmptyResetEnabled() {
        return false;
    }

    /**
     * Returns if the arena is enabled
     *
     * @return enabled
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * Sets the arena enabled
     *
     * @param enabled enabled
     */
    @Override
    public void setEnabled(boolean enabled) {

    }

    /**
     * Returns the displayName of the arena if present
     *
     * @return displayName
     */
    @Override
    public Optional<String> getDisplayName() {
        return null;
    }

    /**
     * Sets the displayName of the arena
     *
     * @param displayName displayName
     */
    @Override
    public void setDisplayName(String displayName) {

    }

    /**
     * Returns the gameType of the arena
     *
     * @return gameType
     */
    @Override
    public GameType getGameType() {
        return null;
    }

    /**
     * Sets the gameType of the arena
     *
     * @param type type
     */
    @Override
    public void setGameType(GameType type) {

    }

    /**
     * Returns the center of the arena
     *
     * @return center
     */
    @Override
    public Object getCenter() {
        return null;
    }

    /**
     * Returns the spawnpoint of the ball
     *
     * @return ballSpawnpoint
     */
    @Override
    public Object getBallSpawnLocation() {
        return null;
    }

    /**
     * Sets the spawnpoint of the ball
     *
     * @param location location
     */
    @Override
    public void setBallSpawnLocation(Object location) {

    }

    /**
     * Returns if the given location is inside of this arena
     *
     * @param location location
     * @return isInside
     */
    @Override
    public boolean isLocationInArena(Object location) {
        return false;
    }

    /**
     * Resets the object to the default values
     */
    @Override
    public void reset() {

    }
}
