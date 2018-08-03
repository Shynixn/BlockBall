package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.SpectatorMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.LocationBuilder
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
class SpectatorProperties : SpectatorMeta {
    /**
     * Should nearby players inside of the [notificationRadius] be messaged by title messages, scoreboard, holograms and bossbar.
     */
    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "notify-nearby-players-enabled")
    override var notifyNearbyPlayers: Boolean = false
    /**
     * The radius from the center of the arena a player has to be in order to get notified when [notifyNearbyPlayers] is enabled.
     */
    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "notify-nearby-players-radius")
    override var notificationRadius: Int = 50

    /**
     * Should the spectator mode be enabled for this arena?
     */
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "spectatormode-enabled")
    override var spectatorModeEnabled: Boolean = true

    /**
     * Spectate asking message.
     */
    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "spectatormode-start-message")
    override var spectateStartMessage: MutableList<String> = arrayListOf("Game is full! Do you want to spectate?", ChatColor.GREEN.toString() + "[Start spectating]")

    /**
     *  Spawnpoint of the spectators.
     */
    @YamlSerializer.YamlSerialize(orderNumber = 5, value = "spectatormode-spawnpoint", implementation = LocationBuilder::class)
    override var spectateSpawnpoint: StorageLocation? = null
}