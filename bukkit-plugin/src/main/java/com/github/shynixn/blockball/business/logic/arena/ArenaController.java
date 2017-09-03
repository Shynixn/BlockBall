package com.github.shynixn.blockball.business.logic.arena;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.business.logic.game.GameController;
import com.github.shynixn.blockball.business.logic.persistence.Factory;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ArenaController implements com.github.shynixn.blockball.api.persistence.controller.ArenaController{
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

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(Arena item) {
        if (item != null) {
            final int id = Integer.parseInt(item.getName());
            if (!this.contains(id)) {
                this.arenas.add((ArenaEntity) item);
            }
            this.fileManager.save(item);
        }
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(Arena item) {
        this.fileManager.delete(item);
        this.reload();
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.arenas.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<Arena> getAll() {
        return Collections.unmodifiableList(this.arenas);
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

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.arenas.clear();
    }
}
