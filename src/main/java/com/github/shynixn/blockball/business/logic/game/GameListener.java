package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.api.entities.GameType;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.api.events.BallKickEvent;
import com.github.shynixn.blockball.api.events.BallMoveEvent;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.entities.items.BoostItem;
import com.github.shynixn.blockball.api.events.BallHitWallEvent;
import com.github.shynixn.blockball.api.events.PlaceHolderRequestEvent;
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
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
class GameListener extends SEvents {
    private final Map<Player, SLocation> lastLocation = new HashMap<>();
    private final Map<Player, Game> connectedGames = new HashMap<>();
    private final List<Player> toggledPlayers = new ArrayList<>();
    private final Map<Player, Integer> moveCounter = new HashMap<>();
    private final GameController controller;

    GameListener(final GameController controller) {
        this.controller = controller;
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

    @EventHandler
    public void onItemPickUpEvent(PlayerPickupItemEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromPlayer(event.getPlayer())) != null) {
            for (final Item item : game.getArena().getBoostItemHandler().getDroppedItems()) {
                final BoostItem boostItem;
                if (item.equals(event.getItem()) && ((boostItem = game.getArena().getBoostItemHandler().getBoostFromItem(item)) != null)) {
                    try {
                        new FastSound("NOTE_PLING", 2.0, 2.0).play(event.getPlayer().getLocation(), event.getPlayer());
                    } catch (final InterPreter19Exception e) {
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
        for (final Game game : this.controller.games) {
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
        for (final Game game : this.controller.games) {
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
            for (final Game game : this.controller.games) {
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
                            if (game instanceof LobbyGameEntity) {
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
                            if (game instanceof LobbyGameEntity) {
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
                    for (Location location : game.getArena().getLobbyMeta().getSignLocations()) {
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
            if (this.controller.games != null && !SChatMenuManager.getInstance().isUsing(event.getPlayer())) {
                boolean isInArena = false;
                boolean isNeverInArena = true;
                for (int i = 0; i < this.controller.games.length; i++) {
                    if ((this.controller.games[i] instanceof LobbyGameEntity || this.controller.games[i] instanceof EventGameEntity) && this.controller.games[i].arena.isEnabled()) {
                        final Arena arena = this.controller.games[i].getArena();
                        if (this.controller.games[i] instanceof EventGameEntity) {
                            EventGameEntity gameEntity = (EventGameEntity) this.controller.games[i];
                            if (!gameEntity.visitorForceField) {
                                continue;
                            }
                        }
                        if (arena.isLocationInArea(event.getPlayer().getLocation()) && !this.controller.games[i].isInGame(event.getPlayer())) {
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
                                } else {
                                    final Vector knockback = this.lastLocation.get(event.getPlayer()).getLocation().toVector().subtract(event.getPlayer().getLocation().toVector());
                                    event.getPlayer().getLocation().setDirection(knockback);
                                    event.getPlayer().setVelocity(knockback);
                                    event.getPlayer().setAllowFlight(true);
                                    this.toggledPlayers.add(event.getPlayer());
                                }
                            }
                            if (this.controller.games[i] instanceof LobbyGameEntity && !this.connectedGames.containsKey(event.getPlayer())) {
                                if (event.getPlayer().hasPermission("blockball.user")) {
                                    if (arena.getTeamMeta().isFastJoin()) {
                                        if (!this.controller.games[i].join(event.getPlayer(), null)) {
                                            event.getPlayer().setVelocity(new Vector(0, 0, 0));
                                            event.getPlayer().sendMessage(Language.PREFIX + arena.getTeamMeta().getTeamFullMessage());
                                        }
                                    } else {
                                        String s = arena.getTeamMeta().getHowToJoinMessage().replace(":red", ChatColor.stripColor(arena.getTeamMeta().getRedTeamName()));
                                        s = s.replace(":blue", ChatColor.stripColor(arena.getTeamMeta().getBlueTeamName()));
                                        event.getPlayer().sendMessage(Language.PREFIX + s);
                                        this.connectedGames.put(event.getPlayer(), this.controller.games[i]);
                                    }
                                } else {
                                    event.getPlayer().sendMessage(Language.PREFIX + Language.NO_PERMISSION);
                                }
                            }
                        } else if (this.moveCounter.containsKey(event.getPlayer()) && isNeverInArena && i == this.controller.games.length - 1) {
                            this.moveCounter.remove(event.getPlayer());
                        }
                        if (!arena.isLocationInArea(event.getPlayer().getLocation()) && this.controller.games[i].isInGame(event.getPlayer())) {
                            isInArena = true;
                            if (arena.getGameType() == GameType.LOBBY)
                                this.controller.games[i].leave(event.getPlayer());
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
                    player.setVelocity(player.getLocation().getDirection().multiply(2.6D).setY(1.0D));
                    try {
                        game.getArena().getTeamMeta().getDoubleJumpSound().play(player.getLocation());
                    } catch (InterPreter19Exception e) {
                        SConsoleUtils.sendColoredMessage("Invalid 1.8/1.9 sound. [DoubleJumpSound]", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
                    }
                    game.getArena().getTeamMeta().getDoubleJumpParticle().play(player.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        Game game;
        if (this.lastLocation.containsKey(event.getPlayer()))
            this.lastLocation.remove(event.getPlayer());
        if ((game = this.controller.getGameFromPlayer(event.getPlayer())) != null)
            game.leave(event.getPlayer());
        if ((game = this.controller.isInGameLobby(event.getPlayer())) != null)
            game.leave(event.getPlayer());
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

    @EventHandler
    public void ballKickEvent(BallKickEvent event) {
        final Game game;
        if ((game = this.controller.getGameFromBall(event.getBall())) != null) {
            game.playBallKickEffects(event.getPlayer());
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
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerAsyncChatEvent(PlayerChatEvent event) {
        if (Config.getInstance().isHighpriority() && !Config.getInstance().isAsyncChat()) {
            String message = ChatColor.stripColor(event.getMessage());
            Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                Game game = this.controller.getGameFromPlayer(player);
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
                Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    GameListener.this.handleChatMessage(player, message);
                }
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAsyncChatEvent2(PlayerChatEvent event) {
        if (!Config.getInstance().isHighpriority() && !Config.getInstance().isAsyncChat()) {
            String message = ChatColor.stripColor(event.getMessage());
            Player player = event.getPlayer();
            if (this.controller.getGameFromPlayer(player) != null) {
                Game game = this.controller.getGameFromPlayer(player);
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
                Game game = this.controller.getGameFromPlayer(player);
                if (message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getRedTeamName())) || message.equalsIgnoreCase(ChatColor.stripColor(game.getArena().getTeamMeta().getBlueTeamName()))) {
                    this.connectedGames.put(player, game);
                }
            }
            if (this.connectedGames.containsKey(player)) {
                event.setCancelled(true);
            }
            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    GameListener.this.handleChatMessage(player, message);
                }
            }, 1L);
        }
    }

    private void handleChatMessage(Player player, String message) {
        if (this.connectedGames.containsKey(player)) {
            Arena arena = this.connectedGames.get(player).getArena();
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
