package com.github.shynixn.blockball.api;

import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectionController;
import com.github.shynixn.blockball.api.business.controller.GameController;
import com.github.shynixn.blockball.api.persistence.controller.LinkSignController;

/**
 * Created by Shynixn 2017.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BlockBallApi {

    private static LinkSignController bungeeCordSignController;
    private static BungeeCordConnectionController bungeeCordConnectController;

    private static GameController gameController;

    private BlockBallApi() {
        super();
    }

    /**
     * Initializes the bungeecord api.
     *
     * @param signController    signController
     * @param connectController connectController
     */
    private static void initializeBungeeCord(LinkSignController signController, BungeeCordConnectionController connectController) {
        BlockBallApi.bungeeCordSignController = signController;
        BlockBallApi.bungeeCordConnectController = connectController;
    }

    /**
     * Initializes the blockBall api.
     *
     * @param gameController gameController
     */
    private static void initializeBlockBall(GameController gameController) {
        BlockBallApi.gameController = gameController;
    }

    /**
     * Returns the default bungeeCordSignController.
     *
     * @return controller
     */
    public static LinkSignController getDefaultBungeeCordSignController() {
        return bungeeCordSignController;
    }

    /**
     * Returns the default bungeeCordConnectController.
     *
     * @return controller
     */
    public static BungeeCordConnectionController getDefaultBungeeCordConnectController() {
        return bungeeCordConnectController;
    }

    /**
     * Returns the default gamController
     *
     * @return controller
     */
    public static GameController getDefaultGameController() {
        return gameController;
    }
}
