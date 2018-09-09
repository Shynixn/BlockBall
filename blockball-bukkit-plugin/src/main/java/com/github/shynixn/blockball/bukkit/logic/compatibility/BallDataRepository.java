package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.compatibility.BallMeta;
import com.github.shynixn.blockball.api.compatibility.BallMetaController;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class BallDataRepository implements BallMetaController {

    private final Plugin plugin;
    private final String fileName;
    private final List<BallMeta> items = new ArrayList<>();

    /**
     * Initializes a new ball data repository where to store ball meta data into the given file.
     *
     * @param plugin   plugin
     * @param fileName fileName
     */
    public BallDataRepository(Plugin plugin, String fileName) {
        super();
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        if (fileName == null)
            throw new IllegalArgumentException("FileName cannot be null!");
        this.plugin = plugin;
        this.fileName = fileName;
    }

    /**
     * Creates a new ballMeta wih the given skin.
     *
     * @param skin skin
     * @return ballMeta
     */
    @Override
    public BallMeta create(String skin) {
        return new BallData(skin);
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(BallMeta item) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null!");
        if (!this.items.contains(item)) {
            this.items.add(item);
        }
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(BallMeta item) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null!");
        if (this.items.contains(item)) {
            this.items.remove(item);
        }
    }

    /**
     * Saves all stored items into the file asynchronly.
     */
    @Override
    public void persist() {
        final BallMeta[] items = this.items.toArray(new BallMeta[this.items.size()]);
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                synchronized (this.fileName) {
                    final File storage = new File(this.plugin.getDataFolder(), this.fileName);
                    final FileConfiguration configuration = new YamlConfiguration();
                    configuration.load(storage);
                    for (int i = 0; i < items.length; i++) {
                        final ConfigurationSerializable serializable = (ConfigurationSerializable) items[i];
                        configuration.set("meta." + (i + 1) + ".ball", serializable.serialize());
                        configuration.save(storage);
                    }
                }
            } catch (IOException | InvalidConfigurationException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to save meta.", ex);
            }
        });
    }

    /**
     * Reloads the content from the fileSystem.
     */
    @Override
    public void reload() {
        this.items.clear();
        try {
            synchronized (this.fileName) {
                final File storage = new File(this.plugin.getDataFolder(), this.fileName);
                final FileConfiguration configuration = new YamlConfiguration();
                configuration.load(storage);
                final Map<String, Object> data = ((MemorySection) configuration.get("meta")).getValues(false);
                for (final String key : data.keySet()) {
                    try {
                        final BallMeta ballMeta = new BallData(((MemorySection) ((MemorySection) data.get(key)).get("ball")).getValues(true));
                        this.items.add(ballMeta);
                    } catch (final Exception e) {
                        this.plugin.getLogger().log(Level.WARNING, "Failed to load meta " + key + '.', e);
                    }
                }
            }
        } catch (IOException | InvalidConfigurationException ex) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to load meta.", ex);
        }
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    @Override
    public int size() {
        return this.items.size();
    }

    /**
     * Clears all items in the repository.
     */
    @Override
    public void clear() {
        this.items.clear();
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    @Override
    public List<BallMeta> getAll() {
        return Collections.unmodifiableList(this.items);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.items.clear();
    }
}
