package com.github.shynixn.blockball.bukkit.logic.business.entity.action;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class SimpleScoreboard implements AutoCloseable {
    private static final String DEFAULT_OBJECTIVE = "default";
    public static final String DUMMY_TYPE = "dummy";

    private Scoreboard scoreboard;

    /**
     * Initializes a new simpleScoreboard from a given scoreboard
     *
     * @param scoreboard scoreboard
     */
    public SimpleScoreboard(Scoreboard scoreboard) {
        super();
        this.scoreboard = scoreboard;
    }

    /**
     * Initializes a fresh new Scoreboard
     */
    public SimpleScoreboard() {
        super();
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    /**
     * Sets the default objective
     *
     * @param type type
     * @return scoreboard
     */
    public SimpleScoreboard setDefaultObjective(String type) {
        this.addObjective(DEFAULT_OBJECTIVE, type);
        return this;
    }

    /**
     * Adds a new objective to the scoreboard
     *
     * @param name name
     * @param type type
     * @return scoreboard
     */
    public SimpleScoreboard addObjective(String name, String type) {
        if (this.scoreboard.getObjective(name) != null)
            throw new IllegalArgumentException("This objective does already exist.");
        this.scoreboard.registerNewObjective(name, type);
        return this;
    }

    /**
     * Sets the displaySlot for the default objective
     *
     * @param displaySlot displaySlot
     * @return scoreboard
     */
    public SimpleScoreboard setDefaultDisplaySlot(DisplaySlot displaySlot) {
        return this.setDisplaySlot(DEFAULT_OBJECTIVE, displaySlot);
    }

    /**
     * Sets the displaySlot for the given objectiveName
     *
     * @param objectiveName objectiveName
     * @param displaySlot   displaySlot
     * @return scoreboard
     */
    public SimpleScoreboard setDisplaySlot(String objectiveName, DisplaySlot displaySlot) {
        final Objective objective = this.getObjective(objectiveName);
        objective.setDisplaySlot(displaySlot);
        return this;
    }

    /**
     * Sets the title for the default objective
     *
     * @param title title
     * @return scoreboard
     */
    public SimpleScoreboard setDefaultTitle(String title) {
        return this.setTitle(DEFAULT_OBJECTIVE, title);
    }

    /**
     * Sets the title for the given objectiveName
     *
     * @param objectiveName objectiveName
     * @param title         title
     * @return scoreboard
     */
    public SimpleScoreboard setTitle(String objectiveName, String title) {
        final Objective objective = this.getObjective(objectiveName);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', title));
        return this;
    }

    /**
     * Updates the given text for the line and default objective
     *
     * @param line line
     * @param text text
     * @return scoreboard
     */
    public SimpleScoreboard setDefaultLine(int line, String text) {
        return this.setLine(DEFAULT_OBJECTIVE, line, text);
    }

    /**
     * Updates the given text for the line and given objectiveName
     *
     * @param objectiveName objectiveName
     * @param line          line
     * @param text          text
     * @return scoreboard
     */
    public SimpleScoreboard setLine(String objectiveName, int line, String text) {
        final Objective objective = this.getObjective(objectiveName);
        final Objective bufferObjective;
        if (objective.getName().endsWith("_buf")) {
            bufferObjective = this.scoreboard.registerNewObjective(objectiveName.substring(0, objective.getName().indexOf("_buf")), objective.getCriteria());
        } else {
            bufferObjective = this.scoreboard.registerNewObjective(objectiveName + "_buf", objective.getCriteria());
        }
        for (final String s : this.scoreboard.getEntries()) {
            final Score score = objective.getScore(s);
            if (score.getScore() != 0 && score.getScore() != line) {
                bufferObjective.getScore(s).setScore(score.getScore());
            } else {
                this.scoreboard.resetScores(s);
            }
        }
        if (text != null) {
            String finalText = ChatColor.translateAlternateColorCodes('&', text);
            finalText = this.duplicateTextFinder(finalText, this.scoreboard.getEntries());
            bufferObjective.getScore(finalText).setScore(line);
        }
        bufferObjective.setDisplayName(objective.getDisplayName());
        bufferObjective.setDisplaySlot(objective.getDisplaySlot());
        objective.unregister();
        return this;
    }

    /**
     * Removes the line from the default objective
     *
     * @param line line
     * @return scoreboard
     */
    public SimpleScoreboard removeDefaultLine(int line) {
        return this.removeLine(DEFAULT_OBJECTIVE, line);
    }

    /**
     * Removes the line from the given objective Name
     *
     * @param objectiveName objectiveName
     * @param line          line
     * @return scoreboard
     */
    public SimpleScoreboard removeLine(String objectiveName, int line) {
        return this.setLine(objectiveName, line, null);
    }

    /**
     * Sets the score with the given name and value on the default objective
     *
     * @param scoreName scoreName
     * @param value     value
     * @return scoreboard
     */
    public SimpleScoreboard setDefaultScore(String scoreName, int value) {
        return this.setScore(DEFAULT_OBJECTIVE, scoreName, value);
    }

    /**
     * Sets the score with the given name and value on the given objectiveName
     *
     * @param objectiveName objectiveName
     * @param scoreName     scoreName
     * @param value         value
     * @return scoreboard
     */
    public SimpleScoreboard setScore(String objectiveName, String scoreName, int value) {
        final Objective objective = this.getObjective(objectiveName);
        objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreName)).setScore(value);
        return this;
    }

    /**
     * Returns value of the given scoreName on the default objective
     *
     * @param scoreName scoreName
     * @return value
     */
    public int getDefaultScore(String scoreName) {
        return this.getScore(DEFAULT_OBJECTIVE, scoreName);
    }

    /**
     * Returns the value of the given scoreName on the given objectiveName
     *
     * @param objectiveName objectiveName
     * @param scoreName     scoreName
     * @return value
     */
    public int getScore(String objectiveName, String scoreName) {
        final Objective deleteObjective = this.getObjective(objectiveName);
        return deleteObjective.getScore(ChatColor.translateAlternateColorCodes('&', scoreName)).getScore();
    }

    /**
     * Removes the score from the default objective
     *
     * @param scoreName scoreName
     * @return scoreboard
     */
    public SimpleScoreboard removeDefaultScore(String scoreName) {
        return this.removeScore(DEFAULT_OBJECTIVE, scoreName);
    }

    /**
     * Removes the score from the given objectiveName
     *
     * @param objectiveName objectiveName
     * @param scoreName     scoreName
     * @return scoreboard
     */
    public SimpleScoreboard removeScore(String objectiveName, String scoreName) {
        final Objective deleteObjective = this.getObjective(objectiveName);
        final Map<Objective, Integer> cachedScores = new HashMap<>();
        for (final Objective objective : this.scoreboard.getObjectives()) {
            if (!objective.equals(deleteObjective)) {
                cachedScores.put(objective, objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreName)).getScore());
            }
        }
        this.scoreboard.resetScores(ChatColor.translateAlternateColorCodes('&', scoreName));
        for (final Objective objective : cachedScores.keySet()) {
            objective.getScore(ChatColor.translateAlternateColorCodes('&', scoreName)).setScore(cachedScores.get(objective));
        }
        return this;
    }

    public boolean containsPlayer(Player player) {
        return player.getScoreboard() != null && this.scoreboard.equals(player.getScoreboard());
    }

    /**
     * Adds players to the scoreboard
     *
     * @param players players
     */
    public void addPlayer(Collection<Player> players) {
        this.addPlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Adds players to the scoreboard
     *
     * @param players players
     */
    public void addPlayer(Player... players) {
        for (final Player player : players) {
            if (!player.getScoreboard().equals(this.scoreboard)) {
                player.setScoreboard(this.scoreboard);
            }
        }
    }

    /**
     * Removes players from the scoreboard.
     *
     * @param players players
     */
    public void removePlayer(Collection<Player> players) {
        this.removePlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Removes players from the scoreboard.
     *
     * @param players players
     */
    public void removePlayer(Player... players) {
        for (final Player player : players) {
            if (player.getScoreboard().equals(this.scoreboard)) {
                player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            }
        }
    }

    /**
     * Returns the scoreboard
     *
     * @return scoreboard
     */
    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    /**
     * Avoids duplicates
     *
     * @param text  text
     * @param lines lines
     * @return text
     */
    private String duplicateTextFinder(String text, Collection<String> lines) {
        for (final String s : lines) {
            if (text.equals(s)) {
                text += ChatColor.translateAlternateColorCodes('&', "&r");
                text = this.duplicateTextFinder(text, lines);
                return text;
            }
        }
        return text;
    }

    /**
     * Returns the objective
     *
     * @param name name
     * @return objective
     */
    private Objective getObjective(String name) {
        Objective objective;
        if ((objective = this.scoreboard.getObjective(name)) == null) {
            if ((objective = this.scoreboard.getObjective(name + "_buf")) == null) {
                throw new IllegalArgumentException("Objective cannot be null!");
            }
        }
        return objective;
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
        if (this.scoreboard == null)
            return;
        for (final World world : Bukkit.getWorlds()) {
            this.removePlayer(world.getPlayers());
        }
        this.scoreboard = null;
    }
}
