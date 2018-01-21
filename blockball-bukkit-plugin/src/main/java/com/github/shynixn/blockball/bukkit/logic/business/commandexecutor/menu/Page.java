package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
public abstract class Page {

    private int id;
    private int previousId;

    public Page(int id, int previousId) {
        this.id = id;
        this.previousId = previousId;
    }

    public int getPreviousId() {
        return this.previousId;
    }

    public int getId() {
        return this.id;
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    public abstract PageKey getCommandKey();

    /**
     * Builds the page content.
     *
     * @param cache cache
     * @return content
     */
    public ChatBuilder buildPage(Object[] cache) {
        return null;
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     */
    public CommandResult execute(Player player, BlockBallCommand command, Object[] cache, String[] args) {
        return CommandResult.SUCCESS;
    }

    TeamMeta getTeamMeta(Arena arena, Team team) {
        return arena.getMeta().findByTeam(TeamMeta[].class, team).get();
    }

    String mergeArgs(int starting, String[] args) {
        final StringBuilder builder = new StringBuilder();
        for (int i = starting; i < args.length; i++) {
            if (builder.length() != 0)
                builder.append(' ');
            builder.append(args[i]);
        }
        return builder.toString();
    }

    String printLocation(Object mlocation) {
        final Location location = (Location) mlocation;
        return location.getWorld().getName() + " " + location.getBlockX() + "x " + location.getBlockY() + "y " + location.getBlockZ() + "z";
    }
}
