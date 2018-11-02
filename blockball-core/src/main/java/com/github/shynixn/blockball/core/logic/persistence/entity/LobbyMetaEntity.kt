@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.api.persistence.entity.LobbyMeta

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
class LobbyMetaEntity : LobbyMeta {
    /** Max score of a team until the match ends and the arena gets reset. */
    @YamlSerialize(value = "max-score", orderNumber = 1)
    override var maxScore: Int = 10
    /** Should players automatically join the other team to even out them?*/
    @YamlSerialize(value = "even-teams", orderNumber = 2)
    override var onlyAllowEventTeams: Boolean = false
    /** List of signs which can be clicked to join the game. */
    override val joinSigns: MutableList<Position>
        get() = sign.joinSigns as MutableList<Position>
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerialize(orderNumber = 3, value = "join-sign-lines", implementation = List::class) // Compatibility implementation. Support will be removed after August 2018.
    override var joinSignLines: List<String> = arrayListOf("&lBlockBall", PlaceHolder.ARENA_DISPLAYNAME.placeHolder, PlaceHolder.ARENA_STATE.placeHolder, PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder + '/' + PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder)
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerialize(orderNumber = 4, value = "leave-sign-lines", implementation = List::class) // Compatibility implementation. Support will be removed after August 2018.
    override var leaveSignLines: List<String> = arrayListOf("&lBlockBall", PlaceHolder.ARENA_DISPLAYNAME.placeHolder, ChatColor.WHITE.toString() + "Leave", PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder + '/' + PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder)
    /** List of signs which can be clicked to leave the game. */
    override val leaveSigns: MutableList<Position>
        get() = sign.leaveSigns as MutableList<Position>
    /** Spawnpoint when someone leaves the hub game. */
    @YamlSerialize(orderNumber = 5, value = "leave-spawnpoint", implementation = PositionEntity::class)
    override var leaveSpawnpoint: Position? = null
    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    @YamlSerialize(orderNumber = 6, value = "gamemode", implementation = GameMode::class)
    override var gamemode: GameMode = GameMode.ADVENTURE

    @YamlSerialize(orderNumber = 7, value = "signs")
    private val sign: SignCollection = SignCollection()
}