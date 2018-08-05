package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.BungeeCordServerState
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.service.BungeeCordService
import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.PersistenceLinkSignService
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordServerStatus
import com.github.shynixn.blockball.bukkit.logic.business.extension.async
import com.github.shynixn.blockball.bukkit.logic.business.extension.convertChatColors
import com.github.shynixn.blockball.bukkit.logic.business.extension.toBukkitLocation
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.BungeeCordConfig
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.BungeeCordServerStatusEntity
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.messaging.PluginMessageListener
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
class BungeeCordServiceImpl @Inject constructor(private val plugin: Plugin, private val configurationService: ConfigurationService, private val persistenceLinkSignService: PersistenceLinkSignService) : BungeeCordService, Runnable, PluginMessageListener {
    init {
        val serverLink = configurationService.findValue<Boolean>("game.allow-server-linking")

        if (serverLink) {
            plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this, 0L, 10L)
            plugin.server.messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
            plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)
            plugin.logger.log(Level.INFO, "Enabled BungeeCord linking between BlockBall servers.")
        }
    }

    /**
     * Refreshes all signs by pinging the bungeecord network.
     */
    override fun pingServers() {
        var player: Player? = null

        Bukkit.getWorlds().forEach { w ->
            w.players.forEach { p ->
                player = p
            }
        }

        if (player == null) {
            return
        }

        this.persistenceLinkSignService.getAll().thenAccept { items ->
            val set = items.map { s -> s.server }.toHashSet()

            set.forEach { server ->
                val out = ByteStreams.newDataOutput()
                out.writeUTF("ServerIP")
                out.writeUTF(server)
                player!!.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray())
            }
        }
    }

    /**
     * Receives the [payload] on the given [channel] via the given [player].
     */
    override fun onPluginMessageReceived(channel: String, player: Player, payload: ByteArray) {
        if (channel != "BungeeCord") {
            return
        }

        val `in` = ByteStreams.newDataInput(payload)
        val type = `in`.readUTF()
        if (type == "ServerIP") {
            val serverName = `in`.readUTF()
            val ip = `in`.readUTF()
            val port = `in`.readShort()

            async(plugin) {
                val status = getServerInformation(serverName, ip, port.toInt())

                this.persistenceLinkSignService.getAll().thenAccept { signs ->
                    signs.filter { sign -> sign.server == serverName }.forEach { sign ->
                        val location = sign.position.toBukkitLocation()

                        if (location.block.state is Sign) {
                            val internalSign = location.block.state as Sign

                            for (i in 0..3) {
                                internalSign.setLine(i, replaceSign(BungeeCordConfig.bungeeCordConfiguration!!.singLines[i], status))
                            }

                            internalSign.update()
                        } else {
                            this.persistenceLinkSignService.remove(sign)
                        }
                    }
                }
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
        pingServers()
    }

    /**
     * Connects the given [player] to the given [server]. Does
     * nothing when the server does not exist or connection fails.
     */
    override fun <P> connectToServer(player: P, server: String) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(server)
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray())
    }

    /**
     * Connects the given [player] to the given server which is specified on the [sign].
     * Does nothing when the location does not contain a server sign.
     */
    override fun <P, S> clickOnConnectSign(player: P, sign: S) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }


        if (sign !is Sign) {
            throw IllegalArgumentException("Sign has to be a BukkitSign!")
        }

        persistenceLinkSignService.getFromLocation(sign.location).thenAccept { opt ->
            if (opt.isPresent) {
                connectToServer(player, opt.get().server)
            }
        }
    }


    private fun replaceSign(line: String, info: BungeeCordServerStatus): String {
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


    private fun getServerInformation(server: String, hostname: String, port: Int): BungeeCordServerStatus {
        var data: String? = null
        val serverStatus = BungeeCordServerStatusEntity(server)

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

            parseServerInformationInto(serverStatus, data!!)
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to reach server " + server + " (" + hostname + ':'.toString() + port + ").")
            serverStatus.playerAmount = 0
            serverStatus.status = BungeeCordServerState.RESTARTING
        }

        return serverStatus
    }

    private fun parseServerInformationInto(serverStatus: BungeeCordServerStatus, data: String) {
        var state = BungeeCordServerState.UNKNOWN
        var motd: String
        val motdBuilder = StringBuilder()
        var started = false
        var i = 0

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

        try {
            motd = ChatColor.translateAlternateColorCodes('&', motdBuilder.toString()).substring(0, motdBuilder.length - 2).replace("§", "&")
            when {
                motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.inGameMotd, true) -> state = BungeeCordServerState.INGAME
                motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.restartingMotd, true) -> state = BungeeCordServerState.RESTARTING
                motd.equals(BungeeCordConfig.bungeeCordConfiguration!!.waitingForPlayersMotd, true) -> state = BungeeCordServerState.WAITING_FOR_PLAYERS
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

        serverStatus.playerAmount = Integer.parseInt(motd)
        motd = ""
        started = false
        while (i < data.length) {
            if (data[i] == '§')
                started = true
            else if (started)
                motd += data[i]
            i++
        }

        serverStatus.status = state
        serverStatus.playerMaxAmount = Integer.parseInt(motd)
    }

}