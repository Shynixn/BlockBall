@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.context

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.persistence.context.SqlDbContext
import com.google.inject.Inject
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement
import java.util.*
import kotlin.collections.HashMap

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
class SqlDbContextImpl @Inject constructor(
    private val configurationService: ConfigurationService,
    private val loggingService: LoggingService
) : SqlDbContext {
    companion object {
        /**
         * SQLiteDriver classPath.
         */
        const val SQLITE_DRIVER = "org.sqlite.JDBC"

        /**
         * MySQLDriver classPath.
         */
        const val MYSQL_DRIVER = "com.mysql.jdbc.Driver"
    }

    private lateinit var dataSource: HikariDataSource

    init {
        if (!configurationService.findValue<Boolean>("sql.enabled")) {
            connectToSqlite()
        } else {
            try {
                connectToMySql()
            } catch (e: Exception) {
                loggingService.warn("Cannot connect to the MYSQL database!", e)
                loggingService.warn("Fallback mode activated. Using SQLite database instead.")
                connectToSqlite()
            }
        }
    }

    /**
     * Deletes the given [parameters] into the given [connection] [table].
     */
    override fun <C> delete(connection: C, table: String, rowSelection: String, vararg parameters: Pair<String, Any?>) {
        if (connection !is Connection) {
            throw IllegalArgumentException("Connection has to be a Java Connection!")
        }

        val statement = StringBuilder("DELETE FROM ")
            .append(table)
            .append(" ")
            .append(rowSelection)

        val preparedStatement = connection.prepareStatement(statement.toString())

        preparedStatement.use {
            for (i in parameters.indices) {
                preparedStatement.setObject(i + 1, parameters[i].second)
            }

            preparedStatement.executeUpdate()
        }
    }

    /**
     * Updates the given row by the [rowSelection] of the given [table] with the given [parameters].
     * Does not close the connection.
     */
    override fun <C> update(connection: C, table: String, rowSelection: String, vararg parameters: Pair<String, Any?>) {
        if (connection !is Connection) {
            throw IllegalArgumentException("Connection has to be a Java Connection!")
        }

        val statement = StringBuilder("UPDATE ")
            .append(table)
            .append(" SET ")

        parameters.forEach { p ->
            if (!statement.endsWith("SET ")) {
                statement.append(", ")
            }

            statement.append(p.first).append(" = ?")
        }

        statement.append(" ").append(rowSelection)

        val preparedStatement = connection.prepareStatement(statement.toString())

        preparedStatement.use {
            for (i in parameters.indices) {
                preparedStatement.setObject(i + 1, parameters[i].second)
            }

            preparedStatement.executeUpdate()
        }
    }

    /**
     * Inserts the given [parameters] into the given [connection] [table].
     * Gets the created id of the inserted data. Does not close the connection.
     */
    override fun <C> insert(connection: C, table: String, vararg parameters: Pair<String, Any?>): Long {
        if (connection !is Connection) {
            throw IllegalArgumentException("Connection has to be a Java Connection!")
        }

        val statement = StringBuilder("INSERT INTO ")
            .append(table)
            .append(" (")

        parameters.forEach { p ->
            if (!statement.endsWith("(")) {
                statement.append(", ")
            }

            statement.append(p.first)
        }

        statement.append(") VALUES (")

        parameters.forEach {
            if (!statement.endsWith("(")) {
                statement.append(", ")
            }

            statement.append("?")
        }

        statement.append(")")

        val preparedStatement = connection.prepareStatement(statement.toString(), Statement.RETURN_GENERATED_KEYS)

        preparedStatement.use {
            for (i in parameters.indices) {
                preparedStatement.setObject(i + 1, parameters[i].second)
            }

            preparedStatement.executeUpdate()

            preparedStatement.generatedKeys.use { resultSet ->
                resultSet.next()
                return resultSet.getInt(1).toLong()
            }
        }
    }

    /**
     * Creates a query to the database with the given [connection] [sqlStatement] [parameters]. Iterates the
     * result set automatically. Does not close the connection.
     * [R] result type.
     */
    override fun <R, C> multiQuery(
        connection: C,
        sqlStatement: String,
        f: (Map<String, Any>) -> R,
        vararg parameters: Any
    ): List<R> {
        if (connection !is Connection) {
            throw IllegalArgumentException("Connection has to be a Java Connection!")
        }

        val preparedStatement = connection.prepareStatement(sqlStatement)
        val list = ArrayList<R>()

        preparedStatement.use {
            for (i in parameters.indices) {
                preparedStatement.setObject(i + 1, parameters[i])
            }

            val resultSet = preparedStatement.executeQuery()

            resultSet.use {
                while (resultSet.next()) {
                    val metaData = resultSet.metaData
                    val data = HashMap<String, Any>()

                    for (i in 1..metaData.columnCount) {
                        data[metaData.getColumnLabel(i)] = resultSet.getObject(i)
                    }

                    list.add(f.invoke(data))
                }
            }
        }

        return list
    }

    /**
     * Creates a query to the database with the given [connection] [sqlStatement] [parameters]. Iterates the
     * result set automatically. Does not close the connection.
     * [R] result type.
     */
    override fun <R, C> singleQuery(
        connection: C,
        sqlStatement: String,
        f: (Map<String, Any>) -> R,
        vararg parameters: Any
    ): R? {
        if (connection !is Connection) {
            throw IllegalArgumentException("Connection has to be a Java Connection!")
        }

        val preparedStatement = connection.prepareStatement(sqlStatement)

        preparedStatement.use {
            for (i in parameters.indices) {
                preparedStatement.setObject(i + 1, parameters[i])
            }

            val resultSet = preparedStatement.executeQuery()

            resultSet.use {
                while (resultSet.next()) {
                    val metaData = resultSet.metaData
                    val data = HashMap<String, Any>()

                    for (i in 1..metaData.columnCount) {
                        data[metaData.getColumnName(i)] = resultSet.getObject(i)
                    }

                    return f.invoke(data)
                }
            }
        }

        return null
    }

    /**
     * Creates a new transaction to the database.
     * [f] Handles creation and closing the transaction connection automatically and
     * manages connection pools in the background.
     */
    override fun <R, C> transaction(f: (C) -> R): R {
        val con = this.dataSource.connection
        var result: R? = null

        con.use { connection ->
            connection.autoCommit = false

            try {
                result = f.invoke(connection as C)
                connection.commit()
            } catch (e: SQLException) {
                loggingService.error("Failed to execute sql statement.", e)
            }

            connection.autoCommit = true
        }

        return result!!
    }

    /**
     * Closes remaining resources.
     */
    override fun close() {
        if (!this.dataSource.isClosed) {
            this.dataSource.close()
        }
    }

    /**
     * Connects the service to sqlite.
     */
    private fun connectToSqlite() {
        val path = createSQLLiteFile()
        this.dataSource = createDataSource(SQLITE_DRIVER, "jdbc:sqlite:" + path.toAbsolutePath().toString())

        val connection = this.dataSource.connection

        connection.use {
            connection.prepareStatement("PRAGMA foreign_keys=ON").use { statement ->
                statement.execute()
            }

            val tablePrefix = "SHY"

            configurationService.openResource("assets/blockball/sql/create-sqlite.sql").bufferedReader()
                .use { reader ->
                    for (text in reader.readText().replace("TABLE_PREFIX", tablePrefix).split(";")) {
                        connection.prepareStatement(text).use { statement ->
                            statement.execute()
                        }
                    }
                }
        }

        loggingService.info("Connected to " + this.dataSource.jdbcUrl)
    }

    /**
     * Connect to the mysql database.
     */
    private fun connectToMySql() {
        this.dataSource = createDataSource(
            MYSQL_DRIVER,
            "jdbc:mysql://" + configurationService.findValue<String>("sql.host") + ":" + configurationService.findValue<Int>(
                "sql.port"
            ) + "/" + configurationService.findValue<String>(
                "sql.database"
            ),
            configurationService.findValue<String>("sql.username"),
            configurationService.findValue<String>("sql.password"),
            configurationService.findValue("sql.usessl")
        )

        val connection = this.dataSource.connection
        val tablePrefix = "SHY"

        connection.use {
            configurationService.openResource("assets/blockball/sql/create-mysql.sql").bufferedReader()
                .use { reader ->
                    for (text in reader.readText().replace("TABLE_PREFIX", tablePrefix).split(";")) {
                        connection.prepareStatement(text).use { statement ->
                            statement.execute()
                        }
                    }
                }
        }

        loggingService.info("Connected to " + this.dataSource.jdbcUrl)
    }

    /**
     * Creates the sqlLite file.
     */
    private fun createSQLLiteFile(): Path {
        if (!Files.exists(configurationService.applicationDir)) {
            Files.createDirectories(configurationService.applicationDir)
        }

        val path = Paths.get(configurationService.applicationDir.toFile().absolutePath, "BlockBall.db")

        if (!Files.exists(path)) {
            Files.createFile(path)
        }

        return path
    }

    /**
     * Creates a new hikari datasource.
     */
    private fun createDataSource(
        driver: String,
        url: String,
        userName: String? = null,
        password: String? = null,
        useSSL: Boolean = false
    ): HikariDataSource {
        val config = HikariConfig()
        config.connectionTestQuery = "SELECT 1"
        config.jdbcUrl = url

        if (isBukkitServer()) {
            config.driverClassName = driver
        }

        if (userName != null) {
            config.username = userName
        }

        if (password != null) {
            config.password = password
        }

        config.addDataSourceProperty("useSSL", useSSL)
        config.addDataSourceProperty("cachePrepStmts", "true")
        config.addDataSourceProperty("prepStmtCacheSize", "250")
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048")

        if (driver == SQLITE_DRIVER) {
            config.maximumPoolSize = 1
        } else {
            config.maximumPoolSize = 10
        }

        return HikariDataSource(config)
    }

    /**
     * Gets if bukkit server.
     */
    private fun isBukkitServer(): Boolean {
        return try {
            Class.forName("com.github.shynixn.blockball.bukkit.BlockBallPlugin")
            true
        } catch (e: Exception) {
            false
        }
    }
}