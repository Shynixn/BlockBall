package com.github.shynixn.blockball.api.business.enumeration

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
enum class MenuCommandResult(
    /**
     * Gets the message result.
     */
    val message: String?
) {
    SUCCESS(null),
    CANCEL_MESSAGE(null),
    BACK(null),

    WESELECTIONHEIGHTGOAL_TOSMALL("The height of the goal is less than 3 blocks. Select a cube instead of a square!"),
    WESELECTIONXAXEGOAL_TOSMALL("The x-axe size of the goal is less than 3 blocks. Select a cube instead of a square!"),
    WESELECTIONZAXEGOAL_TOSMALL("The z-axe size of the goal is less than 3 blocks. Select a cube instead of a square!"),

    WESELECTION_TOSMALL("The height of the arena is less than 10 blocks. Select a cube instead of a square!"),
    WESELECTION_MISSING("Please select an area via the wand!"),
    MINIGAMEARENA_NOTVALID("Please set the center, goal1, goal2, ball spawpoint, lobby spawnpoint and leave spawnpoint before saving!"),
    ARENA_NOTVALID("Please set the center, goal1, goal2 and ball spawpoint before saving!"),

    MAX_PLAYERS("Max amount of players cannot be lower than min amount of players!"),
    MINPLAYERS("Min amount of players cannot be bigger than max amount of players!");
}