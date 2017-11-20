package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.bukkit.event.ball.BallInteractWithEntityEvent;
import com.github.shynixn.blockball.api.bukkit.event.misc.PlaceHolderRequestEvent;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.business.enumeration.GameStatus;
import com.github.shynixn.blockball.api.business.enumeration.GameType;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.*;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.CustomizingMeta;
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.RGame;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder;
import com.github.shynixn.blockball.lib.SimpleListener;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.regex.Pattern;

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
public class GameListener extends SimpleListener {

    private final GameController gameController;

    private final Map<Player, LocationBuilder> lastLocation = new HashMap<>();
    private final Set<Player> toggledPlayers = new HashSet<>();
    private final Map<Player, Integer> moveCounter = new HashMap<>();

    /**
     * Initializes a new listener by plugin.
     *
     * @param plugin plugin
     */
    public GameListener(Plugin plugin, GameController controller) {
        super(plugin);
        if (controller == null) {
            throw new IllegalArgumentException("Gamecontroller cannot be null!");
        }
        this.gameController = controller;
    }

    /**
     * Joins a bungeeCord game if one is available on the server and fixes spawnpoint
     * when a player tries to spawn in an arena.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        final Optional<Game> optGame = this.getBungeeCordGame();
        optGame.ifPresent(game -> this.joinBungeeCordGame(event.getPlayer(), game));
        this.fixPlayersJoiningInArena(event.getPlayer());
    }

    /**
     * Leaves a game if the player is currently inside of one.
     *
     * @param event event
     */
    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        optGame.ifPresent(game -> game.leave(event.getPlayer()));
    }

    /**
     * Cancels hunger for players in games.
     *
     * @param event event
     */
    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getEntity());
        if (optGame.isPresent()) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels inventory actions for players in games.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerClickInventoryEvent(InventoryClickEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getWhoClicked());
        if (optGame.isPresent()) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
    }

    /**
     * Cancels inventory actions for players in games.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerOpenInventoryEvent(InventoryOpenEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        if (optGame.isPresent()) {
            event.setCancelled(true);
        }
    }

    /**
     * Cancels world interactions for players in minigame-lobbies.
     *
     * @param event event
     */
    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        if (optGame.isPresent()) {
            if (optGame.get().getArena().getGameType() == GameType.BUNGEE || optGame.get().getArena().getGameType() == GameType.MINIGAME) {
                if (optGame.get().getStatus() == GameStatus.ENABLED) {
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * Cancels all fall damage for players in games and all damage if enabled in the arena configuration.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        final Player player = (Player) event.getEntity();
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(player);
        if (optGame.isPresent()) {
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            } else if (!optGame.get().getArena().getMeta().find(CustomizingMeta.class).get().isDamagingPlayersEnabled()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Cancels command executions in minigame games.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerExecuteCommand(PlayerCommandPreprocessEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
        if (optGame.isPresent() && this.isNotLeaveMessage(event.getMessage())) {
            if (optGame.get().getArena().getGameType() == GameType.BUNGEE || optGame.get().getArena().getGameType() == GameType.MINIGAME) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerRequestPlaceHolder(PlaceHolderRequestEvent event) {
        int gameId = event.getGameId();
        if (gameId == -1) {
            final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getPlayer());
            if (optGame.isPresent()) {
                gameId = Integer.parseInt(optGame.get().getArena().getName());
            } else {
                return;
            }
        }
        final Optional<Game> optGame = this.gameController.getGameFromArenaName(String.valueOf(gameId));
        optGame.ifPresent(game -> event.setResult(game.getValueForPlaceHolder(event.getType())));
    }

    @EventHandler
    public void onPlayerClickShieldEvent(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock().getType() != Material.SIGN_POST && event.getClickedBlock().getType() != Material.WALL_SIGN)
            return;
        final Optional<Game> optGame = this.getGameFromSign((Sign) event.getClickedBlock().getState());
        final IPosition signPosition = new LocationBuilder(event.getClickedBlock().getLocation());
        if (optGame.isPresent()) { //Joining
            if (optGame.get().getArena().getGameType() == GameType.HUBGAME) {
                this.tryJoinGame(event.getPlayer(), optGame.get(), Team.RED, signPosition, optGame.get().getArena().getMeta().find(HubLobbyMeta.class).get().getRedTeamSignPositions(), true);
                this.tryJoinGame(event.getPlayer(), optGame.get(), Team.BLUE, signPosition, optGame.get().getArena().getMeta().find(HubLobbyMeta.class).get().getBlueTeamSignPositions(), true);
            } else if (optGame.get().getArena().getGameType() == GameType.MINIGAME) {
                this.tryJoinGame(event.getPlayer(), optGame.get(), null, signPosition, optGame.get().getArena().getMeta().find(MinigameLobbyMeta.class).get().getJoinSignPositions(), true);
            }
        } else { //Leaving
            final Optional<Game> optPGame = this.gameController.getGameFromPlayer(event.getPlayer());
            optPGame.ifPresent(game -> this.tryJoinGame(event.getPlayer(), game, null, signPosition, optGame.get().getArena().getMeta().find(CustomizingMeta.class).get().getLeaveSigns(), true));
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (event.getTo().distance(event.getFrom()) <= 0)
            return;
        if (this.gameController.getGameFromPlayer(event.getPlayer()).isPresent())
            return;
        for (final Game game : this.gameController.getAll()) {
            if (game.getArena().isEnabled() && game.getArena().getGameType() == GameType.HUBGAME) {
                if (game.getArena().isLocationInSelection(event.getTo())) {
                    if (!this.lastLocation.containsKey(event.getPlayer())) {
                        event.getPlayer().setVelocity(Config.getInstance().getPlayerrotectionVelocity());
                    } else {
                        if (!this.moveCounter.containsKey(event.getPlayer()))
                            this.moveCounter.put(event.getPlayer(), 1);
                        else if (this.moveCounter.get(event.getPlayer()) < 50)
                            this.moveCounter.put(event.getPlayer(), this.moveCounter.get(event.getPlayer()) + 1);
                        if (this.moveCounter.get(event.getPlayer()) > 20) {
                            event.getPlayer().setVelocity(Config.getInstance().getPlayerrotectionVelocity());
                        } else if (!game.getArena().getMeta().find(CustomizingMeta.class).get().isFastJoiningEnabled()) {
                            final Vector knockback = this.lastLocation.get(event.getPlayer()).toVector().subtract(event.getPlayer().getLocation().toVector());
                            event.getPlayer().getLocation().setDirection(knockback);
                            event.getPlayer().setVelocity(knockback);
                            event.getPlayer().setAllowFlight(true);
                            this.toggledPlayers.add(event.getPlayer());
                        }
                    }
                    if (event.getPlayer().hasPermission("blockball.user")) {
                        if (game.getArena().getMeta().find(CustomizingMeta.class).get().isFastJoiningEnabled()) {
                            this.joinGame(event.getPlayer(), null, game);
                        } else {
                            event.getPlayer().sendMessage("TYPE THIS TO JOIN!");
                        }
                    } else {
                        event.getPlayer().sendMessage(Config.getInstance().getPrefix() + Config.getInstance().getJoinCommandPermissionMessage());
                    }
                }
            }
        }
        if (this.moveCounter.containsKey(event.getPlayer())) {
            this.moveCounter.remove(event.getPlayer());
        }
        this.lastLocation.put(event.getPlayer(), new LocationBuilder(event.getPlayer().getLocation()));
    }

    @EventHandler
    public void onPlayerToggleEvent(final PlayerToggleFlightEvent event) {
        if (this.toggledPlayers.contains(event.getPlayer()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
            event.setCancelled(true);
            this.toggledPlayers.remove(event.getPlayer());
        }
    }

    /**
     * Gets called when a player interacts with a ball which is in a game.
     *
     * @param event event
     */
    @EventHandler
    public void ballInteractEvent(BallInteractWithEntityEvent event) {
        final Optional<Game> optGame = this.gameController.getGameFromPlayer(event.getEntity());
        if (!optGame.isPresent())
            return;
        final RGame entity = (RGame) optGame.get();
        final Location ballLocation = (Location) event.getBall().getLocation();

        if (entity.ballPreviousCacheLocation != null && entity.ballPreviousCacheLocation.distance(ballLocation.toVector()) < 2) {
            entity.ballCornerBumper++;
        } else {
            entity.ballCornerBumper = 0;
        }
        if (entity.ballCornerBumper >= 3) {
            final Vector direction = ((Location) entity.getArena().getBallSpawnLocation()).toVector().subtract(ballLocation.toVector());
            int x = 1;
            int z = 1;
            if (direction.getX() < 0)
                x = -1;
            if (direction.getZ() < 0)
                z = -1;
            event.getBall().teleport(new Location(ballLocation.getWorld(), ballLocation.getX() + x, ballLocation.getY(), ballLocation.getZ() + z));
            entity.ballCornerBumper = 0;
        }
        entity.ballPreviousCacheLocation = ballLocation.toVector();
        entity.lastHitTeam = entity.getTeamFromPlayer(event.getEntity());
        entity.lastHit = (Player) event.getEntity();
    }

    private void tryJoinGame(Player player, Game game, Team team, IPosition signPosition, List<IPosition> signPositions, boolean join) {
        for (final IPosition stored : signPositions) {
            if (stored.equals(signPosition)) {
                if (join) {
                    this.joinGame(player, team, game);
                } else {
                    game.leave(player);
                }
                return;
            }
        }
    }

    private void joinGame(Player player, Team team, Game game) {
        final TeamMeta teamMeta = game.getArena().getMeta().findByTeam(TeamMeta[].class, team).get();
        final boolean result = game.join(player, team);
        if (result) {
            if (teamMeta == null) {
                player.sendMessage(Config.getInstance().getPrefix() + game.getArena().getMeta().find(MinigameLobbyMeta.class).get().getJoinMessage());
            } else {
                player.sendMessage(Config.getInstance().getPrefix() + teamMeta.getJoinMessage());
            }
        } else {
            player.sendMessage(Config.getInstance().getPrefix() + game.getArena().getMeta().find(CustomizingMeta.class).get().getTeamFullMessage());
        }
    }

    private boolean isNotLeaveMessage(String message) {
        try {
            return !message.split(Pattern.quote(":"))[0].equalsIgnoreCase('/' + Config.getInstance().getLeaveCommandName());
        } catch (final Exception ex) {
            return true;
        }
    }

    private void fixPlayersJoiningInArena(Player player) {
        if (Config.getInstance().isRescueJoinSpawnpointEnabled()) {
            final Location spawnpoint = ((LocationBuilder) Config.getInstance().getRescueJoinSpawnpoint()).toLocation();
            for (final Arena arena : this.gameController.getArenaController().getAll()) {
                if (player.getLocation().getWorld().getName().equals(((Location) arena.getBallSpawnLocation()).getWorld().getName())
                        && arena.isLocationInSelection(player.getLocation())) {
                    player.teleport(spawnpoint);
                    return;
                }
            }
        }
    }

    private void joinBungeeCordGame(Player player, Game game) {
        final boolean result = !game.join(player, null);
        if (result) {
            final BungeeCordLobbyMeta bungeeCordLobbyMeta = game.getArena().getMeta().find(BungeeCordLobbyMeta.class).get();
            if (bungeeCordLobbyMeta.getKickMessage().isPresent()) {
                player.kickPlayer(bungeeCordLobbyMeta.getKickMessage().get());
            } else {
                player.kickPlayer(null);
            }
        }
    }

    private Optional<Game> getGameFromSign(Sign sign) {
        try {
            int lineIndex = -1;
            for (final String s : Config.getInstance().getHubGameSign()) {
                if (s.equalsIgnoreCase("<game>")) {
                    break;
                }
                lineIndex++;
            }
            final String line = sign.getLine(lineIndex);
            Optional<Game> game;
            if ((game = this.gameController.getGameFromArenaName(line)).isPresent()) {
                return game;
            } else if ((game = this.gameController.getGameFromDisplayName(line)).isPresent()) {
                return game;
            }
            return Optional.empty();
        } catch (final Exception ex) {
            return Optional.empty();
        }
    }

    private Optional<Game> getBungeeCordGame() {
        for (final Game game : this.gameController.getAll()) {
            if (game.getArena().getGameType() == GameType.BUNGEE && game.getArena().isEnabled()) {
                return Optional.of(game);
            }
        }
        return Optional.empty();
    }
}
