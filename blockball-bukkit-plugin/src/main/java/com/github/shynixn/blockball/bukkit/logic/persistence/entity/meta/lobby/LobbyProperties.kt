package com.github.shynixn.blockball.bukkit.logic.persistence.entity.meta.lobby

import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.api.persistence.entity.meta.lobby.LobbyMeta
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.PersistenceObject
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder

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
    /** List of signs which can be clicked to join the game. */
    override val joinSigns: MutableList<IPosition>
        get() = sign.joinSigns as MutableList<IPosition>
    /** Lines displayed on the sign for leaving the match. */
    override var joinSignLines: Array<String> = arrayOf("&lBlockBall", "<game>", "<state>", "")
    /** Join asking message. */
    override var joinMesssage: Array<String> = arrayOf("Click on the team to join the match.", "&c[Team Red]", "&9[Team Blue]")
    /** Lines displayed on the sign for leaving the match. */
    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "leave-sign-lines")
    override var leaveSignLines: Array<String> = arrayOf("&lBlockBall", "<game>", "Leave", "")
    /** List of signs which can be clicked to join the red team.*/
    override val redTeamSigns: MutableList<IPosition>
        get() = sign.redTeamSigns as MutableList<IPosition>
    /** List of signs which can be clicked to join the red team.*/
    override val blueTeamSigns: MutableList<IPosition>
        get() = sign.blueTeamSigns as MutableList<IPosition>
    /** List of signs which can be clicked to leave the game. */
    override val leaveSigns: MutableList<IPosition>
        get() = sign.leaveSigns as MutableList<IPosition>
    /** Spawnpoint when someone leaves the hub game. */
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "leave-spawnpoint")
    override var leaveSpawnpoint: LocationBuilder? = null

    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "signs")
    private val sign: SignCollection = SignCollection()

    /** Helper class to wrap signs. */
    private class SignCollection {
        /** List of signs which can be clicked to join the red team.*/
        @YamlSerializer.YamlSerialize(orderNumber = 1, value = "red-team")
        val redTeamSigns: MutableList<LocationBuilder> = ArrayList()
        /** List of signs which can be clicked to join the red team.*/
        @YamlSerializer.YamlSerialize(orderNumber = 2, value = "blue-team")
        val blueTeamSigns: MutableList<LocationBuilder> = ArrayList()
        /** List of signs which can be clicked to join the game. */
        @YamlSerializer.YamlSerialize(orderNumber = 3, value = "joining")
        val joinSigns: MutableList<LocationBuilder> = ArrayList()
        /** List of signs which can be clicked to leave the game. */
        @YamlSerializer.YamlSerialize(orderNumber = 4, value = "leaving")
        val leaveSigns: MutableList<LocationBuilder> = ArrayList()
    }
}