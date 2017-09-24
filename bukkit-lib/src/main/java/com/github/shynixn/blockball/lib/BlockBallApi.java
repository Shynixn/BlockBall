package com.github.shynixn.blockball.lib;

import com.github.shynixn.blockball.api.business.controller.BallController;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.entities.Arena;
import com.github.shynixn.blockball.api.entities.Ball;
import com.github.shynixn.blockball.api.persistence.controller.ArenaController;
import com.github.shynixn.blockball.lib.ReflectionUtils;
import org.bukkit.World;

import java.lang.reflect.InvocationTargetException;

/**
 * API to access BlockBall games.
 */
public final class BlockBallApi {
    private static BallController ballController;
    private static GameController gameController;

    private BlockBallApi() {
        super();
    }

    private static void initialize() {
        try {
            ballController = ReflectionUtils.invokeConstructor(Class.forName("com.github.shynixn.blockball.business.logic.ball.BallRepository"), new Class[]{}, new Object[]{});
            gameController = ReflectionUtils.invokeConstructor(Class.forName("com.github.shynixn.blockball.business.logic.game.GameController"), new Class[]{}, new Object[]{});
            gameController.reload();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static BallController getBallController() {
        return ballController;
    }

    public static GameController getGameController() {
        return gameController;
    }

    public static ArenaController getArenaController() {
        return gameController.getArenaController();
    }

    @Deprecated
    public static Ball createNewBall(World world) {
        if (world == null)
            throw new IllegalArgumentException("World cannot be null!");
        final Ball ball = ballController.createBall(world);
        ballController.store(ball);
        return ball;
    }

    @Deprecated
    public static void closeGames() {
        try {
            gameController.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void reloadGames() {
        try {
            gameController.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void save(Arena arena) {
        gameController.getArenaController().store(arena);
    }
}
