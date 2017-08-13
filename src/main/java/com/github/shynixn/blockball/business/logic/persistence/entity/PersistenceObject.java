package com.github.shynixn.blockball.business.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.Persistenceable;

public class PersistenceObject implements Persistenceable {
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
}
