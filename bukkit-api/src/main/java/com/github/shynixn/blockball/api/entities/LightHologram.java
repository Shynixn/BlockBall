package com.github.shynixn.blockball.api.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@Deprecated
public interface LightHologram {
    void show(Player... players);

    void remove(Player... players);

    void teleport(Location location);

    void setText(String text);

    String getText();

    Location getLocation();
}
