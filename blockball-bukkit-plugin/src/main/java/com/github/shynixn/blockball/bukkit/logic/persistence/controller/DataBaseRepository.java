package com.github.shynixn.blockball.bukkit.logic.persistence.controller;

import com.github.shynixn.blockball.api.persistence.controller.IDatabaseController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

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
abstract class DataBaseRepository<T> implements IDatabaseController<T> {
    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(T item) {
        if (this.hasId(item))
            this.update(item);
        else
            this.insert(item);
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(T item) {
        this.delete(item);
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<T> getAll() {
        return Collections.unmodifiableList(this.select());
    }

    /**
     * Checks if the item has got an valid databaseId
     *
     * @param item item
     * @return hasGivenId
     */
    protected abstract boolean hasId(T item);

    /**
     * Selects all items from the database into the list
     *
     * @return listOfItems
     */
    protected abstract List<T> select();

    /**
     * Updates the item inside of the database
     *
     * @param item item
     */
    protected abstract void update(T item);

    /**
     * Deletes the item from the database
     *
     * @param item item
     */
    protected abstract void delete(T item);

    /**
     * Inserts the item into the database and sets the id
     *
     * @param item item
     */
    protected abstract void insert(T item);

    /**
     * Generates the entity from the given resultSet
     *
     * @param resultSet resultSet
     * @return entity
     * @throws SQLException exception
     */
    protected abstract T from(ResultSet resultSet) throws SQLException;
}