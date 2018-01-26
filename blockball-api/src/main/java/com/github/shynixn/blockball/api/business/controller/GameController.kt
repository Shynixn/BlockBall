package com.github.shynixn.blockball.api.business.controller

import com.github.shynixn.blockball.api.business.entity.Game
import com.github.shynixn.blockball.api.persistence.controller.ArenaController
import com.github.shynixn.blockball.api.persistence.controller.Controller
import com.github.shynixn.blockball.api.persistence.controller.ReloadableController
import com.github.shynixn.blockball.api.persistence.entity.Arena

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
interface GameController<Location : Any,Vector, Player, ItemStack, ArenaEntity, GameEntity : Game<ArenaEntity, Player, Location, ItemStack, Vector>>
    : ReloadableController<GameEntity> where ArenaEntity : Arena<Location, ItemStack, Vector> {

    /** ArenaController of the gameController. */
    val arenaController: ArenaController<Location, ArenaEntity>

    /** Returns the game with the given arena name. */
    fun getGameFromArenaName(name: String): GameEntity?

    /** Returns the game with the given arena displayName. */
    fun getGameFromArenaDisplayName(name: String): GameEntity?

    /** Returns the game with the [player] inside. */
    fun getGameFromPlayer(player: Player): GameEntity?
}