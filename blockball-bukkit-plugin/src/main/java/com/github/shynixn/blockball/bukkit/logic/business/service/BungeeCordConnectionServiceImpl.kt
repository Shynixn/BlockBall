@file:Suppress("UNCHECKED_CAST", "UnstableApiUsage")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.BungeeCordConnectionService
import com.github.shynixn.blockball.api.business.service.BungeeCordService
import com.github.shynixn.blockball.api.business.service.PersistenceLinkSignService
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.core.logic.business.extension.thenAcceptSafely
import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.messaging.PluginMessageListener

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
class BungeeCordConnectionServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val bungeeCordService: BungeeCordService,
    private val persistenceLinkSignService: PersistenceLinkSignService
) :
    BungeeCordConnectionService, PluginMessageListener {
    private var channelsRegistered = false


    /**
     * Restarts the channel listeners on this connection service.
     */
    override fun restartChannelListeners() {
        if (channelsRegistered) {
            return
        }

        plugin.server.messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this)

        bungeeCordService.restartConnectionHandling()

        channelsRegistered = true
    }

    /**
     * Gets called when a plugin message gets received.
     */
    override fun onPluginMessageReceived(channel: String, player: Player, content: ByteArray) {
        if (channel != "BungeeCord") {
            return
        }

        val input: ByteArrayDataInput = ByteStreams.newDataInput(content)
        val type = input.readUTF()

        if (type != "ServerIP") {
            return
        }

        val serverName = input.readUTF()

        bungeeCordService.getServerSignLines(serverName, input.readUTF(), input.readShort().toInt()).thenAcceptSafely { lines ->
            updateSigns(serverName, lines)
        }
    }

    /**
     * Updates all signs of the given server.
     */
    private fun updateSigns(serverName: String, lines: Array<String>) {
        val serverSigns = persistenceLinkSignService.getAll().filter { s -> s.server == serverName }.toList()

        for (sign in serverSigns) {
            val blockState = sign.position.toLocation().block.state

            if (blockState !is Sign) {
                persistenceLinkSignService.remove(sign)
                continue
            }

            for (i in 0..3) {
                blockState.setLine(i, lines[i])
            }

            blockState.update()
        }
    }
}