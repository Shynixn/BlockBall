package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.GameType;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.business.logic.arena.ArenaController;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GameController implements com.github.shynixn.blockball.api.business.controller.GameController {
    private final Plugin plugin;
    final ArenaController arenaManager;
    private List<Game> games = new ArrayList<>();

    public GameController() {
        super();
        this.plugin = JavaPlugin.getPlugin(BlockBallPlugin.class);
        this.arenaManager = ArenaController.createArenaController(this);
        new EventCommandExecutor(this);
        if (Config.getInstance().getGlobalJoinCommand().isEnabled())
            new GlobalJoinCommandExecutor(this);
        if (Config.getInstance().getGlobalLeaveCommand().isEnabled())
            new GlobalLeaveCommandExecutor(this);
        new GameListener(this);
        this.run();
    }

    private void run() {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            if (GameController.this.games != null) {
                for (final Game game : GameController.this.games) {
                    if (game == null)
                        throw new RuntimeException("There cannot be a game null!");
                    ((GameEntity) game).run();
                }
            }
        }, 0L, 1L);
    }

    /**
     * Stores a new a item in the repository
     *
     * @param item item
     */
    @Override
    public void store(Game item) {
        if (!this.games.contains(item)) {
            this.games.add(item);
        }
    }

    /**
     * Removes an item from the repository
     *
     * @param item item
     */
    @Override
    public void remove(Game item) {
        if (this.games.contains(item)) {
            this.games.remove(item);
        }
    }

    /**
     * Returns the amount of items in the repository
     *
     * @return size
     */
    @Override
    public int size() {
        return this.games.size();
    }

    /**
     * Returns all items from the repository as unmodifiableList
     *
     * @return items
     */
    @Override
    public List<Game> getAll() {
        return Collections.unmodifiableList(this.games);
    }

    Game getGameFromBall(Ball ball) {
        for (final Game game : this.games) {
            if (game.getBall() != null && game.getBall().equals(ball))
                return game;
        }
        return null;
    }

    Game isInGameLobby(Player player) {
        for (final Game game : this.games) {
            if (game instanceof HelperGameEntity && ((HelperGameEntity) game).isInLobby(player))
                return game;
        }
        return null;
    }

    Game getGameFromAlias(String alias) {
        for (final Game game : this.games) {
            if (game.getArena().getAlias() != null && ChatColor.stripColor(game.getArena().getAlias()).equalsIgnoreCase(ChatColor.stripColor(alias)))
                return game;
        }
        return null;
    }

    Game getGameFromPlayer(Player player) {
        for (final Game game : this.games) {
            if (game.isInGame(player))
                return game;
        }
        return null;
    }

    Game getGameFromArenaId(int id) {
        for (final Game game : this.games) {
            if (game.getArena().getId() == id)
                return game;
        }
        return null;
    }

    /**
     * Returns the controller of the arenas
     *
     * @return controller
     */
    @Override
    public com.github.shynixn.blockball.api.persistence.controller.ArenaController getArenaController() {
        return this.arenaManager;
    }

    /**
     * Resets and reloads all games
     */
    @Override
    public void reload() {
        this.arenaManager.reload();
        if (this.games != null) {
            for (final Game game : this.games) {
                ((GameEntity) game).reset();
            }
        }
        this.games = new ArrayList<>();
        for (int i = 0; i < this.arenaManager.size(); i++) {
            final Arena arena = this.arenaManager.getArenas().get(i);
            if (arena.getGameType() == GameType.BUNGEE) {
                this.games.add(new BungeeGameEntity(arena));
            } else if (arena.getGameType() == GameType.LOBBY) {
                this.games.add(new HubGameEntity(arena));
            } else if (arena.getGameType() == GameType.MINIGAME) {
                this.games.add(new MiniGameEntity(arena));
            } else if (arena.getGameType() == GameType.EVENT) {
                this.games.add(new EventGameEntity(arena));
            }
        }
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        if (this.games == null)
            return;
        for (final Game game : this.games) {
            ((GameEntity) game).reset();
        }
    }
}
