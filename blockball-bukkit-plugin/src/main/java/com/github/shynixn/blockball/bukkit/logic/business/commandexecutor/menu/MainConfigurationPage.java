package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.menu;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.dependencies.worldedit.WorldEditConnection;
import com.github.shynixn.blockball.bukkit.logic.business.helper.ChatBuilder;
import org.bukkit.ChatColor;
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
public class MainConfigurationPage extends Page {
    public static final int ID = 2;

    public MainConfigurationPage() {
        super(MainConfigurationPage.ID, OpenPage.ID);
    }

    /**
     * Returns the key of the command when this page should be executed.
     *
     * @return key
     */
    @Override
    public PageKey getCommandKey() {
        return PageKey.MAINCONFIGURATION;
    }

    /**
     * Executes actions for this page.
     *
     * @param cache cache
     * @param args
     */
    @Override
    public CommandResult execute(Player player, BlockBallCommand command, Object[] cache, String[] args) {
        if (command == BlockBallCommand.ARENA_CREATE) {
            cache[0] = BlockBallApi.getDefaultGameController().getArenaController().create();
        } else if (command == BlockBallCommand.ARENA_EDIT) {
            cache[0] = BlockBallApi.getDefaultGameController().getArenaController().getById(args[2]).get();
        } else if (command == BlockBallCommand.ARENA_DELETE) {
            final Arena arena = BlockBallApi.getDefaultGameController().getArenaController().getById(args[2]).get();
            BlockBallApi.getDefaultGameController().getArenaController().remove(arena);
            return CommandResult.BACK;
        } else if (command == BlockBallCommand.ARENA_SETBALLSPAWNPOINT) {
            final Arena arena = (Arena) cache[0];
            arena.setBallSpawnLocation(player.getLocation());
        } else if (command == BlockBallCommand.ARENA_SETDISPLAYNAME) {
            final Arena arena = (Arena) cache[0];
            arena.setDisplayName(this.mergeArgs(2, args));
        } else if (command == BlockBallCommand.ARENA_SETAREA) {
            final Arena arena = (Arena) cache[0];
            final Location left = WorldEditConnection.getLeftSelection(player);
            final Location right = WorldEditConnection.getRightSelection(player);
            if (left != null && right != null) {
                arena.setCorners(left, right);
            } else {
                return CommandResult.WESELECTION_MISSING;
            }
        } else if (command == BlockBallCommand.ARENA_SETGOALRED) {
            final Arena arena = (Arena) cache[0];
            final Location left = WorldEditConnection.getLeftSelection(player);
            final Location right = WorldEditConnection.getRightSelection(player);
            if (left != null && right != null) {
                arena.getMeta().findByTeam(TeamMeta[].class, Team.RED).get()
                        .getGoal()
                        .setCorners(left, right);
            } else {
                return CommandResult.WESELECTION_MISSING;
            }
        } else if (command == BlockBallCommand.ARENA_SETGOALBLUE) {
            final Arena arena = (Arena) cache[0];
            final Location left = WorldEditConnection.getLeftSelection(player);
            final Location right = WorldEditConnection.getRightSelection(player);
            if (left != null && right != null) {
                arena.getMeta().findByTeam(TeamMeta[].class, Team.BLUE).get()
                        .getGoal()
                        .setCorners(left, right);
            } else {
                return CommandResult.WESELECTION_MISSING;
            }
        } else if (command == BlockBallCommand.ARENA_SAVE) {
            final Arena arena = (Arena) cache[0];
            if (arena.getLowerCorner() != null
                    && arena.getMeta().findByTeam(TeamMeta[].class, Team.BLUE).get().getGoal().getLowerCorner() != null
                    && arena.getMeta().findByTeam(TeamMeta[].class, Team.RED).get().getGoal().getLowerCorner() != null
                    && arena.getBallSpawnLocation() != null
                    ) {
                BlockBallApi.getDefaultGameController()
                        .getArenaController().store(arena);
            } else {
                return CommandResult.ARENA_NOTVALID;
            }
        }
        return super.execute(player, command, cache, args);
    }

    /**
     * Builds this page for the player.
     *
     * @return page
     */
    @Override
    public ChatBuilder buildPage(Object[] cache) {
        final Arena arena = (Arena) cache[0];
        String corners = "none";
        String goal1 = "none";
        String goal2 = "none";
        String ballSpawn = "none";
        if (arena.getUpperCorner() != null && arena.getLowerCorner() != null) {
            corners = this.printLocation(arena.getCenter());
        }
        if (this.getTeamMeta(arena, Team.RED).getGoal().getLowerCorner() != null) {
            goal1 = this.printLocation(this.getTeamMeta(arena, Team.RED).getGoal().getCenter());
        }
        if (this.getTeamMeta(arena, Team.BLUE).getGoal().getLowerCorner() != null) {
            goal2 = this.printLocation(this.getTeamMeta(arena, Team.BLUE).getGoal().getCenter());
        }
        if (arena.getBallSpawnLocation() != null) {
            ballSpawn = this.printLocation(arena.getBallSpawnLocation());
        }
        return new ChatBuilder()
                .component("- Id: " + arena.getId())
                .setColor(ChatColor.GRAY)
                .builder()
                .component(", " + arena.getDisplayName().get()).builder()
                .addComponent(ClickableComponent.EDIT.getComponent())
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, BlockBallCommand.ARENA_SETDISPLAYNAME.getCommand())
                .setHoverText("Edit the name of the arena.")
                .builder().nextLine()
                .component("- Center: " + corners).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETAREA.getCommand())
                .setHoverText("Uses the selected worldedit blocks to span the field of the arena.")
                .builder().nextLine()
                .component("- Goal 1: " + goal1).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETGOALRED.getCommand())
                .setHoverText("Uses the selected worldedit blocks to span the goal for the red team.")
                .builder().nextLine()
                .component("- Goal 2: " + goal2).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETGOALBLUE.getCommand())
                .setHoverText("Uses the selected worldedit blocks to span the goal for the blue team.")
                .builder().nextLine()
                .component("- Ball spawnpoint: " + ballSpawn).builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, BlockBallCommand.ARENA_SETBALLSPAWNPOINT.getCommand())
                .setHoverText("Uses your current location to set the spawnpoint of the ball.")
                .builder().nextLine()
                .component("- Settings:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball page-settings")
                .setHoverText("Opens the settings page.").builder();

    }
}
