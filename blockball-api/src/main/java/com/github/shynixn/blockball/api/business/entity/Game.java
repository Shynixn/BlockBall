package com.github.shynixn.blockball.api.business.entity;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;

import java.util.List;

public interface Game {

    /**
     * Returns the arena of the game
     *
     * @return arena
     */
    Arena getArena();

    /**
     * Returns the ball of the game
     *
     * @return ball
     */
    Ball getBall();

    /**
     * Adds a player to the game returns false if he doesn't meet the required options.
     *
     * @param player player - @NotNull
     * @param team   team - @Nullable, team gets automatically selection
     * @return success
     */
    boolean join(Object player, Team team);

    /**
     * Returns if the given player has joined the match
     *
     * @param player player - @NotNull
     * @return joined the match
     */
    boolean hasJoined(Object player);

    /**
     * Removes a player from the given game returns false if it did not work
     *
     * @param player player - @NotNull
     * @return success
     */
    boolean leave(Object player);

    /**
     * Returns the players who a players in the blue team
     *
     * @return player
     */
    Object[] getBlueTeamPlayers();

    /**
     * Returns the players who a players in the red team
     *
     * @return player
     */
    Object[] getRedTeamPlayers();

    /**
     * Returns a list of players who are in this game
     *
     * @return playerList
     */
    List<Object> getPlayers();
}
