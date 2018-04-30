package com.github.shynixn.blockball.api.business.enumeration;

/**
 * Created by Shynixn 2018.
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
public enum PlaceHolder {

    ARENA_DISPLAYNAME("<game>"),
    ARENA_SUM_MAXPLAYERS("<summaxplayers>"),
    ARENA_SUM_CURRENTPLAYERS("<sumplayers>"),
    RED_GOALS("<redscore>"),
    BLUE_GOALS("<bluescore>"),
    TEAM_RED("<red>"),
    TEAM_BLUE("<blue>"),
    RED_COLOR("<redcolor>"),
    BLUE_COLOR("<bluecolor>"),

    TIME("<time>"),
    LASTHITBALL("<player>"),
    ARENA_STATE("<state>"),
    REMAINING_PLAYERS_TO_START("<remaining>"),

    STATS_PLAYER_NAME("<playername>"),
    STATS_WINRATE("<winrate>"),
    STATS_PLAYEDGAMES("<playedgames>"),
    STATS_GOALS_PER_GAME("<goalspergame>"),

    BUNGEECORD_SERVER_NAME("<server>"),

    ARENA_TEAMDISPLAYNAME("<team>"),
    ARENA_TEAMCOLOR("<teamcolor>"),
    ARENA_MAX_PLAYERS_ON_TEAM("<maxplayers>"),
    ARENA_PLAYERS_ON_TEAM("<players>"),

    PLACEHOLDERAPI_ARENA_DISPLAYNAME("%blockball_" + ARENA_DISPLAYNAME.placeHolder),
    PLACEHOLDERAPI_ARENA_SUM_MAXPLAYERS("%blockball_" + ARENA_SUM_MAXPLAYERS.placeHolder),
    PLACEHOLDERAPI_ARENA_SUM_CURRENTPLAYERS("%blockball_" + ARENA_SUM_CURRENTPLAYERS.placeHolder),
    PLACEHOLDERAPI_RED_GOALS("%blockball_" + RED_GOALS.placeHolder),
    PLACEHOLDERAPI_BLUE_GOALS("%blockball_" + BLUE_GOALS.placeHolder),
    PLACEHOLDERAPI_TEAM_RED("%blockball_" + TEAM_RED.placeHolder),
    PLACEHOLDERAPI_TEAM_BLUE("%blockball_" + TEAM_BLUE.placeHolder),
    PLACEHOLDERAPI_RED_COLOR("%blockball_" + RED_COLOR.placeHolder),
    PLACEHOLDERAPI_BLUE_COLOR("%blockball_" + BLUE_COLOR.placeHolder),

    PLACEHOLDERAPI_TIME("%blockball_" + TIME.placeHolder),
    PLACEHOLDERAPI_LASTHITBALL("%blockball_" + LASTHITBALL.placeHolder),
    PLACEHOLDERAPI_ARENA_STATE("%blockball_" + ARENA_STATE.placeHolder);

    public String concat(PlaceHolder placeHolder) {
        return this.placeHolder + placeHolder.placeHolder;
    }

    private final String placeHolder;

    PlaceHolder(String placeholder) {
        this.placeHolder = placeholder;
    }

    public String getPlaceHolder() {
        return this.placeHolder;
    }
}
