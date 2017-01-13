package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.lib.SArenaLite;
import com.github.shynixn.blockball.lib.SLocation;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.LinkedHashMap;
import java.util.Map;

class GoalEntity extends SArenaLite implements ConfigurationSerializable {
    private static final long serialVersionUID = 1L;

    GoalEntity(Location right, Location left) {
        super("", right, left);
    }

    GoalEntity(Map<String, Object> map) throws Exception {
        super();
        this.setCornerLocations(new SLocation(((MemorySection) map.get("corner-1")).getValues(true)).toLocation(), new SLocation(((MemorySection) map.get("corner-2")).getValues(true)).toLocation());
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("corner-1", this.getDownCornerLocation().serialize());
        map.put("corner-2", this.getUpCornerLocation().serialize());
        return map;
    }
}
