package com.github.shynixn.blockball.bukkit.logic.business.entity;

import com.github.shynixn.blockball.api.persistence.entity.Arena;
import com.github.shynixn.blockball.api.persistence.entity.SoundMeta;
import com.github.shynixn.blockball.bukkit.logic.business.configuration.Language;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.builder.SoundBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class EventGameEntity extends GameEntity {
    Player referee;
    private int countdown;

    boolean innerForcefield;
    boolean visitorForceField;
    private int secondbumper = 20;

    private GameStage gameStage = GameStage.DISABLED;
    private int bumeper = 40;
    private final Map<Player, Integer> bumpers = new HashMap<>();
    private boolean interruptgame = true;

    private final SoundMeta blingsound = new SoundBuilder("NOTE_PLING", 1.0, 2.0);

    public EventGameEntity(Arena arena) {
        super(arena);
    }

    void executeJoinAllCommand() {
        for (final String s : this.arena.getEventMeta().getRegisteredRedPlayers()) {
            Player splayer = null;
            for (final Player player : SFileUtils.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(s)) {
                    splayer = player;
                }
            }
            if (splayer == null) {
                this.referee.sendMessage(Language.PREFIX + ChatColor.RED + "Warning, Player " + s + " on team red is missing!");
            } else if (!this.redTeam.contains(splayer)) {
                this.join(splayer, Team.RED);
            }
        }
        for (final String s : this.arena.getEventMeta().getRegisteredBluePlayers()) {
            Player splayer = null;
            for (final Player player : SFileUtils.getOnlinePlayers()) {
                if (player.getName().equalsIgnoreCase(s)) {
                    splayer = player;
                }
            }
            if (splayer == null) {
                this.referee.sendMessage(Language.PREFIX + ChatColor.RED + "Warning, Player " + s + " on team blue is missing!");
            } else if (!this.blueTeam.contains(splayer)) {
                this.join(splayer, Team.BLUE);
            }
        }
        this.countdown = 0;
        this.interruptgame = true;
        this.visitorForceField = true;
    }

    void executeInterrupt() {
        this.interruptgame = true;
    }

    void executeContinue() {
        if (this.gameStage == GameStage.DISABLED) {
            this.gameStage = GameStage.RUNNING;
        }
        this.interruptgame = false;
    }

    void executeEndGame() {
        if (this.redGoals > this.blueGoals) {
            this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getRedwinnerTitleMessage()), this.decryptText(this.arena.getTeamMeta().getRedwinnerSubtitleMessage()));
        } else if (this.redGoals < this.blueGoals) {
            this.sendMessageToPlayers(this.decryptText(this.arena.getTeamMeta().getBluewinnerTitleMessage()), this.decryptText(this.arena.getTeamMeta().getBluewinnerSubtitleMessage()));
        } else {
            this.sendMessageToPlayers(Language.GAME_DRAW_TITLE, Language.GAME_DRAW_SUBTITLE);
        }
        this.gameStage = GameStage.DISABLED;
        this.countdown = 0;
        this.interruptgame = false;
        if (this.getBall() != null)
            this.getBall().despawn();
        this.interruptgame = true;
    }

    @Override
    public void reset() {
        super.reset();
        this.bumpers.clear();
    }

    @Override
    public void run() {
        if (!this.arena.isEnabled() || this.gameStage != GameStage.RUNNING)
            return;
        this.secondbumper--;
        if (this.secondbumper <= 0) {
            this.secondbumper = 20;
            if (this.gameStage == GameStage.RUNNING) {
                if (this.getPlayers().isEmpty()) {
                    this.reset();
                } else if (this.getPlayers().size() < this.arena.getLobbyMeta().getMinPlayers()) {
                    this.sendMessageToPlayers(Language.GAME_DRAW_TITLE, Language.GAME_DRAW_SUBTITLE);
                    this.reset();
                }
                for (final Player player : this.getPlayers()) {
                    player.setLevel(this.countdown);
                    player.setExp(((float) this.countdown) / ((float) this.arena.getLobbyMeta().getGameTime()));
                }
                this.referee.setLevel(this.countdown);
                this.referee.setExp(((float) this.countdown) / ((float) this.arena.getLobbyMeta().getGameTime()));
                if (this.countdown < this.arena.getLobbyMeta().getGameTime())
                    this.countdown++;
                else {
                    try {
                        this.blingsound.apply(this.referee);
                    } catch (final Exception e) {
                        Bukkit.getServer().getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.RED + "Invalid 1.8/1.9 sound. [BlingSound]");
                    }
                    this.referee.setLevel(this.countdown);
                }
            }
        }
        this.playerForcefield();
        if (this.interruptgame) {
            this.ballSpawning = false;
            if (this.getBall() != null) {
                this.getBall().getDesignEntity().setGravity(false);
                this.getBall().getMovementEntity().teleport(this.getBall().getMovementEntity().getLocation());
            }
        } else if (this.getBall() != null) {
            this.getBall().getDesignEntity().setGravity(true);
        }
        super.run();
    }

    private void playerForcefield() {
        if (this.innerForcefield) {
            this.bumeper--;
            if (this.bumeper <= 0) {
                this.bumeper = 40;
                for (final Player player : this.getPlayers()) {
                    if (!this.arena.isLocationInArea(player.getLocation())) {
                        if (!this.bumpers.containsKey(player))
                            this.bumpers.put(player, 0);
                        else
                            this.bumpers.put(player, this.bumpers.get(player) + 1);
                        final Vector knockback = this.arena.getBallSpawnLocation().toVector().subtract(player.getLocation().toVector());
                        player.getLocation().setDirection(knockback);
                        player.setVelocity(knockback);
                        final Vector direction = this.arena.getBallSpawnLocation().toVector().subtract(player.getLocation().toVector());
                        player.setVelocity(direction.multiply(0.1));
                        if (this.bumpers.get(player) == 5) {
                            player.teleport(this.arena.getBallSpawnLocation());
                        }
                    } else if (this.bumpers.containsKey(player)) {
                        this.bumpers.remove(player);
                    }
                }
            }
        }
    }

    @Override
    protected String decryptText(String text) {
        try {
            text = ChatColor.translateAlternateColorCodes('&', text.replace(":countdown", String.valueOf(this.countdown / 60)));
        } catch (final Exception e) {
            this.sendErrorMessage();
        }
        return super.decryptText(text);
    }

    @Override
    public boolean join(Player player, Team team) {
        this.storeTemporaryInventory(player);
        player.getInventory().clear();
        player.updateInventory();
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        if (team == Team.RED) {
            player.getInventory().setArmorContents(this.arena.getTeamMeta().getRedItems());
            this.redTeam.add(player);
            if (this.arena.getTeamMeta().getRedSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getRedSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.getHologram() != null) {
                this.getHologram().show(player);
            }
        } else {
            player.getInventory().setArmorContents(this.arena.getTeamMeta().getBlueItems());
            this.blueTeam.add(player);
            if (this.arena.getTeamMeta().getBlueSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getBlueSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.getHologram() != null) {
                this.getHologram().show(player);
            }
        }
        return true;
    }

    private void storeTemporaryInventory(Player player) {
        final TemporaryPlayerStorage storage = new TemporaryPlayerStorage();
        storage.armorContent = player.getInventory().getArmorContents().clone();
        storage.isFlying = player.getAllowFlight();
        storage.inventory = player.getInventory().getContents().clone();
        storage.gameMode = player.getGameMode();
        storage.level = player.getLevel();
        storage.exp = player.getExp();
        storage.scoreboard = player.getScoreboard();
        this.temporaryStorage.put(player, storage);
    }
}
