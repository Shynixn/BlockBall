package com.github.shynixn.blockball.bukkit.logic.business.commandexecutor;

import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.controller.ArenaController;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.dependencies.worldedit.WorldEditConnection;
import com.github.shynixn.blockball.bukkit.logic.business.BlockBallManager;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.lib.ChatBuilder;
import com.github.shynixn.blockball.lib.SimpleCommandExecutor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
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
public class NewArenaCommandExecutor extends SimpleCommandExecutor.Registered {
    private static final String HEADER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                        Balls                         ";
    private static final String FOOTER_STANDARD = ChatColor.WHITE + "" + ChatColor.BOLD + "" + ChatColor.UNDERLINE + "                           ┌1/1┐                            ";

    private final BlockBallManager blockBallManager;
    private final Map<Player, Arena> cache = new HashMap<>();

    /**
     * Initializes a new commandExecutor by command, plugin.
     *
     * @param plugin plugin
     */
    public NewArenaCommandExecutor(BlockBallManager blockBallManager, Plugin plugin) {
        super("blockball", (JavaPlugin) plugin);
        this.blockBallManager = blockBallManager;
    }

    /**
     * Can be overwritten to listen to player executed commands
     *
     * @param player player
     * @param args   args
     */
    @Override
    public void onPlayerExecuteCommand(Player player, String[] args) {
        for (int i = 0; i < 20; i++) {
            player.sendMessage("");
        }
        player.sendMessage(HEADER_STANDARD);
        if (args.length == 0) {
            this.printFirstPage(player);
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("crna")) {

                ArenaController controller = this.blockBallManager.getGameController().getArenaController();
                final Arena arena = controller.create();

                this.cache.put(player, arena);
                this.printArenaPage(player);
            }
            else if(args[0].equalsIgnoreCase("cna"))
            {
                this.printArenaPage(player);
            }
            else if (args[0].equalsIgnoreCase("set-wecorners") && this.cache.containsKey(player)) {
                final Location left = WorldEditConnection.getLeftSelection(player);
                final Location right = WorldEditConnection.getRightSelection(player);
                if (left != null && right != null) {
                    this.cache.get(player).setCorners(left, right);
                }
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("set-goalred") && this.cache.containsKey(player)) {
                this.setGoal(player, Team.RED);
            } else if (args[0].equalsIgnoreCase("set-goalblue") && this.cache.containsKey(player)) {
                this.setGoal(player, Team.BLUE);
            } else if (args[0].equalsIgnoreCase("set-ballspawn") && this.cache.containsKey(player)) {
                this.cache.get(player).setBallSpawnLocation(player.getLocation());
                this.printArenaPage(player);
            } else if (args[0].equalsIgnoreCase("page-settings") && this.cache.containsKey(player)) {
                this.printSettingsSelectionPage(player);
            }

        } else if (args.length > 1) {
            if (args[0].equalsIgnoreCase("set-displayname") && this.cache.containsKey(player)) {
                final String name = this.mergeArgs(1, args);
                this.cache.get(player).setDisplayName(name);
                this.printArenaPage(player);
            }
            if (args[0].equalsIgnoreCase("save") && this.cache.containsKey(player)) {
                final Arena arena = this.cache.get(player);
                this.blockBallManager.getGameController().getArenaController().store(arena);
                player.sendMessage("Arena was saved.");
                onPlayerExecuteCommand(player, new String[] {args[1]});
            }
        }

        player.sendMessage(FOOTER_STANDARD);
    }

    private void setGoal(Player player, Team team) {
        final Location left = WorldEditConnection.getLeftSelection(player);
        final Location right = WorldEditConnection.getRightSelection(player);
        if (left != null && right != null) {
            this.getTeamMeta(player, team).getGoal().setCorners(left, right);
        }
        this.printArenaPage(player);
    }

    private void printSettingsSelectionPage(Player player) {
        if (!this.cache.containsKey(player))
            return;

        this.sendMessage(player, new ChatBuilder()
                .nextLine()
                .component("- Type configuration:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball page-set-types")
                .setHoverText("Opens the type configuration to change the gamemodes.")
                .builder().nextLine()
                .text(ChatColor.STRIKETHROUGH + "--------------------")
                .nextLine()
                .component(">>Save<<")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball save")
                .setHoverText("Saves the arena.")
                .builder().text(" ")
                .component(">>Back<<")
                .setColor(ChatColor.RED)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball crna")
                .setHoverText("Closes the current page.")
        );

    }

    private void printArenaPage(Player player) {
        if (!this.cache.containsKey(player))
            return;
        final Arena arena = this.cache.get(player);
        String display = "BlockBall arena " + arena.getId();
        if (arena.getDisplayName().isPresent()) {
            display = arena.getDisplayName().get();
        }
        String corners = "none";
        String goal1 = "none";
        String goal2 = "none";
        String ballSpawn = "none";
        if (arena.getUpperCorner() != null && arena.getLowerCorner() != null) {
            corners = this.printLocation(arena.getCenter());
        }
        if (this.getTeamMeta(player, Team.RED).getGoal().getLowerCorner() != null) {
            goal1 = this.printLocation(this.getTeamMeta(player, Team.RED).getGoal().getCenter());
        }
        if (this.getTeamMeta(player, Team.BLUE).getGoal().getLowerCorner() != null) {
            goal2 = this.printLocation(this.getTeamMeta(player, Team.BLUE).getGoal().getCenter());
        }
        if (arena.getBallSpawnLocation() != null) {
            ballSpawn = this.printLocation(arena.getBallSpawnLocation());
        }
        this.sendMessage(player, new ChatBuilder()
                .nextLine()
                .component("- Id: " + arena.getId())
                .setColor(ChatColor.GRAY)
                .builder()
                .component(", " + display).builder()
                .component(" [edit..]").setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.SUGGEST_COMMAND, "/blockball set-displayname ")
                .setHoverText("Edit the name of the arena.")
                .builder().nextLine()
                .component("- Center: " + corners).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball set-wecorners")
                .setHoverText("Uses the selected worldedit blocks to span the field of the arena.")
                .builder().nextLine()
                .component("- Goal 1: " + goal1).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball set-goalred")
                .setHoverText("Uses the selected worldedit blocks to span the goal for the red team.")
                .builder().nextLine()
                .component("- Goal 2: " + goal2).builder()
                .component(" [worldedit..]").setColor(ChatColor.GOLD)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball set-goalblue")
                .setHoverText("Uses the selected worldedit blocks to span the goal for the blue team.")
                .builder().nextLine()
                .component("- Ball spawnpoint: " + ballSpawn).builder()
                .component(" [location..]").setColor(ChatColor.BLUE)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball set-ballspawn")
                .setHoverText("Uses your current location to set the spawnpoint of the ball.")
                .builder().nextLine()
                .component("- Settings:").builder()
                .component(" [page..]").setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball page-settings")
                .setHoverText("Opens the settings page.")
                .builder().nextLine()
                .text(ChatColor.STRIKETHROUGH + "--------------------")
                .nextLine()
                .component(">>Save<<")
                .setColor(ChatColor.GREEN)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball save cna")
                .setHoverText("Saves the arena.")
                .builder().text(" ")
                .component(">>Back<<")
                .setColor(ChatColor.RED)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball")
                .setHoverText("Closes the current page.")
        );
    }

    private void printFirstPage(Player player) {
        this.sendMessage(player, new ChatBuilder()
                .component(">>Create arena<<")
                .setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball crna")
                .setHoverText("Creates a new blockball arena.")
                .builder().nextLine()
                .component(">>Edit arena<<")
                .setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball eda")
                .setHoverText("Opens the blockball arena configuration.")
                .builder().nextLine()
                .component(">>Remove arena<<")
                .setColor(ChatColor.YELLOW)
                .setClickAction(ChatBuilder.ClickAction.RUN_COMMAND, "/blockball rma")
                .setHoverText("Deletes a blockball arena.")
                .builder().nextLine()
        );
    }

    private BallMeta getBallMeta(Player player) {
        return this.cache.get(player).getMeta().find(BallMeta.class).get();
    }

    private TeamMeta getTeamMeta(Player player, Team team) {
        return this.cache.get(player).getMeta().findByTeam(TeamMeta[].class, team).get();
    }

    private Optional<Arena> getArena(String id) {
        return this.blockBallManager.getGameController().getArenaController().getById(id);
    }

    private void sendMessage(Player player, ChatBuilder builder) {
        builder.sendMessage(player);
    }

    private void sendMessage(Player player, ChatBuilder.Component builder) {
        this.sendMessage(player, builder.builder());
    }

    private String printLocation(Object mlocation) {
        final Location location = (Location) mlocation;
        return location.getWorld().getName() + " " + location.getBlockX() + "x " + location.getBlockY() + "y " + location.getBlockZ() + "z";
    }

    private String mergeArgs(int starting, String[] args) {
        final StringBuilder builder = new StringBuilder();
        for (int i = starting; i < args.length; i++) {
            if (builder.length() != 0)
                builder.append(' ');
            builder.append(args[i]);
        }
        return builder.toString();
    }
}
