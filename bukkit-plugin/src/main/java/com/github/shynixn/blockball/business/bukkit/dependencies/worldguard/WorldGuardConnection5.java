package com.github.shynixn.blockball.business.bukkit.dependencies.worldguard;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WorldGuardConnection5 {
    private WorldGuardConnection5() {
        super();
    }

    private static final ArrayList<ProtectedRegion> flags = new ArrayList<>();

    public synchronized static void allowSpawn(Location location, Plugin plugin) {
        final WorldGuardPlugin worldGuard = (WorldGuardPlugin) plugin;
        final RegionManager regionManager = worldGuard.getRegionManager(location.getWorld());
        final ApplicableRegionSet set = regionManager.getApplicableRegions(location);
        for (final ProtectedRegion region : set) {
            if (region.getFlag(DefaultFlag.MOB_SPAWNING) == State.DENY) {
                region.setFlag(DefaultFlag.MOB_SPAWNING, State.ALLOW);
                flags.add(region);
            }
        }
    }

    public synchronized static void rollBack() {
        for (final ProtectedRegion region : flags.toArray(new ProtectedRegion[flags.size()])) {
            region.setFlag(DefaultFlag.MOB_SPAWNING, State.DENY);
        }
        flags.clear();
    }
}

