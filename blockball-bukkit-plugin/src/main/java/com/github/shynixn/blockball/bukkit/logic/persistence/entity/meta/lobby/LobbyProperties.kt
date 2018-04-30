package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby

import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.meta.lobby.LobbyMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import org.bukkit.ChatColor
import org.bukkit.GameMode

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
class LobbyProperties : PersistenceObject(), LobbyMeta {
    /** Max score of a team until the match ends and the arena gets reset. */
    @YamlSerializer.YamlSerialize(value = "max-score", orderNumber = 1)
    override var maxScore: Int = 10
    /** Should players automatically join the other team to even out them?*/
    @YamlSerializer.YamlSerialize(value = "even-teams", orderNumber = 2)
    override var onlyAllowEventTeams: Boolean = false
    /** List of signs which can be clicked to join the game. */
    override val joinSigns: MutableList<StorageLocation>
        get() = sign.joinSigns as MutableList<StorageLocation>
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "join-sign-lines", implementation = List::class) // Compatibility implementation. Support will be removed after August 2018.
    override var joinSignLines: List<String> = arrayListOf("&lBlockBall", PlaceHolder.ARENA_DISPLAYNAME.placeHolder, PlaceHolder.ARENA_STATE.placeHolder, PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder + '/' + PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder)
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "leave-sign-lines", implementation = List::class) // Compatibility implementation. Support will be removed after August 2018.
    override var leaveSignLines: List<String> = arrayListOf("&lBlockBall", PlaceHolder.ARENA_DISPLAYNAME.placeHolder, ChatColor.WHITE.toString() + "Leave", PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder + '/' + PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder)
    /** List of signs which can be clicked to leave the game. */
    override val leaveSigns: MutableList<StorageLocation>
        get() = sign.leaveSigns as MutableList<StorageLocation>
    /** Spawnpoint when someone leaves the hub game. */
    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "leave-spawnpoint", implementation = LocationBuilder::class)
    override var leaveSpawnpoint: StorageLocation? = null
    /** Minecraft gamemode (Survival, Adventure, Creative) the players should be */
    @YamlSerializer.YamlSerialize(orderNumber = 6, value = "gamemode", implementation = GameMode::class)
    override var gamemode: Enum<*> = GameMode.ADVENTURE

    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "signs")
    private val sign: SignCollection = SignCollection()

    /** Helper class to wrap signs. */
    private class SignCollection {
        /** List of signs which can be clicked to join the game. */
        @YamlSerializer.YamlSerialize(orderNumber = 1, value = "joining")
        val joinSigns: MutableList<LocationBuilder> = ArrayList()
        /** List of signs which can be clicked to leave the game. */
        @YamlSerializer.YamlSerialize(orderNumber = 2, value = "leaving")
        val leaveSigns: MutableList<LocationBuilder> = ArrayList()
    }
}