package com.github.shynixn.blockball.business.logic.game;

import org.bukkit.GameMode;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Shynixn
 */
class PlayerProperties {
    ItemStack[] contents;
    int level;
    float exp;
    int foodLevel;
    double health;
    GameMode mode;

    PlayerProperties(ItemStack[] contents, int level, float exp, int foodLevel, double health, GameMode gameMode) {
        super();
        this.contents = contents;
        this.level = level;
        this.exp = exp;
        this.foodLevel = foodLevel;
        this.health = health;
        this.mode = gameMode;
    }
}
