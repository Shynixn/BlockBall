package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@Deprecated
public final class SConsoleUtils {
    public static void sendColoredMessage(String message, ChatColor color, String prefix) {
        Bukkit.getServer().getConsoleSender().sendMessage(prefix + color + message);
    }

    public static void sendColoredMessage(String message) {
        Bukkit.getServer().getConsoleSender().sendMessage(message);
    }

    public static void sendColoredMessage(String message, String prefix) {
        Bukkit.getServer().getConsoleSender().sendMessage(prefix + message);
    }
}
