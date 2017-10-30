package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.misc;

import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CommandMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.RewardMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.lib.YamlSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
public class RewardProperties extends PersistenceObject<RewardMeta> implements RewardMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "money.per-goal")
    private int pergoal;
    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "money.per-win")
    private int perWin;
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "money.per-match")
    private int perMatch;

    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "commands.goal")
    private final List<CommandMeta> goalCommands = new ArrayList<>();
    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "commands.win")
    private final List<CommandMeta> winCommands = new ArrayList<>();
    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "commands.match")
    private final List<CommandMeta> matchCommands = new ArrayList<>();

    /**
     * Sets the amount of money the player receives per goal.
     *
     * @param amount amount
     */
    @Override
    public void setPerGoal(int amount) {
        this.pergoal = amount;
    }

    /**
     * Returns the amount of money the player receives per goal.
     *
     * @return amount
     */
    @Override
    public int getPerGoal() {
        return this.pergoal;
    }

    /**
     * Sets the amount of money the player receives per win.
     *
     * @param amount amount
     */
    @Override
    public void setPerWin(int amount) {
        this.perWin = amount;
    }

    /**
     * Returns the amount of money the player receives per win.
     *
     * @return amount
     */
    @Override
    public int getPerWin() {
        return this.perWin;
    }

    /**
     * Sets the amount of money the player receives by playing a full match regardless if he wins or not.
     *
     * @param amount amount
     */
    @Override
    public void setPerMatch(int amount) {
        this.perMatch = amount;
    }

    /**
     * Returns the amount of money the player receives by playing a full match regardless if he wins or not.
     *
     * @return amount
     */
    @Override
    public int getPerMatch() {
        return this.perMatch;
    }

    /**
     * Adds a command which get executed by every single goal.
     *
     * @param commandMeta command
     */
    @Override
    public void addPerGoalCommand(CommandMeta commandMeta) {
        this.goalCommands.add(commandMeta);
    }

    /**
     * Removes a command which get executed by every single goal.
     *
     * @param commandMeta command
     */
    @Override
    public void removePerGoalCommand(CommandMeta commandMeta) {
        if (this.goalCommands.contains(commandMeta)) {
            this.goalCommands.remove(commandMeta);
        }
    }

    /**
     * Returns the commands which get executed by every single goal.
     *
     * @return commands
     */
    @Override
    public List<CommandMeta> getPerGoalCommands() {
        return Collections.unmodifiableList(this.goalCommands);
    }

    /**
     * Adds a commands which gets executed for the winning team.
     *
     * @param commandMeta command
     */
    @Override
    public void addPerWinCommand(CommandMeta commandMeta) {
        this.winCommands.add(commandMeta);
    }

    /**
     * Removes the command which gets executed for the winning team.
     *
     * @param commandMeta command
     */
    @Override
    public void removePerWinCommand(CommandMeta commandMeta) {
        if (this.winCommands.contains(commandMeta)) {
            this.winCommands.remove(commandMeta);
        }
    }

    /**
     * Returns the commands which gets executed for the winning team.
     *
     * @return commands
     */
    @Override
    public List<CommandMeta> getPerWinCommands() {
        return Collections.unmodifiableList(this.winCommands);
    }

    /**
     * Adds a command which gets played after a full match regardless if the team wins or not
     *
     * @param commandMeta command
     */
    @Override
    public void addPerMatchCommand(CommandMeta commandMeta) {
        this.matchCommands.add(commandMeta);
    }

    /**
     * Removes the command which gets played after a full match regardless if the team wins or not
     *
     * @param commandMeta command
     */
    @Override
    public void removePerMatchCommand(CommandMeta commandMeta) {
        if(this.matchCommands.contains(commandMeta))
        {
            this.matchCommands.remove(commandMeta);
        }
    }

    /**
     * Returns the commands which gets played after a full match regardless if the team wins or not.
     *
     * @return commands
     */
    @Override
    public List<CommandMeta> getPerMatchCommands() {
        return Collections.unmodifiableList(this.matchCommands);
    }
}
