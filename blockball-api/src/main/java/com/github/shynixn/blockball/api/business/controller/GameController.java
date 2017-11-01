package com.github.shynixn.blockball.api.business.controller;

import com.github.shynixn.blockball.api.business.entity.Game;
import com.github.shynixn.blockball.api.persistence.controller.ArenaController;
import com.github.shynixn.blockball.api.persistence.controller.IController;
import com.github.shynixn.blockball.api.persistence.controller.IFileController;

import java.util.Optional;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public interface GameController extends IFileController<Game> {

    /**
     * Returns the controller of the arenas.
     *
     * @return controller
     */
    ArenaController getArenaController();

    /**
     * Returns the game from the given unique arena name.
     *
     * @param name name
     * @return game
     */
    Optional<Game> getGameFromArenaName(String name);

    /**
     * Returns the game where the given player is currently part of.
     *
     * @param player player
     * @return game
     */
    Optional<Game> getGameFromPlayer(Object player);

    /**
     * Returns the game from the displayName. Prints a warning if there are more than one with this displayName.
     *
     * @param arg arg
     * @return game
     */
    Optional<Game> getGameFromDisplayName(String arg);
}
