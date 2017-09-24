package com.github.shynixn.blockball.api.persistence.entity;

public interface BallMeta extends Persistenceable<BallMeta>{

    /**
     * Returns the sound played when the ball gets hit
     *
     * @return meta
     */
    SoundMeta getGenericHitSound();

    /**
     * Returns the sound played when the ball spawns
     *
     * @return meta
     */
    SoundMeta getSpawnSound();

    /**
     * Returns the horizontal strength the ball is going to fly
     *
     * @return strength
     */
    double getHorizontalStrength();

    /**
     * Sets the horizontal strength of the ball  is going to fly
     *
     * @param strength strength
     */
    void setHorizontalStrength(double strength);

    /**
     * Returns the vertical strength the ball is going to fly
     *
     * @return strength
     */
    double getVerticalStrength();

    /**
     * Sets the vertical strength the ball is going to fly
     *
     * @param strength strength
     */
    void setVerticalStrength(double strength);

    /**
     * Sets if rotating is enabled
     *
     * @param enabled enabled
     */
    void setRotatingEnabled(boolean enabled);

    /**
     * Returns if rotating is enabled
     *
     * @return enabled
     */
    boolean isRotatingEnabled();

    /**
     * Changes the skin of the ball. Has to be a skin-URL or name of a player
     *
     * @param skin skin
     */
    void setSkin(String skin);

    /**
     * Returns the skin of the ball
     *
     * @return skin
     */
    String getSkin();
}
