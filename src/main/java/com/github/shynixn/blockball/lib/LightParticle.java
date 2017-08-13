package com.github.shynixn.blockball.lib;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

@Deprecated
public interface LightParticle extends ConfigurationSerializable {
    LightParticle copy();

    LightParticle setColors(int red, int green, int blue);

    SParticle setNoteColor(int color);

    int getAmount();

    void setAmount(int amount);

    double getX();

    void setX(double x);

    double getY();

    void setY(double y);

    double getZ();

    void setZ(double z);

    double getSpeed();

    void setSpeed(double speed);

    ParticleEffect getEffect();

    void setEffect(ParticleEffect effect);

    Integer getBlue();

    void setBlue(Integer blue);

    void setEnabled(boolean enabled);

    boolean isEnabled();

    Integer getRed();

    void setRed(Integer red);

    Integer getGreen();

    void setGreen(Integer green);

    Material getMaterial();

    void setMaterial(Material material);

    Byte getData();

    void setData(Byte data);

    void play(Location location);

    void play(Location location, Player... players);

    boolean isColorParticleEffect();

    boolean isNoteParticleEffect();

    boolean isMaterialParticleEffect();
}
