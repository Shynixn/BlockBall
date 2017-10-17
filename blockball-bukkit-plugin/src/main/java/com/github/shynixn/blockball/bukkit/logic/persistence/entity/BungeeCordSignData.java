package com.github.shynixn.blockball.bukkit.logic.persistence.entity;

import com.github.shynixn.blockball.api.persistence.entity.BungeeCordSign;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

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
public class BungeeCordSignData extends PersistenceObject<BungeeCordSign> implements BungeeCordSign {
    private final String world;
    private final double x;
    private final double y;
    private final double z;
    private final String server;

    /**
     * Initializes a new bungeeCord sign data from the data.
     *
     * @param data data
     */
    public BungeeCordSignData(Map<String, Object> data) {
        super();
        this.server = (String) data.get("server");
        this.x = (double) data.get("x");
        this.y = (double) data.get("y");
        this.z = (double) data.get("z");
        this.world = (String) data.get("world");
    }

    /**
     * Initializes a new bungeeCord sign data from the given parameters.
     *
     * @param location location
     * @param server   server
     */
    public BungeeCordSignData(Location location, String server) {
        super();
        this.server = server;
        this.world = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    /**
     * Returns the server linking to.
     *
     * @return server
     */
    @Override
    public String getServer() {
        return this.server;
    }

    /**
     * Returns the location of the sign.
     *
     * @return location
     */
    @Override
    public Object getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    /**
     * Serializes the given object.
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new HashMap<>();
        data.put("server", this.getServer());
        data.put("world", this.world);
        data.put("x", this.x);
        data.put("y", this.y);
        data.put("z", this.z);
        return data;
    }
}
