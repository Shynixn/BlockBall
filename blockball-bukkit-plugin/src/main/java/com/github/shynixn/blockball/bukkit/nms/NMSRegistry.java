package com.github.shynixn.blockball.bukkit.nms;

import com.github.shynixn.blockball.api.business.entity.Ball;
import com.github.shynixn.blockball.api.persistence.entity.BallMeta;
import com.github.shynixn.blockball.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.bukkit.dependencies.bossbar.BossBarConnection;
import com.github.shynixn.blockball.bukkit.dependencies.placeholderapi.PlaceHolderApiConnection;
import com.github.shynixn.blockball.bukkit.dependencies.vault.VaultConnection;
import com.github.shynixn.blockball.bukkit.dependencies.worldguard.WorldGuardConnection5;
import com.github.shynixn.blockball.bukkit.dependencies.worldguard.WorldGuardConnection6;
import com.github.shynixn.blockball.lib.LightRegistry;
import com.github.shynixn.blockball.lib.ReflectionUtils;
import com.github.shynixn.blockball.lib.RegisterHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public final class NMSRegistry {
    /**
     * Initializes a new registry.
     */
    private NMSRegistry() {
        super();
    }

    /**
     * Creates a new ball at the given location.
     *
     * @param location location
     * @param ballMeta ballMeta
     * @return ball
     */
    public static Ball createBall(Location location, BallMeta ballMeta) {
        try {
            final Class<?> clazz = ReflectionUtils.invokeClass("com.github.shynixn.blockball.business.bukkit.nms.VERSION.CustomArmorstand".replace("VERSION", VersionSupport.getServerVersion().getVersionText()));
            return ReflectionUtils.invokeConstructor(clazz, new Class[]{World.class, BallMeta.class}, new Object[]{location.getWorld(), ballMeta});
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            BlockBallPlugin.logger().log(Level.WARNING, "Failed to create ball.", e);
            throw new RuntimeException(e);
        }
    }

    public static void accessWorldGuardSpawn(Location location) {
        if (RegisterHelper.isRegistered("WorldGuard")) {
            try {
                if (RegisterHelper.isRegistered("WorldGuard", '6'))
                    WorldGuardConnection6.setSpawningAllowedAt(location);
                else if (RegisterHelper.isRegistered("WorldGuard", '5'))
                    WorldGuardConnection5.setSpawningAllowedAt(location);
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
                PlaceHolderApiConnection.initializeHook(Bukkit.getPluginManager().getPlugin("BlockBall"));
            }
        } catch (final Error ex) {
            Bukkit.getConsoleSender().sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.DARK_RED + "Failed to register the last dependency.");
        }
    }

    public static void unregisterAll() {
        LightRegistry.unregister();
    }
}
