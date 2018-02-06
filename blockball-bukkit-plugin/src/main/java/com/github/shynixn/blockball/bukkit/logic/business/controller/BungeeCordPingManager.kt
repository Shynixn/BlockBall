package com.github.shynixn.blockball.bukkit.logic.business.controller

import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectionController
import com.github.shynixn.blockball.api.business.entity.BungeeCordServerStatus
import com.github.shynixn.blockball.bukkit.logic.business.entity.bungeecord.BungeeCordServerStats
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.NetworkSignRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.messaging.PluginMessageListener
import org.bukkit.scheduler.BukkitTask
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.util.logging.Level

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
class BungeeCordPingManager : BungeeCordConnectionController<Player>, Runnable, PluginMessageListener {

    @Inject
    private var signController: NetworkSignRepository? = null

    private val plugin: Plugin
    private val task: BukkitTask

    @Inject
    constructor(plugin: Plugin) {
        this.plugin = plugin
        this.task = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0L, 10L)
        plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)
    }


    /** Connects the given player to the given server.**/
    override fun connectToServer(player: Player, serverName: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(serverName)
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray())
    }

    /** Pings all servers which are present in the sub sign controller. **/
    override fun pingServers() {
        val player = getFirstPlayer()
        for (s in this.signController!!.linkedServers) {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("ServerIP")
            out.writeUTF(s)
            player!!.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray())
        }
    }

    override fun onPluginMessageReceived(p0: String?, p1: Player?, p2: ByteArray?) {
        if (p0 != "BungeeCord") {
            return
        }
        val `in` = ByteStreams.newDataInput(p2)
        val type = `in`.readUTF()
        if (type == "ServerIP") {
            val serverName = `in`.readUTF()
            val ip = `in`.readUTF()
            val port = `in`.readShort()
            this.plugin.server.scheduler.runTaskAsynchronously(this.plugin) {
                val data = this.receiveResultFromServer(serverName, ip, port.toInt())
                this.parseData(serverName, data)
            }
        }
    }

    private fun receiveResultFromServer(serverName: String, hostname: String, port: Int): String? {
        var data: String? = null
        try {
            Socket(hostname, port).use { socket ->
                DataOutputStream(socket.getOutputStream()).use { out ->
                    out.write(0xFE)
                    DataInputStream(socket.getInputStream()).use { cin ->
                        val buffer = StringBuilder()
                        while (true) {
                            val b = cin.read()
                            if (b == -1)
                                break
                            if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                                buffer.append(b.toChar())
                            }
                        }
                        data = buffer.toString()
                    }
                }
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to reach server " + serverName + " (" + hostname + ':'.toString() + port + ").")
        }
        return data
    }

    private fun parseData(serverName: String, data: String?) {
        try {
            if (data == null)
                return
            val serverInfo = BungeeCordServerStats.from(serverName, data)
            this.plugin.server.scheduler.runTask(this.plugin) { this.updateSigns(serverInfo) }
        } catch (e: Exception) {
            Bukkit.getLogger().log(Level.WARNING, "Cannot parse result from server.", e)
        }

    }

    private fun updateSigns(status: BungeeCordServerStatus) {
        for (signInfo in this.signController!!.getAll()) {
            if (signInfo.server == status.serverName) {
                val location = (signInfo.position as LocationBuilder).toLocation()
                if (location.block.state is Sign) {
                    val sign = location.block.state as Sign
                    /*     sign.setLine(0, this.replaceSign(BungeeCord.SIGN_LINE_1, status));
                    sign.setLine(1, this.replaceSign(BungeeCord.SIGN_LINE_2, status));
                    sign.setLine(2, this.replaceSign(BungeeCord.SIGN_LINE_3, status));
                    sign.setLine(3, this.replaceSign(BungeeCord.SIGN_LINE_4, status));*/
                    sign.update()
                } else {
                //   this.signController.remove(signInfo)
                }
            }
        }
    }


    private fun replaceSign(line: String, info: BungeeCordServerStatus): String {
/*    if (info.getStatus() == BungeeCordServerState.INGAME)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_INGAME);
        else if (info.getStatus() == BungeeCordServerState.RESTARTING)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_RESTARTING);
        else if (info.getStatus() == BungeeCordServerState.WAITING_FOR_PLAYERS)
            customLine = customLine.replace("<state>", BungeeCord.SIGN_WAITING_FOR_PLAYERS);
        else if (info.getStatus() == BungeeCordServerState.UNKNOWN)
            customLine = customLine.replace("<state>", "UNKNOWN");*/
        return line.replace("<maxplayers>", (info.playerMaxAmount * 2).toString())
                .replace("<players>", info.playerAmount.toString())
                .replace("<server>", info.serverName)
    }

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        this.pingServers()
    }

    /**
     * Returns a nullable player.
     *
     * @return player
     */
    private fun getFirstPlayer(): Player? {
        for (world in Bukkit.getWorlds()) {
            if (!world.players.isEmpty())
                return world.players[0]
        }
        return null
    }
}