package com.github.shynixn.blockball.lib;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Deprecated
public final class SFileUtils {

    public static Map<String, Object> serialize(ConfigurationSerializable serializable) {
        if (serializable == null)
            return null;
        return serializable.serialize();
    }

    public static void restartServer() {
        try {
            Bukkit.getServer().shutdown();
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.INFO, "Failed shutdown server.", ex);
        }
    }

    public static List<Player> getOnlinePlayers() {
        final List<Player> players = new ArrayList<>();
        for (final World world : Bukkit.getWorlds()) {
            players.addAll(world.getPlayers());
        }
        return players;
    }

    public static String[] readAllLines(File file) {
        final List<String> data = new ArrayList<>();
        try {
            if (!file.exists())
                file.createNewFile();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    data.add(line);
                }
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to read all lines.", e);
        }
        return data.toArray(new String[data.size()]);
    }

    public static boolean writeAllLines(File file, String... text) {
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
                for (final String aText : text) {
                    bufferedWriter.write(aText + '\n');
                }
            }
            return true;
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to write all lines.", e);
            return false;
        }
    }

    public static boolean writeAllLines(File file, List<String> text) {
        return writeAllLines(file, text.toArray(new String[text.size()]));
    }
}
