package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordConfiguration

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
class BungeeCordConfigurationEntity : BungeeCordConfiguration {
    /**
     * Modt being sent when the server is restarting.
     */
    @YamlSerialize("motd-restarting", orderNumber = 1)
    override var restartingMotd: String = "&cRestarting..."

    /**
     * Modt being sent when the server is waiting for players.
     */
    @YamlSerialize("motd-waiting-players", orderNumber = 2)
    override var waitingForPlayersMotd: String = "&aWaiting for players..."

    /**
     * Modt being sent when the server is ingame.
     */
    @YamlSerialize("motd-ingame", orderNumber = 3)
    override var inGameMotd: String = "&9Ingame"

    /**
     * Server state being displayed on signs when restarting.
     */
    @YamlSerialize("sign-restarting", orderNumber = 4)
    override var restartingSignState: String = "&cRestarting"

    /**
     * Server state being displayed on signs when waiting for players.
     */
    @YamlSerialize("sign-waiting-players", orderNumber = 5)
    override var waitingForPlayersSignState: String = "&aJoin"

    /**
     * Server state being displayed on signs when ingame.
     */
    @YamlSerialize("sign-ingame", orderNumber = 6)
    override var duringMatchSignState: String = "&9Running"
    /**
     * Template of the server sign.
     */
    @YamlSerialize("sign-lines", orderNumber = 7)
    override var serverSignTemplate: Array<String> = arrayOf("&lBlockBall", PlaceHolder.BUNGEECORD_SERVER_NAME.placeHolder, PlaceHolder.ARENA_STATE.placeHolder, PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder + '/' + PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder)

}