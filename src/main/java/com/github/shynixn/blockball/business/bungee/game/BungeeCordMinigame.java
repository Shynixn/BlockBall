package com.github.shynixn.blockball.business.bungee.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;


public final class BungeeCordMinigame {
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
}
