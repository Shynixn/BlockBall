package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
class MiniGameEntity extends HelperGameEntity {
    private final Map<Player, PlayerProperties> playerStorage = new HashMap<>();

    MiniGameEntity(Arena arena) {
        super(arena);
    }

    @Override
    public synchronized boolean leave(Player player) {
        if (this.playerStorage.containsKey(player)) {
            player.getInventory().setContents(this.playerStorage.get(player).contents);
            player.setLevel(this.playerStorage.get(player).level);
            player.setExp(this.playerStorage.get(player).exp);
            player.setFoodLevel(this.playerStorage.get(player).foodLevel);
            player.setHealth(this.playerStorage.get(player).health);
            player.setGameMode(this.playerStorage.get(player).mode);
            player.updateInventory();
            this.playerStorage.remove(player);
        }
        final boolean isSuccess = super.leave(player);
        player.teleport(this.arena.getLobbyMeta().getLobbyLeave());
        return isSuccess;
    }

    @Override
    public synchronized boolean joinLobby(Player player) {
        if (this.canJoinLobby(player)) {
            this.playerStorage.put(player, new PlayerProperties(player.getInventory().getContents().clone(), player.getLevel(), player.getExp(), player.getFoodLevel(), player.getHealth(), player.getGameMode()));
        }
        return super.joinLobby(player);
    }

    @Override
    public void reset() {
        for (final Player player : this.playerStorage.keySet().toArray(new Player[this.playerStorage.size()])) {
            this.leave(player);
        }
        this.playerStorage.clear();
        super.reset();
    }
}
