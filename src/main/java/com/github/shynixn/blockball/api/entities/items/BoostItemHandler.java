package com.github.shynixn.blockball.api.entities.items;

import com.github.shynixn.blockball.api.entities.Game;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Item;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Shynixn
 */
public interface BoostItemHandler extends Serializable, ConfigurationSerializable {
    void setBoostItem(BoostItem boostItem);

    void removeBoostItem(BoostItem boostItem);

    void clear();

    Spawnrate getRate();

    void setSpawnRate(Spawnrate rate);

    List<BoostItem> getBoostItems();

    Item[] getDroppedItems();

    BoostItem getBoostFromItem(Item item);

    void removeItem(Item item);

    void run(Game game);
}
