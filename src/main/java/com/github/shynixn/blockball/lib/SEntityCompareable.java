package com.github.shynixn.blockball.lib;

import org.bukkit.entity.Entity;

@Deprecated
@FunctionalInterface
public interface SEntityCompareable {
    boolean isSameEntity(Entity entity);
}
