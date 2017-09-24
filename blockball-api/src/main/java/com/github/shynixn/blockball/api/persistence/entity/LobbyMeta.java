package com.github.shynixn.blockball.api.persistence.entity;

import java.util.List;

public interface LobbyMeta extends Persistenceable<LobbyMeta> {

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

    /**
     * Adds a redTeamSignLocation
     *
     * @param position position
     */
    void addRedTeamSignLocation(IPosition position);

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

    List<IPosition> getBlueTeamSignLocations();

    void removeLeaveSignLocation(int positon);

    void addLeaveignLocation(Object location);

    Object getLobbyLeave();

    Object getLobbySpawn();

    void setLobbySpawnpoint(Object lobbySpawnpoint);

    void setLobbyLeave(Object location);

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
