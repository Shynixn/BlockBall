package com.github.shynixn.blockball.api.persistence.entity;

import java.util.UUID;

public interface PlayerMeta extends Persistenceable<PlayerMeta> {

    /**
     * Returns the name of the playerData
     *
     * @return playerData
     */
    String getName();

    /**
     * Sets the name of the playerData
     *
     * @param name name
     */
    void setName(String name);

    /**
     * Returns the uuid of the playerData
     *
     * @return uuid
     */
    UUID getUUID();

    /**
     * Sets the uuid of the playerData
     *
     * @param uuid uuid
     */
    void setUuid(UUID uuid);

    /**
     * Returns the player of the playerData
     *
     * @return player
     */
    Object getPlayer();
}
