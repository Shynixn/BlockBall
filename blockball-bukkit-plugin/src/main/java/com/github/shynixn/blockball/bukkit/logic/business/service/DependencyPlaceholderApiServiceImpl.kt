package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.PlaceHolderRequestEvent
import com.github.shynixn.blockball.api.business.service.DependencyPlaceholderApiService
import com.google.inject.Inject
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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
class DependencyPlaceholderApiServiceImpl @Inject constructor(private val plugin: Plugin) : PlaceholderExpansion(), DependencyPlaceholderApiService {
    private var registerd: Boolean = false

    /**
     * Registers the placeholder hook if it is not already registered.
     */
    override fun registerListener() {
        if (!registerd) {
            this.register()
            registerd = true
        }
    }

    /**
     * Gets the expansion version which is the same of the plugin version.
     */
    override fun getVersion(): String {
        return plugin.description.version
    }

    /**
     * Gets the expansion author for placeholderapi.
     */
    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    /**
     * Gets the identifier which is required by placeholderapi to match the placeholder against this plugin.
     */
    override fun getIdentifier(): String {
        return "blockball"
    }

    /**
     * OnPlaceHolder Request
     *
     * @param player player
     * @param s      customText
     * @return result
     */
    override fun onPlaceholderRequest(player: Player?, s: String?): String? {
        if (player == null) {
            return ""
        }

        try {
            val event = PlaceHolderRequestEvent(s!!, null, player)
            Bukkit.getPluginManager().callEvent(event)
            return event.result
        } catch (ignored: Exception) {
        }

        return null
    }
}