package com.github.shynixn.blockball.lib;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;

import java.io.Serializable;

@Deprecated
public interface LightPotioneffect extends Serializable, ConfigurationSerializable {
    LightPotioneffect setType(int type);

    LightPotioneffect setSeconds(int seconds);

    LightPotioneffect setTicks(int ticks);

    LightPotioneffect setStrength(int strength);

    LightPotioneffect setAmbientVisible(boolean ambient);

    LightPotioneffect setParticleVisible(boolean visible);

    int getType();

    int getDuration();

    int getStrength();

    boolean isAmbientVisible();

    boolean isParticleVisible();

    void apply(LivingEntity entity);
}
