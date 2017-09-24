package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.PlaceHolderType;
import com.github.shynixn.blockball.lib.BlockBallApi;
import com.github.shynixn.blockball.api.events.GameWinEvent;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.logic.items.ItemSpawner;
import com.github.shynixn.blockball.api.events.GoalShootEvent;
import com.github.shynixn.blockball.lib.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameEntity implements Game {
    protected final Plugin plugin;

    /**
     * Static arena
     */
    protected final Arena arena;

    /**
     * Temporary Storage
     */
    final Map<Player, TemporaryPlayerStorage> temporaryStorage = new HashMap<>();

    /**
     * Teams and goals per team
     */
    final List<Player> redTeam = new ArrayList<>();
    final List<Player> blueTeam = new ArrayList<>();
    int blueGoals;
    int redGoals;

    /**
     * Ball calculations
     */
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
     */
    Team lastHitTeam;
    Player lastHit;

    /**
     * BossBar
     */
    private Object bossBar;
    final List<Player> playData = new ArrayList<>();
    /**
     * Hologram
     */
    private LightHologram hologram;

    /**
     * Scoreboard
     */
    GameScoreboard gameScoreboard;

    GameEntity(Arena arena) {
        super();
        this.plugin = JavaPlugin.getPlugin(BlockBallPlugin.class);
        this.arena = arena;
        if (arena.getTeamMeta().isScoreboardEnabled()) {
            this.gameScoreboard = new GameScoreboard(arena);
        }
    }

    /**
     * Adds the player to the temporary storage
     *
     * @param player player
     * @param team   team
     * @return success
     */
    @Override
    public abstract boolean join(Player player, Team team);

    @Override
    public boolean isInGame(Player player) {
        return this.getPlayers().contains(player);
    }

    @Override
    public boolean leave(Player player) {
        return this.leave(player, true);
    }

    synchronized boolean leave(Player player, boolean message) {
        if (this.redTeam.contains(player))
            this.redTeam.remove(player);
        if (this.blueTeam.contains(player))
            this.blueTeam.remove(player);
        if (this.temporaryStorage.containsKey(player)) {
            final TemporaryPlayerStorage storage = this.temporaryStorage.get(player);
            if (storage.inventory != null) {
                player.getInventory().setContents(storage.inventory);
            }
            if (storage.armorContent != null) {
                player.getInventory().setArmorContents(storage.armorContent);
            }
            if (storage.gameMode != null) {
                player.setGameMode(storage.gameMode);
            }
            if (storage.level != null) {
                player.setLevel(storage.level);
            }
            if (storage.exp != null) {
                player.setExp(storage.exp);
            }
            if (storage.foodLevel != null) {
                player.setFoodLevel(storage.foodLevel);
            }
            if (storage.health != null) {
                player.setHealthScale(storage.health);
            }
            if (storage.scoreboard != null) {
                player.setScoreboard(storage.scoreboard);
            }
            player.setWalkSpeed(storage.walkingSpeed);
            player.setFlying(false);
            player.setAllowFlight(storage.isFlying);
            player.updateInventory();
            this.temporaryStorage.remove(player);
        }
        if (player.isOnline() && message)
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getLeaveMessage());
        this.arena.getTeamMeta().getBossBar().stopPlay(this.bossBar, player);
        this.removePlayerFromScoreboard(player);
        if (this.arena.getTeamMeta().isBossBarPluginEnabled()) {
            NMSRegistry.setBossBar(player, null);
        }
        if (!ReflectionLib.getServerVersion().contains("1_8")) {
            Interpreter19.setGlowing(player, false);
        }
        if (this.getHologram() != null) {
            this.getHologram().remove(player);
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
            }catch (final Exception e) {
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

    final void bumpBallBack() {
        if (this.lastBallLocation != null) {
            final Vector knockback = this.lastBallLocation.toVector().subtract(this.ball.getLocation().toVector());
            this.ball.getLocation().setDirection(knockback);
            this.ball.setVelocity(knockback);
            final Vector direction = this.arena.getBallSpawnLocation().toVector().subtract(this.ball.getLocation().toVector());
            this.ball.setVelocity(direction.multiply(0.1));
            this.bumper = 40;
            this.bumperCounter++;
            if (this.bumperCounter == 5) {
                this.ball.teleport(this.arena.getBallSpawnLocation());
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

    final String getPlaceHolder(PlaceHolderType type) {
        if (type == PlaceHolderType.BLUESCORE)
            return String.valueOf(this.blueGoals);
        else if (type == PlaceHolderType.BLUEAMOUNT)
            return String.valueOf(this.blueTeam.size());
        else if (type == PlaceHolderType.BLUECOLOR)
            return String.valueOf(this.arena.getTeamMeta().getBlueColor());
        else if (type == PlaceHolderType.BLUENAME)
            return String.valueOf(this.arena.getTeamMeta().getBlueTeamName());
        else if (this.lastHit != null && type == PlaceHolderType.LASTHITPLAYERNAME)
            return this.lastHit.getName();
        else if (type == PlaceHolderType.REDSCORE)
            return String.valueOf(this.redGoals);
        else if (type == PlaceHolderType.REDAMOUNT)
            return String.valueOf(this.redTeam.size());
        else if (type == PlaceHolderType.REDCOLOR)
            return String.valueOf(this.arena.getTeamMeta().getRedColor());
        else if (type == PlaceHolderType.REDNAME)
            return String.valueOf(this.arena.getTeamMeta().getRedTeamName());
        return "";
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

    static class TemporaryPlayerStorage {
        ItemStack[] inventory;
        ItemStack[] armorContent;
        Integer level;
        Float exp;
        boolean isFlying;
        GameMode gameMode;
        Integer foodLevel;
        Double health;
        Scoreboard scoreboard;
        float walkingSpeed = 0.2F;
    }
}
