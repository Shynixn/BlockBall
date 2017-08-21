package com.github.shynixn.blockball.business.bukkit.nms;

import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.business.bukkit.dependencies.bossbar.BossBarConnection;
import com.github.shynixn.blockball.business.bukkit.dependencies.placeholderapi.PlaceHolderApiConnection;
import com.github.shynixn.blockball.business.bukkit.dependencies.vault.VaultConnection;
import com.github.shynixn.blockball.business.bukkit.dependencies.worldguard.WorldGuardConnection5;
import com.github.shynixn.blockball.business.bukkit.dependencies.worldguard.WorldGuardConnection6;
import com.github.shynixn.blockball.lib.LightRegistry;
import com.github.shynixn.blockball.lib.ReflectionLib;
import com.github.shynixn.blockball.lib.RegisterHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class NMSRegistry {
    private NMSRegistry() {
        super();
    }

    public static Ball createBall(World world) {
        return (Ball) ReflectionLib.invokeConstructor(ReflectionLib.getClassFromName("com.github.shynixn.blockball.business.bukkit.nms.VERSION.CustomArmorstand"), world, true);
    }

    public static void registerDynamicCommand(String command, BukkitCommand clazz) {
        Object obj = ReflectionLib.getClassFromName("org.bukkit.craftbukkit.VERSION.CraftServer").cast(Bukkit.getServer());
        obj = ReflectionLib.invokeMethodByObject(obj, "getCommandMap");
        ReflectionLib.invokeMethodByObject(obj, "register", command, clazz);
    }

    public static void accessWorldGuardSpawn(Location location) {
        if (RegisterHelper.isRegistered("WorldGuard")) {
            try {
                if (RegisterHelper.isRegistered("WorldGuard", '6'))
                    WorldGuardConnection6.allowSpawn(location, getWorldGuard());
                else if (RegisterHelper.isRegistered("WorldGuard", '5'))
                    WorldGuardConnection5.allowSpawn(location, getWorldGuard());
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot access worldguard.", e);
            }
        }
    }

    public static String getCurrencyName() {
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            return VaultConnection.getCurrencyName();
        }
        return null;
    }

    public static void addMoney(double amount, Player... players) {
        if (RegisterHelper.isRegistered("Vault") && VaultConnection.setupEconomy()) {
            VaultConnection.add(amount, players);
        }
    }

    public static void rollbackWorldGuardSpawn(Location location) {
        if (RegisterHelper.isRegistered("WorldGuard")) {
            try {
                if (RegisterHelper.isRegistered("WorldGuard", '6'))
                    WorldGuardConnection6.rollBack();
                else if (RegisterHelper.isRegistered("WorldGuard", '5'))
                    WorldGuardConnection5.rollBack();
            } catch (final Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot access worldguard.", e);
            }
        }
    }

    public static void setBossBar(Player player, String message) {
        if (RegisterHelper.isRegistered("BossBarAPI")) {
            if (message == null) {
                BossBarConnection.removeBossBar(player);
            } else {
                BossBarConnection.updateBossBar(player, message);
            }
        }
    }

    public static void registerAll() {
        try {
            LightRegistry.RABBIT.register("com.github.shynixn.blockball.business.bukkit.nms.VERSION.CustomRabbit");
            RegisterHelper.PREFIX = BlockBallPlugin.PREFIX_CONSOLE;
            RegisterHelper.register("WorldGuard", "com.sk89q.worldguard.protection.ApplicableRegionSet", '5');
            RegisterHelper.register("WorldGuard", "com.sk89q.worldguard.protection.ApplicableRegionSet", '6');
            RegisterHelper.register("BossBarAPI");
            RegisterHelper.register("Vault");
            if (RegisterHelper.register("PlaceholderAPI")) {
                PlaceHolderApiConnection.init((JavaPlugin) Bukkit.getPluginManager().getPlugin("BlockBall"));
            }
        } catch (final Error ex) {
            Bukkit.getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.DARK_RED + "Failed to register the last dependency.");
        }
    }

    public static void unregisterAll() {
        LightRegistry.unregister();
    }

    private static Plugin getWorldGuard() {
        return Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
    }
}
