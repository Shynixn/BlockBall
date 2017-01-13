package com.github.shynixn.blockball.api.entities.items;

/**
 * Created by Shynixn
 */
public enum Spawnrate {
    NONE(0, 0),
    LITTLE(20, 2),
    MEDIUM(40, 4),
    HIGH(60, 6),
    HIGHEST(80, 10);

    private final int spawnChance;
    private final int maxAmount;

    Spawnrate(int spawnChance, int maxAmount) {
        this.spawnChance = spawnChance;
        this.maxAmount = maxAmount;
    }

    public int getSpawnChance() {
        return this.spawnChance;
    }

    public int getMaxAmount() {
        return this.maxAmount;
    }

    public static Spawnrate getSpawnrateFromName(String name) {
        for (final Spawnrate type : Spawnrate.values()) {
            if (type.name().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }
}