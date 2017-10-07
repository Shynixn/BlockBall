package com.github.shynixn.blockball.bukkit.logic.business.listener;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.GameType;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.api.persistence.controller.PlayerMetaController;
import com.github.shynixn.blockball.api.persistence.controller.StatsController;
import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Language;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.Factory;
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameController;
import com.github.shynixn.blockball.bukkit.logic.business.entity.GameEntity;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.SoundBuilder;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.entities.items.BoostItem;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

class GameListener extends SimpleListener {
    private final Map<Player, SLocation> lastLocation = new HashMap<>();
    private final Map<Player, Game> connectedGames = new HashMap<>();
    private final List<Player> toggledPlayers = new ArrayList<>();
    private final Map<Player, Integer> moveCounter = new HashMap<>();
    private Map<Player, StatsScoreboard> statsScoreboards;
    private final GameController controller;
    private final StatsController statsController;
    private final PlayerMetaController playerMetaController;

    GameListener(final GameController controller) {
        super(JavaPlugin.getPlugin(BlockBallPlugin.class));
        this.controller = controller;
        this.playerMetaController = Factory.createPlayerDataController();
        this.statsController = Factory.createStatsController();
        if (Config.getInstance().isScoreboardPlayerStatsEnabled()) {
            this.statsScoreboards = new HashMap<>();
            this.updateStatsScoreboard();
        }
        for (final World world : Bukkit.getWorlds()) {
            for (final Player player : world.getPlayers()) {
                this.enableJoinStats(player);
            }
        }
        if (Config.getInstance().getForcefieldHelperCommand().isEnabled()) {
            new DynamicCommandHelper(Config.getInstance().getForcefieldHelperCommand()) {
                @Override
                public void onCommandSend(CommandSender sender, String[] args) {
                    if (sender instanceof Player) {
                        final String message = ChatColor.stripColor(this.getText(args));
                        final Player player = (Player) sender;
                        if (controller.getGameFromPlayer(player) != null) {
                            GameListener.this.connectedGames.put(player, controller.getGameFromPlayer(player));
                        }
                        GameListener.this.handleChatMessage(player, message);
                    }
                }
            };
        }
    }

    /**
     * Gets called when a player shoots a goal
     *
     * @param shootEvent shootEvent
     */
    @EventHandler
    public void onPlayerShootGoalEvent(GoalShootEvent shootEvent) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                final Stats stats = GameListener.this.statsController.getByPlayer(shootEvent.getPlayer());
                stats.setAmountOfGoals(stats.getAmountOfGoals() + 1);
                this.updateStats(shootEvent.getPlayer(), stats);
                this.statsController.store(stats);
            }
        });
    }

    /**
     * Gets called when a player joins the match
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerJoinGameEvent(GameJoinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                final Stats stats = GameListener.this.statsController.getByPlayer(event.getPlayer());
                stats.setAmountOfGamesPlayed(stats.getAmountOfGamesPlayed() + 1);
                this.statsController.store(stats);
                this.updateStats(event.getPlayer(), stats);
            }
        });
    }

    /**
     * Gets called when a game gets won
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerShootGoalEvent(GameWinEvent event) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                for (final Player player : event.getWinningTeam()) {
                    final Stats stats = GameListener.this.statsController.getByPlayer(player);
                    stats.setAmountOfWins(stats.getAmountOfWins() + 1);
                    this.statsController.store(stats);
                    this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> this.updateStats(player, stats), 40L);
                }
            }
        });
    }

    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromPlayer(event.getPlayer())) != null) {
            for (final Item item : game.getArena().getBoostItemHandler().getDroppedItems()) {
                final BoostItem boostItem;
                if (item.equals(event.getItem()) && ((boostItem = game.getArena().getBoostItemHandler().getBoostFromItem(item)) != null)) {
                    try {
                        new SoundBuilder("NOTE_PLING", 2.0, 2.0).apply(event.getPlayer().getLocation(), event.getPlayer());
                    } catch (final Exception e) {
                        Bukkit.getLogger().log(Level.WARNING, "Failed to play sound.", e);
                    }
                    game.getArena().getBoostItemHandler().removeItem(event.getItem());
                    boostItem.apply(event.getPlayer());
                    event.setCancelled(true);
                    event.getItem().remove();
                }
            }
        }
    }

    @EventHandler
    public void onItemDespawnEvent(ItemDespawnEvent event) {
        for (final Game game : this.controller.getAll()) {
            if (game.getArena().isLocationInArea(event.getLocation()))
                game.getArena().getBoostItemHandler().removeItem(event.getEntity());
        }
    }

    @EventHandler
    public void onPlayerRequestPlaceHolder(PlaceHolderRequestEvent event) {
        int gameId = event.getGame();
        if (gameId == -1) {
            if (this.controller.getGameFromPlayer(event.getPlayer()) == null)
                return;
            gameId = this.controller.getGameFromPlayer(event.getPlayer()).getArena().getId();
        }
        final GameEntity game = (GameEntity) this.controller.getGameFromArenaId(gameId);
        if (game != null) {
            event.setResult(game.getPlaceHolder(event.getType()));
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        this.prepareDatabaseStats(event.getPlayer());
        this.enableJoinStats(event.getPlayer());
        for (final Game game : this.controller.getAll()) {
            if (game instanceof BungeeGameEntity) {
                if (!game.getArena().isEnabled())
                    return;
                if (!((BungeeGameEntity) game).joinLobby(event.getPlayer())) {
                    event.getPlayer().kickPlayer(Language.ARENA_LOBBYFULL_MESSAGE);
                }
            }
        }
        if (Config.getInstance().isJoiningSpawnpointEnabled() && Config.getInstance().getJoinSpawnpoint() != null) {
            for (final Arena arena : this.controller.arenaManager.getArenas()) {
                if (event.getPlayer().getLocation().getWorld().getName().equals(arena.getBallSpawnLocation().getWorld().getName()) && arena.isLocationInArea(event.getPlayer().getLocation())) {
                    event.getPlayer().teleport(Config.getInstance().getJoinSpawnpoint());
                    return;
                }
            }
        }
    }

    private Game getGameFromSign(Sign sign) {
        try {
            final String line = sign.getLine(Config.getInstance().getTeamSign().getGameLine());
            for (final Game game : this.controller.getAll()) {
                if (String.valueOf(game.getArena().getId()).equals(line) || (game.getArena().getAlias() != null && ChatColor.stripColor(game.getArena().getAlias()).equals(ChatColor.stripColor(line)))) {
                    return game;
                }
            }
            return null;
        } catch (final Exception ex) {
            return null;
        }
    }

    @EventHandler
    public void onPlayerClickShieldEvent(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN) {
                Game game;
                if ((game = this.getGameFromSign((Sign) event.getClickedBlock().getState())) != null) {
                    for (final Location location : game.getArena().getLobbyMeta().getBlueTeamSignLocations()) {
                        if (SLocation.compareLocation(location, event.getClickedBlock().getLocation())) {
                            if (game instanceof HubGameEntity) {
                                if (!game.join(event.getPlayer(), Team.BLUE)) {
                                    event.getPlayer().sendMessage(Language.PREFIX + game.getArena().getTeamMeta().getTeamFullMessage());
                                }
                            } else if (game instanceof HelperGameEntity) {
                                ((HelperGameEntity) game).setTeam(event.getPlayer(), Team.BLUE);
                            }
                            event.setCancelled(true);
                            break;
                        }
                    }
                    for (final Location location : game.getArena().getLobbyMeta().getRedTeamSignLocations()) {
                        if (SLocation.compareLocation(location, event.getClickedBlock().getLocation())) {
                            if (game instanceof HubGameEntity) {
                                if (!game.join(event.getPlayer(), Team.RED)) {
                                    event.getPlayer().sendMessage(Language.PREFIX + game.getArena().getTeamMeta().getTeamFullMessage());
                                }
                            } else if (game instanceof HelperGameEntity) {
                                ((HelperGameEntity) game).setTeam(event.getPlayer(), Team.RED);
                            }
                            event.setCancelled(true);
                            break;
                        }
                    }
                    for (final Location location : game.getArena().getLobbyMeta().getSignLocations()) {
                        if (game instanceof MiniGameEntity && SLocation.compareLocation(location, event.getClickedBlock().getLocation())) {
                            if ((((MiniGameEntity) game)).isLobbyFull())
                                event.getPlayer().sendMessage(Language.PREFIX + Language.ARENA_LOBBYFULL_MESSAGE);
                            else
                                ((MiniGameEntity) game).joinLobby(event.getPlayer());
                            break;
                        }
                    }
                }
                if ((this.controller.isInGameLobby(event.getPlayer()) != null && this.controller.isInGameLobby(event.getPlayer()) instanceof HelperGameEntity)) {
                    game = this.controller.isInGameLobby(event.getPlayer());
                    for (final Location location : game.getArena().getLobbyMeta().getLeaveSignLocations()) {
                        if (SLocation.compareLocation(location, event.getClickedBlock().getLocation())) {
                            game.leave(event.getPlayer());
                        }
                    }
                } else if (this.controller.getGameFromPlayer(event.getPlayer()) != null && this.controller.getGameFromPlayer(event.getPlayer()) instanceof HelperGameEntity) {
                    game = this.controller.getGameFromPlayer(event.getPlayer());
                    for (final Location location : game.getArena().getLobbyMeta().getLeaveSignLocations()) {
                        if (SLocation.compareLocation(location, event.getClickedBlock().getLocation())) {
                            game.leave(event.getPlayer());
                        }
                    }
                }
            }
        }
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

    @EventHandler
    public void onBallHitWallEvent(BallHitWallEvent event) {
        final GameEntity gameEntity;
        if ((gameEntity = ((GameEntity) this.controller.getGameFromBall(event.getBall()))) != null) {
            for (final String s : gameEntity.getArena().getBounceTypes()) {
                try {
                    final int id;
                    int subId = 0;
                    if (s.contains(":")) {
                        id = Integer.parseInt(s.split(Pattern.quote(":"))[0]);
                        subId = Integer.parseInt(s.split(Pattern.quote(":"))[1]);
                    } else {
                        id = Integer.parseInt(s);
                    }
                    if (event.getBlock().getType().getId() == id && ((int) event.getBlock().getData()) == subId) {
                        gameEntity.bumpBallBack();
                    }
                } catch (final Exception ex) {
                    Bukkit.getLogger().log(Level.WARNING, "Invalid bounce type '" + s + "' in " + gameEntity.getArena().getId() + '.');
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(final PlayerMoveEvent event) {
        if (event.getTo().distance(event.getFrom()) > 0) {
            if (this.controller.getAll() != null && !SChatMenuManager.getInstance().isUsing(event.getPlayer())) {
                boolean isInArena = false;
                boolean isNeverInArena = true;
                for (int i = 0; i < this.controller.size(); i++) {
                    GameEntity selectedGame = (GameEntity) this.controller.getAll().get(i);
                    if ((selectedGame instanceof HubGameEntity || selectedGame instanceof EventGameEntity) && selectedGame.arena.isEnabled()) {
                        final Arena arena = selectedGame.getArena();
                        if (selectedGame instanceof EventGameEntity) {
                            final EventGameEntity gameEntity = (EventGameEntity) selectedGame;
                            if (!gameEntity.visitorForceField) {
                                continue;
                            }
                        }
                        if (arena.isLocationInArea(event.getPlayer().getLocation()) && !selectedGame.isInGame(event.getPlayer())) {
                            isNeverInArena = false;
                            isInArena = true;
                            if (!this.lastLocation.containsKey(event.getPlayer())) {
                                event.getPlayer().setVelocity(Config.getInstance().getPlayerLaunchUpProtectionVelocity());
                            } else {
                                if (!this.moveCounter.containsKey(event.getPlayer()))
                                    this.moveCounter.put(event.getPlayer(), 1);
                                else if (this.moveCounter.get(event.getPlayer()) < 50)
                                    this.moveCounter.put(event.getPlayer(), this.moveCounter.get(event.getPlayer()) + 1);
                                if (this.moveCounter.get(event.getPlayer()) > 20) {
                                    event.getPlayer().setVelocity(Config.getInstance().getPlayerLaunchUpProtectionVelocity());
                                } else if (!selectedGame.arena.getTeamMeta().isTeamAutoJoin()) {
                                    final Vector knockback = this.lastLocation.get(event.getPlayer()).getLocation().toVector().subtract(event.getPlayer().getLocation().toVector());
                                    event.getPlayer().getLocation().setDirection(knockback);
                                    event.getPlayer().setVelocity(knockback);
                                    event.getPlayer().setAllowFlight(true);
                                    this.toggledPlayers.add(event.getPlayer());
                                }
                            }
                            if (selectedGame instanceof HubGameEntity && !this.connectedGames.containsKey(event.getPlayer())) {
                                if (event.getPlayer().hasPermission("blockball.user")) {
                                    if (arena.getTeamMeta().isFastJoin()) {
                                        if (!selectedGame.join(event.getPlayer(), null)) {
                                            event.getPlayer().setVelocity(new Vector(0, 0, 0));
                                            event.getPlayer().sendMessage(Language.PREFIX + arena.getTeamMeta().getTeamFullMessage());
                                        }
                                    } else {
                                        String s = arena.getTeamMeta().getHowToJoinMessage().replace(":red", ChatColor.stripColor(arena.getTeamMeta().getRedTeamName()));
                                        s = s.replace(":blue", ChatColor.stripColor(arena.getTeamMeta().getBlueTeamName()));
                                        event.getPlayer().sendMessage(Language.PREFIX + s);
                                        this.connectedGames.put(event.getPlayer(),selectedGame);
                                    }
                                } else {
                                    event.getPlayer().sendMessage(Language.PREFIX + Language.NO_PERMISSION);
                                }
                            }
                        } else if (this.moveCounter.containsKey(event.getPlayer()) && isNeverInArena && i == this.controller.size() - 1) {
                            this.moveCounter.remove(event.getPlayer());
                        }
                        if (!arena.isLocationInArea(event.getPlayer().getLocation()) && selectedGame.isInGame(event.getPlayer())) {
                            isInArena = true;
                            if (arena.getGameType() == GameType.LOBBY)
                                selectedGame.leave(event.getPlayer());
                        }
                    }
                }
                if (!isInArena) {
                    this.lastLocation.put(event.getPlayer(), new SLocation(event.getPlayer().getLocation()));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerExecuteCommand(final PlayerCommandPreprocessEvent event) {
        if (this.controller.isInGameLobby(event.getPlayer()) != null && this.isValidNotLeave(event.getMessage())) {
            event.setCancelled(true);
        } else if (this.controller.getGameFromPlayer(event.getPlayer()) != null && this.controller.getGameFromPlayer(event.getPlayer()).getArena().getGameType() == GameType.MINIGAME && this.isValidNotLeave(event.getMessage())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerOpenInventoryEvent(InventoryOpenEvent event) {
        if (this.controller.getGameFromPlayer((Player) event.getPlayer()) != null || this.controller.isInGameLobby((Player) event.getPlayer()) != null)
            event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerOpenInventoryEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL && (this.controller.getGameFromPlayer((Player) event.getEntity()) != null || this.controller.isInGameLobby((Player) event.getEntity()) != null)) {
            ((Player) event.getEntity()).closeInventory();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Game game;
            if ((game = this.controller.getGameFromPlayer((Player) event.getEntity())) != null) {
                if (!game.getArena().getTeamMeta().isDamageEnabled())
                    event.setCancelled(true);
            } else if ((game = this.controller.isInGameLobby((Player) event.getEntity())) != null) {
                if (!game.getArena().getTeamMeta().isDamageEnabled())
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerClickInventoryEvent(InventoryClickEvent event) {
        if (this.controller.getGameFromPlayer((Player) event.getWhoClicked()) != null || this.controller.isInGameLobby((Player) event.getWhoClicked()) != null) {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (this.controller.getGameFromPlayer((Player) event.getEntity()) != null || this.controller.isInGameLobby((Player) event.getEntity()) != null)
            event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().isOnGround() && this.controller.getGameFromPlayer(event.getPlayer()) != null)
            event.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        if (this.controller.getGameFromPlayer(event.getPlayer()) != null) {
            final Game game = this.controller.getGameFromPlayer(event.getPlayer());
            final Player player = event.getPlayer();
            if ((player.getGameMode() != GameMode.CREATIVE)) {
                event.setCancelled(true);
                player.setAllowFlight(false);
                player.setFlying(false);
                if (game.getArena().getTeamMeta().isAllowDoubleJump()) {
                    player.setVelocity(player.getLocation().getDirection()
                            .multiply(game.getArena().getTeamMeta().getDoubleJumpMeta().getHorizontalStrength())
                            .setY(game.getArena().getTeamMeta().getDoubleJumpMeta().getVerticalStrength()));
                    try {
                        game.getArena().getTeamMeta().getDoubleJumpMeta().getSoundEffect().apply(player.getLocation());
                    }catch (final Exception e) {
                        Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [DoubleJumpSound]");
                    }
                    game.getArena().getTeamMeta().getDoubleJumpParticle().play(player.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) throws Exception {
        Game game;
        if (this.lastLocation.containsKey(event.getPlayer()))
            this.lastLocation.remove(event.getPlayer());
        if ((game = this.controller.getGameFromPlayer(event.getPlayer())) != null)
            game.leave(event.getPlayer());
        if ((game = this.controller.isInGameLobby(event.getPlayer())) != null)
            game.leave(event.getPlayer());
        if (this.statsScoreboards != null && this.statsScoreboards.containsKey(event.getPlayer())) {
            final StatsScoreboard scoreboard = this.statsScoreboards.get(event.getPlayer());
            scoreboard.close();
            this.statsScoreboards.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent event) {
        if (this.controller.isInGameLobby(event.getPlayer()) != null)
            event.setCancelled(true);
    }

    @EventHandler
    public void ballMoveEvent(BallMoveEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromBall(event.getBall())) != null) {
            game.playBallMoveEffects();
        }
    }

    /**
     * Gets called when a player interacts with a ball
     *
     * @param event event
     */
    @EventHandler
    public void ballInteractEvent(BallInteractEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromBall(event.getBall())) != null) {
            final GameEntity entity = (GameEntity) game;
            if (entity.ballPreviousCacheLocation != null && entity.ballPreviousCacheLocation.distance(event.getBall().getLocation().toVector()) < 2) {
                entity.ballCornerBumper++;
            } else {
                entity.ballCornerBumper = 0;
            }
            if (entity.ballCornerBumper >= 3) {
                final Vector direction = entity.arena.getBallSpawnLocation().toVector().subtract(event.getBall().getLocation().toVector());
                int x = 1;
                int z = 1;
                if (direction.getX() < 0)
                    x = -1;
                if (direction.getZ() < 0)
                    z = -1;
                event.getBall().teleport(new Location(event.getBall().getLocation().getWorld(), event.getBall().getLocation().getX() + x, event.getBall().getLocation().getY(), event.getBall().getLocation().getZ() + z));
                entity.ballCornerBumper = 0;
            }
            entity.ballPreviousCacheLocation = event.getBall().getLocation().toVector();
            if (entity.blueTeam.contains(event.getPlayer())) {
                entity.lastHitTeam = Team.BLUE;
            } else {
                entity.lastHitTeam = Team.RED;
            }
            entity.lastHit = event.getPlayer();
        }
    }

    /**
     * Gets called when a player kicks the ball
     *
     * @param event event
     */
    @EventHandler
    public void ballKickEvent(BallKickEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromBall(event.getBall())) != null) {
            game.playBallKickEffects(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncChatEvent(PlayerChatEvent event) {
        if (Config.getInstance().isHighpriority() && !Config.getInstance().isAsyncChat()) {
            final String message = ChatColor.stripColor(event.getMessage());
            final Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                final Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            this.handleChatMessage(player, message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (Config.getInstance().isHighpriority() && Config.getInstance().isAsyncChat()) {
            final String message = ChatColor.stripColor(event.getMessage());
            final Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                final Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> GameListener.this.handleChatMessage(player, message), 1L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAsyncChatEvent2(PlayerChatEvent event) {
        if (!Config.getInstance().isHighpriority() && !Config.getInstance().isAsyncChat()) {
            final String message = ChatColor.stripColor(event.getMessage());
            final Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                final Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            this.handleChatMessage(player, message);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChatEvent4(AsyncPlayerChatEvent event) {
        if (!Config.getInstance().isHighpriority() && Config.getInstance().isAsyncChat()) {
            final String message = ChatColor.stripColor(event.getMessage());
            final Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                final Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> GameListener.this.handleChatMessage(player, message), 1L);
        }
    }

    /**
     * Updates the stats for a player
     *
     * @param player player
     * @param stats  stats
     */
    private void updateStats(Player player, Stats stats) {
        if (this.statsScoreboards != null) {
            GameListener.this.statsScoreboards.get(player).updateStats(player, stats);
        }
    }

    /**
     * Updates the StatsScoreabord
     */
    private void updateStatsScoreboard() {
        this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, () -> {
            for (final Player player : GameListener.this.statsScoreboards.keySet()) {
                final Stats stats = GameListener.this.statsController.getByPlayer(player);
                GameListener.this.updateStats(player, stats);
            }
        }, 0, 20L * 60);
    }

    /**
     * Prepares the stats collector
     *
     * @param player player
     */
    private void prepareDatabaseStats(Player player) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            synchronized (this.statsController) {
                if (this.statsController.getByPlayer(player) == null) {
                    PlayerMeta meta;
                    if ((meta = this.playerMetaController.getByUUID(player.getUniqueId())) == null) {
                        meta = this.playerMetaController.create(player);
                        this.playerMetaController.store(meta);
                    }
                    final Stats stats = this.statsController.create();
                    stats.setPlayerId(meta.getId());
                    this.statsController.store(stats);
                }
            }
        });
    }

    /**
     * Enables the join stats
     *
     * @param player player
     */
    private void enableJoinStats(Player player) {
        if (Config.getInstance().isScoreboardPlayerStatsEnabled()) {
            final StatsScoreboard scoreboard = new StatsScoreboard(player);
            this.statsScoreboards.put(player, scoreboard);
            this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
                synchronized (this.statsController) {
                    final Stats stats = this.statsController.getByPlayer(player);
                    if (stats != null) {
                        scoreboard.updateStats(player, stats);
                    }
                }
            }, 20 * 2L);
        }
    }

    private void handleChatMessage(Player player, String message) {
        if (this.connectedGames.containsKey(player)) {
            final Arena arena = this.connectedGames.get(player).getArena();
            if (message.equalsIgnoreCase(ChatColor.stripColor(arena.getTeamMeta().getRedTeamName()))) {
                if (!this.connectedGames.get(player).join(player, Team.RED)) {
                    player.sendMessage(Language.PREFIX + this.connectedGames.get(player).getArena().getTeamMeta().getTeamFullMessage());
                }
            } else if (message.equalsIgnoreCase(ChatColor.stripColor(arena.getTeamMeta().getBlueTeamName()))) {
                if (!this.connectedGames.get(player).join(player, Team.BLUE)) {
                    player.sendMessage(Language.PREFIX + this.connectedGames.get(player).getArena().getTeamMeta().getTeamFullMessage());
                }
            }
            this.connectedGames.remove(player);
        }
    }

    private boolean isValidNotLeave(String message) {
        try {
            return !message.split(Pattern.quote(":"))[0].equalsIgnoreCase('/' + Config.getInstance().getGlobalLeaveCommand().getCommand());
        } catch (final Exception ex) {
            return true;
        }
    }
}
