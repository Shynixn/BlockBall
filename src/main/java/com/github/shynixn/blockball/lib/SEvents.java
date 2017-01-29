package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SEvents implements Listener {
    @SPluginLoader.PluginLoader
    protected static JavaPlugin plugin;

    public SEvents() {
        if (plugin == null)
            throw new IllegalArgumentException("Pluginloader failed to load " + getClass().getSimpleName() + '.');
        register();
    }

    protected void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void unregister() {
        HandlerList.unregisterAll(this);
    }
}
