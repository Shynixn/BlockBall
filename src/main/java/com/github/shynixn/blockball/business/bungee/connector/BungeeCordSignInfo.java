package com.github.shynixn.blockball.business.bungee.connector;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

interface BungeeCordSignInfo extends ConfigurationSerializable {
    String getServer();

    Location getLocation();

    class Container implements BungeeCordSignInfo {
        private final String world;
        private final double x;
        private final double y;
        private final double z;
        private final String server;

        Container(Map<String, Object> data) {
            super();
            this.server = (String) data.get("server");
            this.x = (double) data.get("x");
            this.y = (double) data.get("y");
            this.z = (double) data.get("z");
            this.world = (String) data.get("world");
        }

        Container(Location location, String server) {
            super();
            this.server = server;
            this.world = location.getWorld().getName();
            this.x = location.getX();
            this.y = location.getY();
            this.z = location.getZ();
        }

        @Override
        public String getServer() {
            return this.server;
        }

        @Override
        public Location getLocation() {
            return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
        }

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
}
