package com.github.shynixn.blockball.api;

import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.blockball.business.logic.ball.BallController;
import com.github.shynixn.blockball.business.logic.game.GameController;
import org.bukkit.World;

/**
 * API to access BlockBall games.
 */
public final class BlockBallApi {
    private static BallController manager;
    private static GameController gameManager;

    private BlockBallApi() {
        super();
    }

    private static void initialize() {
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
