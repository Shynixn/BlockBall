package com.github.shynixn.blockball.api.persistence.entity;

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
public interface RewardMeta extends Persistenceable<RewardMeta> {

    /**
     * Sets the amount of money the player receives per goal.
     *
     * @param amount amount
     */
    void setPerGoal(int amount);

    /**
     * Returns the amount of money the player receives per goal.
     *
     * @return amount
     */
    int getPerGoal();

    /**
     * Sets the amount of money the player receives per win.
     *
     * @param amount amount
     */
    void setPerWin(int amount);

    /**
     * Returns the amount of money the player receives per win.
     *
     * @return amount
     */
    int getPerWin();

    /**
     * Sets the amount of money the player receives by playing a full match regardless if he wins or not.
     *
     * @param amount amount
     */
    void setPerMatch(int amount);

    /**
     * Returns the amount of money the player receives by playing a full match regardless if he wins or not.
     *
     * @return amount
     */
    int getPerMatch();

    /**
     * Adds a command which get executed by every single goal.
     *
     * @param commandMeta command
     */
    void addPerGoalCommand(CommandMeta commandMeta);

    /**
     * Removes a command which get executed by every single goal.
     *
     * @param commandMeta command
     */
    void removePerGoalCommand(CommandMeta commandMeta);

    /**
     * Returns the commands which get executed by every single goal.
     *
     * @return commands
     */
    List<CommandMeta> getPerGoalCommands();

    /**
     * Adds a commands which gets executed for the winning team.
     *
     * @param commandMeta command
     */
    void addPerWinCommand(CommandMeta commandMeta);

    /**
     * Removes the command which gets executed for the winning team.
     *
     * @param commandMeta command
     */
    void removePerWinCommand(CommandMeta commandMeta);

    /**
     * Returns the commands which gets executed for the winning team.
     *
     * @return commands
     */
    List<CommandMeta> getPerWinCommands();

    /**
     * Adds a command which gets played after a full match regardless if the team wins or not
     *
     * @param commandMeta command
     */
    void addPerMatchCommand(CommandMeta commandMeta);

    /**
     * Removes the command which gets played after a full match regardless if the team wins or not
     *
     * @param commandMeta command
     */
    void removePerMatchCommand(CommandMeta commandMeta);

    /**
     * Returns the commands which gets played after a full match regardless if the team wins or not.
     *
     * @return commands
     */
    List<CommandMeta> getPerMatchCommands();
}
