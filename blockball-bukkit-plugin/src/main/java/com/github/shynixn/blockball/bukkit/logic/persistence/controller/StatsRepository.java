package com.github.shynixn.blockball.bukkit.logic.persistence.controller;

import com.github.shynixn.blockball.api.persistence.controller.StatsController;
import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.StatsData;
import com.github.shynixn.blockball.lib.ExtensionHikariConnectionContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class StatsRepository extends DataBaseRepository<Stats> implements StatsController {

    private ExtensionHikariConnectionContext dbContext;

    public StatsRepository(ExtensionHikariConnectionContext connectionContext) {
        super();
        this.dbContext = connectionContext;
    }

    /**
     * Returns the item of the given id
     *
     * @param id id
     * @return item
     */
    @Override
    public Optional<Stats> getById(long id) {
        try (Connection connection = this.dbContext.getConnection()) {
            try (PreparedStatement preparedStatement = this.dbContext.executeStoredQuery("stats/selectbyid", connection,
                    id)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(this.from(resultSet));
                    }
                }
            }
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
        return Optional.empty();
    }

    /**
     * Create a new player Stats
     *
     * @return stats
     */
    @Override
    public Stats create() {
        return new StatsData();
    }

    /**
     * Returns the stats of the player
     *
     * @param player player
     * @return stats
     */
    @Override
    public Optional<Stats> getByPlayer(Object player) {
        try (Connection connection = this.dbContext.getConnection()) {
            try (PreparedStatement preparedStatement = this.dbContext.executeStoredQuery("stats/selectbyplayer", connection,
                    ((Player)player).getUniqueId().toString())) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return Optional.of(this.from(resultSet));
                    }
                }
            }
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
        return Optional.empty();
    }

    /**
     * Returns the amount of items in the repository
     */
    @Override
    public int size() {
        try (Connection connection = this.dbContext.getConnection()) {
            try (PreparedStatement preparedStatement = this.dbContext.executeStoredQuery("stats/count", connection)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    return resultSet.getInt(1);
                }
            }
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
        return 0;
    }

    /**
     * Checks if the item has got an valid databaseId
     *
     * @param item item
     * @return hasGivenId
     */
    @Override
    protected boolean hasId(Stats item) {
        return item.getId() != 0;
    }

    /**
     * Selects all items from the database into the list
     *
     * @return listOfItems
     */
    @Override
    protected List<Stats> select() {
        final List<Stats> statsList = new ArrayList<>();
        try (Connection connection = this.dbContext.getConnection()) {
            try (PreparedStatement preparedStatement = this.dbContext.executeStoredQuery("stats/selectall", connection)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        final Stats stats = this.from(resultSet);
                        statsList.add(stats);
                    }
                }
            }
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
        return statsList;
    }

    /**
     * Updates the item inside of the database
     *
     * @param item item
     */
    @Override
    protected void update(Stats item) {
        try (Connection connection = this.dbContext.getConnection()) {
            this.dbContext.executeStoredUpdate("stats/update", connection,
                    item.getAmountOfWins(),
                    item.getAmountOfGamesPlayed(),
                    item.getAmountOfGoals(),
                    item.getId());
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
    }

    /**
     * Deletes the item from the database
     *
     * @param item item
     */
    @Override
    protected void delete(Stats item) {
        try (Connection connection = this.dbContext.getConnection()) {
            this.dbContext.executeStoredUpdate("stats/delete", connection,
                    item.getId());
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
    }

    /**
     * Inserts the item into the database and sets the id
     *
     * @param item item
     */
    @Override
    protected void insert(Stats item) {
        try (Connection connection = this.dbContext.getConnection()) {
            final long id = this.dbContext.executeStoredInsert("stats/insert", connection,
                    ((StatsData)item).getPlayerId(),
                    item.getAmountOfWins(),
                    item.getAmountOfGamesPlayed(),
                    item.getAmountOfGoals());
            ((StatsData) item).setId(id);
        } catch (final SQLException e) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e);
        }
    }

    /**
     * Generates the entity from the given resultSet
     *
     * @param resultSet resultSet
     * @return entity
     * @throws SQLException exception
     */
    @Override
    protected Stats from(ResultSet resultSet) throws SQLException {
        final StatsData stats = new StatsData();
        stats.setId(resultSet.getLong("id"));
        stats.setPlayerId(resultSet.getLong("shy_player_id"));
        stats.setAmountOfWins(resultSet.getInt("wins"));
        stats.setAmountOfGamesPlayed(resultSet.getInt("games"));
        stats.setAmountOfGoals(resultSet.getInt("goals"));
        return stats;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.dbContext = null;
    }
}
