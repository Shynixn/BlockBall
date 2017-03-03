package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Team;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.Language;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

/**
 * Created by Shynixn
 */
class LobbyGameEntity extends GameEntity {
    private int timer;

    LobbyGameEntity(Arena arena) {
        super(arena);
    }

    @Override
    public synchronized boolean join(Player player, Team team) {
        this.leave(player, false);
        if (team == null) {
            if (this.redTeam.size() > this.blueTeam.size())
                team = Team.BLUE;
            else
                team = Team.RED;
        }
        if (team == Team.RED && this.redTeam.size() <= this.arena.getTeamMeta().getTeamMaxSize() && (!this.arena.getTeamMeta().isTeamAutoJoin() || this.redTeam.size() <= this.blueTeam.size())) {
            if(player.getAllowFlight())
                this.previousFlying.add(player);
            this.armorContents.put(player, new InventoryCache(player));
            player.getInventory().setArmorContents(this.arena.getTeamMeta().getRedItems());
            this.redTeam.add(player);
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.arena.getTeamMeta().getRedSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getRedSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            if (this.getHologram() != null) {
                this.getHologram().setText(this.decryptText(this.arena.getTeamMeta().getHologramText()));
                this.getHologram().show(player);
            }
            return true;
        } else if (team == Team.BLUE && this.blueTeam.size() <= this.arena.getTeamMeta().getTeamMaxSize() && (!this.arena.getTeamMeta().isTeamAutoJoin() || this.blueTeam.size() <= this.redTeam.size())) {
            if(player.getAllowFlight())
                this.previousFlying.add(player);
            this.blueTeam.add(player);
            this.armorContents.put(player, new InventoryCache(player));
            player.getInventory().setArmorContents(this.arena.getTeamMeta().getBlueItems());
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.arena.getTeamMeta().getBlueSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getBlueSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            if (this.getHologram() != null) {
                this.getHologram().setText(this.decryptText(this.arena.getTeamMeta().getHologramText()));
                this.getHologram().show(player);
            }
            return true;
        }
        return false;
    }

    private boolean isCustomDrop(Entity entity) {
        for (final Item item : this.arena.getBoostItemHandler().getDroppedItems()) {
            if (item.equals(entity))
                return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (!this.arena.isEnabled())
            return;
        this.timer--;
        if (this.timer <= 0) {
            for (final Entity entity : this.arena.getBallSpawnLocation().getWorld().getEntities()) {
                if (!(entity instanceof Player) && !(entity instanceof Rabbit) && !(entity instanceof ArmorStand) && !this.isCustomDrop(entity)) {
                    if (this.arena.isLocationInArea(entity.getLocation())) {
                        final Vector vector = Config.getInstance().getEntityProtectionVelocity();
                        entity.getLocation().setDirection(vector);
                        entity.setVelocity(vector);
                    }
                }
            }
            this.updateSigns();
            this.fixCachedRangePlayers();
            if (this.arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                for (final Player player : this.getPlayersInRange()) {
                    if (!this.playData.contains(player))
                        this.playData.add(player);
                }
                this.arena.getTeamMeta().getScoreboard().play(null, this.redGoals, this.blueGoals, this.getPlayersInRange());
            } else {
                this.arena.getTeamMeta().getScoreboard().play(null, this.redGoals, this.blueGoals, this.getPlayers());
            }
            this.timer = 20;
        }
        super.run();
    }

    private void updateSigns() {
        Location[] locations = this.arena.getLobbyMeta().getRedTeamSignLocations().toArray(new Location[this.arena.getLobbyMeta().getRedTeamSignLocations().size()]);
        for (int i = 0; i < locations.length; i++) {
            final Location location = locations[i];
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                Config.getInstance().getTeamSign().updateTeamSignConsideringMaxPlayers((Sign) location.getBlock().getState(), this, Team.RED, this.redTeam);
            } else {
                this.arena.getLobbyMeta().removeRedTeamSignLocation(i);
            }
        }
        locations = this.arena.getLobbyMeta().getBlueTeamSignLocations().toArray(new Location[this.arena.getLobbyMeta().getBlueTeamSignLocations().size()]);
        for (int i = 0; i < locations.length; i++) {
            final Location location = locations[i];
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                Config.getInstance().getTeamSign().updateTeamSignConsideringMaxPlayers((Sign) location.getBlock().getState(), this, Team.BLUE, this.blueTeam);
            } else {
                this.arena.getLobbyMeta().removeBlueTeamSignLocation(i);
            }
        }
    }
}
