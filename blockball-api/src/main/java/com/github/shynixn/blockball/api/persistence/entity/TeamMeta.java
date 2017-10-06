package com.github.shynixn.blockball.api.persistence.entity;

import java.util.Optional;

public interface TeamMeta extends Persistenceable<TeamMeta> {
    /**
     * Sets the spawnpoint of the team.
     *
     * @param location location
     */
    void setSpawnPoint(Object location);

    /**
     * Returns the spawnpoint of the team.
     *
     * @return location
     */
    Optional<Object> getSpawnPoint();

    /**
     * Sets the title of the message getting played when a player scores a goal.
     *
     * @param title title
     */
    void setScoreMessageTitle(String title);

    /**
     * Returns the title of the message getting played when a player scores a goal.
     *
     * @return title
     */
    Optional<String> getScoreMessageTitle();

    /**
     * Sets the subTitle of the message getting played when a player scores a goal
     *
     * @param subtitle subtitle
     */
    void setScoreMessageSubtitle(String subtitle);

    /**
     * Returns the subTitle of the message getting played when a player scores a goal
     *
     * @return title
     */
    Optional<String> getScoreMessageSubtitle();

    /**
     * Sets the title of the message getting played when the team wins a match.
     *
     * @param title title
     */
    void setWinningMessageTitle(String title);

    /**
     * Returns the title of the message getting played when the team wins a match.
     *
     * @return title
     */
    Optional<String> getWinningMessageTitle();

    /**
     * Sets the subTitle of the message getting played when the team wins a match.
     *
     * @param subtitle subtitle
     */
    void setWinningMessageSubtitle(String subtitle);

    /**
     * Returns the subTitle of the message getting played when the team wins a match.
     *
     * @return title
     */
    Optional<String> getWinningMessageSubtitle();

    /**
     * Sets the name of the team which gets displayed by the placeholder :red or :blue.
     *
     * @param name name
     */
    void setDisplayName(String name);

    /**
     * Returns he name of the team which gets displayed by the placeholder :red or :blue.
     *
     * @return name
     */
    String getDisplayName();

    /**
     * Sets the prefix of the team which gets displayed by the placeholder :redcolor or :bluecolor.
     *
     * @param prefix prefix
     */
    void setPrefix(String prefix);

    /**
     * Returns the prefix of the team which gets displayed by the placeholder :redcolor or :bluecolor.
     *
     * @return prefix
     */
    String getPrefix();

    /**
     * Returns the armor items of the team.
     *
     * @return armor
     */
    Object[] getArmorContents();

    /**
     * Sets the armor items of the team.
     *
     * @param itemStacks armor
     */
    void setArmorContents(Object[] itemStacks);
}