package com.github.shynixn.blockball.bukkit.logic.business.entity.bungeecord

import com.github.shynixn.blockball.api.business.entity.BungeeCordServerStatus
import com.github.shynixn.blockball.api.business.enumeration.BungeeCordServerState
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.BungeeCordConfig
import org.bukkit.ChatColor

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
class BungeeCordServerStats : BungeeCordServerStatus {
    /** State of the server. **/
    override var status: BungeeCordServerState = BungeeCordServerState.UNKNOWN
    /** Amount of players on the server. **/
    override var playerAmount: Int = 0
    /** MaxAmount of players on the server. **/
    override var playerMaxAmount: Int = 0
    /** Name of the server. **/
    override var serverName: String? = null

    constructor(serverName: String) {
        this.serverName = serverName
    }

    constructor(serverName: String, data: String) {
        this.serverName = serverName;
        try {
            var state = BungeeCordServerState.UNKNOWN
            var motd: String
            val motdBuilder = StringBuilder()
            var started = false
            var i: Int
            i = 0
            while (i < data.length) {
                if (data[i] == '[') {
                    started = true
                } else if (data[i] == ']') {
                    break
                } else if (started) {
                    motdBuilder.append(data[i])
                }
                i++
            }
            try { //TODO MANAGE SIGNS CORRETLY
                motd = ChatColor.translateAlternateColorCodes('&', motdBuilder.toString()).substring(0, motdBuilder.length - 2).replace("§","&")
                if (motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.inGameMotd, true)) {
                    state = BungeeCordServerState.INGAME;
                } else if (motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.restartingMotd, true)) {
                    state = BungeeCordServerState.RESTARTING;
                } else if (motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.waitingForPlayersMotd, true)) {
                    state = BungeeCordServerState.WAITING_FOR_PLAYERS;
                }
            } catch (ex: Exception) {
                state = BungeeCordServerState.RESTARTING
            }

            motd = ""
            started = false
            while (i < data.length) {
                if (data[i] == '§') {
                    if (started) {
                        break
                    } else {
                        started = true
                    }
                } else if (started) {
                    motd += data[i]
                }
                i++
            }
            this.playerAmount = Integer.parseInt(motd)
            motd = ""
            started = false
            while (i < data.length) {
                if (data[i] == '§')
                    started = true
                else if (started)
                    motd += data[i]
                i++
            }
            this.status = state
            this.playerMaxAmount = Integer.parseInt(motd)
        } catch (ex: Exception) {
            this.status = BungeeCordServerState.RESTARTING
        }
    }
}