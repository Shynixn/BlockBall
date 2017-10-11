package com.github.shynixn.blockball.bukkit.logic.business.entity;

import com.github.shynixn.blockball.api.persistence.entity.Arena;
import org.bukkit.entity.Player;

public class MiniGameEntity extends HelperGameEntity {
   public MiniGameEntity(Arena arena) {
        super(arena);
    }

    @Override
    public synchronized boolean leave(Player player) {
        final boolean isSuccess = super.leave(player);
        player.teleport(this.arena.getLobbyMeta().getLobbyLeave());
        return isSuccess;
    }

    @Override
    public synchronized boolean joinLobby(Player player) {
        if (this.canJoinLobby(player)) {
            this.storeTemporaryInventory(player);
        }
        return super.joinLobby(player);
    }
}
