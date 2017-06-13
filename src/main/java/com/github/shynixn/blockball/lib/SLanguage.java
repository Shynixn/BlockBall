package com.github.shynixn.blockball.lib;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class SLanguage {
    @SPluginLoader.PluginLoader
    private static JavaPlugin plugin;

    public static void reload(Class<?> cls) {
        if (plugin == null)
            throw new IllegalArgumentException("Pluginloader failed to load " + SLanguage.class.getSimpleName() + '.');
        final File file = new File(plugin.getDataFolder(), "lang.yml");
        if (!file.exists())
            buildFile(cls, plugin, file);
        loadFile(cls, file);
    }

    private SLanguage() {
        super();
    }

    private static void loadFile(Class<?> cls, File file) {
        try {
            final HashMap<String, String> map = new HashMap<>();
            for (final String s : SFileUtils.readAllLines(file)) {
                if (s.contains(":")) {
                    String key = "";
                    String value = "";
                    boolean switcher = false;
                    for (int i = 0; i < s.length(); i++) {
                        if (s.charAt(i) == ':')
                            break;
                        key += s.charAt(i);
                    }
                    for (int i = 0; i < s.length(); i++) {
                        if (s.charAt(i) == '"')
                            switcher = !switcher;
                        else if (switcher)
                            value += s.charAt(i);
                    }
                    map.put(key.toLowerCase(), value);
                }
            }
            for (final Field field : cls.getDeclaredFields()) {
                for (final String key : map.keySet()) {
                    if (field.getName().equalsIgnoreCase(key)) {
                        field.set(null, ChatColor.translateAlternateColorCodes('&', map.get(key)));
                    }
                }
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot load config file.", e);
        }
    }

    private static void buildFile(Class<?> cls, JavaPlugin plugin, File file) {
        try {
            file.createNewFile();
            final ArrayList<String> s = new ArrayList<>();
            s.add("#Language");
            s.add("");
            for (final Field field : cls.getDeclaredFields()) {
                s.add(field.getName().toLowerCase() + ": \"" + String.valueOf(field.get(null)).replace('§', '&') + '"');
            }
            SFileUtils.writeAllLines(file, s);
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot build config file.", e);
        }
    }
}
