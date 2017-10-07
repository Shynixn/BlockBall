package com.github.shynixn.blockball.bukkit.logic.persistence.entity.properties;

import com.github.shynixn.blockball.api.persistence.entity.CommandMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.lib.YamlSerializer;

import java.util.Optional;

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
public class CommandProperties extends PersistenceObject<CommandMeta> implements CommandMeta {

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "mode")
    private CommandMode mode;

    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "command")
    private String command;

    /**
     * Returns the mode of the command.
     *
     * @return mode
     */
    @Override
    public CommandMode getMode() {
        return this.mode;
    }

    /**
     * Sets the mode of the command.
     *
     * @param mode mode
     */
    @Override
    public void setMode(CommandMode mode) {
        if (mode == null)
            throw new IllegalArgumentException("Mode cannot be null!");
        this.mode = mode;
    }

    /**
     * Sets the command to be played.
     *
     * @param command command
     */
    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns the command to be played
     *
     * @return command
     */
    @Override
    public Optional<String> getCommand() {
        return Optional.ofNullable(this.command);
    }
}
