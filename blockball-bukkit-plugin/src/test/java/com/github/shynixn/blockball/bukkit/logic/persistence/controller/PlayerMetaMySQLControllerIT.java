package com.github.shynixn.blockball.bukkit.logic.persistence.controller;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.ConnectionContextService;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PlayerData;
import com.github.shynixn.blockball.bukkit.logic.persistence.repository.PlayerSqlRepository;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings({"HardCodedStringLiteral", "Duplicates", "NestedTryStatement"})
public class PlayerMetaMySQLControllerIT {

    private static Plugin mockPlugin() {
        final YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("sql.enabled", false);
        configuration.set("sql.host", "localhost");
        configuration.set("sql.port", 3306);
        configuration.set("sql.database", "db");
        configuration.set("sql.username", "root");
        configuration.set("sql.password", "");
        final Plugin plugin = mock(Plugin.class);
        final Server server = mock(Server.class);
        when(server.getLogger()).thenReturn(Logger.getGlobal());
        if(Bukkit.getServer() == null)
            Bukkit.setServer(server);
        new File("BlockBall/BlockBall.db").delete();
        when(plugin.getDataFolder()).thenReturn(new File("BlockBall"));
        when(plugin.getConfig()).thenReturn(configuration);
        when(plugin.getResource(any(String.class))).thenAnswer(invocationOnMock -> {
            final String file = invocationOnMock.getArgument(0);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
        });
        return plugin;
    }


    private static DB database;

    @AfterAll
    public static void stopMariaDB()
    {
        try {
            database.stop();
        } catch (final ManagedProcessException e) {
            Logger.getLogger(PlayerMetaMySQLControllerIT.class.getSimpleName()).log(Level.WARNING, "Failed to stop mariadb.", e);
        }
    }

    @BeforeAll
    public static void startMariaDB() {
        try {
            database = DB.newEmbeddedDB(3306);
            database.start();
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=")) {
                try (Statement statement = conn.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE db");
                }
            }
        } catch (SQLException | ManagedProcessException e) {
            Logger.getLogger(PlayerMetaMySQLControllerIT.class.getSimpleName()).log(Level.WARNING, "Failed to start mariadb.", e);
        }
    }

    @Test
    public void insertSelectPlayerMetaTest() throws ClassNotFoundException {
        final Plugin plugin = mockPlugin();
        plugin.getConfig().set("sql.enabled", true);
        final ConnectionContextService connectionContextService = new ConnectionContextService(plugin);
        try (PlayerSqlRepository controller = new PlayerSqlRepository(connectionContextService)) {
            for (final PlayerMeta<? extends Player> item : controller.getAll()) {
                controller.remove(item);
            }
            final UUID uuid = UUID.randomUUID();
            final PlayerMeta playerMeta = new PlayerData();

            assertThrows(IllegalArgumentException.class, () -> controller.store(playerMeta));
            assertEquals(0, controller.getCount());

            playerMeta.setUuid(uuid);
            controller.store(playerMeta);
            assertEquals(0, controller.getCount());

            playerMeta.setName("Sample");
            controller.store(playerMeta);
            assertEquals(1, controller.getCount());
            assertEquals(uuid, controller.getById((int) playerMeta.getId()).get().getUuid());
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assertions.fail(e);
        }
    }


    @Test
    public void storeLoadPlayerMetaTest() throws ClassNotFoundException {
        final Plugin plugin = mockPlugin();
        plugin.getConfig().set("sql.enabled", true);
        final ConnectionContextService connectionContextService = new ConnectionContextService(plugin);
        try (PlayerSqlRepository controller = new PlayerSqlRepository(connectionContextService)) {
            for (final PlayerMeta item : controller.getAll()) {
                controller.remove(item);
            }
            UUID uuid = UUID.randomUUID();
            PlayerMeta playerMeta = new PlayerData();
            playerMeta.setName("Second");
            playerMeta.setUuid(uuid);
            controller.store(playerMeta);

            assertEquals(1, controller.getCount());
            playerMeta = controller.getAll().get(0);
            assertEquals(uuid, playerMeta.getUuid());
            assertEquals("Second", playerMeta.getName());

            uuid = UUID.randomUUID();
            playerMeta.setName("Shynixn");
            playerMeta.setUuid(uuid);
            controller.store(playerMeta);

            playerMeta = controller.getAll().get(0);
            assertEquals(uuid, playerMeta.getUuid());
            assertEquals("Shynixn", playerMeta.getName());
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assertions.fail(e);
        }
    }
}
