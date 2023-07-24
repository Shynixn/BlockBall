package com.github.shynixn.blockball.api.business.enumeration

/**
 * Placeholder being used in BlockBall.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
enum class PlaceHolder(
    /**
     * Placeholder value.
     */
    val placeHolder: String
) {

    /**
     * Replaces this placeholder with the display name of the arena.
     */
    ARENA_DISPLAYNAME("<game>"),

    /**
     * Replaces this placeholder with the max player amount of this arena.
     */
    ARENA_SUM_MAXPLAYERS("<summaxplayers>"),

    /**
     * Replaces this placeholder with the current amount of players of this game.
     */
    ARENA_SUM_CURRENTPLAYERS("<sumplayers>"),

    /**
     * Replaces this placeholder with the amount of goals team red has scored.
     */
    RED_GOALS("<redscore>"),

    /**
     * Replaces this placeholder with the amount of goals team blue has scored.
     */
    BLUE_GOALS("<bluescore>"),

    /**
     * Replaces this placeholder with the name of team red.
     */
    TEAM_RED("<red>"),

    /**
     * Replaces this placeholder with the name of team blue.
     */
    TEAM_BLUE("<blue>"),

    /**
     * Replaces this placeholder with the prefix/color of the team red.
     */
    RED_COLOR("<redcolor>"),

    /**
     * Replaces this placeholder with the prefix/color of the team blue.
     */
    BLUE_COLOR("<bluecolor>"),

    /**
     * Replaces this placeholder with the current time of the game.
     */
    TIME("<time>"),

    /**
     * Replaces this placeholder with the player which was the last one interacting with the ball.
     */
    LASTHITBALL("<player>"),

    /**
     * Replaces this placeholder with the state [GameStatus] of the game.
     */
    ARENA_STATE("<state>"),

    /**
     * Replaces this placeholder with the amount of players required to start.
     */
    REMAINING_PLAYERS_TO_START("<remaining>"),

    /**
     * Replaces this placeholder with the server name of the current bungeecord server.
     */
    BUNGEECORD_SERVER_NAME("<server>"),

    /**
     * Replaces this placeholder with displayname of the current team of the action.
     */
    ARENA_TEAMDISPLAYNAME("<team>"),

    /**
     * Replaces this placeholder with prefix/color of the current team of the action.
     */
    ARENA_TEAMCOLOR("<teamcolor>"),

    /**
     * Replaces this placeholder with max players of the current team of the action.
     */
    ARENA_MAX_PLAYERS_ON_TEAM("<maxplayers>"),

    /**
     * Replaces this placeholder with current players of the current team of the action.
     */
    ARENA_PLAYERS_ON_TEAM("<players>")
}
