@file:Suppress("UnstableApiUsage", "UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.BungeeCordServerState
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordConfiguration
import com.github.shynixn.blockball.api.persistence.entity.BungeeCordServerStatus
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.github.shynixn.blockball.core.logic.business.extension.sync
import com.github.shynixn.blockball.core.logic.business.extension.thenAcceptSafely
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.github.shynixn.blockball.core.logic.persistence.entity.BungeeCordConfigurationEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.BungeeCordServerStatusEntity
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class BungeeCordServiceImpl @Inject constructor(
    private val proxyService: ProxyService,
    private val concurrencyService: ConcurrencyService,
    private val persistenceLinkSignService: PersistenceLinkSignService,
    private val loggingService: LoggingService,
    configurationService: ConfigurationService,
    yamlService: YamlService,
    yamlSerializationService: YamlSerializationService
) : BungeeCordService {
    private var registered = false
    /**
     * Gets the bungeeCordConfiguration.
     */
    override var bungeeCordConfiguration: BungeeCordConfiguration = BungeeCordConfigurationEntity()

    /**
     * Init.
     */
    init {
        try {
            val file = configurationService.applicationDir.resolve("bungeecord.yml")

            if (Files.exists(file)) {
                val data = yamlService.read(file)
                bungeeCordConfiguration =
                    yamlSerializationService.deserialize(BungeeCordConfigurationEntity::class.java, data["bungeecord"] as Map<String, Any?>)
            }

            val data = yamlSerializationService.serialize(bungeeCordConfiguration)
            val finalData = HashMap<String, Any>()
            finalData["bungeecord"] = data
            yamlService.write(file, finalData)
        } catch (e: Exception) {
            loggingService.warn("Failed to parse bungeecord.yml.")
        }
    }

    /**
     * Gets the lines of a sign connecting to the given server.
     */
    override fun getServerSignLines(serverName: String, ip: String, port: Int): CompletableFuture<Array<String>> {
        val completable = CompletableFuture<Array<String>>()

        async(concurrencyService) {
            val statusInformation = getServerInformation(serverName, ip, port)
            val lines = ArrayList<String>()


            for (configLine in bungeeCordConfiguration.serverSignTemplate) {
                val customLine = when {
                    statusInformation.status == BungeeCordServerState.INGAME -> configLine.replace(
                        PlaceHolder.ARENA_STATE.placeHolder,
                        bungeeCordConfiguration.duringMatchSignState
                    )
                    statusInformation.status == BungeeCordServerState.WAITING_FOR_PLAYERS -> configLine.replace(
                        PlaceHolder.ARENA_STATE.placeHolder,
                        bungeeCordConfiguration.waitingForPlayersSignState
                    )
                    statusInformation.status == BungeeCordServerState.RESTARTING -> configLine.replace(
                        PlaceHolder.ARENA_STATE.placeHolder,
                        bungeeCordConfiguration.restartingSignState
                    )
                    else -> {
                        configLine.replace(PlaceHolder.ARENA_STATE.placeHolder, "No connection")
                    }
                }

                lines.add(
                    customLine.replace(PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder, (statusInformation.playerMaxAmount).toString())
                        .replace(PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder, statusInformation.playerAmount.toString())
                        .replace(PlaceHolder.BUNGEECORD_SERVER_NAME.placeHolder, statusInformation.serverName!!).translateChatColors()
                )
            }

            sync(concurrencyService) {
                completable.complete(lines.toTypedArray())
            }
        }

        return completable
    }

    /**
     * Restarts all connection handling and caching between servers.
     */
    override fun restartConnectionHandling() {
        if (registered) {
            return
        }

        registered = true

        persistenceLinkSignService.refresh().thenAcceptSafely {
            async(concurrencyService, 0L, 60L) {
                pingAllServersInNetwork()
            }
        }
    }

    /**
     * Connects the given [player] to the given [server]. Does
     * nothing when the server does not exist or connection fails.
     */
    override fun <P> connectToServer(player: P, server: String) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF("Connect")
        out.writeUTF(server)

        proxyService.sendPlayerPluginMessage(player, "BungeeCord", out.toByteArray())
    }

    /**
     * Pings all servers in the network.
     */
    private fun pingAllServersInNetwork() {
        val player: Any?
        val players = proxyService.getOnlinePlayers<Any>()

        if (players.isEmpty()) {
            return
        } else {
            player = players[0]
        }

        val uniqueServers = persistenceLinkSignService.getAll().map { s -> s.server }.toHashSet()

        for (server in uniqueServers) {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("ServerIP")
            out.writeUTF(server)

            proxyService.sendPlayerPluginMessage(player, "BungeeCord", out.toByteArray())
        }
    }

    /**
     * Gets the current server status.
     */
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
            loggingService.warn("Failed to reach server $server ($hostname:$port).")
            serverStatus.playerAmount = 0
            serverStatus.status = BungeeCordServerState.RESTARTING
        }

        return serverStatus
    }

    /**
     * Parses the received server information into it.
     */
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
            motd = motdBuilder.toString().translateChatColors().substring(0, motdBuilder.length - 2).replace("§", "&")
            when {
                motd.equals(bungeeCordConfiguration.inGameMotd, true) -> state = BungeeCordServerState.INGAME
                motd.equals(bungeeCordConfiguration.restartingMotd, true) -> state = BungeeCordServerState.RESTARTING
                motd.equals(bungeeCordConfiguration.waitingForPlayersMotd, true) -> state = BungeeCordServerState.WAITING_FOR_PLAYERS
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