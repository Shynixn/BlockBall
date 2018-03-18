package com.github.shynixn.blockball.bukkit.dependencies;

import com.github.shynixn.blockball.bukkit.dependencies.bossbar.BossBarConnection;
import com.github.shynixn.blockball.bukkit.dependencies.placeholderapi.PlaceHolderApiConnection;
import com.github.shynixn.blockball.bukkit.dependencies.vault.VaultConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashMap;

@Deprecated
public final class RegisterHelper {
    public static String PREFIX;
    private static final String PREFIX_CONSOLE = ChatColor.BLUE.toString() + "[BlockBall] ";

    private static final HashMap<String, String> registered = new HashMap<>();

    public static boolean register(String pluginName) {
        return register(pluginName, null, null);
    }

    public static boolean register(String pluginName, String path) {
        return register(pluginName, path, null);
    }

    public static boolean isRegistered(String pluginName) {
        return isRegistered(pluginName, null);
    }

    public static boolean isRegistered(String pluginName, char version) {
        return registered.containsKey(pluginName) && registered.get(pluginName).charAt(0) == version;
    }

    public static boolean isRegistered(String pluginName, String version) {
        return registered.containsKey(pluginName) && !(version != null && !registered.get(pluginName).equals(version));
    }

    public static boolean register(String pluginName, String path, char version) {
        boolean canregister = true;
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (plugin != null && !plugin.getDescription().getVersion().startsWith(String.valueOf(version)))
            return false;
        if (pluginName != null && plugin != null) {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "found dependency [" + pluginName + ']' + '.');
            if (path != null) {
                try {
                    Class.forName(path);
                } catch (final ClassNotFoundException e) {
                    canregister = false;
                }
            }
            if (Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion().charAt(0) != version) {
                canregister = false;
            }
            if (canregister) {
                registered.put(pluginName, Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion());
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_GREEN + "hooked successfully into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
                return true;
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_RED + "failed to hook into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
            }
        }
        return false;
    }

    public static String getCurrencyName() {
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            return VaultConnection.getCurrencyName();
        }
        return ChatColor.RED + "<Cannot find currency name>";
    }

    public static void addMoney(double amount, Player... players) {
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            VaultConnection.add(amount, players);
        }
    }

    public static void addMoney(double amount, Collection<Player> players) {
        if (amount == 0)
            return;
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            VaultConnection.add(amount, players.toArray(new Player[0]));
        }
    }

    public static String getCurrenyName() {
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            return VaultConnection.getCurrencyName();
        }
        return ChatColor.RED + "<Could obtain currency from Vault>";
    }

    public static void registerAll() {
        try {
            RegisterHelper.PREFIX = PREFIX_CONSOLE;
            RegisterHelper.register("WorldGuard", "com.sk89q.worldguard.protection.ApplicableRegionSet", '5');
            RegisterHelper.register("WorldGuard", "com.sk89q.worldguard.protection.ApplicableRegionSet", '6');
            RegisterHelper.register("BossBarAPI");
            RegisterHelper.register("Vault");
            if (RegisterHelper.register("PlaceholderAPI")) {
                PlaceHolderApiConnection.initializeHook(Bukkit.getPluginManager().getPlugin("BlockBall"));
            }
        } catch (final Error ex) {
            Bukkit.getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.DARK_RED + "Failed to register the last dependency.");
        }
    }

    public static void setBossBar(Player player, String message, double percentage) {
        if (RegisterHelper.isRegistered("BossBarAPI")) {
            if (message == null) {
                BossBarConnection.removeBossBar(player);
            } else {
                BossBarConnection.updateBossBar(player, message, (float) percentage);
            }
        }
    }

    public static boolean register(String pluginName, String path, String version) {
        boolean canregister = true;
        final Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        if (pluginName != null && plugin != null) {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.GRAY + "found dependency [" + pluginName + "].");
            if (path != null) {
                try {
                    Class.forName(path);
                } catch (final ClassNotFoundException e) {
                    canregister = false;
                }
            }
            if (version != null && !Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion().equals(version)) {
                canregister = false;
            }
            if (canregister) {
                registered.put(pluginName, Bukkit.getPluginManager().getPlugin(pluginName).getDescription().getVersion());
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_GREEN + "hooked successfully into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
                return true;
            } else {
                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX + ChatColor.DARK_RED + "failed to hook into [" + pluginName + "] " + plugin.getDescription().getVersion() + '.');
            }
        }
        return false;
    }
}

