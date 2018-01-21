package com.github.shynixn.blockball.api.bukkit.event;

import com.github.shynixn.blockball.api.bukkit.event.BlockBallCancelAbleEvent;
import org.bukkit.entity.Player;

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
public class PlaceHolderRequestEvent extends BlockBallCancelAbleEvent {
    private String result;
    private final Player player;
    private final PlaceHolderType type;
    private final int game;

    /**
     * Initializes a new placeHolderRequest
     *
     * @param player player
     * @param type   type
     * @param game   game
     */
    public PlaceHolderRequestEvent(Player player, PlaceHolderType type, int game) {
        super();
        this.player = player;
        this.game = game;
        this.type = type;
    }

    /**
     * Returns the game of the player
     *
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Returns the id of the gam
     *
     * @return gameId
     */
    public int getGameId() {
        return this.game;
    }

    /**
     * Returns the type
     *
     * @return type
     */
    public PlaceHolderType getType() {
        return this.type;
    }

    /**
     * Returns the result
     *
     * @return result
     */
    public String getResult() {
        return this.result;
    }

    /**
     * Sets the result of the request
     *
     * @param result result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Type
     */
    public enum PlaceHolderType {
        REDSCORE,
        REDAMOUNT,
        REDCOLOR,
        REDNAME,

        BLUESCORE,
        BLUEAMOUNT,
        BLUECOLOR,
        BLUENAME,

        LASTHITPLAYERNAME;

        /**
         * Returns the type from the name
         *
         * @param name name
         * @return type
         */
        public static Optional<PlaceHolderType> getTypeFromName(String name) {
            for (final PlaceHolderType type : PlaceHolderType.values()) {
                if (name.toUpperCase().contains(type.name().toUpperCase()))
                    return Optional.of(type);
            }
            return Optional.empty();
        }
    }
}
