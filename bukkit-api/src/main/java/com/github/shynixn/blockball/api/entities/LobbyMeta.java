package com.github.shynixn.blockball.api.entities;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.List;

public interface LobbyMeta extends ConfigurationSerializable {

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeSignPosition(IPosition position);

    List<Location> getSignLocations();

    @Deprecated
    void removeSignLocation(int positon);

    void addSignLocation(Location location);

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getRedTeamSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeRedTeamSignPosition(IPosition position);

    List<Location> getRedTeamSignLocations();

    @Deprecated
    void removeRedTeamSignLocation(int positon);

    void addRedTeamSignLocation(Location location);

    /**
     * Returns the positions of each sign
     *
     * @return positions
     */
    List<IPosition> getBlueTeamSignPositions();

    /**
     * Removes the sign-position
     *
     * @param position position
     */
    void removeBlueTeamSignPosition(IPosition position);

    List<Location> getBlueTeamSignLocations();

    @Deprecated
    void removeBlueTeamSignLocation(int positon);

    void addBlueTeamSignLocation(Location location);

    List<Location> getLeaveSignLocations();

    void removeLeaveSignLocation(int positon);

    void addLeaveignLocation(Location location);

    Location getLobbyLeave();

    Location getLobbySpawn();

    void setLobbySpawnpoint(Location lobbySpawnpoint);

    void setLobbyLeave(Location location);

    void setMinPlayers(int minPlayers);

    void setMaxPlayers(int maxPlayers);

    void setGameTime(int gameTime);

    int getGameTime();

    int getMinPlayers();

    int getMaxPlayers();

    void setCountDown(int countDown);

    int getCountDown();

    String getGameSubTitleMessage();

    void setGameSubTitleMessage(String gamesubTitleMessage);

    String getGameTitleMessage();

    void setGameTitleMessage(String gameTitleMessage);
}
