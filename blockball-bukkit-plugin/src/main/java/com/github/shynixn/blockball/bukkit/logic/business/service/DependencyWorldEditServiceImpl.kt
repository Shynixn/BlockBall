package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.DependencyWorldEditService
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*
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
class DependencyWorldEditServiceImpl(private val plugin: Plugin) : DependencyWorldEditService {
    /**
     * Returns the leftclick worledit selection of the given [player].
     */
    override fun <L, P> getLeftClickLocation(player: P): Optional<L> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return getSelection(player, "getMinimumPoint") as Optional<L>
    }

    /**
     * Returns the rightclick worledit selection of the given [player].
     */
    override fun <L, P> getRightClickLocation(player: P): Optional<L> {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        return getSelection(player, "getMaximumPoint") as Optional<L>
    }

    /**
     * Returns the we selection.
     */
    private fun getSelection(player: Player, name: String): Optional<Location> {
        try {
            val clazz = Class.forName("com.sk89q.worldedit.bukkit.WorldEditPlugin")
            val plugin = Bukkit.getPluginManager().getPlugin("WorldEdit")
            val selection = clazz.getDeclaredMethod("getSelection").invoke(plugin, player)

            if (selection != null) {
                Optional.of(Class.forName("com.sk89q.worldedit.bukkit.selections.RegionSelection").getDeclaredMethod(name).invoke(selection))
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to gather selection from worldedit.", e)
        }

        return Optional.empty()
    }
}