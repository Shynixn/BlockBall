package com.github.shynixn.blockball.api.persistence.controller;

import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface PlayerMetaController extends IDatabaseController<PlayerMeta> {

    /**
     * Creates a new playerData from the given player
     * @param player player
     * @return playerData
     */
    PlayerMeta create(Player player);

    /**
     * Returns the playerMeta of the given uuid
     * @param uuid uuid
     * @return playerMeta
     */
    PlayerMeta getByUUID(UUID uuid);
}
