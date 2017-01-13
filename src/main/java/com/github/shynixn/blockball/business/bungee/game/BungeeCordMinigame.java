package com.github.shynixn.blockball.business.bungee.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


/**
 * Created by Shynixn
 */
public class BungeeCordMinigame {
    public void setMotd(String motd) {
        try {
            motd = motd.replace("[", "").replace("]", "");
            Class<?> clazz = Class.forName("org.bukkit.craftbukkit.VERSION.CraftServer".replace("VERSION", BungeeCord.getServerVersion()));
            Object obj = clazz.cast(Bukkit.getServer());
            obj = BungeeCord.invokeMethodByObject(obj, "getServer");
            BungeeCord.invokeMethodByObject(obj, "setMotd", "[" + motd + ChatColor.RESET + "]");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
