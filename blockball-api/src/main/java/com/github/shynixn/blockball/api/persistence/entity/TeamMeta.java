package com.github.shynixn.blockball.api.persistence.entity;

import java.util.Optional;

public interface TeamMeta {

    /**
     * Forces even teams on both sides. Red and blue team amount has to be the same
     *
     * @param enabled enabled
     */
    void setForceEvenTeams(boolean enabled);

    /**
     * Returns if even teams on both sides is enabled. Red and blue team amount has to be the same to start
     *
     * @return enabled
     */
    boolean isForceEvenTeamsEnabled();

    /**
     * Returns the walkingSpeed of the players
     *
     * @return speed
     */
    float getWalkingSpeed();

    /**
     * Sets the amount of speed for the players
     *
     * @param amount amount
     */
    void setWalkingSpeed(float amount);

    int getMaxScore();

    void setMaxScore(int maxScore);

    Optional<Object> getBlueSpawnPoint();

    Optional<Object> getHologramLocation();

    void setHologramLocation(Object location);

    String getHologramText();

    void setHologramText(String text);

    void setHologramEnabled(boolean enabled);

    boolean isHologramEnabled();

    void setBlueSpawnPoint(Object blueSpawnPoint);

    Optional<Object> getRedSpawnPoint();

    void setRedSpawnPoint(Object redSpawnPoint);

    boolean isFastJoin();

    int getRewardGoals();

    String getWinCommand();

    void setWinCommand(String winCommand);

    String getGamendCommand();

    void setGamendCommand(String gamendCommand);

    boolean isSpectatorMessagesEnabled();

    void setSpecatorMessages(boolean enabled);

    int getSpecatorradius();

    void setSpecatorradius(int specatorradius);

    void setRewardGoals(int rewardGoals);

    int getRewardGames();

    void setRewardGames(int rewardGames);

    int getRewardWinning();

    void setRewardWinning(int rewardWinning);

    void setFastJoin(boolean enable);

    String getRedtitleScoreMessage();

    void setRedtitleScoreMessage(String redtitleScoreMessage);

    String getRedsubtitleMessage();

    void setRedsubtitleMessage(String redsubtitleMessage);

    String getBluetitleScoreMessage();

    void setBluetitleScoreMessage(String bluetitleScoreMessage);

    String getBluesubtitleMessage();

    void setBluesubtitleMessage(String bluesubtitleMessage);

    String getRedwinnerTitleMessage();

    void setRedwinnerTitleMessage(String redwinnerTitleMessage);

    String getBluewinnerTitleMessage();

    void setBluewinnerTitleMessage(String bluewinnerTitleMessage);

    String getRedwinnerSubtitleMessage();

    void setRedwinnerSubtitleMessage(String redwinnerSubtitleMessage);

    String getBluewinnerSubtitleMessage();

    void setBluewinnerSubtitleMessage(String bluewinnerSubtitleMessage);

    void reset();

    boolean isTeamAutoJoin();

    void setTeamAutoJoin(boolean autoJoin);

    String getTeamFullMessage();

    void setTeamFullMessage(String teamFullMessage);

    void resetArmor();

    Optional<Object> getGameEndSpawnpoint();

    void setGameEndSpawnpoint(Object location);

    String getRedTeamName();

    void setRedTeamName(String redTeamName);

    String getBlueTeamName();

    void setBlueTeamName(String blueTeamName);

    int getTeamMaxSize();

    void setTeamMaxSize(int teamMaxSize);

    int getTeamMinSize();

    void setTeamMinSize(int teamMinSize);

    String getRedColor();

    boolean isEmtptyReset();

    void setEmptyReset(boolean enabled);

    void setRedColor(String redColor);

    String getBlueColor();

    void setBlueColor(String blueColor);

    Object[] getBlueItems();

    boolean isDamageEnabled();

    void setDamage(boolean enabled);

    void setBlueItems(Object[] itemStacks);

    Object[] getRedItems();

    void setRedItems(Object[] redItems);

    String getJoinMessage();

    void setJoinMessage(String joinMessage);

    String getLeaveMessage();

    void setLeaveMessage(String leaveMessage);

    void setHowToJoinMessage(String message);

    String getHowToJoinMessage();

    /**
     * Returns the settings for the double jump
     *
     * @return doubleJumpMeta
     */
    DoubleJumpMeta getDoubleJumpMeta();

    /**
     * Sets the title of the scoreboard
     *
     * @param scoreboardTitle scoreboardTitle
     */
    void setScoreboardTitle(String scoreboardTitle);

    /**
     * Returns the title of the scoreboard
     *
     * @return title
     */
    String getScoreboardTitle();

    /**
     * Enables or disables the scoreboard
     *
     * @param enabled scoreboard
     */
    void setScoreboardEnabled(boolean enabled);

    /**
     * Returns if the scoreboard is enabled
     *
     * @return enabled
     */
    boolean isScoreboardEnabled();

    /**
     * Sets the lines of the scoreboard
     *
     * @param scoreboardLines scoreboardLines
     */
    void setScoreboardLines(String[] scoreboardLines);

    /**
     * Returns the lines of the scoreboard
     *
     * @return lines
     */
    String[] getScoreboardLines();

    String getBossBarPluginMessage();

    boolean isBossBarPluginEnabled();

    void setBossBarPluginMessage(String message);

    void setBossBarPluginEnabled(boolean enable);

    void setGoalShooterGlowing(boolean enable);

    boolean isGoalShooterGlowing();

    void setGoalShooterGlowingSeconds(int seconds);

    int getGoalShooterGlowingSeconds();
}