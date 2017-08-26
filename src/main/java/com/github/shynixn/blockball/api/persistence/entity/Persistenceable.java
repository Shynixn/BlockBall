package com.github.shynixn.blockball.api.persistence.entity;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public interface Persistenceable<T> extends ConfigurationSerializable{

    /**
     * Returns the id of the object
     *
     * @return id
     */
    long getId();

    /**
     * Clones the current object
     *
     * @return object
     */
    T clone();
}
