package com.github.shynixn.blockball.bukkit.logic.persistence.controller

import com.github.shynixn.blockball.api.persistence.controller.DatabaseController
import com.github.shynixn.blockball.api.persistence.entity.PersistenceAble
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ConnectionContextService
import org.bukkit.Bukkit
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.*
import java.util.logging.Level

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
abstract class DatabaseRepository<T>(protected val dbContext: ConnectionContextService, private val folder: String) : DatabaseController<T> where T : PersistenceAble {


    /** Stores a new item into the repository. */
    override fun store(item: T) {
        if(item == null)
            throw IllegalArgumentException("Item cannot be null!")
        if (this.hasId(item)) {
            this.update(item)
        } else {
            this.insert(item)
        }
    }

    /** Removes an item from the repository. */
    override fun remove(item: T) {
        this.delete(item)
    }

    /** Returns all items from the repository. */
    override fun getAll(): List<T> {
        return Collections.unmodifiableList(this.select())
    }

    /**
     * Checks if the item has got an valid databaseId.
     *
     * @param item item
     * @return hasGivenId
     */
    private fun hasId(item: T): Boolean {
        return item.id != 0L
    }

    /** Returns the item by the given database id. */
    override fun getById(id: Int): Optional<T> {
        try {
            this.dbContext.connection.use({ connection: Connection ->
                this.dbContext.executeStoredQuery("$folder/selectbyid", connection,
                        id).use({ preparedStatement ->
                    preparedStatement.executeQuery().use({ resultSet ->
                        if (resultSet.next()) {
                            return Optional.of(this.from(resultSet))
                        }
                    })
                })
            })
        } catch (e: SQLException) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e)
        }
        return Optional.empty()
    }

    /** Returns the amount of items in the repository. */
    override val count: Int
        get() {
            try {
                this.dbContext.connection.use { connection ->
                    this.dbContext.executeStoredQuery("$folder/count", connection).use { preparedStatement ->
                        preparedStatement.executeQuery().use { resultSet ->
                            resultSet.next()
                            return resultSet.getInt(1)
                        }
                    }
                }
            } catch (e: SQLException) {
                Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e)
            }
            return 0
        }

    /**
     * Selects all items from the database into the list.
     *
     * @return listOfItems
     */
    protected fun select(): List<T> {
        val statsList = ArrayList<T>()
        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredQuery("$folder/selectall", connection).use { preparedStatement ->
                    preparedStatement.executeQuery().use { resultSet ->
                        while (resultSet.next()) {
                            val stats = this.from(resultSet)
                            statsList.add(stats)
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e)
        }
        return statsList
    }


    /**
     * Deletes the item from the database.
     *
     * @param item item
     */
    protected fun delete(item: T){
        try {
            this.dbContext.connection.use { connection ->
                this.dbContext.executeStoredUpdate("$folder/delete", connection,
                        item.id)
            }
        } catch (e: SQLException) {
            Bukkit.getLogger().log(Level.WARNING, "Database error occurred.", e)
        }
    }

    /**
     * Updates the item inside of the database.
     *
     * @param item item
     */
    protected abstract fun update(item: T)

    /**
     * Inserts the item into the database and sets the id.
     *
     * @param item item
     */
    protected abstract fun insert(item: T)

    /**
     * Generates the entity from the given resultSet.
     *
     * @param resultSet resultSet
     * @return entity
     * @throws SQLException exception
     */
    @Throws(SQLException::class)
    protected abstract fun from(resultSet: ResultSet): T

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their `close` methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    override fun close() {
    }
}