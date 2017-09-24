package com.github.shynixn.blockball.api.business.enumeration;

import java.util.Optional;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public enum Spawnrate {
    NONE(0, 0),
    LITTLE(20, 2),
    MEDIUM(40, 4),
    HIGH(60, 6),
    HIGHEST(80, 10);

    private final int spawnChance;
    private final int maxAmount;

    /**
     * Initializes a new spawnRate
     *
     * @param spawnChance spawnRate
     * @param maxAmount   maxAmount
     */
    Spawnrate(int spawnChance, int maxAmount) {
        this.spawnChance = spawnChance;
        this.maxAmount = maxAmount;
    }

    /**
     * Returns the chance in percent if a item can spawn every second
     *
     * @return percent
     */
    public int getSpawnChance() {
        return this.spawnChance;
    }

    /**
     * Returns the max amount of items which can be spawned at once
     * @return
     */
    public int getMaxAmount() {
        return this.maxAmount;
    }

    /**
     * Returns the spawnRate from the given name if present
     * @param name name
     * @return spawnRate
     */
    public static Optional<Spawnrate> getSpawnrateFromName(String name) {
        for (final Spawnrate type : Spawnrate.values()) {
            if (type.name().equalsIgnoreCase(name))
                return Optional.of(type);
        }
        return Optional.empty();
    }
}
