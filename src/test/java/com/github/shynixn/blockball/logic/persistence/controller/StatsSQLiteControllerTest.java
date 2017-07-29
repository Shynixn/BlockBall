package com.github.shynixn.blockball.logic.persistence.controller;

import com.github.shynixn.blockball.api.persistence.controller.PlayerMetaController;
import com.github.shynixn.blockball.api.persistence.controller.StatsController;
import com.github.shynixn.blockball.api.persistence.entity.PlayerMeta;
import com.github.shynixn.blockball.api.persistence.entity.Stats;
import com.github.shynixn.blockball.business.logic.persistence.Factory;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatsSQLiteControllerTest {

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
        if (Bukkit.getServer() == null)
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

    @BeforeAll
    public static void disableFactory() {
        Factory.disable();
    }

    @Test
    public void insertSelectStatsTest() throws ClassNotFoundException {
        Factory.initialize(mockPlugin());
        final UUID uuid = UUID.randomUUID();
        final Player player = mock(Player.class);
        when(player.getName()).thenReturn("Shynixn");
        when(player.getUniqueId()).thenReturn(uuid);
        try (StatsController controller = Factory.createStatsController()) {
            try (PlayerMetaController playerController = Factory.createPlayerDataController()) {
                for (final Stats item : controller.getAll()) {
                    controller.remove(item);
                }
                final Stats meta = controller.create();
                controller.store(meta);
                assertEquals(0, controller.size());

                final PlayerMeta playerMeta = playerController.create(player);
                playerController.store(playerMeta);
                meta.setPlayerId(playerMeta.getId());
                meta.setAmountOfWins(2);

                controller.store(meta);
                assertEquals(0, controller.size());

                meta.setAmountOfGamesPlayed(2);
                controller.store(meta);

                assertEquals(1, controller.size());
                assertEquals(2, controller.getByPlayer(player).getAmountOfWins());
            }
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assertions.fail(e);
        }
    }

    @Test
    public void storeLoadPetMetaTest() throws ClassNotFoundException {
        Factory.initialize(mockPlugin());
        final UUID uuid = UUID.randomUUID();
        final Player player = mock(Player.class);
        when(player.getName()).thenReturn("Shynixn");
        when(player.getUniqueId()).thenReturn(uuid);
        try (StatsController controller = Factory.createStatsController()) {
            try (PlayerMetaController playerController = Factory.createPlayerDataController()) {
                for (final Stats item : controller.getAll()) {
                    controller.remove(item);
                }
                Stats stats = controller.create();

                final PlayerMeta playerMeta = playerController.create(player);
                playerController.store(playerMeta);

                stats.setPlayerId(playerMeta.getId());
                stats.setAmountOfGamesPlayed(5);
                stats.setAmountOfWins(2);
                stats.setAmountOfGoals(20);

                controller.store(stats);

                stats = controller.getByPlayer(player);
                assertEquals(5, stats.getAmountOfGamesPlayed());
                assertEquals(2, stats.getAmountOfWins());
                assertEquals(20, stats.getAmountOfGoals());

                assertEquals(4, stats.getGoalsPerGame());
                assertEquals(0.4, stats.getWinRate());
            }
        } catch (final Exception e) {
            Logger.getLogger(this.getClass().getSimpleName()).log(Level.WARNING, "Failed to run test.", e);
            Assertions.fail(e);
        }
    }
}
