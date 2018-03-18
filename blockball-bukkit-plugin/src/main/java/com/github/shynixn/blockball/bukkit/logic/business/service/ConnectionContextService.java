package com.github.shynixn.blockball.bukkit.logic.business.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

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
@Singleton
public class ConnectionContextService implements AutoCloseable {
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private HikariDataSource ds;
    private SQlRetriever retriever;
    private Map<String, String> cache = new HashMap<>();

    @Inject
    public ConnectionContextService(Plugin plugin) {
        super();
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.retriever = fileName -> {
            try (InputStream stream = plugin.getResource("sql/" + fileName + ".sql")) {
                return IOUtils.toString(stream, "UTF-8");
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot read file.", fileName);
                throw new RuntimeException(e);
            }
        };
        this.connectInternal(plugin);
    }

    private void connectInternal(Plugin plugin) {
        if (!plugin.getConfig().getBoolean("sql.enabled")) {
            try {
                if (!plugin.getDataFolder().exists())
                    plugin.getDataFolder().mkdir();
                final File file = new File(plugin.getDataFolder(), "BlockBall.db");
                if (!file.exists())
                    file.createNewFile();
                this.enableData(ConnectionContextService.SQLITE_DRIVER, "jdbc:sqlite:" + file.getAbsolutePath(), null, null, this.retriever);
                try (Connection connection = this.getConnection()) {
                    this.execute("PRAGMA foreign_keys=ON", connection);
                }
            } catch (final SQLException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute statement.", e);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot read file.", e);
            }
            try (Connection connection = this.getConnection()) {
                for (final String data : this.getStringFromFile("create-sqlite").split(Pattern.quote(";"))) {
                    this.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute creation.", e);
            }
        } else {
            final FileConfiguration c = plugin.getConfig();
            try {
                this.enableData(ConnectionContextService.MYSQL_DRIVER, "jdbc:mysql://"
                        , c.getString("sql.host")
                        , c.getInt("sql.port")
                        , c.getString("sql.database")
                        , c.getString("sql.username")
                        , c.getString("sql.password")
                        , this.retriever);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot connect to MySQL database!", e);
                Bukkit.getLogger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
                plugin.getConfig().set("sql.enabled", false);
                this.connectInternal(plugin);
                return;
            }
            try (Connection connection = this.getConnection()) {
                for (final String data : this.getStringFromFile("create-mysql").split(Pattern.quote(";"))) {
                    this.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute creation.", e);
                Bukkit.getLogger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
                plugin.getConfig().set("sql.enabled", false);
                this.connectInternal(plugin);
            }
        }
    }

    private void enableData(String driver, String urlPrefix, String ip, int port, String database, String userName, String password, SQlRetriever retriever) throws IOException {
        this.enableData(driver, urlPrefix + ip + ':' + port + '/' + database, userName, password, retriever);
    }

    private void enableData(String driver, String url, String userName, String password, SQlRetriever retriever) throws IOException {
        this.retriever = retriever;
        final HikariConfig config = new HikariConfig();
        config.setDriverClassName(driver);
        config.setConnectionTestQuery("SELECT 1");
        config.setJdbcUrl(url);
        if (userName != null)
            config.setUsername(userName);
        if (password != null)
            config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        if (driver.equals(SQLITE_DRIVER))
            config.setMaximumPoolSize(1);
        else
            config.setMaximumPoolSize(10);
        this.ds = new HikariDataSource(config);
        Logger.getLogger(this.getClass().getSimpleName()).log(Level.INFO, "Connected to " + url);
    }

    /**
     * Provides an connection to the database which has to be closed after being used
     *
     * @return connection
     * @throws SQLException exception
     */
    public Connection getConnection() throws SQLException {
        return this.ds.getConnection();
    }

    /**
     * Sets the parameters of the preparedStatement
     *
     * @param preparedStatement preparedStatement
     * @param parameters        parameters
     * @throws SQLException exception
     */
    private void setParameters(PreparedStatement preparedStatement, Object[] parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
    }

    /**
     * Reads a sqlStatement from the given fileName, executes it and sets the given parameter
     *
     * @param fileName   fileName
     * @param connection connection
     * @param parameters parameters
     * @return success
     * @throws SQLException exception
     */
    public boolean executeStored(String fileName, Connection connection, Object... parameters) throws SQLException {
        if (fileName == null)
            throw new IllegalArgumentException("FileName cannot be null!");
        return this.execute(this.getStringFromFile(fileName), connection, parameters);
    }

    /**
     * Executes a preparedStatement and sets the given parameters to the statement
     *
     * @param sql        sql
     * @param connection connection
     * @param parameters parameters
     * @return success
     * @throws SQLException exception
     */
    public boolean execute(String sql, Connection connection, Object... parameters) throws SQLException {
        if (sql == null)
            throw new IllegalArgumentException("Sql cannot be null!");
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null!");
        if (connection.isClosed())
            throw new IllegalArgumentException("Connection is closed. Cannot create statement!");
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            this.setParameters(preparedStatement, parameters);
            return preparedStatement.execute();
        }
    }

    /**
     * Reads a sqlStatement from the given fileName, executes it and sets the given parameter
     *
     * @param fileName   fileName
     * @param connection connection
     * @param parameters parameters
     * @return resultSet
     * @throws SQLException exception
     */
    public PreparedStatement executeStoredQuery(String fileName, Connection connection, Object... parameters) throws SQLException {
        if (fileName == null)
            throw new IllegalArgumentException("FileName cannot be null!");
        return this.executeQuery(this.getStringFromFile(fileName), connection, parameters);
    }

    /**
     * Executes a preparedStatement and sets the given parameters to the statement
     *
     * @param sql        sql
     * @param connection connection
     * @param parameters parameters
     * @return resultSet
     * @throws SQLException exception
     */
    public PreparedStatement executeQuery(String sql, Connection connection, Object... parameters) throws SQLException {
        if (sql == null)
            throw new IllegalArgumentException("Sql cannot be null!");
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null!");
        if (connection.isClosed())
            throw new IllegalArgumentException("Connection is closed. Cannot create statement!");
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        this.setParameters(preparedStatement, parameters);
        return preparedStatement;
    }

    /**
     * Reads a sqlStatement from the given fileName, executes it and sets the given parameter
     *
     * @param fileName   fileName
     * @param connection connection
     * @param parameters parameters
     * @return resultCode
     * @throws SQLException exception
     */
    public int executeStoredUpdate(String fileName, Connection connection, Object... parameters) throws SQLException {
        if (fileName == null)
            throw new IllegalArgumentException("FileName cannot be null!");
        return this.executeUpdate(this.getStringFromFile(fileName), connection, parameters);
    }

    /**
     * Executes a preparedStatement and sets the given parameters to the statement
     *
     * @param sql        sql
     * @param connection connection
     * @param parameters parameters
     * @return resultCode
     * @throws SQLException exception
     */
    public int executeUpdate(String sql, Connection connection, Object... parameters) throws SQLException {
        if (sql == null)
            throw new IllegalArgumentException("Sql cannot be null!");
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null!");
        if (connection.isClosed())
            throw new IllegalArgumentException("Connection is closed. Cannot create statement!");
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            this.setParameters(preparedStatement, parameters);
            return preparedStatement.executeUpdate();
        }
    }

    /**
     * Reads a sqlStatement from the given fileName, executes it and sets the given parameter
     *
     * @param fileName   fileName
     * @param connection connection
     * @param parameters parameters
     * @return id
     * @throws SQLException exception
     */
    public int executeStoredInsert(String fileName, Connection connection, Object... parameters) throws SQLException {
        if (fileName == null)
            throw new IllegalArgumentException("FileName cannot be null!");
        return this.executeInsert(this.getStringFromFile(fileName), connection, parameters);
    }

    /**
     * Executes a preparedStatement and sets the given parameters to the statement
     *
     * @param sql        sql
     * @param connection connection
     * @param parameters parameters
     * @return id
     * @throws SQLException exception
     */
    public int executeInsert(String sql, Connection connection, Object... parameters) throws SQLException {
        if (sql == null)
            throw new IllegalArgumentException("Sql cannot be null!");
        if (connection == null)
            throw new IllegalArgumentException("Connection cannot be null!");
        if (connection.isClosed())
            throw new IllegalArgumentException("Connection is closed. Cannot create statement!");
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            this.setParameters(preparedStatement, parameters);
            preparedStatement.executeUpdate();
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                resultSet.next();
                return resultSet.getInt(1);
            }
        }
    }

    /**
     * Closes the database context
     */
    @Override
    public void close() {
        if (this.ds != null && !this.ds.isClosed()) {
            this.ds.close();
            this.ds = null;
        }
        if (this.cache != null) {
            this.cache.clear();
            this.cache = null;
        }
    }

    /**
     * Manages caching of statements
     *
     * @param statementName statementName
     * @return sqlStatement
     */
    private String getStringFromFile(String statementName) {
        if (statementName == null)
            throw new IllegalArgumentException("Statement cannot be null!");
        if (!this.cache.containsKey(statementName)) {
            this.cache.put(statementName, this.retriever.loadSqlStatement(statementName));
        }
        return this.cache.get(statementName);
    }

    @FunctionalInterface
    public interface SQlRetriever {
        /**
         * Loads a sqlStatement from the givenFile
         *
         * @param fileName fileName
         * @return sqlStatement
         */
        String loadSqlStatement(String fileName);
    }
}