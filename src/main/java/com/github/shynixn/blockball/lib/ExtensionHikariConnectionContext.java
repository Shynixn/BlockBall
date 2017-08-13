package com.github.shynixn.blockball.lib;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class ExtensionHikariConnectionContext implements AutoCloseable {
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    private HikariDataSource ds;
    private final SQlRetriever retriever;
    private Map<String, String> cache = new HashMap<>();

    /**
     * Initializes a new databaseConnectionContext for the given url, userName and password
     *
     * @param url      url
     * @param userName userName
     * @param password password
     */
    private ExtensionHikariConnectionContext(String driver, String url, String userName, String password, SQlRetriever retriever) {
        super();
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
    public String getStringFromFile(String statementName) {
        if (statementName == null)
            throw new IllegalArgumentException("Statement cannot be null!");
        if (!this.cache.containsKey(statementName)) {
            this.cache.put(statementName, this.retriever.loadSqlStatement(statementName));
        }
        return this.cache.get(statementName);
    }

    /**
     * Opens a new DatabaseContext for the given url
     *
     * @param driver    driver
     * @param url       url
     * @param retriever retriever
     * @return DbConnectionContext
     * @throws IOException exception
     */
    public static ExtensionHikariConnectionContext from(String driver, String url, SQlRetriever retriever) throws IOException {
        if (driver == null)
            throw new IllegalArgumentException("Driver cannot be null!");
        if (url == null)
            throw new IllegalArgumentException("Database cannot be null!");
        if (retriever == null)
            throw new IllegalArgumentException("Retriever cannot be null!");
        try {
            Class.forName(driver);
            return new ExtensionHikariConnectionContext(driver, url, null, null, retriever);
        } catch (final ClassNotFoundException ex) {
            Logger.getLogger(ExtensionHikariConnectionContext.class.getSimpleName()).log(Level.WARNING, "JDBC Driver not found!");
            throw new IOException(ex);
        } catch (final Exception ex) {
            Logger.getLogger(ExtensionHikariConnectionContext.class.getSimpleName()).log(Level.WARNING, "Cannot connect to database!");
            throw new IOException(ex);
        }
    }

    /**
     * Opens a new DatabaseContext for the given ip, port, database, username and password
     *
     * @param driver    driver
     * @param ip        ip
     * @param port      port
     * @param urlPrefix prefix
     * @param database  database
     * @param userName  userName
     * @param password  password
     * @param retriever retriever
     * @return DbConnectionContext
     * @throws IOException exception
     */
    public static ExtensionHikariConnectionContext from(String driver, String urlPrefix, String ip, int port, String database, String userName, String password, SQlRetriever retriever) throws IOException {
        if (driver == null)
            throw new IllegalArgumentException("Driver cannot be null!");
        if (ip == null)
            throw new IllegalArgumentException("Ip cannot be null!");
        if (database == null)
            throw new IllegalArgumentException("Database cannot be null!");
        if (userName == null)
            throw new IllegalArgumentException("Username cannot be null!");
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null!");
        if (retriever == null)
            throw new IllegalArgumentException("Retriever cannot be null!");
        try {
            Class.forName(driver);
            return new ExtensionHikariConnectionContext(driver, urlPrefix + ip + ':' + port + '/' + database, userName, password, retriever);
        } catch (final ClassNotFoundException ex) {
            Logger.getLogger(ExtensionHikariConnectionContext.class.getSimpleName()).log(Level.WARNING, "JDBC Driver not found!");
            throw new IOException(ex);
        } catch (final Exception ex) {
            Logger.getLogger(ExtensionHikariConnectionContext.class.getSimpleName()).log(Level.WARNING, "Cannot connect to database!");
            throw new IOException(ex);
        }
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