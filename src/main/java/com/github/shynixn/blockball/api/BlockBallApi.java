package com.github.shynixn.blockball.api;

import com.github.shynixn.blockball.business.logic.game.GameController;
import com.github.shynixn.blockball.lib.SPluginLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.logic.ball.BallController;
import org.bukkit.util.Vector;

/**
 * API to access BlockBall games.
 */
public final class BlockBallApi {
    private static BallController manager;
    private static GameController gameManager;

    private BlockBallApi() {
        super();
    }

    @SPluginLoader.PluginLoader
    private static void initialize(JavaPlugin plugin) {
        manager = new BallController();
        gameManager = new GameController();
        gameManager.reload();
    }

    public static Ball createNewBall(World world) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null!");
        final Ball ball = NMSRegistry.createBall(world);
        manager.addBall(ball);


        return ball;
    }

    public static void closeGames() {
        gameManager.close();
    }

    public static void reloadGames() {
        gameManager.reload();
    }

    public static void save(Arena arena) {
        gameManager.save(arena);
    }
}
