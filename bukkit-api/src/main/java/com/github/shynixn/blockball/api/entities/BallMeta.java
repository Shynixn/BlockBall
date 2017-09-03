package com.github.shynixn.blockball.api.entities;

import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

@Deprecated
public interface BallMeta extends ConfigurationSerializable {

    /**
     * Returns the sound played when the ball gets hit
     *
     * @return meta
     */
    SoundMeta getGenericHitSound();

    /**
     * Sets the sound played when the ball gets hit
     *
     * @param meta meta
     */
    void setGenericHitSound(SoundMeta meta);

    /**
     * Returns the horizontal strength the ball is going to fly
     * @return strength
     */
    double getHorizontalStrength();

    /**
     * Sets the horizontal strength of the ball  is going to fly
     * @param strength strength
     */
    void setHorizontalStrength(double strength);

    /**
     * Returns the vertical strength the ball is going to fly
     * @return strength
     */
    double getVerticalStrength();

    /**
     * Sets the vertical strength the ball is going to fly
     * @param strength strength
     */
    void setVerticalStrength(double strength);

    @Deprecated
    boolean isRotating();

    @Deprecated
    void setRotating(boolean isNotRotating);

    @Deprecated
    SoundMeta getBallSpawnSound();

    @Deprecated
    void setBallSpawnSound(SoundMeta meta);

    @Deprecated
    String getBallSkin();

    @Deprecated
    void setBallSkin(String ballSkin);

    @Deprecated
    SoundMeta getBallGoalSound();

    @Deprecated
    void setBallGoalSound(SoundMeta meta);

    @Deprecated
    LightParticle getPlayerTeamBlueHitParticle();

    @Deprecated
    void setPlayerTeamBlueHitParticle(LightParticle playerTeamBlueHitParticle);

    @Deprecated
    LightParticle getPlayerTeamRedHitParticle();

    @Deprecated
    void setPlayerTeamRedHitParticle(LightParticle playerTeamRedHitParticle);

    @Deprecated
    LightParticle getBallSpawnParticle();

    @Deprecated
    void setBallSpawnParticle(LightParticle ballSpawnParticle);

    @Deprecated
    int getBallSpawnTime();

    @Deprecated
    void setBallSpawnTime(int ballSpawnTime);

    @Deprecated
    LightParticle getBallGoalParticle();

    @Deprecated
    void setBallGoalParticle(LightParticle balGoalParticle);

    LightParticle getGenericHitParticle();

    @Deprecated
    void setGenericHitParticle(LightParticle genericHitParticle);
}
