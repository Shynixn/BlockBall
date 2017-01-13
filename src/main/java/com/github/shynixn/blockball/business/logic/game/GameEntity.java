package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.BlockBallApi;
import com.github.shynixn.blockball.api.entities.*;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.logic.items.ItemSpawner;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.events.GoalShootEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class GameEntity implements Game {
    @SPluginLoader.PluginLoader
    protected static JavaPlugin plugin;

    protected final Arena arena;
    private Object bossBar;
    private Ball ball;

    //Storage
    final Map<Player, ItemStack[]> armorContents = new HashMap<>();
    final List<Player> redTeam = new ArrayList<>();
    final List<Player> blueTeam = new ArrayList<>();
    int blueGoals = 0;
    int redGoals = 0;

    //Bumper objects
    private int bumper = 20;
    private int bumerCounter = 0;
    private int buffer = 2;
    int ballcornerbumper = 0;
    Vector ballpreviousbumperLocation;
    private int counter = 3;
    private boolean freshReset = false;
    boolean ballSpawning = false;

    //Hit calculator
    private Team lastHitTeam = null;
    private Player lastHit = null;

    final List<Player> playeddata = new ArrayList<>();

    private LightHologram hologram;

    GameEntity(Arena arena) {
        this.arena = arena;
    }

    public abstract boolean join(Player player, Team team);

    LightHologram getHologram() {
        if (hologram == null && this.arena.getTeamMeta().isHologramEnabled() && this.arena.getTeamMeta().getHologramLocation() != null) {
            hologram = new LightHologram.Builder(this.arena.getTeamMeta().getHologramLocation());
        }
        return hologram;
    }

    @Override
    public Player[] getBlueTeamPlayers() {
        return blueTeam.toArray(new Player[0]);
    }

    @Override
    public Player[] getRedTeamPlayers() {
        return redTeam.toArray(new Player[0]);
    }

    @Override
    public boolean isInGame(Player player) {
        return getPlayers().contains(player);
    }

    synchronized boolean leave(Player player, boolean message) {
        if (redTeam.contains(player))
            redTeam.remove(player);
        if (blueTeam.contains(player))
            blueTeam.remove(player);
        if (player.isOnline() && message)
            player.sendMessage(Language.PREFIX + arena.getTeamMeta().getLeaveMessage());
        if (armorContents.containsKey(player))
            player.getInventory().setArmorContents(armorContents.get(player).clone());
        player.updateInventory();
        armorContents.remove(player);
        player.setFlying(false);
        player.setAllowFlight(false);
        arena.getTeamMeta().getBossBar().stopPlay(bossBar, player);
        arena.getTeamMeta().getScoreboard().remove(player);
        if (arena.getTeamMeta().isBossBarPluginEnabled()) {
            NMSRegistry.setBossBar(player, null);
        }
        if (getHologram() != null) {
            getHologram().remove(player);
        }
        return true;
    }

    public synchronized boolean leave(Player player) {
        return leave(player, true);
    }

    @Override
    public void playBallMoveEffects() {
        if (lastHitTeam != null) {
            if (lastHitTeam == Team.RED) {
                arena.getBallMeta().getPlayerTeamRedHitParticle().play(ball.getLocation());
            } else {
                arena.getBallMeta().getPlayerTeamBlueHitParticle().play(ball.getLocation());
            }
        }
    }

    @Override
    public void playBallKickEffects(Player player) {
        if (buffer == 0) {
            arena.getBallMeta().getGenericHitParticle().play(ball.getLocation());
            try {
                arena.getBallMeta().getGenericHitSound().play(ball.getLocation());
            } catch (InterPreter19Exception e) {
                SConsoleUtils.sendColoredMessage("Invalid 1.8/1.9 sound. [GenericHitSound]", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
            }
            buffer = 10;
        }
        if (blueTeam.contains(player))
            lastHitTeam = Team.BLUE;
        else
            lastHitTeam = Team.RED;
        lastHit = player;
    }

    public Ball getBall() {
        return ball;
    }

    public Arena getArena() {
        return arena;
    }

    private Location lastBallLocation;

    final void fixCachedRangePlayers() {
        for (Player player : playeddata.toArray(new Player[0])) {
            if (player.getLocation().getWorld().getName().equals(arena.getBallSpawnLocation().getWorld().getName())) {
                if (player.getLocation().distance(arena.getCenter()) > arena.getTeamMeta().getSpecatorradius()) {
                    arena.getTeamMeta().getScoreboard().remove(player);
                    arena.getTeamMeta().getBossBar().stopPlay(bossBar, player);
                    if (arena.getTeamMeta().isBossBarPluginEnabled()) {
                        NMSRegistry.setBossBar(player, null);
                    }
                    playeddata.remove(player);
                }
            } else {
                arena.getTeamMeta().getScoreboard().remove(player);
                arena.getTeamMeta().getBossBar().stopPlay(bossBar, player);
                if (arena.getTeamMeta().isBossBarPluginEnabled()) {
                    NMSRegistry.setBossBar(player, null);
                }
                playeddata.remove(player);
            }
        }
    }

    public void run() {
        getArena().getBoostItemHandler().run(this);
        if (buffer > 0)
            buffer--;
        if (ballSpawning) {
            counter--;
            if (counter <= 0) {
                if (arena.getTeamMeta().isBossBarPluginEnabled()) {
                    if (arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                        for (Player player : getPlayersInRange()) {
                            if (!playeddata.contains(player))
                                playeddata.add(player);
                            NMSRegistry.setBossBar(player, decryptText(arena.getTeamMeta().getBossBarPluginMessage()));
                        }
                    } else {
                        for (Player player : getPlayers()) {
                            NMSRegistry.setBossBar(player, decryptText(arena.getTeamMeta().getBossBarPluginMessage()));
                        }
                    }
                }
                if (arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                    fixCachedRangePlayers();
                    for (Player player : getPlayersInRange()) {
                        if (!playeddata.contains(player))
                            playeddata.add(player);
                    }
                    bossBar = arena.getTeamMeta().getBossBar().play(bossBar, decryptText(arena.getTeamMeta().getBossBar().getMessage()), getPlayersInRange());
                } else {
                    bossBar = arena.getTeamMeta().getBossBar().play(bossBar, decryptText(arena.getTeamMeta().getBossBar().getMessage()), getPlayers());
                }
                ball = BlockBallApi.createNewBall(arena.getBallSpawnLocation().getWorld());
                ball.spawn(arena.getBallSpawnLocation());
                ball.setSkin(arena.getBallMeta().getBallSkin());
                ball.setKickStrengthHorizontal(arena.getBallMeta().getHorizontalStrength());
                ball.setKickStrengthVertical(arena.getBallMeta().getVerticalStrength());
                ball.setRotating(arena.getBallMeta().isRotating());
                ballSpawning = false;
                freshReset = true;
                counter = 0;
                arena.getBallMeta().getBallSpawnParticle().play(ball.getLocation());
                try {
                    arena.getBallMeta().getBallSpawnSound().play(ball.getLocation());
                } catch (InterPreter19Exception e) {
                    SConsoleUtils.sendColoredMessage("Invalid 1.8/1.9 sound. [BallSpawnSound]", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
                }
            }
        } else if ((ball == null || ball.isDead()) && (redTeam.size() > 0 || blueTeam.size() > 0) && getPlayers().size() >= arena.getTeamMeta().getTeamMinSize()) {
            ballSpawning = true;
            counter = arena.getBallMeta().getBallSpawnTime() * 20;
        }
        if (ball != null) {
            if (!this.arena.isLocationInArea(ball.getLocation())) {
                if (bumper == 0)
                    bumpBallBack();
            } else {
                bumerCounter = 0;
                lastBallLocation = ball.getLocation().clone();
            }
            if (getPlayers().size() == 0)
                ball.despawn();
            if (bumper > 0)
                bumper--;
        }
        if (freshReset && arena.getTeamMeta().isEmtptyReset() && getPlayers().size() == 0) {
            reset();
            freshReset = false;
        }
        if (ball != null && !ball.isDead() && arena.isLocationInGoal(ball.getLocation())) {
            Team team = arena.getTeamFromGoal(ball.getLocation());
            arena.getBallMeta().getBallGoalParticle().play(ball.getLocation());
            try {
                arena.getBallMeta().getBallGoalSound().play(ball.getLocation());
            } catch (InterPreter19Exception e) {
                SConsoleUtils.sendColoredMessage("Invalid 1.8/1.9 sound. [BallGoalSound]", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
            }
            ball.despawn();
            if (Team.RED == team) {
                redGoals++;
                sendMessageToPlayers(decryptText(arena.getTeamMeta().getRedtitleScoreMessage()), decryptText(arena.getTeamMeta().getRedsubtitleMessage()));
                if (lastHit != null && redTeam.contains(lastHit)) {
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGoals(), lastHit);
                    Bukkit.getPluginManager().callEvent(new GoalShootEvent(this, lastHit, team));
                }
                if (redGoals >= arena.getTeamMeta().getMaxScore() && lastHit != null) {
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGames(), blueTeam.toArray(new Player[0]));
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGames(), redTeam.toArray(new Player[0]));
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardWinning(), redTeam.toArray(new Player[0]));

                    executeCommand(arena.getTeamMeta().getGamendCommand(), getPlayers());
                    executeCommand(arena.getTeamMeta().getWinCommand(), redTeam);

                    sendMessageToPlayers(decryptText(arena.getTeamMeta().getRedwinnerTitleMessage()), decryptText(arena.getTeamMeta().getRedwinnerSubtitleMessage()));
                    reset();
                }
            } else if (Team.BLUE == team) {
                blueGoals++;
                sendMessageToPlayers(decryptText(arena.getTeamMeta().getBluetitleScoreMessage()), decryptText(arena.getTeamMeta().getBluesubtitleMessage()));
                if (lastHit != null && blueTeam.contains(lastHit)) {
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGoals(), lastHit);
                    Bukkit.getPluginManager().callEvent(new GoalShootEvent(this, lastHit, team));
                }
                if (blueGoals >= arena.getTeamMeta().getMaxScore()) {
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGames(), blueTeam.toArray(new Player[0]));
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardGames(), redTeam.toArray(new Player[0]));
                    NMSRegistry.addMoney(arena.getTeamMeta().getRewardWinning(), blueTeam.toArray(new Player[0]));

                    executeCommand(arena.getTeamMeta().getGamendCommand(), getPlayers());
                    executeCommand(arena.getTeamMeta().getWinCommand(), blueTeam);

                    sendMessageToPlayers(decryptText(arena.getTeamMeta().getBluewinnerTitleMessage()), decryptText(arena.getTeamMeta().getBluewinnerSubtitleMessage()));
                    reset();
                }
            }
            if (getHologram() != null) {
                getHologram().setText(decryptText(arena.getTeamMeta().getHologramText()));
            }
        }
    }

    void executeCommand(String command, List<Player> players) {
        if (command == null)
            return;
        if (command.replace(":player", "").length() == command.length()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        } else {
            for (Player player : players) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace(":player", player.getName()));
            }
        }
    }

    final void bumpBallBack() {
        if (lastBallLocation != null) {
            Vector knockback = lastBallLocation.toVector().subtract(ball.getLocation().toVector());
            ball.getLocation().setDirection(knockback);
            ball.setVelocity(knockback);
            Vector direction = this.arena.getBallSpawnLocation().toVector().subtract(ball.getLocation().toVector());
            ball.setVelocity(direction.multiply(0.1));
            bumper = 40;
            bumerCounter++;
            if (bumerCounter == 5) {
                ball.teleport(arena.getBallSpawnLocation());
            }
        }
    }

    void reset(boolean teleport) {
        for (Player player : armorContents.keySet()) {
            player.getInventory().setArmorContents(armorContents.get(player).clone());
            player.updateInventory();
        }
        if (teleport && arena.getTeamMeta().getGameEndSpawnpoint() != null) {
            for (Player player : getPlayers()) {
                player.teleport(arena.getTeamMeta().getGameEndSpawnpoint());
            }
        }
        for (Player player : playeddata) {
            arena.getTeamMeta().getBossBar().stopPlay(bossBar, player);
            if (arena.getTeamMeta().isBossBarPluginEnabled()) {
                NMSRegistry.setBossBar(player, null);
            }
        }
        ((ItemSpawner) arena.getBoostItemHandler()).clearGroundItems();
        FastBossBar.dispose(bossBar);
        if (ball != null)
            ball.despawn();
        if (hologram != null)
            hologram.remove(SFileUtils.getOnlinePlayers().toArray(new Player[0]));
        redTeam.clear();
        blueTeam.clear();
        playeddata.clear();
        ball = null;
        blueGoals = 0;
        redGoals = 0;
    }

    public void reset() {
        reset(true);
    }

    void sendMessageToPlayers(String title, String subTitle) {
        if (arena.getTeamMeta().isSpectatorMessagesEnabled()) {
            for (Player player : getPlayersInRange()) {
                LightScreenMessenger.Builder.getInstance().setPlayerTitle(player, title, subTitle, 0, 20 * 3, 10);
            }
        } else {
            for (Player player : getPlayers()) {
                LightScreenMessenger.Builder.getInstance().setPlayerTitle(player, title, subTitle, 0, 20 * 3, 10);
            }
        }
    }

    List<Player> getPlayersInRange() {
        List<Player> players = new ArrayList<>();
        for (Player player : getArena().getCenter().getWorld().getPlayers()) {
            if (player.getLocation().distance(getArena().getCenter()) <= arena.getTeamMeta().getSpecatorradius())
                players.add(player);
        }
        return players;
    }

    public final List<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        for (Player player : blueTeam) {
            players.add(player);
        }
        for (Player player : redTeam) {
            players.add(player);
        }
        return players;
    }

    protected String decryptText(String text) {
        try {
            if (lastHit == null) {
                return ChatColor.translateAlternateColorCodes('&', text
                        .replace(":redscore", String.valueOf(this.redGoals))
                        .replace(":bluescore", String.valueOf(this.blueGoals))
                        .replace(":redcolor", arena.getTeamMeta().getRedColor())
                        .replace(":bluecolor", arena.getTeamMeta().getBlueColor())
                        .replace(":red", arena.getTeamMeta().getRedTeamName())
                        .replace(":blue", arena.getTeamMeta().getBlueTeamName()));
            } else {
                return ChatColor.translateAlternateColorCodes('&', text
                        .replace(":redscore", String.valueOf(this.redGoals))
                        .replace(":bluescore", String.valueOf(this.blueGoals))
                        .replace(":redcolor", arena.getTeamMeta().getRedColor())
                        .replace(":bluecolor", arena.getTeamMeta().getBlueColor())
                        .replace(":player", lastHit.getName())
                        .replace(":red", arena.getTeamMeta().getRedTeamName())
                        .replace(":blue", arena.getTeamMeta().getBlueTeamName()));
            }
        } catch (Exception e) {
            sendErrorMessage();
        }
        throw new RuntimeException("The following error has already been fixed. Please wait for the games to get restarted...");
    }

    void sendErrorMessage() {
        SConsoleUtils.sendColoredMessage(ChatColor.YELLOW + "[BlockBall] " + ChatColor.GREEN + "Found invalid score configuration.");
        this.arena.getTeamMeta().reset();
        BlockBallApi.save(this.arena);
        SConsoleUtils.sendColoredMessage(ChatColor.YELLOW + "[BlockBall] " + ChatColor.GREEN + "Fix finished. Games are getting restarted.");
        BlockBallApi.reloadGames();
    }

    final String getPlaceHolder(PlaceHolderType type) {
        if (type == PlaceHolderType.BLUESCORE)
            return String.valueOf(this.blueGoals);
        else if (type == PlaceHolderType.BLUEAMOUNT)
            return String.valueOf(blueTeam.size());
        else if (type == PlaceHolderType.BLUECOLOR)
            return String.valueOf(arena.getTeamMeta().getBlueColor());
        else if (type == PlaceHolderType.BLUENAME)
            return String.valueOf(arena.getTeamMeta().getBlueTeamName());
        else if (lastHit != null && type == PlaceHolderType.LASTHITPLAYERNAME)
            return lastHit.getName();
        else if (type == PlaceHolderType.REDSCORE)
            return String.valueOf(this.redGoals);
        else if (type == PlaceHolderType.REDAMOUNT)
            return String.valueOf(redTeam.size());
        else if (type == PlaceHolderType.REDCOLOR)
            return String.valueOf(arena.getTeamMeta().getRedColor());
        else if (type == PlaceHolderType.REDNAME)
            return String.valueOf(arena.getTeamMeta().getRedTeamName());
        return "";
    }
}
