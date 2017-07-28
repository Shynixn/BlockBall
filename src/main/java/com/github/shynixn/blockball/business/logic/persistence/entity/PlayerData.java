package com.github.shynixn.blockball.business.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData extends PersistenceObject implements PlayerMeta {
    private String name;
    private UUID uuid;

    /**
     * Returns the name of the playerData
     * @return playerData
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the playerData
     * @param name name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the uuid of the playerData
     * @return uuid
     */
    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     * Sets the uuid of the playerData
     * @param uuid uuid
     */
    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Returns the player of the playerData
     * @return player
     */
    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    /**
     * Generates the playerData from a player
     * @param player player
     * @return playerData
     */
    public static PlayerData from(Player player) {
        final PlayerData playerStats = new PlayerData();
        playerStats.setName(player.getName());
        playerStats.setUuid(player.getUniqueId());
        return playerStats;
    }
}
