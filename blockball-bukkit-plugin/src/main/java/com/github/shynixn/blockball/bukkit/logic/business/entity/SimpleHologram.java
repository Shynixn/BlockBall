package com.github.shynixn.blockball.bukkit.logic.business.entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class SimpleHologram implements AutoCloseable {
    private static Object[] reflectionCache;
    private final Map<Player, Boolean> players = new HashMap<>();
    private final List<Object> armorstands = new ArrayList<>();
    private final Location defaultLocation;
    private final int taskId;
    private Plugin plugin;

    private SimpleHologram(Plugin plugin, Location location, Collection<String> lines) {
        this.defaultLocation = location;
        this.plugin = plugin;
        for (final String s : lines) {
            this.addLine(s);
        }
        this.taskId = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            synchronized (this.players) {
                final Location holoLocation = this.getLocation();
                for (final Player player : this.players.keySet()) {
                    if (player.getLocation().getChunk().equals(holoLocation.getChunk())) {
                        if (!this.players.get(player)) {
                            this.respawnHolograms(player);
                            this.players.put(player, true);
                        }
                    } else {
                        if (this.players.get(player)) {
                            this.despawnHolograms(player);
                            this.players.put(player, false);
                        }
                    }
                }
            }
        }, 0L, 60L).getTaskId();
    }

    /**
     * Adds a line to the end of a hologram.
     *
     * @param text text
     */
    public void addLine(String text) {
        if (text == null)
            throw new IllegalArgumentException("Text cannot be null!");
        Object entityArmorstand = null;
        final ArmorStand newArmorstand = this.getBukkitEntity(entityArmorstand);
        newArmorstand.setCustomNameVisible(true);
        newArmorstand.setVisible(false);
        newArmorstand.setCustomName(ChatColor.translateAlternateColorCodes('&', text));
        this.armorstands.add(text);
    }

    /**
     * Removes the line at the position.
     *
     * @param position position
     */
    public void removeLine(int position) {
        if (position < this.armorstands.size() && position > 0) {
            final Object entityArmorstandToBeRemoved = this.armorstands.get(position);
            this.removeHologram(entityArmorstandToBeRemoved);
        }
        this.teleport(this.defaultLocation);
    }

    /**
     * Removes all matching lines from the hologram.
     *
     * @param text
     */
    public void removeLine(String text) {
        if (text == null)
            throw new IllegalArgumentException("Text cannot be null!");
        final String translatedText = ChatColor.translateAlternateColorCodes('&', text);
        final Object[] armorStands = this.armorstands.toArray(new Object[this.armorstands.size()]);
        for (int i = 0; i < armorStands.length; i++) {
            final ArmorStand stand = this.getBukkitEntity(armorStands[i]);
            if (stand.getCustomName().equals(translatedText)) {
                this.removeHologram(stand);
            }
        }
        this.teleport(this.defaultLocation);
    }

    /**
     * Sets a text at the given position.
     *
     * @param position position
     * @param text     text
     */
    public void setLine(int position, String text) {
        if (text == null)
            throw new IllegalArgumentException("Text cannot be null!");
        final ArmorStand stand = this.getBukkitEntity(this.armorstands.get(position));
        stand.setCustomName(text);
    }

    /**
     * Returns a line at the given position.
     *
     * @param position position
     * @return line
     */
    public Optional<String> getLine(int position) {
        if (position >= this.armorstands.size() || position < 0)
            return Optional.empty();
        return Optional.of(this.getBukkitEntity(this.armorstands.get(position)).getCustomName());
    }

    /**
     * Returns the amount of lines.
     *
     * @return amount
     */
    public int getSize() {
        return this.armorstands.size();
    }

    /**
     * Returns all lines in an unmodifiable list.
     *
     * @return lines
     */
    public List<String> getLines() {
        final List<String> lines = new ArrayList<>();
        for (final Object armorstand : this.armorstands) {
            lines.add(this.getBukkitEntity(armorstand).getCustomName());
        }
        return lines;
    }

    /**
     * Teleports the hologram to the given location
     *
     * @param location location
     */
    public void teleport(Location location) {
        try {
            Location targetLocation = location;
            final Constructor teleportPacketConstructor = (Constructor) reflectionCache[0];
            for (final Object stand : this.armorstands) {
                this.getBukkitEntity(stand).teleport(targetLocation);
                targetLocation = targetLocation.add(0, -0.2, 0);
                final Object teleportPacket = teleportPacketConstructor.newInstance(stand);
                for (final Player player : this.players.keySet()) {
                    this.sendPacket(player, teleportPacket);
                }
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the location of the hologram.
     *
     * @return location
     */
    public Location getLocation() {
        return this.defaultLocation.clone();
    }

    /**
     * Adds players which should see the hologram.
     *
     * @param players players
     */
    public void addPlayer(Collection<Player> players) {
        this.addPlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Adds players which should see the hologram.
     *
     * @param players players
     */
    public void addPlayer(Player... players) {
        synchronized (this.players) {
            for (final Player player : players) {
                this.players.put(player, true);
                this.respawnHolograms(player);
            }
        }
    }

    /**
     * Removes players which should no longer see the hologram.
     *
     * @param players players
     */
    public void removePlayer(Collection<Player> players) {
        this.removePlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Removes players which should no longer see the hologram.
     *
     * @param players players
     */
    public void removePlayer(Player... players) {
        synchronized (this.players) {
            for (final Player player : players) {
                if (this.players.containsKey(player)) {
                    this.despawnHolograms(player);
                    this.players.remove(player);
                }
            }
        }
    }

    /**
     * Returns the player which can see the hologram.
     *
     * @return players
     */
    public List<Player> getPlayers() {
        synchronized (this.players) {
            return new ArrayList<>(this.players.keySet());
        }
    }

    /**
     * Creates a hologram from the given parameters
     *
     * @param plugin   plugin
     * @param location location
     * @param lines    lines
     * @return hologram
     */
    public static SimpleHologram from(Plugin plugin, Location location, String... lines) {
        return from(plugin, location, Arrays.asList(lines));
    }

    /**
     * Creates a hologram from the given parameters
     *
     * @param plugin   plugin
     * @param location location
     * @param lines    lines
     * @return hologram
     */
    public static SimpleHologram from(Plugin plugin, Location location, Collection<String> lines) {
        try {
            if (reflectionCache == null) {
                initializeReflectionCache();
            }
            return new SimpleHologram(plugin, location, lines);
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Respawns all armorstands for a player
     *
     * @param player player
     */
    private void respawnHolograms(Player player) {
        try {
            for (final Object entityArmorstand : this.armorstands) {
                final Constructor spawnPacketConstructor = (Constructor) reflectionCache[0];
                final Object spawnPacket = spawnPacketConstructor.newInstance(entityArmorstand);
                this.sendPacket(player, spawnPacket);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Despawns all armorstands for a player
     *
     * @param player player
     */
    private void despawnHolograms(Player player) {
        try {
            for (final Object entityArmorstand : this.armorstands) {
                final Constructor deSpawnPacketConstructor = (Constructor) reflectionCache[5];
                final Object spawnPacket = deSpawnPacketConstructor.newInstance(entityArmorstand);
                this.sendPacket(player, spawnPacket);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a single hologram
     *
     * @param armorstand armorstand
     */
    private void removeHologram(Object armorstand) {
        this.armorstands.remove(armorstand);
        this.teleport(this.defaultLocation);
    }

    /**
     * Sends a packet to the given player
     *
     * @param player player
     * @param packet packet
     * @throws InvocationTargetException exception
     * @throws IllegalAccessException    exception
     */
    private void sendPacket(Player player, Object packet) throws InvocationTargetException, IllegalAccessException {
        final Method getEntityPlayerMethod = (Method) reflectionCache[2];
        final Field playerConnectionField = (Field) reflectionCache[3];
        final Method sendPacketMethod = (Method) reflectionCache[4];
        final Object entityPlayer = getEntityPlayerMethod.invoke(player);
        final Object playerConnection = playerConnectionField.get(entityPlayer);
        sendPacketMethod.invoke(playerConnection, packet);
    }

    /**
     * Returns the bukkitEntity of the armorstand
     *
     * @param entityArmorstand entityArmorstand
     * @return bukkitArmorstand
     */
    private ArmorStand getBukkitEntity(Object entityArmorstand) {
        try {
            final Method method = (Method) reflectionCache[6];
            return (ArmorStand) method.invoke(entityArmorstand);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static void initializeReflectionCache() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        reflectionCache = new Object[20];
        reflectionCache[0] = createClass("net.minecraft.server.VERSION.PacketPlayOutEntityTeleport").getDeclaredConstructor(createClass("net.minecraft.server.VERSION.Entity"));
        reflectionCache[1] = createClass("net.minecraft.server.VERSION.PacketPlayOutSpawnEntityLiving").getDeclaredConstructor(createClass("net.minecraft.server.VERSION.EntityLiving"));
        reflectionCache[2] = createClass("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer").getDeclaredMethod("getHandle");
        reflectionCache[3] = createClass("net.minecraft.server.VERSION.EntityPlayer").getDeclaredField("playerConnection");
        reflectionCache[4] = createClass("net.minecraft.server.VERSION.PlayerConnection").getDeclaredMethod("sendPacket", createClass("net.minecraft.server.VERSION.Packet"));
        reflectionCache[5] = createClass("net.minecraft.server.VERSION.PacketPlayOutEntityDestroy").getDeclaredConstructor(int[].class);
        reflectionCache[6] = createClass("net.minecraft.server.VERSION.Entity").getDeclaredMethod("getBukkitEntity");
    }

    /**
     * Creates a new version independent class
     *
     * @param path path
     * @return class
     * @throws ClassNotFoundException exception
     */
    private static Class<?> createClass(String path) throws ClassNotFoundException {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return Class.forName(path.replace("VERSION", version));
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
        plugin.getServer().getScheduler().cancelTask(this.taskId);
        Bukkit.getServer().getScheduler().cancelTask(this.taskId);
        for (final Object entityArmorstand : this.armorstands) {
            this.removeHologram(entityArmorstand);
        }
        this.armorstands.clear();
        this.players.clear();
    }
}
