package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.lib.SFileUtils;
import com.github.shynixn.blockball.business.bungee.game.BungeeCord;
import org.bukkit.entity.Player;

/**
 * Created by Shynixn
 */
class BungeeGameEntity extends HelperGameEntity {
    BungeeGameEntity(Arena arena) {
        super(arena);
        BungeeCord.setModt(BungeeCord.MOD_WAITING_FOR_PLAYERS);
    }

    @Override
    public void startGame() {
        BungeeCord.setModt(BungeeCord.MOD_INGAME);
        super.startGame();
    }

    @Override
    public synchronized boolean leave(Player player) {
        final boolean success = super.leave(player);
        player.kickPlayer(this.arena.getTeamMeta().getLeaveMessage());
        return success;
    }

    @Override
    public void reset() {
        BungeeCord.setModt(BungeeCord.MOD_RESTARTING);
        for (final Player player : this.armorContents.keySet()) {
            this.leave(player);
        }
        super.reset();
        if (this.arena.isEnabled())
            SFileUtils.restartServer();
    }
}
