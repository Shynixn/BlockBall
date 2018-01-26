package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

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
public enum BlockBallCommand {
    BACK(PageKey.OPEN, "back"),
    CLOSE(PageKey.OPEN, "close"),
    OPEN(PageKey.OPEN, null),
    OPEN_RELOAD(PageKey.OPEN, "reload"),
    OPEN_EDIT_ARENA(PageKey.OPEN, "edit-arenas"),
    OPEN_DELETE_ARENA(PageKey.OPEN, "delete-arenas"),

    ARENA_CREATE(PageKey.MAINCONFIGURATION, "create-arena"),
    ARENA_EDIT(PageKey.MAINCONFIGURATION, "edit-arena"),
    ARENA_DELETE(PageKey.MAINCONFIGURATION, "delete-arena"),

    ARENA_MAINCONFIGURATION(PageKey.MAINCONFIGURATION, "main-configuration"),
    ARENA_SETDISPLAYNAME(PageKey.MAINCONFIGURATION, "set-displayname"),
    ARENA_SETBALLSPAWNPOINT(PageKey.MAINCONFIGURATION, "set-ballspawnpoint"),

    ARENA_SETAREA(PageKey.MAINCONFIGURATION, "set-area"),
    ARENA_SETGOALRED(PageKey.MAINCONFIGURATION, "set-goalred"),
    ARENA_SETGOALBLUE(PageKey.MAINCONFIGURATION, "set-goalblue"),
    ARENA_SAVE(PageKey.MAINCONFIGURATION, "save-arena"),;

    private PageKey key;
    private String command;

    BlockBallCommand(PageKey key, String command) {
        this.key = key;
        this.command = command;
    }

    public PageKey getKey() {
        return this.key;
    }

    public String getCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append("/blockball " + key.getKey() + " " + command + " ");
        return builder.toString();
    }

    public static BlockBallCommand from(String[] args) {
        if (args.length == 0)
            return OPEN;
        if (args.length > 0) {
            for (final BlockBallCommand command : BlockBallCommand.values()) {
                if (command.key.getKey() != null) {
                    if (command.key.getKey().equalsIgnoreCase(args[0])) {
                        if (command.command != null) {
                            if (command.command.equalsIgnoreCase(args[1])) {
                                return command;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
