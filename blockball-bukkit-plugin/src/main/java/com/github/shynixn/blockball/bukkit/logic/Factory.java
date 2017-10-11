package com.github.shynixn.blockball.bukkit.logic;

import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.persistence.controller.ArenaController;
import com.github.shynixn.blockball.api.persistence.controller.PlayerMetaController;
import com.github.shynixn.blockball.api.persistence.controller.StatsController;
import com.github.shynixn.blockball.bukkit.logic.business.controller.BallRepository;
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository;
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.ArenaRepository;
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.PlayerDataRepository;
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.StatsRepository;
import com.github.shynixn.blockball.lib.ExtensionHikariConnectionContext;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class Factory {

    private static ExtensionHikariConnectionContext connectionContext;

    /**
     * Creates a new playerMetaController to the database
     *
     * @return controller
     */
    public static PlayerMetaController createPlayerDataController() {
        return new PlayerDataRepository(connectionContext);
    }

    /**
     * Creates a new ballController
     *
     * @return ballController
     */
    public static BallController createBallController() {
        return new BallRepository();
    }

    /**
     * Creates a new statsController to the database
     *
     * @return controller
     */
    public static StatsController createStatsController() {
        return new StatsRepository(connectionContext);
    }

    /**
     * Creates a new arena controller to manage persistence cycle of arenas.
     *
     * @param plugin plugin owner of the arenas
     * @return controller
     */
    public static ArenaController createArenaController(Plugin plugin) {
        return new ArenaRepository(plugin);
    }

    /**
     * Creates a new game controller to manage starting and running games.
     *
     * @param controller arenas to be used by the game controller
     * @return controller
     */
    public static GameController createGameController(ArenaController controller) {
        return new GameRepository(controller);
    }

    /**
     * Disables the factory
     */
    public static void disable() {
        if (connectionContext == null)
            return;
        connectionContext.close();
        connectionContext = null;
    }

    /**
     * Initializes the factory with the given plugin
     *
     * @param plugin plugin
     */
    public synchronized static void initialize(Plugin plugin) {
        if (connectionContext != null)
            return;
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        final ExtensionHikariConnectionContext.SQlRetriever retriever = fileName -> {
            try (InputStream stream = plugin.getResource("sql/" + fileName + ".sql")) {
                return IOUtils.toString(stream, "UTF-8");
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot read file.", fileName);
                throw new RuntimeException(e);
            }
        };
        if (!plugin.getConfig().getBoolean("sql.enabled")) {
            try {
                if (!plugin.getDataFolder().exists())
                    plugin.getDataFolder().mkdir();
                final File file = new File(plugin.getDataFolder(), "BlockBall.db");
                if (!file.exists())
                    file.createNewFile();
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.SQLITE_DRIVER, "jdbc:sqlite:" + file.getAbsolutePath(), retriever);
                try (Connection connection = connectionContext.getConnection()) {
                    connectionContext.execute("PRAGMA foreign_keys=ON", connection);
                }
            } catch (final SQLException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute statement.", e);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot read file.", e);
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-sqlite").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute creation.", e);
            }
        } else {
            final FileConfiguration c = plugin.getConfig();
            try {
                connectionContext = ExtensionHikariConnectionContext.from(ExtensionHikariConnectionContext.MYSQL_DRIVER, "jdbc:mysql://"
                        , c.getString("sql.host")
                        , c.getInt("sql.port")
                        , c.getString("sql.database")
                        , c.getString("sql.username")
                        , c.getString("sql.password")
                        , retriever);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot connect to MySQL database!", e);
                Bukkit.getLogger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
                connectionContext = null;
                plugin.getConfig().set("sql.enabled", false);
                Factory.initialize(plugin);
                return;
            }
            try (Connection connection = connectionContext.getConnection()) {
                for (final String data : connectionContext.getStringFromFile("create-mysql").split(Pattern.quote(";"))) {
                    connectionContext.executeUpdate(data, connection);
                }
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot execute creation.", e);
                Bukkit.getLogger().log(Level.WARNING, "Trying to connect to SQLite database....", e);
                connectionContext = null;
                plugin.getConfig().set("sql.enabled", false);
                Factory.initialize(plugin);
            }
        }
    }
}
