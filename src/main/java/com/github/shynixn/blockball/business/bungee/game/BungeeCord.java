package com.github.shynixn.blockball.business.bungee.game;

import com.github.shynixn.blockball.business.bungee.connector.BungeeCordController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Shynixn
 */
public class BungeeCord {
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

    public static boolean ENABLED = false;
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

    private static void buildConfigFile(JavaPlugin plugin) {
        try {
            final File file = new File(plugin.getDataFolder(), "bungeecord.yml");
            if (!file.exists()) {
                if (!file.createNewFile())
                    Bukkit.getLogger().log(Level.WARNING, "Cannot create file.");
                ArrayList<String> s = new ArrayList<>();
                s.add("#BungeeCord");
                s.add("enabled: false");
                s.add("");
                s.add("#Select the connection type: MINIGAME or SIGN");
                s.add("connection: MINIGAME");
                s.add("");
                for (Field field : BungeeCord.class.getDeclaredFields()) {
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
}
