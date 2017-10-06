package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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
public class SimpleBossBar implements AutoCloseable{
    private static Object[] reflectionCache;

    private Object bossBar;
    /**
     * Initializes a new bossbar.
     *
     * @param message message
     * @param color   color
     * @param style   style
     * @param flags   flags
     * @throws ClassNotFoundException    exception
     * @throws NoSuchMethodException     exception
     * @throws IllegalAccessException    exception
     */
    private SimpleBossBar(String message, Color color, Style style, Flag... flags) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super();
        final Method method = (Method) reflectionCache[19];
        final List<Object> rFlags = new ArrayList<>();
        for (final Flag flag : flags) {
            rFlags.add(getEnumBarFlag(flag.name()));
        }
        this.bossBar = method.invoke(null, message
                , getEnumBarColor(color.name())
                , getEnumBarStyle(style.name())
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
    public void setColor(Color color) {
        try {
            final Method method = (Method) reflectionCache[10];
            method.invoke(this.bossBar, getEnumBarColor(color.name()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the color of the bossbar.
     *
     * @return color
     */
    public Color getColor() {
        try {
            final Method method = (Method) reflectionCache[11];
            return Color.getFromName(((Enum) method.invoke(this.bossBar)).name()).get();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the style of the bossbar.
     *
     * @param style style
     */
    public void setStyle(Style style) {
        try {
            final Method method = (Method) reflectionCache[12];
            method.invoke(this.bossBar, getEnumBarStyle(style.name()));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the style of the bossbar.
     *
     * @return style
     */
    public Style getStyle() {
        try {
            final Method method = (Method) reflectionCache[13];
            return Style.getFromName(((Enum) method.invoke(this.bossBar)).name()).get();
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
    public void addFlag(Flag flag) {
        try {
            final Method method = (Method) reflectionCache[16];
            final Object enumflag = getEnumBarFlag(flag.name());
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
    public void removeFlag(Flag flag) {
        try {
            final Method method = (Method) reflectionCache[17];
            final Object enumflag = getEnumBarFlag(flag.name());
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
    public List<Flag> getFlags() {
        try {
            final Method method = (Method) reflectionCache[18];
            final List<Flag> flags = new ArrayList<>();
            for (final Object flag : ((Object[]) method.invoke(this.bossBar))) {
                flags.add(Flag.getFromName(((Enum) flag).name()).get());
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

    public static SimpleBossBar from(String message, Color color, Style style, Flag... flags) {
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
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.removePlayer(this.getPlayers());
        this.bossBar = null;
    }

    /**
     * Builds meta data for bossbars.
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
    public static class BossbarBuilder implements ConfigurationSerializable, BossBarMeta {
        private Style style = Style.SOLID;
        private Color color = Color.PURPLE;
        private final Set<Flag> flags = new HashSet<>();
        private boolean enabled;
        private String message;
        private double percentage = 1.0;

        /**
         * Initialize
         */
        public BossbarBuilder() {
        }

        /**
         * Initialize
         *
         * @param data data
         */
        public BossbarBuilder(Map<String, Object> data) {
            if (data == null)
                throw new IllegalArgumentException("Data cannot be null!");
            this.enabled = (boolean) data.get("enabled");
            this.message = (String) data.get("message");
            this.color = Color.getFromName((String) data.get("color")).get();
            this.style = Style.getFromName((String) data.get("style")).get();
            this.percentage = (double) data.get("percentage");
            final List<String> flags = (List<String>) data.get("flags");
            for (final String flag : flags) {
                this.flags.add(Flag.getFromName(flag).get());
            }
        }

        /**
         * Returns the percentage of the bossbar.
         *
         * @return percentage
         */
        @Override
        public double getPercentage() {
            return this.percentage;
        }

        /**
         * Sets the percentage of the bossbar.
         *
         * @param percentage percentage
         */
        @Override
        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        /**
         * Returns the style of the bossbar.
         *
         * @return style
         */
        @Override
        public Style getStyle() {
            return this.style;
        }

        /**
         * Sets the style of the bossbar.
         *
         * @param style style
         */
        @Override
        public BossbarBuilder setStyle(Style style) {
            if (style == null)
                throw new IllegalArgumentException("Style cannot be null!");
            this.style = style;
            return this;
        }

        /**
         * Returns the color of the bossbar.
         *
         * @return color
         */
        @Override
        public Color getColor() {
            return this.color;
        }

        /**
         * Sets the color of the bossbar.
         *
         * @param color color
         */
        @Override
        public BossbarBuilder setColor(Color color) {
            if (color == null)
                throw new IllegalArgumentException("Color cannot be null!");
            this.color = color;
            return this;
        }

        /**
         * Returns the flags of the bossbar.
         *
         * @return flags
         */
        @Override
        public Set<Flag> getFlags() {
            return Collections.unmodifiableSet(this.flags);
        }

        /**
         * Adds a flag to list
         *
         * @param flag flag
         */
        @Override
        public void addFlag(Flag flag) {
            if (flag == null)
                throw new IllegalArgumentException("Flag cannot be null!");
            this.flags.add(flag);
        }

        /**
         * Removes flag from the list
         *
         * @param flag flag
         */
        @Override
        public void removeFlag(Flag flag) {
            if (flag == null)
                throw new IllegalArgumentException("Flag cannot be null!");
            if (this.flags.contains(flag)) {
                this.flags.remove(flag);
            }
        }

        /**
         * Returns if the bossbar should be visible.
         *
         * @return enabled
         */
        @Override
        public boolean isEnabled() {
            return this.enabled;
        }

        /**
         * Sets if the bossbar should be visible.
         *
         * @param enabled enabled
         */
        @Override
        public BossbarBuilder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * Returns the message of the bossbar
         *
         * @return message
         */
        @Override
        public Optional<String> getMessage() {
            return Optional.ofNullable(this.message);
        }

        /**
         * Sets the message of the bossbar
         *
         * @param message message
         */
        @Override
        public BossbarBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Serializes this object
         *
         * @return map
         */
        @Override
        public Map<String, Object> serialize() {
            final Map<String, Object> data = new LinkedHashMap<>();
            data.put("enabled", this.enabled);
            data.put("message", this.message);
            data.put("percentage", this.percentage);
            data.put("color", this.color.name().toUpperCase());
            data.put("style", this.style.name().toUpperCase());
            final List<String> flags = new ArrayList<>();
            for (final Flag flag : this.flags) {
                flags.add(flag.name().toUpperCase());
            }
            data.put("flag", flags);
            return null;
        }
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
