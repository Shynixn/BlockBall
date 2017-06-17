package com.github.shynixn.blockball.lib;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.io.Serializable;

public interface LightSound extends Serializable, ConfigurationSerializable {
    LightSound copy();

    void play(Location location) throws InterPreter19Exception;

    void play(Location location, Player... players) throws InterPreter19Exception;

    void play(Player... players) throws InterPreter19Exception;

    String getSound();

    void setSound(String sound);

    double getVolume();

    void setVolume(double volume);

    double getPitch();

    void setPitch(double pitch);

    FastSound toFastSound();
}
