package com.github.shynixn.blockball.api.business.entity;

import com.github.shynixn.blockball.api.business.enumeration.GameStatus;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.BoosItemMeta;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Game extends AutoCloseable, Runnable {

    /**
     * Returns the arena of the game.
     *
     * @return arena
     */
    Arena getArena();

    /**
     * Returns the ball of the game.
     *
     * @return ball
     */
    Optional<Ball> getBall();

    /**
     * Returns the status of a game.
     *
     * @return status
     */
    GameStatus getStatus();

    /**
     * Adds a player to the game returns false if he doesn't meet the required options.
     *
     * @param player player - @NotNull
     * @param team   team - @Nullable, team gets automatically selection
     * @return success
     */
    boolean join(Object player, Team team);

    /**
     * Returns if the given player has joined the match.
     *
     * @param player player - @NotNull
     * @return joined the match
     */
    boolean hasJoined(Object player);

    /**
     * Removes a player from the given game returns false if the player is not in this game.
     *
     * @param player player - @NotNull
     * @return success
     */
    boolean leave(Object player);

    /**
     * Returns the players who a players in the blue team.
     *
     * @return player
     */
    Object[] getBlueTeamPlayers();

    /**
     * Returns the players who a players in the red team.
     *
     * @return player
     */
    Object[] getRedTeamPlayers();

    /**
     * Returns the team by the given player. Returns null if player is not in this game.
     *
     * @param player player
     * @return team
     */
    Team getTeamFromPlayer(Object player);

    /**
     * Returns a list of players who are in this game.
     *
     * @return playerList
     */
    List<Object> getPlayers();

    /**
     * Returns all boost items lying on the ground.
     *
     * @return boostItems
     */
    Map<Object, BoosItemMeta> getGroundItems();

    /**
     * Removes the ground item.
     *
     * @param item item
     */
    void removeGroundItem(Object item);

    /**
     * Returns a value for the given place holder. Returns empty string if not found.
     *
     * @param type type
     * @return value
     */
    String getValueForPlaceHolder(Object type);
}
