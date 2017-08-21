package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public final class RegisterHelper {
    public static String PREFIX;
    private static final HashMap<String, String> registered = new HashMap<>();

    public static boolean register(String pluginName) {
        return register(pluginName, null, null);
    }

    public static boolean register(String pluginName, String path) {
        return register(pluginName, path, null);
    }

    public static boolean isRegistered(String pluginName) {
        return isRegistered(pluginName, null);
    }

    public static boolean isRegistered(String pluginName, char version) {
        return registered.containsKey(pluginName) && registered.get(pluginName).charAt(0) == version;
    }

    public static boolean isRegistered(String pluginName, String version) {
        return registered.containsKey(pluginName) && !(version != null && !registered.get(pluginName).equals(version));
    }

    public static boolean register(String pluginName, String path, char version) {
        boolean canregister = true;
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null && !plugin.getDescription().getVersion().startsWith(String.valueOf(version)))
            return false;
        if (pluginName != null && plugin != null) {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "found dependency [" + pluginName + ']' + '.');
            if (path != null) {
                try {
                    Class.forName(path);
                } catch (final ClassNotFoundException e) {
                    canregister = false;
                }
            }
            if (Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion().charAt(0) != version) {
                canregister = false;
            }
            if (canregister) {
                registered.put(pluginName, Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion());
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_GREEN + "hooked successfully into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
                return true;
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_RED + "failed to hook into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
            }
        }
        return false;
    }

    public static boolean register(String pluginName, String path, String version) {
        boolean canregister = true;
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (pluginName != null && plugin != null) {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "found dependency [" + pluginName + "].");
            if (path != null) {
                try {
                    Class.forName(path);
                } catch (final ClassNotFoundException e) {
                    canregister = false;
                }
            }
            if (version != null && !Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion().equals(version)) {
                canregister = false;
            }
            if (canregister) {
                registered.put(pluginName, Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion());
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_GREEN + "hooked successfully into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
                return true;
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_RED + "failed to hook into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
            }
        }
        return false;
    }
}

