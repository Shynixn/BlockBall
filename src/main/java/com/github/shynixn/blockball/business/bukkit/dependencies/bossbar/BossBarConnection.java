package com.github.shynixn.blockball.business.bukkit.dependencies.bossbar;

import com.github.shynixn.blockball.lib.ReflectionLib;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

public class BossBarConnection {
    private BossBarConnection() {
        super();
    }

    public static void updateBossBar(Player player, String message) {
        ReflectionLib.invokeMethodByClazz(BossBarAPI.class, "setMessage", player, message);
    }

    public static void removeBossBar(Player player) {
        if (((Boolean) ReflectionLib.invokeMethodByClazz(BossBarAPI.class, "hasBar", player)))
            ReflectionLib.invokeMethodByClazz(BossBarAPI.class, "removeBar", player);
    }
}
