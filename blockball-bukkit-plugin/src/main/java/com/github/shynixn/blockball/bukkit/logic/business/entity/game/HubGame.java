package com.github.shynixn.blockball.bukkit.logic.business.entity.game;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.api.bukkit.event.game.GameJoinEvent;
import com.github.shynixn.blockball.api.bukkit.event.game.GameLeaveEvent;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.HubLobbyMeta;
import com.github.shynixn.blockball.api.persistence.entity.IPosition;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.lib.ScreenUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
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
public class HubGame extends RGame {

    private final String[] hubGameSign;

    private final HubLobbyMeta hubLobbyMeta;

    public HubGame(Arena arena) {
        super(arena);
        this.hubLobbyMeta = arena.getMeta().find(HubLobbyMeta.class).get();
        this.hubGameSign = Config.getInstance().getHubGameSign();
    }

    /**
     * Adds a player to the game returns false if he doesn't meet the required options.
     *
     * @param player player - @NotNull
     * @param team   team - @Nullable, team gets automatically selection
     * @return success
     */
    @Override
    public boolean join(Object player, Team team) {
        this.leave(player);
        Team joiningTeam = team;
        if (this.customizingMeta.isAutoSelectTeamEnabled()) {
            if (this.redTeamPlayers.size() <= this.blueTeamPlayers.size()) {
                joiningTeam = Team.RED;
            } else {
                joiningTeam = Team.BLUE;
            }
        }
        if (joiningTeam == Team.RED && this.redTeamPlayers.size() < this.redTeamMeta.getMaxAmountOfPlayers()) {
            final GameJoinEvent event = new GameJoinEvent(this, (Player) player);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.redTeamPlayers.add((Player) player);
                this.preparePlayerForGame((Player) player, team, this.redTeamMeta);
                return true;
            }
            return false;
        } else if (joiningTeam == Team.BLUE && this.blueTeamPlayers.size() < this.blueTeamMeta.getMaxAmountOfPlayers()) {
            final GameJoinEvent event = new GameJoinEvent(this, (Player) player);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.blueTeamPlayers.add((Player) player);
                this.preparePlayerForGame((Player) player, team, this.blueTeamMeta);
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * Removes a player from the given game returns false if it did not work.
     *
     * @param player player - @NotNull
     * @return success
     */
    @Override
    public boolean leave(Object player) {
        final Optional<GamePlayer> optPlayer = this.getGamePlayerByPlayer((Player) player);
        if (optPlayer.isPresent()) {
            final GamePlayer gamePlayer = optPlayer.get();
            if (gamePlayer.team == Team.BLUE) {
                this.blueTeamPlayers.remove(player);
            } else {
                this.redTeamPlayers.remove(player);
            }
            this.gamePlayers.remove(gamePlayer);

            gamePlayer.restoreFromHubGame();

            ((Player) player).sendMessage(this.customizingMeta.getLeaveMessage().get());

            final GameLeaveEvent gameLeaveEvent = new GameLeaveEvent(this, (Player) player);
            Bukkit.getPluginManager().callEvent(gameLeaveEvent);
            return true;
        }
        return false;
    }

    /**
     * Gets called when a team scores enough goals to reach the max amount of goals.
     *
     * @param teamMeta teamMeta of the winning team
     */
    @Override
    public void onWin(TeamMeta teamMeta) {
        ScreenUtils.setTitle(this.replaceMessagePlaceholders(teamMeta.getWinningMessageTitle().get())
                , teamMeta.getWinningMessageSubtitle().get()
                , 0, 20 * 3, 10, (List<Player>) (Object) this.getPlayers());
        try {
            this.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets called when a team scores a goal.
     *
     * @param teamMeta teamMeta of the scorring team
     */
    @Override
    public void onScore(TeamMeta teamMeta) {
        try {
            this.arena.getBallGoalParticle().apply(this.ball.getLocation());
            this.arena.getBallGoalSound().applyToLocation(this.ball.getLocation());
        } catch (final Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [BallGoalSound]");
        }
        ScreenUtils.setTitle(this.replaceMessagePlaceholders(teamMeta.getScoreMessageTitle().get())
                , teamMeta.getScoreMessageSubtitle().get()
                , 0, 20 * 3, 10, (List<Player>) (Object) this.getPlayers());
    }

    /**
     * Gets called when the game life cycle allows updating signs.
     */
    @Override
    public void onUpdateSigns() {
        for (int i = 0; i < this.hubLobbyMeta.getRedTeamSignPositions().size(); i++) {
            final IPosition position = this.hubLobbyMeta.getRedTeamSignPositions().get(i);
            final Location location = ((LocationBuilder) position).toLocation();
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                this.replacePlaceTextOnSign((Sign) location.getBlock().getState()
                        , this.hubGameSign, this.redTeamMeta, this.redTeamPlayers.size(), this.redTeamMeta.getMaxAmountOfPlayers());
            } else {
                this.hubLobbyMeta.removeRedTeamSignPosition(position);
            }
        }
        for (int i = 0; i < this.hubLobbyMeta.getBlueTeamSignPositions().size(); i++) {
            final IPosition position = this.hubLobbyMeta.getBlueTeamSignPositions().get(i);
            final Location location = ((LocationBuilder) position).toLocation();
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                this.replacePlaceTextOnSign((Sign) location.getBlock().getState()
                        , this.hubGameSign, this.blueTeamMeta, this.blueTeamPlayers.size(), this.blueTeamMeta.getMaxAmountOfPlayers());
            } else {
                this.hubLobbyMeta.removeBlueTeamSignPosition(position);
            }
        }
    }

    private void preparePlayerForGame(Player player, Team team, TeamMeta teamMeta) {
        final GamePlayer gamePlayer = new GamePlayer(player);
        this.gamePlayers.add(gamePlayer);
        gamePlayer.storeForHubGame();
        gamePlayer.team = team;

        player.setWalkSpeed(teamMeta.getWalkingSpeed());
        player.getInventory().setArmorContents((ItemStack[]) teamMeta.getArmorContents());

        if (teamMeta.getSpawnPoint().isPresent()) {
            player.teleport((Location) teamMeta.getSpawnPoint().get());
        } else {
            player.teleport((Location) this.getArena().getBallSpawnLocation());
        }

        player.sendMessage(Config.getInstance().getPrefix() + teamMeta.getJoinMessage());
    }
}
