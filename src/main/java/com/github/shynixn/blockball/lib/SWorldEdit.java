package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Deprecated
public final class SWorldEdit {
    private SWorldEdit() {
        super();
    }

    private static Object getPlugin() {
        try {
            return Class.forName("com.sk89q.worldedit.bukkit.WorldEditPlugin").cast(Bukkit.getPluginManager().getPlugin("WorldEdit"));
        } catch (final ClassNotFoundException ignored) {
        }
        return null;
    }

    public static Location getRightSelection(Player player) {
        final Object object = ReflectionLib.invokeMethodByObject(getPlugin(), "getSelection", player);
        if (object != null)
            return (Location) ReflectionLib.invokeMethodByObject(object, "getMaximumPoint");
        return null;
    }

    public static Location getLeftSelection(Player player) {
        final Object object = ReflectionLib.invokeMethodByObject(getPlugin(), "getSelection", player);
        if (object != null)
            return (Location) ReflectionLib.invokeMethodByObject(object, "getMinimumPoint");
        return null;
    }

    public static boolean isInstalled() {
        return Bukkit.getPluginManager().getPlugin("WorldEdit") != null;
    }

    public static boolean hasSelections(Player player) {
        return getRightSelection(player) != null && getLeftSelection(player) != null;
    }
}
