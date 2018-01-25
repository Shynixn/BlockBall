package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.stats;

import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject;
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerData extends PersistenceObject implements PlayerMeta {
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
        if (this.uuid == null)
            return null;
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
}
