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

public final class SFileUtils {
    public static File createFile(File file) {
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            return file;
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to create file.", e);
        }
        return null;
    }

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
            for (final Player player : world.getPlayers()) {
                players.add(player);
            }
        }
        return players;
    }

    public static String[] readAllLines(File file) {
        final List<String> data = new ArrayList<>();
        try {
            if (!file.exists())
                file.createNewFile();
            final FileReader in = new FileReader(file.getPath());
            final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                data.add(line);
            }
            in.close();
            br.close();
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
            final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
            for (final String aText : text) {
                bufferedWriter.write(aText + "\n");
            }
            bufferedWriter.close();
            return true;
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to write all lines.", e);
            return false;
        }
    }

    public static boolean writeAllLines(File file, List<String> text) {
        return writeAllLines(file, text.toArray(new String[text.size()]));
    }

    public static boolean deleteAll(File folder) {
        try {
            if (folder.isDirectory()) {
                if (folder.list().length != 0) {
                    for (final String s : folder.list()) {
                        final File file = new File(folder, s);
                        if (file.isDirectory()) {
                            deleteAll(file);
                        } else {
                            file.delete();
                        }
                    }
                    deleteAll(folder);
                } else {
                    folder.delete();
                }
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to delete directory.", e);
        }
        return true;
    }

    public static boolean copyResourceFile(InputStream in, File file) {
        try (OutputStream out = new FileOutputStream(file)) {
            final byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
        catch (final Exception e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to copy resource.", e);
            return false;
        }
        try {
            in.close();
        } catch (final IOException e) {
            Bukkit.getLogger().log(Level.INFO, "Failed to copy resource.", e);
        }
        return true;
    }
}
