package com.github.shynixn.blockball.lib;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface SEntityCompareable {
    boolean isSameEntity(Entity entity);
}
