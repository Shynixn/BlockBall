package com.github.shynixn.blockball.business.logic.game;

import com.github.shynixn.blockball.api.entities.GameType;
import com.github.shynixn.blockball.business.Config;
import com.github.shynixn.blockball.business.bukkit.BlockBallPlugin;
import com.github.shynixn.blockball.lib.SimpleListener;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.entities.Game;
import com.github.shynixn.blockball.business.logic.arena.ArenaController;
import org.bukkit.plugin.java.JavaPlugin;

public final class GameController extends SimpleListener {
    ArenaController arenaManager;
    GameEntity[] games;

    public GameController() {
        super(JavaPlugin.getPlugin(BlockBallPlugin.class));
        this.arenaManager = ArenaController.createArenaController(this);
        new EventCommandExecutor(this);
        if (Config.getInstance().getGlobalJoinCommand().isEnabled())
            new GlobalJoinCommandExecutor(this);
        if (Config.getInstance().getGlobalLeaveCommand().isEnabled())
            new GlobalLeaveCommandExecutor(this);
        new GameListener(this);
        this.run();
    }

    public void save(Arena arena) {
        this.arenaManager.persist(arena);
    }

    private void run() {
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            if (GameController.this.games != null) {
                for (final GameEntity game : GameController.this.games) {
                    if (game == null)
                        throw new RuntimeException("There cannot be a game null!");
                    game.run();
                }
            }
        }, 0L, 1L);
    }

    Game getGameFromBall(Ball ball) {
        for (final GameEntity game : this.games) {
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

    public void close() {
        if (this.games == null)
            return;
        for (final GameEntity game : this.games) {
            game.reset();
        }
    }

    public void reload() {
        this.arenaManager.reload();
        if (this.games != null) {
            for (final GameEntity game : this.games) {
                game.reset();
            }
        }
        this.games = new GameEntity[this.arenaManager.getArenas().size()];
        for (int i = 0; i < this.games.length; i++) {
            final Arena arena = this.arenaManager.getArenas().get(i);
            if (arena.getGameType() == GameType.BUNGEE) {
                this.games[i] = new BungeeGameEntity(arena);
            } else if (arena.getGameType() == GameType.LOBBY) {
                this.games[i] = new HubGameEntity(arena);
            } else if (arena.getGameType() == GameType.MINIGAME) {
                this.games[i] = new MiniGameEntity(arena);
            } else if (arena.getGameType() == GameType.EVENT) {
                this.games[i] = new EventGameEntity(arena);
            }
        }
    }
}
