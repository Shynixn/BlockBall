package com.github.shynixn.blockball.business.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.lib.YamlSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerData extends PersistenceObject<PlayerMeta> implements PlayerMeta {
    @YamlSerializer.YamlSerialize(value = "name", orderNumber = 1)
    private String name;
    @YamlSerializer.YamlSerialize(value = "uuid", orderNumber = 2)
    private String uuid;

    /**
     * Returns the name of the playerData
     *
     * @return playerData
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the playerData
     *
     * @param name name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the uuid of the playerData
     *
     * @return uuid
     */
    @Override
    public UUID getUUID() {
        return UUID.fromString(this.uuid);
    }

    /**
     * Sets the uuid of the playerData
     *
     * @param uuid uuid
     */
    @Override
    public void setUuid(UUID uuid) {
        if (uuid != null) {
            this.uuid = uuid.toString();
        }
    }

    /**
     * Returns the player of the playerData
     *
     * @return player
     */
    @Override
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    /**
     * Generates the playerData from a player
     *
     * @param player player
     * @return playerData
     */
    public static PlayerData from(Player player) {
        final PlayerData playerStats = new PlayerData();
        playerStats.setName(player.getName());
        playerStats.setUuid(player.getUniqueId());
        return playerStats;
    }

    /**
     * Clones the current object
     *
     * @return object
     */
    @Override
    public PlayerMeta clone() {
        try {
            return YamlSerializer.deserializeObject(PlayerData.class, this.serialize());
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serializes the playerData
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        try {
            return YamlSerializer.serialize(this);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
