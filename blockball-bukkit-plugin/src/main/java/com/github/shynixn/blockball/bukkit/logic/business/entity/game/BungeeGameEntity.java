package com.github.shynixn.blockball.bukkit.logic.business.entity.game;

/*
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.bungeecord.game.BungeeCord;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;


 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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

public class BungeeGameEntity extends HelperGameEntity {
    /**
     * Initializes a new bungee game
     *
     * @param arena arena

    public BungeeGameEntity(Arena arena) {
        super(arena);
        BungeeCord.setModt(BungeeCord.MOD_WAITING_FOR_PLAYERS);
    }


    final List<PlayerStorage> gamePlayers = new ArrayList<>();
    final List<Player> redTeamPlayers = new ArrayList<>();
    final List<Player> blueTeamPlayers = new ArrayList<>();

    final Arena arena = null;
    Ball ball;
    private GameStatus gameStatus = GameStatus.DISABLED;

    private int redGoals;
    private int blueGoals;

    private boolean isBallSpawning;

    private int bumperTimer = 20;
    private int ballSpawnCounter;
    private int bumper;
    private int bumperCounter;
    public int ballCornerBumper;

    final TeamMeta redTeamMeta = null;
    final TeamMeta blueTeamMeta = null;
    final BallMeta ballMeta = null;
    final CustomizingMeta customizingMeta = null;

    private Location lastBallLocation;
    public Player lastHit;
    public Team lastHitTeam;

    private final Map<Item, BoosItemMeta> boostItemsLyingAround = new HashMap<>();
    public Vector ballPreviousCacheLocation;

    private Map<Player, PlayerStorage> playerStorage = new HashMap<>();




   /* public RGame(Arena arena) {
        this.arena = arena;
        if (this.arena.isEnabled()) {
            this.gameStatus = GameStatus.ENABLED;
        }
        this.redTeamMeta = arena.getMeta().findByTeam(TeamMeta[].class, Team.RED).get();
        this.blueTeamMeta = arena.getMeta().findByTeam(TeamMeta[].class, Team.BLUE).get();
        this.ballMeta = arena.getMeta().find(BallMeta.class).get();
        this.customizingMeta = arena.getMeta().find(CustomizingMeta.class).get();
    }


    public PlayerStorage playerStorage(Player player){
        return playerStorage.get(player);
    }


    /**
     * Returns the arena of the game.
     *
     * @return arena

@Override
public final Arena getArena() {
        return this.arena;
        }

/**
 * Returns the ball of the game.
 *
 * @return ball

@Override
public final Optional<Ball> getBall() {
        return Optional.ofNullable(this.ball);
        }

/**
 * Gets called every twenty ticks in the game life cycle.

public void onTwentyTick() {

        }

/**
 * Gets called every tick in the game life cycle.

public void onTick() {

        }

/**
 * Gets called when the game life cycle allows updating signs.

public void onUpdateSigns() {

        }

/**
 * Gets called when a team scores enough goals to reach the max amount of goals.
 *
 * @param teamMeta teamMeta of the winning team

public void onWin(TeamMeta teamMeta) {

        }

/**
 * Gets called when a team scores a goal.
 *
 * @param teamMeta teamMeta of the scorring team

public void onScore(TeamMeta teamMeta) {

        }

/**
 * When an object implementing interface <code>Runnable</code> is used
 * to create a thread, starting the thread causes the object's
 * <code>run</code> method to be called in that separately executing
 * thread.
 * <p>
 * The general contract of the method <code>run</code> is that it may
 * take any action whatsoever.
 *
 * @see Thread#run()

@Override
public void run() {
        //  if (!this.arena.isEnabled()) {
        //     return;
        //    }
        if (this.haveTwentyTicksPassed()) {
        this.kickUnwantedEntitiesOutOfForcefield();
        this.onUpdateSigns();
        }
        this.handleBallSpawning();
        if (this.ball != null && !this.ball.isDead()) {
        this.fixBallPositionSpawn();
        this.checkBallInGoal();
        }
        }

/**
 * Returns the players who a players in the blue team.
 *
 * @return player

@Override
public final Object[] getBlueTeamPlayers() {
        return this.blueTeamPlayers.toArray(new Player[this.blueTeamPlayers.size()]);
        }

/**
 * Returns the players who a players in the red team.
 *
 * @return player

@Override
public final Object[] getRedTeamPlayers() {
        return this.redTeamPlayers.toArray(new Player[this.redTeamPlayers.size()]);
        }

/**
 * Returns a list of players who are in this game.
 *
 * @return playerList

@Override
public final List<Object> getPlayers() {
final List<Object> players = new ArrayList<>();
        for (final BlockBallPlayer player : this.gamePlayers) {
        //  players.add(player.player);
        }
        return players;
        }

/**
 * Returns all boost items lying on the ground.
 *
 * @return boostItems

@Override
public Map<Object, BoosItemMeta> getGroundItems() {
        return Collections.unmodifiableMap(this.boostItemsLyingAround);
        }

@Override
public final void close() throws Exception {
        //   for (final GamePlayer gamePlayer : this.gamePlayers) {
        //    this.leave(gamePlayer);
        //  }
        this.gamePlayers.clear();
        this.redTeamPlayers.clear();
        this.blueTeamPlayers.clear();
        }


 * Returns if the given player has joined the match.
 *


@Override
public final boolean hasJoined(Object player) {
        //  return this.getGamePlayerByPlayer((Player) player).isPresent();
        return  false;
        }


 * Returns a value for the given place holder. Returns empty string if not found.
 *
 * @param
 * @return value
 */
 /*   @Override
    public String getValueForPlaceHolder(PlaceHolderType type) {
        switch (type) {
            case BLUESCORE:
                return String.valueOf(this.blueGoals);
            case BLUEAMOUNT:
                return String.valueOf(this.blueTeamPlayers.size());
            case BLUECOLOR:
                return this.blueTeamMeta.getPrefix();
            case BLUENAME:
                return this.blueTeamMeta.getDisplayName();
            case REDSCORE:
                return String.valueOf(this.redGoals);
            case REDAMOUNT:
                return String.valueOf(this.redTeamPlayers.size());
            case REDCOLOR:
                return this.redTeamMeta.getPrefix();
            case REDNAME:
                return this.redTeamMeta.getDisplayName();
            case LASTHITPLAYERNAME: {
                if (this.lastHit != null) {
                    return this.lastHit.getName();
                }
            }
            default:
                return "";
        }
    }

    final Optional<GamePlayer> getGamePlayerByPlayer(Player player) {
        for (final GamePlayer gamePlayer : this.gamePlayers) {
            if (gamePlayer.player.equals(player)) {
                return Optional.of(gamePlayer);
            }
        }
        return Optional.empty();
    }

private void kickUnwantedEntitiesOutOfForcefield() {
        /*or (final Entity entity : ((Location) this.arena.getBallSpawnLocation()).getWorld().getEntities()) {
            if (!(entity instanceof Player) &&
                    !(entity instanceof Rabbit)
                    && !(entity instanceof ArmorStand)
                    ) {
                if (this.arena.isLocationInSelection(entity.getLocation())) {
                    final Vector vector = Config.getInstance().getEntityProtectionVelocity();
                    entity.getLocation().setDirection(vector);
                    entity.setVelocity(vector);
                }
            }
        }
        }


        }

private void checkBallInGoal() {
      /*  if (this.redTeamMeta.getGoal().isLocationInSelection(this.ball.getLocation())) {
            this.redGoals++;
            this.ball.remove();
            this.onScore(this.redTeamMeta);
            if (this.redGoals >= this.customizingMeta.getMaxScore()) {
                this.onWin(this.redTeamMeta);
            }
        } else if (this.blueTeamMeta.getGoal().isLocationInSelection(this.ball.getLocation())) {
            this.blueGoals++;
            this.ball.remove();
            this.onScore(this.blueTeamMeta);
            if (this.blueGoals >= this.customizingMeta.getMaxScore()) {
                this.onWin(this.blueTeamMeta);
            }
        }
        }

private void fixBallPositionSpawn() {
        if (!this.arena.isLocationInSelection(this.ball.getLocation())) {
        if (this.bumper == 0) {
        this.rescueBall();
        }
        } else {
        this.bumperCounter = 0;
        this.lastBallLocation = ((Location) this.ball.getLocation()).clone();
        }
        if (this.gamePlayers.isEmpty()) {
        this.ball.remove();
        }
        if (this.bumper > 0) {
        this.bumper--;
        }
        }

private void handleBallSpawning() {
      if (this.isBallSpawning) {
            this.ballSpawnCounter--;
            if (this.ballSpawnCounter <= 0) {
                this.ball = BlockBallApi.getDefaultBallController().create(this.arena.getBallSpawnLocation(), this.ballMeta);
                BlockBallApi.getDefaultBallController().store(this.ball);
                this.isBallSpawning = false;
                this.ballSpawnCounter = 0;
                try {
                    this.ballMeta.getSpawnParticleEffect().apply(this.ball.getLocation());
                    this.ballMeta.getSpawnSound().applyToLocation(this.ball.getLocation());
                } catch (final Exception e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.Companion.getPREFIX_CONSOLE() + ChatColor.RED + "Invalid 1.8/1.9 sound. [BallSpawnSound]");
                }
            }
        } else if ((this.ball == null || this.ball.isDead())
                && (!this.redTeamPlayers.isEmpty() || !this.blueTeamPlayers.isEmpty())
                && (this.redTeamPlayers.size() >= this.redTeamMeta.getMinAmountOfPlayers() && this.blueTeamPlayers.size() >= this.blueTeamMeta.getMinAmountOfPlayers())) {
            this.isBallSpawning = true;
            this.ballSpawnCounter = this.arena.getBallSpawnDelay() * 20;
        }
        }

private void rescueBall() {
        if (this.lastBallLocation != null) {
final Location ballLocation = (Location) this.ball.getLocation();
final Vector knockback = this.lastBallLocation.toVector().subtract(ballLocation.toVector());
        ballLocation.setDirection(knockback);
        this.ball.setVelocity(knockback);
        //   final Vector direction = ((Location) this.arena.getBallSpawnLocation()).toVector().subtract(ballLocation.toVector());
        // this.ball.setVelocity(direction.multiply(0.1));
        this.bumper = 40;
        this.bumperCounter++;
        if (this.bumperCounter == 5) {
        //     this.ball.teleport(this.arena.getBallSpawnLocation());
        }
        }
        }

final String replaceMessagePlaceholders(String text) {
        try {
        if (this.lastHit == null) {
        return ChatColor.translateAlternateColorCodes('&', text
        .replace(":countdown", "∞")
        .replace(":redscore", String.valueOf(this.redGoals))
        .replace(":bluescore", String.valueOf(this.blueGoals))
        .replace(":redcolor", this.redTeamMeta.getPrefix())
        .replace(":bluecolor", this.blueTeamMeta.getPrefix())
        .replace(":red", this.redTeamMeta.getDisplayName())
        .replace(":blue", this.blueTeamMeta.getDisplayName()));
        } else {
        return ChatColor.translateAlternateColorCodes('&', text
        .replace(":countdown", "∞")
        .replace(":redscore", String.valueOf(this.redGoals))
        .replace(":bluescore", String.valueOf(this.blueGoals))
        .replace(":redcolor", this.redTeamMeta.getPrefix())
        .replace(":bluecolor", this.blueTeamMeta.getPrefix())
        .replace(":player", this.lastHit.getName())
        .replace(":red", this.redTeamMeta.getDisplayName())
        .replace(":blue", this.blueTeamMeta.getDisplayName()));
        }
        } catch (final Exception e) {
        //  Config..log(Level.WARNING, "Error while parsing.", e);
        }
        throw new RuntimeException("The following error has already been fixed. Please wait for the games to get restarted...");
        }


    /**
     * Starts the game

    @Override
    public void startGame() {
        BungeeCord.setModt(BungeeCord.MOD_INGAME);
        super.startGame();
    }

    /**
     * Lets a player leave the game
     *
     * @param player player
     * @return success

    @Override
    public synchronized boolean leave(Player player) {
        final boolean success = super.leave(player);
        player.kickPlayer(this.arena.getTeamMeta().getLeaveMessage());
        return success;
    }

    /**
     * Adds a player to the game returns false if he doesn't meet the required options.
     *
     * @param player player - @NotNull
     * @param team   team - @Nullable, team gets automatically selection
     * @return success

    @Override
    public boolean join(Object player, Team team) {
        return false;
    }

    /**
     * Resets the game and restarts the server

    @Override
    public void reset() {
        BungeeCord.setModt(BungeeCord.MOD_RESTARTING);
        super.reset();
        if (this.arena.isEnabled()) {
            this.restartServer();
        }
    }

    /**
     * Restarts the server

    private void restartServer() {
        try {
            Bukkit.getServer().shutdown();
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.INFO, "Failed shutdown server.", ex);
        }
    }
}
*/