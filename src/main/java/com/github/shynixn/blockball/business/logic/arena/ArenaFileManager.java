package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.Arena;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class ArenaFileManager {
    private static final Arena[] A = new Arena[0];
    private final JavaPlugin plugin;

    ArenaFileManager(JavaPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    private File getFolder() {
        if (this.plugin == null)
            throw new IllegalStateException("Plugin cannot be null!");
        final File file = new File(this.plugin.getDataFolder(), "arena");
        if (!file.exists()) {
            if (!file.mkdir())
                throw new IllegalStateException("Cannot create folder!");
        }
        return file;
    }

    void save(Player player, int number) {
        try {
            final File file = new File(this.plugin.getDataFolder(), "goals.yml");
            if (!file.exists()) {
                if (!file.createNewFile())
                    throw new IllegalStateException("Cannot create file!");
            }
            final FileConfiguration config = new YamlConfiguration();
            config.load(file);
            config.set("players." + player.getUniqueId() + ".name", player.getName());
            config.set("players." + player.getUniqueId() + ".amount", number);
            config.save(file);
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, ex.getMessage());
        }
    }

    Map<String, Integer> getTopTenPlayers() {
        try {
            final File file = new File(this.plugin.getDataFolder(), "goals.yml");
            if (!file.exists()) {
                if (!file.createNewFile())
                    throw new IllegalStateException("Cannot create file!");
            }
            final FileConfiguration config = new YamlConfiguration();
            config.load(file);
            final Map<String, Integer> map = new HashMap<>();
            if (config.getConfigurationSection("players") == null)
                return map;
            for (final String item : config.getConfigurationSection("players").getKeys(false)) {
                map.put(config.getString("players." + item + ".name"), config.getInt("players." + item + ".amount"));
            }
            final List<Map.Entry<String, Integer>> map2 = new ArrayList<>();
            final Map<String, Integer> resultMap = new HashMap<>();
            int i = 0;
            for (final Map.Entry<String, Integer> s : map2) {
                if (i < 10)
                    resultMap.put(s.getKey(), s.getValue());
                i++;
            }
            return resultMap;
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    Integer getNumber(Player player) {
        try {
            final File file = new File(this.plugin.getDataFolder(), "goals.yml");
            if (!file.exists()) {
                if (!file.createNewFile())
                    throw new IllegalStateException("Cannot create file!");
            }
            final FileConfiguration config = new YamlConfiguration();
            config.load(file);
            return config.getInt("players." + player.getUniqueId().toString() + ".amount");
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, ex.getMessage());
        }
        return null;
    }

    boolean save(Arena item) {
        if (item != null && item.getName() != null) {
            try {
                final FileConfiguration configuration = new YamlConfiguration();
                final File file = new File(this.getFolder(), "arena_" + item.getName() + ".yml");
                if (file.exists()) {
                    if (!file.delete())
                        throw new IllegalStateException("Cannot delete file!");
                }
                if (!file.createNewFile())
                    throw new IllegalStateException("Cannot create file!");
                configuration.load(file);
                final Map<String, Object> data = item.serialize();
                for (final String key : data.keySet()) {
                    configuration.set("arena." + key, data.get(key));
                }
                configuration.save(file);
            } catch (IOException | InvalidConfigurationException ex) {
                Bukkit.getLogger().log(Level.WARNING,"Cannot save arena." ,ex.getMessage());
            }
        }
        return true;
    }

    boolean delete(Arena item) {
        try {
            final File file = new File(this.getFolder(), "arena_" + item.getName() + ".yml");
            if (file.exists()) {
                if (!file.delete())
                    throw new IllegalStateException("Cannot delete file!");
            }
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot delete arena file.", ex);
        }
        return false;
    }

    Arena[] load() {
        final List<Arena> items = new ArrayList<>();
        for (int i = 0; (this.getFolder() != null) && (i < this.getFolder().list().length); i++) {
            final String s = this.getFolder().list()[i];
            try {
                if (s.contains("arena_")) {
                    final FileConfiguration configuration = new YamlConfiguration();
                    final File file = new File(this.getFolder(), s);
                    configuration.load(file);
                    final Map<String, Object> data = configuration.getConfigurationSection("arena").getValues(true);
                    final Arena arenaEntity = new ArenaEntity(data, configuration.getStringList("arena.properties.wall-bouncing"));
                    items.add(arenaEntity);
                }
            } catch (final Exception ex) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot read arena file " + s + ".", ex);
            }
        }
        return items.toArray(A);
    }
}
