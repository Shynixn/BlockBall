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
    private int timer = 0;

    LobbyGameEntity(Arena arena) {
        super(arena);
    }

    @Override
    public synchronized boolean join(Player player, Team team) {
        leave(player, false);
        if (team == null) {
            if (redTeam.size() > blueTeam.size())
                team = Team.BLUE;
            else
                team = Team.RED;
        }
        if (team == Team.RED && redTeam.size() <= arena.getTeamMeta().getTeamMaxSize() && (!arena.getTeamMeta().isTeamAutoJoin() || redTeam.size() <= blueTeam.size())) {
            armorContents.put(player, player.getInventory().getArmorContents().clone());
            player.getInventory().setArmorContents(arena.getTeamMeta().getRedItems());
            redTeam.add(player);
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.arena.getTeamMeta().getRedSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getRedSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            if (getHologram() != null) {
                getHologram().setText(decryptText(arena.getTeamMeta().getHologramText()));
                getHologram().show(player);
            }
            return true;
        } else if (team == Team.BLUE && blueTeam.size() <= arena.getTeamMeta().getTeamMaxSize() && (!arena.getTeamMeta().isTeamAutoJoin() || blueTeam.size() <= redTeam.size())) {
            blueTeam.add(player);
            armorContents.put(player, player.getInventory().getArmorContents().clone());
            player.getInventory().setArmorContents(arena.getTeamMeta().getBlueItems());
            player.sendMessage(Language.PREFIX + this.arena.getTeamMeta().getJoinMessage());
            if (this.arena.getTeamMeta().getBlueSpawnPoint() != null)
                player.teleport(this.arena.getTeamMeta().getBlueSpawnPoint());
            else
                player.teleport(this.arena.getBallSpawnLocation());
            if (getHologram() != null) {
                getHologram().setText(decryptText(arena.getTeamMeta().getHologramText()));
                getHologram().show(player);
            }
            return true;
        }
        return false;
    }

    private boolean isCustomDrop(Entity entity) {
        for (Item item : arena.getBoostItemHandler().getDroppedItems()) {
            if (item.equals(entity))
                return true;
        }
        return false;
    }

    @Override
    public void run() {
        if (!arena.isEnabled())
            return;
        timer--;
        if (timer <= 0) {
            for (Entity entity : this.arena.getBallSpawnLocation().getWorld().getEntities()) {
                if (!(entity instanceof Player) && !(entity instanceof Rabbit) && !(entity instanceof ArmorStand) && !isCustomDrop(entity)) {
                    if (arena.isLocationInArea(entity.getLocation())) {
                        Vector vector = Config.getInstance().getEntityProtectionVelocity();
                        entity.getLocation().setDirection(vector);
                        entity.setVelocity(vector);
                    }
                }
            }
            updateSigns();
            fixCachedRangePlayers();
            if (arena.getTeamMeta().isSpectatorMessagesEnabled()) {
                for (Player player : getPlayersInRange()) {
                    if (!playData.contains(player))
                        playData.add(player);
                }
                arena.getTeamMeta().getScoreboard().play(null, redGoals, blueGoals, getPlayersInRange());
            } else {
                arena.getTeamMeta().getScoreboard().play(null, redGoals, blueGoals, getPlayers());
            }
            timer = 20;
        }
        super.run();
    }

    private void updateSigns() {
        Location[] locations = arena.getLobbyMeta().getRedTeamSignLocations().toArray(new Location[0]);
        for (int i = 0; i < locations.length; i++) {
            Location location = locations[i];
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                Config.getInstance().getTeamSign().updateTeamSignConsideringMaxPlayers((Sign) location.getBlock().getState(), this, Team.RED, redTeam);
            } else {
                arena.getLobbyMeta().removeRedTeamSignLocation(i);
            }
        }
        locations = arena.getLobbyMeta().getBlueTeamSignLocations().toArray(new Location[0]);
        for (int i = 0; i < locations.length; i++) {
            Location location = locations[i];
            if (location.getBlock().getType() == Material.SIGN_POST || location.getBlock().getType() == Material.WALL_SIGN) {
                Config.getInstance().getTeamSign().updateTeamSignConsideringMaxPlayers((Sign) location.getBlock().getState(), this, Team.BLUE, blueTeam);
            } else {
                arena.getLobbyMeta().removeBlueTeamSignLocation(i);
            }
        }
    }
}
