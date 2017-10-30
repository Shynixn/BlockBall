package com.github.shynixn.blockball.bukkit.logic.persistence.controller;

import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController;
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordSign;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BungeeCordSignData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
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
public class BungeeCordSignRepository implements BungeeCordSignController {
    private final List<BungeeCordSign> signs = new ArrayList<>();
    private final Plugin plugin;

    /**
     * Initializes a new repository for signs.
     *
     * @param plugin plugin
     */
    public BungeeCordSignRepository(Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    /**
     * Creates a new bungeecord sign from the given server and location.
     *
     * @param server   server
     * @param location location
     * @return bungeeCordSign
     */
    @Override
    public BungeeCordSign create(String server, Object location) {
        return new BungeeCordSignData((Location) location, server);
    }

    /**
     * Returns a set of all servers mentioned on signs.
     *
     * @return servers
     */
    @Override
    public Set<String> getAllServers() {
        final Set<String> server = new HashSet<>();
        for (final BungeeCordSign signInfo : this.getAll()) {
            server.add(signInfo.getServer());
        }
        return server;
    }

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(BungeeCordSign item) {
        if (!this.signs.contains(item)) {
            this.signs.add(item);
        }
        this.saveSignsAsynchronly();
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(BungeeCordSign item) {
        if (this.signs.contains(item)) {
            this.signs.remove(item);
        }
        this.saveSignsAsynchronly();
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.signs.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<BungeeCordSign> getAll() {
        return Collections.unmodifiableList(this.signs);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.signs.clear();
    }

    /**
     * Stores the signs asynchronly to the fileSystem.
     */
    private void saveSignsAsynchronly() {
        final BungeeCordSign[] signs = this.getAll().toArray(new BungeeCordSign[this.signs.size()]);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final FileConfiguration configuration = new YamlConfiguration();
                final File file = new File(this.plugin.getDataFolder(), "bungeecord_signs.yml");
                if (file.exists()) {
                    if (!file.delete()) {
                        Bukkit.getLogger().log(Level.WARNING, "File cannot get deleted.");
                    }
                }
                for (int i = 0; i < signs.length; i++) {
                    configuration.set("signs." + i, ((ConfigurationSerializable) signs[i]).serialize());
                }
                configuration.save(file);
            } catch (final IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Save sign location.", e);
            }
        });
    }

    /**
     * Reloads the content from the fileSystem
     */
    @Override
    public void reload() {
        try {
            final FileConfiguration configuration = new YamlConfiguration();
            final File file = new File(this.plugin.getDataFolder(), "bungeecord_signs.yml");
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    BlockBallPlugin.logger().log(Level.WARNING, "File cannot get created.");
                }
            }
            configuration.load(file);
            if (configuration.getConfigurationSection("signs") != null) {
                final Map<String, Object> data = configuration.getConfigurationSection("signs").getValues(false);
                for (final String s : data.keySet()) {
                    this.signs.add(new BungeeCordSignData(((ConfigurationSection) data.get(s)).getValues(true)));
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            BlockBallPlugin.logger().log(Level.WARNING, "Save load location.", e);
        }
    }
}
