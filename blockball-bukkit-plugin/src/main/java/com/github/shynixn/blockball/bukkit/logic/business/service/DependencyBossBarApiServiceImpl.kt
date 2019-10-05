package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.DependencyBossBarApiService
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.google.inject.Inject
import org.bukkit.entity.Player
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
class DependencyBossBarApiServiceImpl @Inject constructor(private val plugin: Plugin) : DependencyBossBarApiService {
    /**
     * Sets the bossbar [message] for the given [player] with the given [percent].
     */
    override fun <P> setBossbarMessage(player: P, message: String, percent: Double) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        try {
            val clazz = Class.forName("org.inventivetalent.bossbar.BossBarAPI")
            clazz.getDeclaredMethod("setMessage", String::class.java, Float::class.java).invoke(null, player, message.translateChatColors(), percent.toFloat())
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to set bossbar message.", e)
        }
    }

    /**
     * Removes the bossbar from the given [player].
     */
    override fun <P> removeBossbarMessage(player: P) {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        try {
            val clazz = Class.forName("org.inventivetalent.bossbar.BossBarAPI")
            val hasBar = clazz.getDeclaredMethod("hasBar", Player::class.java).invoke(null, player)

            if (hasBar as Boolean) {
                clazz.getDeclaredMethod("removeBar", Player::class.java).invoke(null, player)
            }
        } catch (e: Exception) {
            plugin.logger.log(Level.WARNING, "Failed to remove bossbar message.", e)
        }
    }
}