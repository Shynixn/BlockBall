package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.business.Language;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.lib.*;
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.GameStage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
class EventGameEntity extends GameEntity {
    Player referee;
    private int countdown;
    private final Map<Player, PlayerProperties> playerstorage = new HashMap<>();

    boolean innerForcefield;
    boolean visitorForceField;
    private int secondbumper = 20;

    private GameStage gameStage = GameStage.DISABLED;
    private int bumeper = 40;
    private final Map<Player, Integer> bumpers = new HashMap<>();
    private boolean interruptgame = true;

    private final LightSound blingsound = new FastSound("NOTE_PLING", 1.0, 2.0);

    EventGameEntity(Arena arena) {
        super(arena);
    }

    void executeJoinAllCommand() {
        for (String s : this.arena.getEventMeta().getRegisteredRedPlayers()) {
            Player splayer = null;
            for (Player player : SFileUtils.getOnlinePlayers()) {
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
        for (String s : this.arena.getEventMeta().getRegisteredBluePlayers()) {
            Player splayer = null;
            for (Player player : SFileUtils.getOnlinePlayers()) {
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
        for (Player player : this.playerstorage.keySet()) {
            player.getInventory().setContents(this.playerstorage.get(player).contents);
            player.setLevel(this.playerstorage.get(player).level);
            player.setExp(this.playerstorage.get(player).exp);
            player.setFoodLevel(this.playerstorage.get(player).foodlevel);
            player.setHealth(this.playerstorage.get(player).health);
            player.setGameMode(this.playerstorage.get(player).mode);
            player.updateInventory();
        }
        this.bumpers.clear();
        this.playerstorage.clear();
        super.reset();
    }

    @Override
    public void run() {
        if (!this.arena.isEnabled() || this.gameStage != GameStage.RUNNING)
            return;
        this.secondbumper--;
        if (this.secondbumper <= 0) {
            this.secondbumper = 20;
            if (this.gameStage == GameStage.RUNNING) {
                if (this.getPlayers().size() == 0) {
                    this.reset();
                } else if (this.getPlayers().size() < this.arena.getLobbyMeta().getMinPlayers()) {
                    this.sendMessageToPlayers(Language.GAME_DRAW_TITLE, Language.GAME_DRAW_SUBTITLE);
                    this.reset();
                }
                for (Player player : this.getPlayers()) {
                    player.setLevel(this.countdown);
                    player.setExp(((float) this.countdown) / ((float) this.arena.getLobbyMeta().getGameTime()));
                }
                this.referee.setLevel(this.countdown);
                this.referee.setExp(((float) this.countdown) / ((float) this.arena.getLobbyMeta().getGameTime()));
                if (this.countdown < this.arena.getLobbyMeta().getGameTime())
                    this.countdown++;
                else {
                    try {
                        this.blingsound.play(this.referee);
                    } catch (InterPreter19Exception e) {
                        SConsoleUtils.sendColoredMessage("Invalid 1.8/1.9 sound. [BlingSound]", ChatColor.RED, BlockBallPlugin.PREFIX_CONSOLE);
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
                for (Player player : this.getPlayers()) {
                    if (!this.arena.isLocationInArea(player.getLocation())) {
                        if (!this.bumpers.containsKey(player))
                            this.bumpers.put(player, 0);
                        else
                            this.bumpers.put(player, this.bumpers.get(player) + 1);
                        Vector knockback = this.arena.getBallSpawnLocation().toVector().subtract(player.getLocation().toVector());
                        player.getLocation().setDirection(knockback);
                        player.setVelocity(knockback);
                        Vector direction = this.arena.getBallSpawnLocation().toVector().subtract(player.getLocation().toVector());
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
        } catch (Exception e) {
            this.sendErrorMessage();
        }
        return super.decryptText(text);
    }

    @Override
    public boolean join(Player player, Team team) {
        this.armorContents.put(player, player.getInventory().getArmorContents().clone());
        this.playerstorage.put(player, new PlayerProperties(player.getInventory().getContents().clone(), player.getLevel(), player.getExp(), player.getFoodLevel(), player.getHealth(), player.getGameMode()));
        player.getInventory().clear();
        player.updateInventory();
        player.setHealth((double)player.getMaxHealth());
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
}
