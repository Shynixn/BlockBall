package com.github.shynixn.blockball.api.entities;

import com.github.shynixn.blockball.lib.LightParticle;
import com.github.shynixn.blockball.lib.LightSound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface BallMeta extends ConfigurationSerializable {
    String getBallSkin();

    void setBallSkin(String ballSkin);

    LightParticle getPlayerTeamBlueHitParticle();

    void setPlayerTeamBlueHitParticle(LightParticle playerTeamBlueHitParticle);

    LightParticle getPlayerTeamRedHitParticle();

    void setPlayerTeamRedHitParticle(LightParticle playerTeamRedHitParticle);

    LightParticle getBallSpawnParticle();

    void setBallSpawnParticle(LightParticle ballSpawnParticle);

    int getBallSpawnTime();

    void setBallSpawnTime(int ballSpawnTime);

    LightSound getBallSpawnSound();

    void setBallSpawnSound(LightSound ballSpawnSound);

    LightParticle getBallGoalParticle();

    void setBallGoalParticle(LightParticle balGoalParticle);

    LightSound getBallGoalSound();

    void setBallGoalSound(LightSound ballGoalSound);

    LightSound getGenericHitSound();

    void setGenericHitSound(LightSound genericHitSound);

    LightParticle getGenericHitParticle();

    void setGenericHitParticle(LightParticle genericHitParticle);

    double getHorizontalStrength();

    void setHorizontalStrength(double horizontalStrength);

    double getVerticalStrength();

    void setVerticalStrength(double verticalStrength);

    boolean isRotating();

    void setRotating(boolean isNotRotating);
}
