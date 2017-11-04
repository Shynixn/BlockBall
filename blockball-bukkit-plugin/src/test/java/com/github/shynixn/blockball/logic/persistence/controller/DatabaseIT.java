package com.github.shynixn.blockball.logic.persistence.controller;

import ch.vorburger.mariadb4j.DB;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseIT {

    @Test
    public void enableDatabaseSQLiteTest() {
        try {
            final HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            config.setConnectionTestQuery("SELECT 1");
            config.setJdbcUrl("jdbc:sqlite:PetBlocks.db");
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
            config.setMaximumPoolSize(50);
            final HikariDataSource ds = new HikariDataSource(config);
            ds.close();
        } catch (final Exception ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to enable database.", ex);
            Assertions.fail(ex);
        }
    }

    @Test
    public void enableDatabaseMySQLTest() {
        try {
            final DB database = DB.newEmbeddedDB(3306);
            database.start();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=")) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE db");
                }
            }

            final HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.jdbc.Driver");
            config.setConnectionTestQuery("SELECT 1");
            config.setJdbcUrl("jdbc:mysql://localhost:3306/db");
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
            config.setMaximumPoolSize(50);
            final HikariDataSource ds = new HikariDataSource(config);
            ds.close();
            database.stop();
        } catch (final Exception ex) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to enable database.", ex);
            Assertions.fail(ex);
        }
    }
}
