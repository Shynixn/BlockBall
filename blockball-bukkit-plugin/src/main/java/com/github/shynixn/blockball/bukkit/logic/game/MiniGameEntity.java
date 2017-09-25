package com.github.shynixn.blockball.bukkit.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import org.bukkit.entity.Player;

class MiniGameEntity extends HelperGameEntity {
    MiniGameEntity(Arena arena) {
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
