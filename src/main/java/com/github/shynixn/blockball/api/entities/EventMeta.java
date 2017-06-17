package com.github.shynixn.blockball.api.entities;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface EventMeta extends ConfigurationSerializable {
    void addRegisteredRedPlayer(String name);

    void removeRegisteredRedPlayer(String position);

    String[] getRegisteredRedPlayers();

    void addRegisteredBluePlayer(String name);

    void removeRegisteredBluePlayer(String position);

    String[] getRegisteredBluePlayers();

    String getReferee();

    void setReferee(String name);
}
