@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.BossBarFlag
import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.BossBarService
import com.github.shynixn.blockball.api.business.service.DependencyBossBarApiService
import com.github.shynixn.blockball.api.persistence.entity.BossBarMeta
import com.github.shynixn.blockball.core.logic.business.extension.translateChatColors
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.reflect.Array
import java.lang.reflect.Method

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
class BossBarServiceImpl @Inject constructor(private val plugin: PluginProxy, private val dependencyBossBarService: DependencyBossBarApiService) :
    BossBarService {
    /**
     * Adds the given [player] to this bossbar.
     * Does nothing if the player is already added.
     */
    override fun <B, P> addPlayer(bossBar: B, player: P) {
        if (plugin.getServerVersion().isVersionSameOrLowerThan(Version.VERSION_1_8_R3)) {
            dependencyBossBarService.setBossbarMessage(player, "", 1.0)
        } else {
            if (!getPlayers<B, P>(bossBar).contains(player)) {
                getBossBarMethod("addPlayer", Player::class.java).invoke(bossBar, player)
            }
        }
    }

    /**
     * Removes the given [player] from this bossbar.
     * Does nothing if the player is already removed.
     */
    override fun <B, P> removePlayer(bossBar: B, player: P) {
        if (plugin.getServerVersion().isVersionSameOrLowerThan(Version.VERSION_1_8_R3)) {
            dependencyBossBarService.removeBossbarMessage(player)
        } else {
            if (getPlayers<B, P>(bossBar).contains(player)) {
                getBossBarMethod("removePlayer", Player::class.java).invoke(bossBar, player)
            }
        }
    }

    /**
     * Returns a list of all players watching thie bossbar.
     */
    override fun <B, P> getPlayers(bossBar: B): List<P> {
        return if (plugin.getServerVersion().isVersionSameOrLowerThan(Version.VERSION_1_8_R3)) {
            ArrayList()
        } else {
            getBossBarMethod("getPlayers").invoke(bossBar) as List<P>
        }
    }

    /**
     * Changes the style of the bossbar with given [bossBarMeta].
     */
    override fun <B, P> changeConfiguration(bossBar: B, title: String, bossBarMeta: BossBarMeta, player: P) {
        if (plugin.getServerVersion().isVersionSameOrLowerThan(Version.VERSION_1_8_R3) && player != null) {
            dependencyBossBarService.setBossbarMessage(player, bossBarMeta.message.translateChatColors(), bossBarMeta.percentage)
        } else {
            getBossBarMethod("setVisible", Boolean::class.java).invoke(bossBar, bossBarMeta.enabled)
            getBossBarMethod("setTitle", String::class.java).invoke(bossBar, title)
            getBossBarMethod("setProgress", Double::class.java).invoke(bossBar, bossBarMeta.percentage / 100.0)
            getBossBarMethod("setColor", Class.forName("org.bukkit.boss.BarColor"))
                .invoke(
                    bossBar,
                    Class.forName("org.bukkit.boss.BarColor").getDeclaredMethod("valueOf", String::class.java).invoke(null, bossBarMeta.color.name)
                )
            getBossBarMethod("setStyle", Class.forName("org.bukkit.boss.BarStyle"))
                .invoke(
                    bossBar,
                    Class.forName("org.bukkit.boss.BarStyle").getDeclaredMethod("valueOf", String::class.java).invoke(null, bossBarMeta.style.name)
                )

            val convertFlagMethod = Class.forName("org.bukkit.boss.BarFlag").getDeclaredMethod("valueOf", String::class.java)
            val flagsMethod = getBossBarMethod("addFlag", Class.forName("org.bukkit.boss.BarFlag"))

            bossBarMeta.flags.filter { f -> f != BossBarFlag.NONE }.forEach { f ->
                flagsMethod.invoke(bossBar, convertFlagMethod.invoke(null, f.name))
            }
        }
    }

    /**
     * Generates a new bossbar from the given bossBar meta values.
     */
    override fun <B> createNewBossBar(bossBarMeta: BossBarMeta): B {
        val method = Bukkit::class.java.getDeclaredMethod(
            "createBossBar",
            String::class.java,
            Class.forName("org.bukkit.boss.BarColor"),
            Class.forName("org.bukkit.boss.BarStyle"),
            Class.forName("[Lorg.bukkit.boss.BarFlag;")
        )
        val color = Class.forName("org.bukkit.boss.BarColor").getDeclaredMethod("valueOf", String::class.java).invoke(null, bossBarMeta.color.name)
        val style = Class.forName("org.bukkit.boss.BarStyle").getDeclaredMethod("valueOf", String::class.java).invoke(null, bossBarMeta.style.name)

        val bossBar = method.invoke(null, bossBarMeta.message, color, style, Array.newInstance(Class.forName("org.bukkit.boss.BarFlag"), 0)) as B
        changeConfiguration(bossBar, bossBarMeta.message, bossBarMeta, null)

        return bossBar
    }

    /**
     * Clears all resources this [bossBar] has allocated from this service.
     */
    override fun <B> cleanResources(bossBar: B) {
        this.getPlayers<Any, Any>(bossBar as Any).forEach { p ->
            this.removePlayer(bossBar, p)
        }
    }

    /**
     * Returns a bossbar [Method].
     */
    private fun getBossBarMethod(name: String, vararg clazzes: Class<*>): Method {
        return Class.forName("org.bukkit.boss.BossBar").getDeclaredMethod(name, *clazzes)
    }
}