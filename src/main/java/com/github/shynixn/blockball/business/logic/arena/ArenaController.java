package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.business.logic.game.GameController;
import com.github.shynixn.blockball.business.logic.persistence.Factory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArenaController {
    private static final int MAX_AMOUNT_ARENA = 10000000;
    private final ArenaFileManager fileManager;
    private final List<ArenaEntity> arenas = new ArrayList<>();

    final GameController manager;

    private ArenaController(GameController manager) {
        super();
        this.fileManager = new ArenaFileManager(JavaPlugin.getPlugin(BlockBallPlugin.class));
        this.manager = manager;
        new ArenaCommandExecutor(this);
        new BlockBallCommandExecutor();
        Factory.initialize(JavaPlugin.getPlugin(BlockBallPlugin.class));
    }

    public void persist(Arena arena) {
        if (arena != null) {
            final int id = Integer.parseInt(arena.getName());
            if (!this.contains(id)) {
                this.arenas.add((ArenaEntity) arena);
            }
            this.fileManager.save(arena);
        }
    }

    public void remove(Arena arena) {
        this.fileManager.delete(arena);
        this.reload();
    }

    public boolean contains(int id) {
        for (final ArenaEntity entity : this.arenas) {
            if (entity.getId() == id)
                return true;
        }
        return false;
    }

    public void reload() {
        this.arenas.clear();
        for (final Arena arena : this.fileManager.load()) {
            this.arenas.add((ArenaEntity) arena);
        }
    }

    ArenaEntity createNewArenaEntity() {
        final ArenaEntity arenaEntity = new ArenaEntity();
        arenaEntity.setName(String.valueOf(this.getNewId()));
        return arenaEntity;
    }

    public List<Arena> getArenas() {
        return Arrays.asList(this.arenas.toArray(new Arena[this.arenas.size()]));
    }

    private int getNewId() {
        for (int i = 0; i < MAX_AMOUNT_ARENA; i++) {
            final String s = String.valueOf(i);
            if (this.getArenaFromName(s) == null) {
                return i;
            }
        }
        return -1;
    }

    Arena getArenaFromName(String name) {
        for (final Arena arena : this.arenas) {
            if (arena.getName().equalsIgnoreCase(name))
                return arena;
        }
        return null;
    }


    public static ArenaController createArenaController(final GameController manager) {
        return new ArenaController(manager);
    }
}
