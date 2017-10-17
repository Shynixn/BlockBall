package com.github.shynixn.blockball.bukkit.logic.business;

import com.github.shynixn.blockball.api.business.controller.BungeeCordSignController;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.Closeable;
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
public class BlockBallBungeeCordManager implements AutoCloseable {
    public Map<Player, String> signPlacementCache = new HashMap<>();

    public static String MOD_RESTARTING = ChatColor.RED + "Restarting";
    public static String MOD_WAITING_FOR_PLAYERS = ChatColor.GREEN + "Waiting for players...";
    public static String MOD_INGAME = ChatColor.BLUE + "Ingame";

    public static String SIGN_RESTARTING = ChatColor.RED + "" + ChatColor.BOLD + "Restarting";
    public static String SIGN_WAITING_FOR_PLAYERS = ChatColor.GREEN + "" + ChatColor.BOLD + "Join";
    public static String SIGN_INGAME = ChatColor.BLUE + "" + ChatColor.BOLD + "Ingame";

    public static String COMMAND_COMMAND = "e";
    public static String COMMAND_USEAGE = "/e <server>";
    public static String COMMAND_PERMISSION_MESSAGE = "You don't have permission.";
    public static String COMMAND_PERMISSION = "blockball.admin";
    public static String COMMAND_DESCRIPTION = "Configures bungee signs.";

    public static String SIGN_LINE_1 = ChatColor.BOLD + "[]";
    public static String SIGN_LINE_2 = "<server>";
    public static String SIGN_LINE_3 = "<state>";
    public static String SIGN_LINE_4 = "<players>/<maxplayers>";

    public static boolean ENABLED;
    public static boolean SIGN_MODE = true;

    private static BungeeCordMinigame minigameListener;

    public static void reload(JavaPlugin plugin, String prefix, String command, String headline) {
        COMMAND_COMMAND = command;
        COMMAND_USEAGE = '/' + command + " <server>";
        COMMAND_PERMISSION = command + ".admin";
        SIGN_LINE_1 = ChatColor.BOLD + headline;
        buildConfigFile(plugin);
        if (isMinigameModeEnabled()) {
            minigameListener = new BungeeCordMinigame();
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "Enabled MINIGAME changing to allow joining.");
        } else if (isSignModeEnabled()) {
            new BungeeCordController(plugin, prefix);
            Bukkit.getConsoleSender().sendMessage(prefix + ChatColor.YELLOW + "Enabled SIGN to manage signs.");
        }
    }

    void setMotd(String motd) {
        try {
            motd = motd.replace("[", "").replace("]", "");
            final Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", BungeeCord.getServerVersion()));
            Object obj = clazz.cast(Bukkit.getServer());
            obj = BungeeCord.invokeMethodByObject(obj, "getServer");
            BungeeCord.invokeMethodByObject(obj, "setMotd", '[' + motd + ChatColor.RESET + ']');
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot set motd.", ex);
        }
    }


    private static void buildConfigFile(JavaPlugin plugin) {
        try {
            final File file = new File(plugin.getDataFolder(), "bungeecord.yml");
            if (!file.exists()) {
                if (!file.createNewFile())
                    Bukkit.getLogger().log(Level.WARNING, "Cannot create file.");
                final List<String> s = new ArrayList<>();
                s.add("#BungeeCord");
                s.add("enabled: false");
                s.add("");
                s.add("#Select the connection type: MINIGAME or SIGN");
                s.add("connection: MINIGAME");
                s.add("");
                for (final Field field : BungeeCord.class.getDeclaredFields()) {
                    if (field.getType() == String.class) {
                        s.add(field.getName().toLowerCase() + ": \"" + String.valueOf(field.get(null)).replace('§', '&') + '"');
                    }
                }
                writeAllLines(file, s.toArray(new String[s.size()]));
            } else {
                final FileConfiguration configuration = new YamlConfiguration();
                configuration.load(file);
                ENABLED = configuration.getBoolean("enabled");
                if (configuration.getString("connection").equalsIgnoreCase("MINIGAME"))
                    SIGN_MODE = false;
                final Map<String, Object> map = configuration.getConfigurationSection("").getValues(true);
                for (final Field field : BungeeCord.class.getDeclaredFields())
                    for (final String key : map.keySet()) {
                        if (field.getName().equalsIgnoreCase(key)) {
                            if (field.getType() == String.class) {
                                field.set(null, ChatColor.translateAlternateColorCodes('&', (String) map.get(key)));
                            } else {
                                field.set(null, map.get(key));
                            }
                        }
                    }
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot build config file.", e);
        }
    }

    private static boolean writeAllLines(File file, String... text) {
        try {
            if (file.exists())
            {
                if(!file.delete())
                    Bukkit.getLogger().log(Level.WARNING, "Cannot delete file.");
            }
            if(!file.createNewFile())
                Bukkit.getLogger().log(Level.WARNING, "Cannot create file.");
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                for (final String aText : text) {
                    bufferedWriter.write(aText + '\n');
                }
            }
            return true;
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot delete file.", e);
            return false;
        }
    }

    public static Object invokeMethodByObject(Object object, String name, Object... params) {
        Class<?> clazz = object.getClass();
        do {
            for (final Method method : clazz.getDeclaredMethods()) {
                try {
                    if (method.getName().equalsIgnoreCase(name)) {
                        method.setAccessible(true);
                        return method.invoke(object, params);
                    }
                } catch (final Exception ex) {
                    Bukkit.getLogger().log(Level.WARNING, "Cannot invoke Method.", ex);
                }
            }
            clazz = clazz.getSuperclass();
        } while (clazz != null);
        throw new RuntimeException("Cannot find correct method.");
    }

    public static String getServerVersion() {
        try {
            return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (final Exception ex) {
            throw new RuntimeException("Version not found!");
        }
    }

    private final BungeeCordSignController signController;


    public static boolean isMinigameModeEnabled() {
        return ENABLED && !SIGN_MODE;
    }

    public static boolean isSignModeEnabled() {
        return ENABLED && SIGN_MODE;
    }

    public static void setModt(String modt) {
        if (isMinigameModeEnabled()) {
            minigameListener.setMotd(modt);
        }
    }

    public BlockBallBungeeCordManager() {
        this.signController = new
    }

    /**
     * Returns the sign controller
     *
     * @return controller
     */
    public BungeeCordSignController getSignController() {
        return this.signController;
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {

    }
}
