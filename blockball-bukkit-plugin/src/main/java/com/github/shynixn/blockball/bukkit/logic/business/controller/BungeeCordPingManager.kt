package com.github.shynixn.blockball.bukkit.logic.business.controller

import com.github.shynixn.blockball.api.business.controller.BungeeCordConnectionController
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordServerStatus
import com.github.shynixn.blockball.api.business.enumeration.BungeeCordServerState
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.controller.LinkSignController
import com.github.shynixn.blockball.bukkit.BlockBallPlugin
import com.github.shynixn.blockball.bukkit.logic.business.commandexecutor.BungeeCordSignCommandExecutor
import com.github.shynixn.blockball.bukkit.logic.business.entity.bungeecord.BungeeCordServerStats
import com.github.shynixn.blockball.bukkit.logic.business.extension.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.listener.BungeeCordNetworkSignListener
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.BungeeCordConfig
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.NetworkSignRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
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
@Singleton
class BungeeCordPingManager @Inject constructor(plugin: Plugin, signController: NetworkSignRepository) : BungeeCordConnectionController<Player>, Runnable, PluginMessageListener {

    //region Private Fields
    private var signController: LinkSignController<Location>? = null
    private var plugin: Plugin? = null
    private var task: BukkitTask? = null
    private val statusCache: MutableMap<String, BungeeCordServerStats> = HashMap()

    @Inject
    private val networkListener: BungeeCordNetworkSignListener? = null

    @Inject
    private val bungeeCordSignCommandExecutor: BungeeCordSignCommandExecutor? = null
    //endregion

    //region Public Properties
    var signCache: MutableMap<Player, String> = HashMap()
    //endregion

    //region Constructor
    init {
        Bukkit.getServer().consoleSender.sendMessage(BlockBallPlugin.PREFIX_CONSOLE + ChatColor.GREEN + "Loading BlockBall ...")
        plugin.saveDefaultConfig()
        Config.reload()
        if (Config.allowServerLinking!!) { //
            this.plugin = plugin
            this.signController = signController
            signController.reload()
            BungeeCordConfig.reload()
            this.task = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0L, 10L)
            plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
            plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)
            plugin.logger.log(Level.INFO, "Enabled BungeeCord linking between BlockBall servers.")
        }
    }
    //endregion

    //region Public Methods
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
        if (player == null) {
            return
        }
        for (s in this.signController!!.linkedServers) {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("ServerIP")
            out.writeUTF(s)
            player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray())
        }
    }

    /**
     * Gets called from the BungeeCord Spigot framework to manage connections.
     */
    override fun onPluginMessageReceived(p0: String?, p1: Player?, p2: ByteArray) {
        if (p0 != "BungeeCord") {
            return
        }
        val `in` = ByteStreams.newDataInput(p2)
        val type = `in`.readUTF()
        if (type == "ServerIP") {
            val serverName = `in`.readUTF()
            val ip = `in`.readUTF()
            val port = `in`.readShort()
            this.plugin!!.server.scheduler.runTaskAsynchronously(this.plugin) {
                val data = this.receiveResultFromServer(serverName, ip, port.toInt())
                this.parseData(serverName, data)
            }
        }
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
    //endregion

    //region Private Methods

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
            plugin!!.logger.log(Level.WARNING, "Failed to reach server " + serverName + " (" + hostname + ':'.toString() + port + ").")
            if (this.statusCache.containsKey(serverName)) {
                val status = statusCache[serverName]
                status!!.playerAmount = 0
                status.status = BungeeCordServerState.RESTARTING
                updateSigns(status)
            }
        }
        return data
    }

    private fun parseData(serverName: String, data: String?) {
        try {
            if (data == null)
                return
            val serverInfo = BungeeCordServerStats(serverName, data)
            this.plugin!!.server.scheduler.runTask(this.plugin) {
                this.statusCache[serverName] = serverInfo
                this.updateSigns(serverInfo)
            }
        } catch (e: Exception) {
            plugin!!.logger.log(Level.WARNING, "Cannot parse result from server.", e)
        }

    }

    private fun updateSigns(status: BungeeCordServerStatus) {
        for (signInfo in this.signController!!.getAll().toTypedArray()) {
            if (signInfo.server == status.serverName) {
                val location = (signInfo.position as LocationBuilder).toLocation()
                if (location.block.state is Sign) {
                    val sign = location.block.state as Sign
                    for (i in 0..3) {
                        sign.setLine(i, replaceSign(BungeeCordConfig.bungeeCordConfiguration!!.singLines[i], status))
                    }
                    sign.update()
                } else {
                    this.signController!!.remove(signInfo)
                }
            }
        }
    }


    fun replaceSign(line: String, info: BungeeCordServerStatus): String {
        var customLine = line.convertChatColors()
        customLine = when {
            info.status == BungeeCordServerState.INGAME -> customLine.replace(PlaceHolder.ARENA_STATE.placeHolder, BungeeCordConfig.bungeeCordConfiguration!!.duringMatchSignState)
            info.status == BungeeCordServerState.WAITING_FOR_PLAYERS -> customLine.replace(PlaceHolder.ARENA_STATE.placeHolder, BungeeCordConfig.bungeeCordConfiguration!!.waitingForPlayersSignState)
            info.status == BungeeCordServerState.RESTARTING -> customLine.replace(PlaceHolder.ARENA_STATE.placeHolder, BungeeCordConfig.bungeeCordConfiguration!!.restartingSignState)
            else -> customLine.replace(PlaceHolder.ARENA_STATE.placeHolder, "No connection")
        }

        return customLine.replace(PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder, (info.playerMaxAmount).toString())
                .replace(PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder, info.playerAmount.toString())
                .replace(PlaceHolder.BUNGEECORD_SERVER_NAME.placeHolder, info.serverName!!).convertChatColors()
    }

    /**
     * Returns a nullable player.
     *
     * @return player
     */
    private fun getFirstPlayer(): Player? {
        Bukkit.getWorlds()
                .filterNot { it.players.isEmpty() }
                .forEach { return it.players[0] }
        return null
    }

    //endregion
}