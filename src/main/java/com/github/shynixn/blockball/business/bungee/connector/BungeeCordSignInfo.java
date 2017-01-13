package com.github.shynixn.blockball.business.bungee.connector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shynixn
 */
interface BungeeCordSignInfo extends ConfigurationSerializable {
    String getServer();

    Location getLocation();

    class Container implements BungeeCordSignInfo {
        private String world;
        private double x;
        private double y;
        private double z;
        private String server;

        Container(Map<String, Object> data) {
            server = (String) data.get("server");
            x = (double) data.get("x");
            y = (double) data.get("y");
            z = (double) data.get("z");
            world = (String) data.get("world");
        }

        Container(Location location, String server) {
            this.server = server;
            world = location.getWorld().getName();
            x = location.getX();
            y = location.getY();
            z = location.getZ();
        }

        @Override
        public String getServer() {
            return server;
        }

        @Override
        public Location getLocation() {
            return new Location(Bukkit.getWorld(world), x, y, z);
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> data = new HashMap<>();
            data.put("server", getServer());
            data.put("world", world);
            data.put("x", x);
            data.put("y", y);
            data.put("z", z);
            return data;
        }
    }
}
