package com.github.shynixn.blockball.bukkit.logic.compatibility;

import com.github.shynixn.blockball.api.compatibility.BounceController;
import com.github.shynixn.blockball.api.compatibility.BounceObject;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

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
public class BounceObjectController implements BounceController<Block, Material>, ConfigurationSerializable {

    private final List<BounceObject> bounceObjects = new ArrayList<>();

    /**
     * New instance.
     */
    public BounceObjectController() {
    }

    /**
     * Initializes the bounceController with serializedContent.
     *
     * @param data data
     */
    public BounceObjectController(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        for (final String key : data.keySet()) {
            final BounceObject bounceObject = new BounceInfo(((MemorySection) data.get(key)).getValues(false));
            this.store(bounceObject);
        }
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(BounceObject item) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null!");
        if (!this.bounceObjects.contains(item)) {
            this.bounceObjects.add(item);
        }
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(BounceObject item) {
        if (item == null)
            throw new IllegalArgumentException("Item cannot be null!");
        if (this.bounceObjects.contains(item)) {
            this.bounceObjects.remove(item);
        }
    }

    /**
     * Creates a new bounceObject from the given parameters.
     *
     * @param type   type
     * @param damage damage
     * @return bounceObject
     */
    @Override
    public BounceObject create(Material type, int damage) {
        final BounceObject object = new BounceInfo(type.getId());
        object.setMaterialDamageValue(damage);
        return object;
    }

    /**
     * Returns the bounceObject from the given block.
     *
     * @param block block
     * @return optBounceObject
     */
    @Override
    public Optional<BounceObject> getBounceObjectFromBlock(Block block) {
        if (block == null)
            throw new IllegalArgumentException("Block cannot be null!");
        for (final BounceObject object : this.bounceObjects) {
            if (object.isBlock(block)) {
                return Optional.of(object);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    @Override
    public int size() {
        return this.bounceObjects.size();
    }

    /**
     * Clears all items in the repository.
     */
    @Override
    public void clear() {
        this.bounceObjects.clear();
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    @Override
    public List<BounceObject> getAll() {
        return Collections.unmodifiableList(this.bounceObjects);
    }

    /**
     * Serializes the given content.
     *
     * @return serializedContent.
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        final List<BounceObject> list = this.getAll();
        for (int i = 0; i < list.size(); i++) {
            final BounceInfo bounceInfo = (BounceInfo) list.get(i);
            data.put(String.valueOf(i + 1), bounceInfo.serialize());
        }
        return data;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.bounceObjects.clear();
    }
}
