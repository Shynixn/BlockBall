package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.persistence.entity.bungeecord.LinkSign
import com.github.shynixn.blockball.bukkit.logic.business.controller.BungeeCordPingManager
import com.github.shynixn.blockball.bukkit.logic.business.entity.bungeecord.BungeeCordServerStats
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.BungeeCordConfig
import com.github.shynixn.blockball.bukkit.logic.persistence.controller.NetworkSignRepository
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Sign
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin
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
class BungeeCordNetworkSignListener @Inject constructor(plugin: Plugin) : SimpleListener(plugin) {
    @Inject
    private val manager: BungeeCordPingManager? = null

    @Inject
    private val signController: NetworkSignRepository? = null

    /**
     * Handles click on signs to create new server signs or to connect players to the server
     * written on the sign.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerInteractEvent(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK)
            return
        if (event.clickedBlock.state !is Sign)
            return
        if (this.manager!!.signCache.containsKey(event.player)) {
            val server = this.manager.signCache[event.player]
            this.manager.signCache.remove(event.player)
            val sign = signController!!.create(server!!, event.clickedBlock.location)
            signController.store(sign)
            this.manager.pingServers()
            val signBlock = event.clickedBlock.state as Sign
            for (i in 0..3) {
                signBlock.setLine(i, manager.replaceSign(BungeeCordConfig.bungeeCordConfiguration!!.singLines[i], BungeeCordServerStats(server)))
            }
            signBlock.update()
        } else {
            val sign = event.clickedBlock.state as Sign
            try {
                val signInfo = this.getBungeeCordSignFromLocation(sign.location)
                if (signInfo != null) {
                    this.manager.connectToServer(event.player, signInfo.server!!)
                }
            } catch (ex: Exception) {
                Bukkit.getLogger().log(Level.WARNING, "Cannot connect player to server.", ex)
            }
        }
    }

    private fun getBungeeCordSignFromLocation(signLocation: Location): LinkSign? {
        for (sign in signController!!.getAll()) {
            val l = (sign.position as LocationBuilder).toLocation()
            if (signLocation.blockX == l.blockX) {
                if (signLocation.blockY == l.blockY) {
                    if (signLocation.blockZ == l.blockZ) {
                        return sign
                    }
                }
            }
        }
        return null
    }
}