package com.github.shynixn.blockball.bukkit.logic.business.entity.game;

/*
import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.business.enumeration.Team;
import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Config;
import com.github.shynixn.blockball.bukkit.logic.business.entity.GameScoreboard;
import com.github.shynixn.blockball.bukkit.logic.business.entity.ItemSpawner;
import com.github.shynixn.blockball.bukkit.logic.business.entity.TemporaryPlayerStorage;
import com.github.shynixn.blockball.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.lib.ScreenUtils;
import jdk.nashorn.internal.ir.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.Closeable;
import java.util.*;
import java.util.logging.Level;

public class GameEntity implements Game {
    protected final Plugin plugin;
    private final Arena arena;
    private final Map<Player, TemporaryPlayerStorage> players = new HashMap<>();

    int bluePoints;
    int redPoints;

    /**
     * Ball calculations

    private Ball ball;
    private Location lastBallLocation;
    private int bumper = 20;
    private int bumperCounter;
    private int buffer = 2;
    int ballCornerBumper;
    Vector ballPreviousCacheLocation;
    private int counter = 3;
    private boolean freshReset;
    boolean ballSpawning;

    /**
     * HitCounter

    Team lastHitTeam;
    Player lastHit;

    GameEntity(Arena arena, Plugin plugin) {
        super();
        this.plugin = plugin;
        this.arena = arena;
    }

    /**
     * Adds a player to the game returns false if he doesn't meet the required options.
     *
     * @param player player - @NotNull
     * @param team   team - @Nullable, team gets automatically selection
     * @return success

    @Override
    public abstract boolean join(Object player, Team team);

    /**
     * Returns if the given player has joined the match
     *
     * @param mPlayer player - @NotNull
     * @return joined the match

    @Override
    public boolean hasJoined(Object mPlayer) {
        final Player player = (Player) mPlayer;
        return this.players.containsKey(player);
    }

    /**
     * Removes a player from the given game returns false if it did not work
     *
     * @param mPlayer player - @NotNull
     * @return success

    @Override
    public boolean leave(Object mPlayer) {
        final Player player = (Player) mPlayer;
        if (this.players.containsKey(player)) {
            final TemporaryPlayerStorage storage = this.players.get(player);
            try {
                storage.close();
            } catch (final Exception e) {
                BlockBallPlugin.logger().log(Level.WARNING, "Failed to restore player.", e);
                return false;
            }
            player.sendMessage(Config.getInstance().getPrefix() + this.arena.getCustomizingMeta().getLeaveMessage());
        }
        return true;
    }


    public void reset() {
        this.reset(true);
    }

    void reset(boolean teleport) {
        for (final Player player : this.getPlayers()) {
            this.leave(player);
        }
        if (teleport && this.arena.getTeamMeta().getGameEndSpawnpoint() != null) {
            for (final Player player : this.getPlayers()) {
                player.teleport(this.arena.getTeamMeta().getGameEndSpawnpoint());
            }
        }
        for (final Player player : this.playData) {
            this.arena.getTeamMeta().getBossBar().stopPlay(this.bossBar, player);
            if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
                NMSRegistry.setBossBar(player, null);
            }
        }
        ((ItemSpawner) this.arena.getBoostItemHandler()).clearGroundItems();
        FastBossBar.dispose(this.bossBar);
        if (this.ball != null)
            this.ball.despawn();
        if (this.hologram != null)
            this.hologram.remove(SFileUtils.getOnlinePlayers().toArray(new Player[SFileUtils.getOnlinePlayers().size()]));
        if (this.arena.getTeamMeta().isScoreboardEnabled()) {
            this.gameScoreboard = new GameScoreboard(this.arena);
        }
        this.redTeam.clear();
        this.blueTeam.clear();
        this.playData.clear();
        this.ball = null;
        this.blueGoals = 0;
        this.redGoals = 0;
    }

    @Override
    public void playBallMoveEffects() {
        if (this.lastHitTeam != null) {
            if (this.lastHitTeam == Team.RED) {
                this.arena.getBallMeta().getPlayerTeamRedHitParticle().play(this.ball.getLocation());
            } else {
                this.arena.getBallMeta().getPlayerTeamBlueHitParticle().play(this.ball.getLocation());
            }
        }
    }

    @Override
    public void playBallKickEffects(Player player) {
        if (this.buffer == 0) {
            this.arena.getBallMeta().getGenericHitParticle().play(this.ball.getLocation());
            try {
                this.arena.getBallMeta().getGenericHitSound().apply(this.ball.getLocation());
            } catch (final Exception e) {
                Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [GenericHitSound]");
            }
            this.buffer = 10;
        }
    }

    final void fixCachedRangePlayers() {
        for (final Player player : this.playData.toArray(new Player[this.playData.size()])) {
            if (player.getLocation().getWorld().getName().equals(this.arena.getBallSpawnLocation().getWorld().getName())) {
                if (player.getLocation().distance(this.arena.getCenter()) > this.arena.getTeamMeta().getSpecatorradius()) {
                    this.removePlayerFromScoreboard(player);
                    this.arena.getTeamMeta().getBossBar().stopPlay(this.bossBar, player);
                    if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
                        NMSRegistry.setBossBar(player, null);
                    }
                    this.playData.remove(player);
                }
            } else {
                this.removePlayerFromScoreboard(player);
                this.arena.getTeamMeta().getBossBar().stopPlay(this.bossBar, player);
                if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
                    NMSRegistry.setBossBar(player, null);
                }
                this.playData.remove(player);
            }
        }
    }


    private void useLastHitGlowing() {
        if (this.arena.getTeamMeta().isGoalShooterGlowing() && (!ReflectionLib.getServerVersion().contains("1_8")) && this.lastHit != null) {
            final Player player = this.lastHit;
            Interpreter19.setGlowing(player, true);
            this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> Interpreter19.setGlowing(player, false), 20L * this.arena.getTeamMeta().getGoalShooterGlowingSeconds());
        }
    }

    void executeCommand(String command, List<Player> players) {
        if (command == null)
            return;
        if (command.replace(":player", "").length() == command.length()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            for (final Player player : players) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace(":player", player.getName()));
            }
        }
    }



    void sendMessageToPlayers(String title, String subTitle) {
        if (this.arena.getTeamMeta().isSpectatorMessagesEnabled()) {
            ScreenUtils.setTitle(title, subTitle, 0, 20 * 3, 10, this.getPlayersInRange().toArray(new Player[this.getPlayersInRange().size()]));

        } else {
            ScreenUtils.setTitle(title, subTitle, 0, 20 * 3, 10, this.getPlayers().toArray(new Player[this.getPlayers().size()]));
        }
    }

    List<Player> getPlayersInRange() {
        final List<Player> players = new ArrayList<>();
        for (final Player player : this.getArena().getCenter().getWorld().getPlayers()) {
            if (player.getLocation().distance(this.getArena().getCenter()) <= this.arena.getTeamMeta().getSpecatorradius())
                players.add(player);
        }
        return players;
    }

    @Override
    public Ball getBall() {
        return this.ball;
    }

    @Override
    public Arena getArena() {
        return this.arena;
    }

    @Override
    public final List<Player> getPlayers() {
        final List<Player> players = new ArrayList<>();
        players.addAll(this.blueTeam);
        players.addAll(this.redTeam);
        return players;
    }

    protected String decryptText(String text) {
        try {
            if (this.lastHit == null) {
                return ChatColor.translateAlternateColorCodes('&', text
                        .replace(":countdown", "∞")
                        .replace(":redscore", String.valueOf(this.redGoals))
                        .replace(":bluescore", String.valueOf(this.blueGoals))
                        .replace(":redcolor", this.arena.getTeamMeta().getRedColor())
                        .replace(":bluecolor", this.arena.getTeamMeta().getBlueColor())
                        .replace(":red", this.arena.getTeamMeta().getRedTeamName())
                        .replace(":blue", this.arena.getTeamMeta().getBlueTeamName()));
            } else {
                return ChatColor.translateAlternateColorCodes('&', text
                        .replace(":countdown", "∞")
                        .replace(":redscore", String.valueOf(this.redGoals))
                        .replace(":bluescore", String.valueOf(this.blueGoals))
                        .replace(":redcolor", this.arena.getTeamMeta().getRedColor())
                        .replace(":bluecolor", this.arena.getTeamMeta().getBlueColor())
                        .replace(":player", this.lastHit.getName())
                        .replace(":red", this.arena.getTeamMeta().getRedTeamName())
                        .replace(":blue", this.arena.getTeamMeta().getBlueTeamName()));
            }
        } catch (final Exception e) {
            this.sendErrorMessage();
        }
        throw new RuntimeException("The following error has already been fixed. Please wait for the games to get restarted...");
    }

    void sendErrorMessage() {
        Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.GREEN + "Found invalid score configuration.");
        this.arena.getTeamMeta().reset();
        BlockBallApi.save(this.arena);
        Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.GREEN + "Fix finished. Games are getting restarted.");
        BlockBallApi.reloadGames();
    }

    @Override
    public Player[] getBlueTeamPlayers() {
        return this.blueTeam.toArray(new Player[this.blueTeam.size()]);
    }

    @Override
    public Player[] getRedTeamPlayers() {
        return this.redTeam.toArray(new Player[this.redTeam.size()]);
    }



    final LightHologram getHologram() {
        if (this.hologram == null && this.arena.getTeamMeta().isHologramEnabled() && this.arena.getTeamMeta().getHologramLocation() != null) {
            this.hologram = new LightHologramBuilder.Builder(this.arena.getTeamMeta().getHologramLocation());
        }
        return this.hologram;
    }

    void removePlayerFromScoreboard(Player player) {
        if (this.arena.getTeamMeta().isScoreboardEnabled()) {
            this.gameScoreboard.removePlayer(player);
        }
    }

    void addPlayerToScoreboard(Player player) {
        if (this.arena.getTeamMeta().isScoreboardEnabled()) {
            this.gameScoreboard.addPlayer(player);
            this.gameScoreboard.update(this);
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed

    @Override
    public void close() throws Exception {

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
        this.getArena().getBoostItemHandler().run(this);
        if (this.buffer > 0)
            this.buffer--;
        if (this.ballSpawning) {
            this.counter--;
            if (this.counter <= 0) {
                if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
                    if (this.arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                        for (final Player player : this.getPlayersInRange()) {
                            if (!this.playData.contains(player))
                                this.playData.add(player);
                            NMSRegistry.setBossBar(player, this.decryptText(this.arena.getTeamMeta().getBossBarPluginMessage()));
                        }
                    } else {
                        for (final Player player : this.getPlayers()) {
                            NMSRegistry.setBossBar(player, this.decryptText(this.arena.getTeamMeta().getBossBarPluginMessage()));
                        }
                    }
                }
                if (this.arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                    this.fixCachedRangePlayers();
                    for (final Player player : this.getPlayersInRange()) {
                        if (!this.playData.contains(player))
                            this.playData.add(player);
                    }
                    this.bossBar = this.arena.getTeamMeta().getBossBar().play(this.bossBar, this.decryptText(this.arena.getTeamMeta().getBossBar().getMessage()), this.getPlayersInRange());
                } else {
                    this.bossBar = this.arena.getTeamMeta().getBossBar().play(this.bossBar, this.decryptText(this.arena.getTeamMeta().getBossBar().getMessage()), this.getPlayers());
                }
                this.ball = BlockBallApi.createNewBall(this.arena.getBallSpawnLocation().getWorld());
                this.ball.spawn(this.arena.getBallSpawnLocation());
                this.ball.setSkin(this.arena.getBallMeta().getBallSkin());
                this.ball.setKickStrengthHorizontal(this.arena.getBallMeta().getHorizontalStrength());
                this.ball.setKickStrengthVertical(this.arena.getBallMeta().getVerticalStrength());
                this.ball.setRotating(this.arena.getBallMeta().isRotating());
                this.ballSpawning = false;
                this.freshReset = true;
                this.counter = 0;
                this.arena.getBallMeta().getBallSpawnParticle().play(this.ball.getLocation());
                try {
                    this.arena.getBallMeta().getSpawnSound().apply(this.ball.getLocation());
                } catch (final Exception e) {
                    Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [BallSpawnSound]");
                }
            }
        } else if ((this.ball == null || this.ball.isDead()) && (!this.redTeam.isEmpty() || !this.blueTeam.isEmpty()) && this.getPlayers().size() >= this.arena.getTeamMeta().getTeamMinSize()) {
            this.ballSpawning = true;
            this.counter = this.arena.getBallMeta().getBallSpawnTime() * 20;
        }
        if (this.ball != null) {
            if (!this.arena.isLocationInArea(this.ball.getLocation())) {
                if (this.bumper == 0)
                    this.bumpBallBack();
            } else {
                this.bumperCounter = 0;
                this.lastBallLocation = this.ball.getLocation().clone();
            }
            if (this.getPlayers().isEmpty())
                this.ball.despawn();
            if (this.bumper > 0)
                this.bumper--;
        }
        if (this.freshReset && this.arena.getTeamMeta().isEmtptyReset() && this.getPlayers().isEmpty()) {
            this.reset();
            this.freshReset = false;
        }
        if (this.ball != null && !this.ball.isDead() && this.arena.isLocationInGoal(this.ball.getLocation())) {
            final Team team = this.arena.getTeamFromGoal(this.ball.getLocation());
            this.useLastHitGlowing();
            this.arena.getBallMeta().getBallGoalParticle().play(this.ball.getLocation());
            try {
                this.arena.getBallMeta().getBallGoalSound().apply(this.ball.getLocation());
            } catch (final Exception e) {
                Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [BallGoalSound]");
            }
            this.ball.despawn();
            if (team == Team.RED) {
                this.redGoals++;
                this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getRedtitleScoreMessage()), this.decryptText(this.arena.getTeamMeta().getRedsubtitleMessage()));
                if (this.lastHit != null && this.redTeam.contains(this.lastHit)) {
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGoals(), this.lastHit);
                    Bukkit.getPluginManager().callEvent(new GoalShootEvent(this, this.lastHit, team));
                }
                if (this.redGoals >= this.arena.getTeamMeta().getMaxScore() && this.lastHit != null) {
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGames(), this.blueTeam.toArray(new Player[this.blueTeam.size()]));
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGames(), this.redTeam.toArray(new Player[this.redTeam.size()]));
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardWinning(), this.redTeam.toArray(new Player[this.redTeam.size()]));

                    this.executeCommand(this.arena.getTeamMeta().getGamendCommand(), this.getPlayers());
                    this.executeCommand(this.arena.getTeamMeta().getWinCommand(), this.redTeam);
                    Bukkit.getPluginManager().callEvent(new GameWinEvent(this.redTeam, this));

                    this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getRedwinnerTitleMessage()), this.decryptText(this.arena.getTeamMeta().getRedwinnerSubtitleMessage()));
                    this.reset();
                }
            } else if (team == Team.BLUE) {
                this.blueGoals++;
                this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getBluetitleScoreMessage()), this.decryptText(this.arena.getTeamMeta().getBluesubtitleMessage()));
                if (this.lastHit != null && this.blueTeam.contains(this.lastHit)) {
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGoals(), this.lastHit);
                    Bukkit.getPluginManager().callEvent(new GoalShootEvent(this, this.lastHit, team));
                }
                if (this.blueGoals >= this.arena.getTeamMeta().getMaxScore()) {
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGames(), this.blueTeam.toArray(new Player[this.blueTeam.size()]));
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardGames(), this.redTeam.toArray(new Player[this.redTeam.size()]));
                    NMSRegistry.addMoney(this.arena.getTeamMeta().getRewardWinning(), this.blueTeam.toArray(new Player[this.blueTeam.size()]));

                    this.executeCommand(this.arena.getTeamMeta().getGamendCommand(), this.getPlayers());
                    this.executeCommand(this.arena.getTeamMeta().getWinCommand(), this.blueTeam);
                    Bukkit.getPluginManager().callEvent(new GameWinEvent(this.blueTeam, this));
                    this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getBluewinnerTitleMessage()), this.decryptText(this.arena.getTeamMeta().getBluewinnerSubtitleMessage()));
                    this.reset();
                }
            }
            if (this.getHologram() != null) {
                this.getHologram().setText(this.decryptText(this.arena.getTeamMeta().getHologramText()));
            }
            if (this.gameScoreboard != null) {
                this.gameScoreboard.update(this);
            }
        }
    }
}
*/