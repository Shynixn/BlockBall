package com.github.shynixn.blockball.api.entities;

import com.github.shynixn.blockball.api.entities.items.BoostItemHandler;
import com.github.shynixn.blockball.api.persistence.entity.*;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;
import java.util.Random;

public interface Arena extends ConfigurationSerializable {
    /**
     * Returns the ball settings for this arena
     *
     * @return ballMeta
     */
    com.github.shynixn.blockball.api.persistence.entity.BallMeta getBallMeta();

    boolean isEnabled();

    String getAlias();

    void setAlias(String name);

    void setIsEnabled(boolean isEnabled);

    TeamMeta getTeamMeta();

    LobbyMeta getLobbyMeta();

    Location getCenter();

    Location getBallSpawnLocation();

    void setBallSpawnLocation(Location location);

    boolean isValid();

    boolean isLocationInGoal(Location location);

    Team getTeamFromGoal(Location location);

    void setGoal(Team team, Location right, Location left);

    boolean isLocationInArea(Location location);

    int getId();

    EventMeta getEventMeta();

    GameType getGameType();

    void setGameType(GameType type);

    void addBounceType(String type);

    void removeBounceType(String type);

    List<String> getBounceTypes();

    Location getRandomFieldPosition(Random random);

    BoostItemHandler getBoostItemHandler();

    String getName();
}
