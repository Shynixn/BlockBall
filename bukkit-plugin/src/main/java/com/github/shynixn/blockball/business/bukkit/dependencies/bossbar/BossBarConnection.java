package com.github.shynixn.blockball.business.bukkit.dependencies.bossbar;

import com.github.shynixn.blockball.lib.ReflectionLib;
import org.bukkit.entity.Player;

public class BossBarConnection {
    private BossBarConnection() {
        super();
    }

    public static void updateBossBar(Player player, String message) {
        ReflectionLib.invokeMethodByClazz(getClazz(), "setMessage", player, message);
    }

    public static void removeBossBar(Player player) {
        if (((Boolean) ReflectionLib.invokeMethodByClazz(getClazz(), "hasBar", player)))
            ReflectionLib.invokeMethodByClazz(getClazz(), "removeBar", player);
    }

    private static Class<?> getClazz() {
        try {
            return Class.forName("org.inventivetalent.bossbar.BossBarAPI");
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
