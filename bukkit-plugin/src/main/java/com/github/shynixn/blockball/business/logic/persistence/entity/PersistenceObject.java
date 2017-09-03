package com.github.shynixn.blockball.business.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

public abstract class PersistenceObject<T> implements Persistenceable<T> {
    long id;
    /**
     * Returns the id of the object
     *
     * @return id
     */
    @Override
    public long getId() {
        return this.id;
    }

    /**
     * Sets the id of the object
     * @param id id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Clones the current object
     *
     * @return object
     */
    @Override
    public abstract T clone();
}
