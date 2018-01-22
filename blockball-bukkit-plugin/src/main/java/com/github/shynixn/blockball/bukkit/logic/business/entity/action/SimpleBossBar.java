package com.github.shynixn.blockball.bukkit.logic.business.entity.action;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages bukkit bossbars.
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
public class SimpleBossBar implements AutoCloseable {
    private static Object[] reflectionCache;

    private Object bossBar;

    /**
     * Initializes a new bossbar.
     *
     * @param message message
     * @param color   color
     * @param style   style
     * @param flags   flags
     * @throws ClassNotFoundException exception
     * @throws NoSuchMethodException  exception
     * @throws IllegalAccessException exception
     */
    private SimpleBossBar(String message, String color, String style, String... flags) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super();
        final Method method = (Method) reflectionCache[19];
        final List<Object> rFlags = new ArrayList<>();
        for (final String flag : flags) {
            rFlags.add(getEnumBarFlag(flag));
        }
        this.bossBar = method.invoke(null, message
                , getEnumBarColor(color)
                , getEnumBarStyle(style)
                , rFlags);
    }

    /**
     * Sets the bossbar visible or not.
     *
     * @param visible visible
     */
    public void setVisible(boolean visible) {
        try {
            final Method method = (Method) reflectionCache[6];
            method.invoke(this.bossBar, visible);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns if the bossbar is visible.
     *
     * @return isVisible
     */
    public boolean isVisible() {
        try {
            final Method method = (Method) reflectionCache[7];
            return (boolean) method.invoke(this.bossBar);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the percentage of the bossbar filled.
     *
     * @param percentage percentage
     */
    public void setPercentage(double percentage) {
        try {
            final Method method = (Method) reflectionCache[8];
            method.invoke(this.bossBar, percentage);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the percentage of the bossbar filled.
     *
     * @return percentage
     */
    public double getPercentage() {
        try {
            final Method method = (Method) reflectionCache[9];
            return (double) method.invoke(this.bossBar);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the color of the bossbar.
     *
     * @param color color
     */
    public void setColor(String color) {
        try {
            final Method method = (Method) reflectionCache[10];
            method.invoke(this.bossBar, getEnumBarColor(color));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the color of the bossbar.
     *
     * @return color
     */
    public String getColor() {
        try {
            final Method method = (Method) reflectionCache[11];
            return ((Enum) method.invoke(this.bossBar)).name();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the style of the bossbar.
     *
     * @param style style
     */
    public void setStyle(String style) {
        try {
            final Method method = (Method) reflectionCache[12];
            method.invoke(this.bossBar, getEnumBarStyle(style));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the style of the bossbar.
     *
     * @return style
     */
    public String getStyle() {
        try {
            final Method method = (Method) reflectionCache[13];
            return ((Enum) method.invoke(this.bossBar)).name();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the message of the bossbar.
     *
     * @param message message
     */
    public void setMessage(String message) {
        try {
            final Method method = (Method) reflectionCache[14];
            method.invoke(this.bossBar, message);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the message of the bossbar.
     *
     * @return message
     */
    public String getMessage() {
        try {
            final Method method = (Method) reflectionCache[15];
            return (String) method.invoke(this.bossBar);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a flag to the bossbar.
     *
     * @param flag flag
     */
    public void addFlag(String flag) {
        try {
            final Method method = (Method) reflectionCache[16];
            final Object enumflag = getEnumBarFlag(flag);
            method.invoke(this.bossBar, enumflag);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a flag from the bossbar.
     *
     * @param flag flag
     */
    public void removeFlag(String flag) {
        try {
            final Method method = (Method) reflectionCache[17];
            final Object enumflag = getEnumBarFlag(flag);
            method.invoke(this.bossBar, enumflag);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns all flags from the bossbar.
     *
     * @return flags
     */
    public List<String> getFlags() {
        try {
            final Method method = (Method) reflectionCache[18];
            final List<String> flags = new ArrayList<>();
            for (final Object flag : ((Object[]) method.invoke(this.bossBar))) {
                flags.add(((Enum) flag).name());
            }
            return flags;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds players which should see the bossbar.
     *
     * @param players players
     */
    public void addPlayer(Collection<Player> players) {
        this.addPlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Adds players which should see the bossbar.
     *
     * @param players players
     */
    public void addPlayer(Player... players) {
        try {
            final Method method = (Method) reflectionCache[3];
            for (final Player player : players) {
                method.invoke(this.bossBar, player);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes players which should no longer see the bossbar.
     *
     * @param players players
     */
    public void removePlayer(Collection<Player> players) {
        this.removePlayer(players.toArray(new Player[players.size()]));
    }

    /**
     * Removes players which should no longer see the bossbar.
     *
     * @param players players
     */
    public void removePlayer(Player... players) {
        try {
            final Method method = (Method) reflectionCache[4];
            for (final Player player : players) {
                method.invoke(this.bossBar, player);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the player which can see the bossbar.
     *
     * @return players
     */
    public List<Player> getPlayers() {
        try {
            final Method method = (Method) reflectionCache[5];
            return (List<Player>) method.invoke(this.bossBar);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new bossbar from message color, style and flags.
     *
     * @param message message
     * @param color   color
     * @param style   style
     * @param flags   flags
     * @return bossbar
     */
    public static SimpleBossBar from(String message, String color, String style, String... flags) {
        try {
            if (reflectionCache == null) {
                initializeReflectionCache();
            }
            return new SimpleBossBar(message, color, style, flags);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getEnumBarColor(String name) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ((Method) reflectionCache[0]).invoke(null, name);
    }

    private static Object getEnumBarStyle(String name) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ((Method) reflectionCache[1]).invoke(null, name);
    }

    private static Object getEnumBarFlag(String name) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return ((Method) reflectionCache[2]).invoke(null, name);
    }

    private static Method getBossBarMethod(String name, Class<?>... clazzes) throws ClassNotFoundException, NoSuchMethodException {
        return Class.forName("org.bukkit.boss.BossBar").getDeclaredMethod(name, clazzes);
    }

    /**
     * Initializes the reflection cache.
     *
     * @throws ClassNotFoundException exception
     * @throws NoSuchMethodException  exception
     */
    private static void initializeReflectionCache() throws ClassNotFoundException, NoSuchMethodException {
        reflectionCache = new Object[20];
        reflectionCache[0] = Class.forName("org.bukkit.boss.BarColor").getDeclaredMethod("valueOf", String.class);
        reflectionCache[1] = Class.forName("org.bukkit.boss.BarStyle").getDeclaredMethod("valueOf", String.class);
        reflectionCache[2] = Class.forName("org.bukkit.boss.BarFlag").getDeclaredMethod("valueOf", String.class);
        reflectionCache[3] = getBossBarMethod("addPlayer", Player.class);
        reflectionCache[4] = getBossBarMethod("removePlayer", Player.class);
        reflectionCache[5] = getBossBarMethod("getPlayers");
        reflectionCache[6] = getBossBarMethod("setVisible", boolean.class);
        reflectionCache[7] = getBossBarMethod("isVisible");
        reflectionCache[8] = getBossBarMethod("setProgress", double.class);
        reflectionCache[9] = getBossBarMethod("getProgress");
        reflectionCache[10] = getBossBarMethod("setColor", Class.forName("org.bukkit.boss.BarColor"));
        reflectionCache[11] = getBossBarMethod("getColor");
        reflectionCache[12] = getBossBarMethod("setStyle", Class.forName("org.bukkit.boss.BarStyle"));
        reflectionCache[13] = getBossBarMethod("getStyle");
        reflectionCache[14] = getBossBarMethod("setTitle", String.class);
        reflectionCache[15] = getBossBarMethod("getTitle");
        reflectionCache[16] = getBossBarMethod("addFlag", Class.forName("org.bukkit.boss.BarFlag"));
        reflectionCache[17] = getBossBarMethod("removeFlag", Class.forName("org.bukkit.boss.BarFlag"));
        reflectionCache[18] = getBossBarMethod("getFlags", List.class);
        reflectionCache[19] = Bukkit.class.getDeclaredMethod("createBossBar", String.class
                , Class.forName("org.bukkit.boss.BarColor")
                , Class.forName("org.bukkit.boss.BarStyle")
                , Class.forName("org.bukkit.boss.BarFlag"));
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
        this.removePlayer(this.getPlayers());
        this.bossBar = null;
    }

    /**
     * Returns if the server version is below 1.9.0.
     *
     * @return isBelow
     */
    private static boolean isServerVersionBelowv1_9_R1() {
        final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        return version.equalsIgnoreCase("v1_8_R1")
                || version.equalsIgnoreCase("v1_8_R2")
                || version.equalsIgnoreCase("v1_8_R3");
    }
}
