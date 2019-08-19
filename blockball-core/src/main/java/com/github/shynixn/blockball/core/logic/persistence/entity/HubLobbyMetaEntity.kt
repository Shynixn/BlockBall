package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.HubLobbyMeta

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
class HubLobbyMetaEntity : HubLobbyMeta {
    /** Join asking message. */
    @YamlSerialize(orderNumber = 1, value = "join-selection")
    override var joinMessage: MutableList<String> = arrayListOf("Click on the team to join the match.", PlaceHolder.RED_COLOR.placeHolder + "[" + PlaceHolder.TEAM_RED.placeHolder + "]", PlaceHolder.BLUE_COLOR.placeHolder + "[" + PlaceHolder.TEAM_BLUE.placeHolder + "]")
    /** Allows to instantly play in games by running into the forcefield.*/
    @YamlSerialize(orderNumber = 2, value = "instant-forcefield-join")
    override var instantForcefieldJoin: Boolean = false
    /** Should the arena be reset when nobody is playing? */
    @YamlSerialize(orderNumber = 3, value = "reset-arena-on-empty")
    override var resetArenaOnEmpty: Boolean = false
    /** Should the player be teleported to the spawnpoint when joining?*/
    @YamlSerialize(orderNumber = 4, value = "teleport-on-join")
    override var teleportOnJoin: Boolean = true
}