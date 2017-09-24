package com.github.shynixn.blockball.api.persistence.entity;

public interface Persistenceable<T> {

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
