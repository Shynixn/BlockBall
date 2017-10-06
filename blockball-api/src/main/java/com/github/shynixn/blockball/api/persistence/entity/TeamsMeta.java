package com.github.shynixn.blockball.api.persistence.entity;

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
@Deprecated
public interface TeamsMeta {

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
     * Sets the amount of speed for the players.
     *
     * @param amount amount
     */
    void setWalkingSpeed(float amount);

    /**
     * Returns the walkingSpeed of the players.
     *
     * @return speed
     */
    float getWalkingSpeed();

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
     * Sets the max amount of players in each team.
     *
     * @param amount amount
     */
    void setMaxAmountOfPlayers(int amount);

    /**
     * Returns the max amount of players in each team.
     *
     * @return amount
     */
    int getMaxAmountOfPlayers();

    /**
     * Sets the min amount of players in each team.
     *
     * @param amount amount
     */
    void setMinAmountOfPlayers(int amount);

    /**
     * Returns the min amount of players in each team.
     *
     * @return amount
     */
    int getMinAmountOfPlayers();

    /**
     * Returns the meta data of the red team
     *
     * @return meta
     */
    TeamMeta getRedTeamMeta();

    /**
     * Returns the meta data of the blue team
     *
     * @return meta
     */
    TeamMeta getBlueTeamMeta();

    /**
     * Returns the meta data of glowing when scoring.
     *
     * @return glowing
     */
    GlowEffectMeta getScoreGlowingMeta();

    String getJoinMessage();

    void setJoinMessage(String joinMessage);

    String getLeaveMessage();

    void setLeaveMessage(String leaveMessage);

    void setHowToJoinMessage(String message);

    String getHowToJoinMessage();

    int getRewardGoals();

    String getWinCommand();

    void setWinCommand(String winCommand);

    String getGamendCommand();

    void setGamendCommand(String gamendCommand);

    String getTeamFullMessage();

    void setTeamFullMessage(String teamFullMessage);

    void setRewardGoals(int rewardGoals);

    int getRewardGames();

    void setRewardGames(int rewardGames);

    int getRewardWinning();

    void setRewardWinning(int rewardWinning);
}
