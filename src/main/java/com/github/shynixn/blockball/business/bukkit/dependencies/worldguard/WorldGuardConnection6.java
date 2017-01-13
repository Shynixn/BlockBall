package com.github.shynixn.blockball.business.bukkit.dependencies.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public final class WorldGuardConnection6 {
    private WorldGuardConnection6() {
        super();
    }

    private static final ArrayList<ProtectedRegion> flags = new ArrayList<>();

    public synchronized static void allowSpawn(Location location, Plugin plugin) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin;
        final RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        final ApplicableRegionSet set = regionManager.getApplicableRegions(location);
        @SuppressWarnings("unchecked") final
        Iterable<ProtectedRegion> regions = (Iterable<ProtectedRegion>) getMethod(set.getClass()).invoke(set);
        for (final ProtectedRegion region : regions) {
            if (region.getFlag(DefaultFlag.MOB_SPAWNING) == State.DENY) {
                region.setFlag(DefaultFlag.MOB_SPAWNING, State.ALLOW);
                flags.add(region);
            }
        }
    }

    private static Method getMethod(Class<?> class1) {
        for (final Method method : class1.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase("getRegions"))
                return method;
        }
        throw new RuntimeException("Cannot hook into WorldGuard!");
    }

    public synchronized static void rollBack() {
        for (final ProtectedRegion region : flags.toArray(new ProtectedRegion[flags.size()])) {
            region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
        }
        flags.clear();
    }
}
