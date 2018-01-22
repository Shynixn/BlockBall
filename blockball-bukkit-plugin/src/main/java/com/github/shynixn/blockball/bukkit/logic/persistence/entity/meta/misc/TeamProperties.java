package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc;

import com.github.shynixn.ball.bukkit.logic.persistence.configuration.Config;
import com.github.shynixn.blockball.api.persistence.entity.AreaSelection;
import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.area.SelectedArea;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.logging.Level;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class TeamProperties extends PersistenceObject<TeamMeta> implements TeamMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "spawnpoint-location")
    private IPosition spawnpoint;

    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "score.title")
    private String scoreTitle;

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "score.subtitle")
    private String scoreSubTitle;

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "win.title")
    private String winTitle;

    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "win.subtitle")
    private String winSubTitle;

    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "displayname")
    private String displayName;

    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "message.join")
    private String joinMessage;

    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "prefix")
    private String prefix;

    @YamlSerializer.YamlSerialize(orderNumber = 8, value = "walkingspeed")
    private double walkingSpeed = 0.2F;

    @YamlSerializer.YamlSerialize(orderNumber = 9, value = "min-amount")
    private int minAmount;

    @YamlSerializer.YamlSerialize(orderNumber = 10, value = "max-amount")
    private int maxAmount = 10;

    @YamlSerializer.YamlSerialize(orderNumber = 11, value = "armor")
    private String[] armor;

    @YamlSerializer.YamlSerialize(orderNumber = 12, value = "goal")
    private final SelectedArea goal = new SelectedArea();

    public TeamProperties() {
    }

    /**
     * Returns the goal of the team.
     *
     * @return goal
     */
    @Override
    public AreaSelection getGoal() {
        return this.goal;
    }

    /**
     * Sets the spawnpoint of the team.
     *
     * @param location location
     */
    @Override
    public void setSpawnPoint(Object location) {
        if (location != null) {
            this.spawnpoint = new LocationBuilder((org.bukkit.Location) location);
        } else {
            this.spawnpoint = null;
        }
    }

    /**
     * Returns the spawnpoint of the team.
     *
     * @return location
     */
    @Override
    public Optional<Object> getSpawnPoint() {
        if (this.spawnpoint == null) {
            return Optional.empty();
        } else {
            return Optional.of(((LocationBuilder) this.spawnpoint).toLocation());
        }
    }

    /**
     * Sets the title of the message getting played when a player scores a goal.
     *
     * @param title title
     */
    @Override
    public void setScoreMessageTitle(String title) {
        this.scoreTitle = title;
    }

    /**
     * Returns the title of the message getting played when a player scores a goal.
     *
     * @return title
     */
    @Override
    public Optional<String> getScoreMessageTitle() {
        return Optional.ofNullable(this.scoreTitle);
    }

    /**
     * Sets the subTitle of the message getting played when a player scores a goal
     *
     * @param subtitle subtitle
     */
    @Override
    public void setScoreMessageSubtitle(String subtitle) {
        this.scoreSubTitle = subtitle;
    }

    /**
     * Returns the subTitle of the message getting played when a player scores a goal
     *
     * @return title
     */
    @Override
    public Optional<String> getScoreMessageSubtitle() {
        return Optional.ofNullable(this.scoreSubTitle);
    }

    /**
     * Sets the title of the message getting played when the team wins a match.
     *
     * @param title title
     */
    @Override
    public void setWinningMessageTitle(String title) {
        this.winTitle = title;
    }

    /**
     * Returns the title of the message getting played when the team wins a match.
     *
     * @return title
     */
    @Override
    public Optional<String> getWinningMessageTitle() {
        return Optional.ofNullable(this.winTitle);
    }

    /**
     * Sets the subTitle of the message getting played when the team wins a match.
     *
     * @param subtitle subtitle
     */
    @Override
    public void setWinningMessageSubtitle(String subtitle) {
        this.winSubTitle = subtitle;
    }

    /**
     * Returns the subTitle of the message getting played when the team wins a match.
     *
     * @return title
     */
    @Override
    public Optional<String> getWinningMessageSubtitle() {
        return Optional.ofNullable(this.winSubTitle);
    }

    /**
     * Sets the name of the team which gets displayed by the placeholder :red or :blue.
     *
     * @param name name
     */
    @Override
    public void setDisplayName(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name cannot be null!");
        this.displayName = name;
    }

    /**
     * Returns he name of the team which gets displayed by the placeholder :red or :blue.
     *
     * @return name
     */
    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    /**
     * Sets the prefix of the team which gets displayed by the placeholder :redcolor or :bluecolor.
     *
     * @param prefix prefix
     */
    @Override
    public void setPrefix(String prefix) {
        if (prefix == null)
            throw new IllegalArgumentException("Prefix cannot be null!");
        this.prefix = prefix;
    }

    /**
     * Returns the prefix of the team which gets displayed by the placeholder :redcolor or :bluecolor.
     *
     * @return prefix
     */
    @Override
    public String getPrefix() {
        return this.prefix;
    }

    /**
     * Sets the amount of speed for the players.
     *
     * @param amount amount
     */
    @Override
    public void setWalkingSpeed(float amount) {
        this.walkingSpeed = amount;
    }

    /**
     * Returns the walkingSpeed of the players.
     *
     * @return speed
     */
    @Override
    public float getWalkingSpeed() {
        return this.maxAmount;
    }

    /**
     * Sets the min amount of players in each team.
     *
     * @param amount amount
     */
    @Override
    public void setMinAmountOfPlayers(int amount) {
        this.minAmount = amount;
    }

    /**
     * Returns the min amount of players in each team.
     *
     * @return amount
     */
    @Override
    public int getMinAmountOfPlayers() {
        return this.minAmount;
    }

    /**
     * Sets the max amount of players in team.
     *
     * @param amount amount
     */
    @Override
    public void setMaxAmountOfPlayers(int amount) {
        this.maxAmount = amount;
    }

    /**
     * Returns the max amount of players in team.
     *
     * @return amount
     */
    @Override
    public int getMaxAmountOfPlayers() {
        return this.maxAmount;
    }

    /**
     * Returns the armor items of the team.
     *
     * @return armor
     */
    @Override
    public Object[] getArmorContents() {
        final ItemStack[] itemStacks = new ItemStack[4];
        final FileConfiguration configuration = new YamlConfiguration();
        for (int i = 0; i < itemStacks.length; i++) {
            if (this.armor[i] != null) {
                try {
                    configuration.loadFromString(this.armor[i]);
                    itemStacks[i] = configuration.getItemStack("item");
                } catch (final InvalidConfigurationException e) {
                    Config.INSTANCE.getLogger().log(Level.WARNING, "Failed to deserialize armor.", e);
                }
            }
        }
        return itemStacks;
    }

    /**
     * Sets the armor items of the team.
     *
     * @param itemStacks armor
     */
    @Override
    public void setArmorContents(Object[] itemStacks) {
        if (itemStacks == null)
            throw new IllegalArgumentException("Itemstacks cannot be null!");
        this.armor = new String[4];
        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i] != null) {
                final FileConfiguration configuration = new YamlConfiguration();
                configuration.set("item", itemStacks[i]);
                this.armor[i] = configuration.saveToString();
            }
        }
    }

    /**
     * Returns the message being sent when a player joins a match.
     *
     * @return message
     */
    @Override
    public String getJoinMessage() {
        return null;
    }

    /**
     * Sets the message being sent when a player joins a match.
     *
     * @param message message
     */
    @Override
    public void setJoinMessage(String message) {

    }
}
