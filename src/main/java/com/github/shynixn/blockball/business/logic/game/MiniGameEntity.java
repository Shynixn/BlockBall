package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
class MiniGameEntity extends HelperGameEntity {
    private final Map<Player, PlayerProperties> playerstorage = new HashMap<>();

    MiniGameEntity(Arena arena) {
        super(arena);
    }

    @Override
    public synchronized boolean leave(Player player) {
        if (playerstorage.containsKey(player)) {
            player.getInventory().setContents(playerstorage.get(player).contents);
            player.setLevel(playerstorage.get(player).level);
            player.setExp(playerstorage.get(player).exp);
            player.setFoodLevel(playerstorage.get(player).foodlevel);
            player.setHealth(playerstorage.get(player).health);
            player.setGameMode(playerstorage.get(player).mode);
            player.updateInventory();
            playerstorage.remove(player);
        }
        boolean isSuccess = super.leave(player);
        player.teleport(arena.getLobbyMeta().getLobbyLeave());
        return isSuccess;
    }

    @Override
    public synchronized boolean joinLobby(Player player) {
        if (canJoinLobby(player)) {
            playerstorage.put(player, new PlayerProperties(player.getInventory().getContents().clone(), player.getLevel(), player.getExp(), player.getFoodLevel(), player.getHealth(), player.getGameMode()));
        }
        return super.joinLobby(player);
    }

    @Override
    public void reset() {
        for (Player player : playerstorage.keySet().toArray(new Player[0])) {
            leave(player);
        }
        playerstorage.clear();
        super.reset();
    }
}
