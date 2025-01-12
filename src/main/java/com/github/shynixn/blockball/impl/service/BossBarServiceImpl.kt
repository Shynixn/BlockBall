@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.BossBarService
import com.github.shynixn.blockball.entity.BossBarMeta
import com.github.shynixn.blockball.enumeration.BossBarFlag
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.lang.reflect.Array
import java.lang.reflect.Method

class BossBarServiceImpl : BossBarService {
    /**
     * Adds the given [player] to this bossbar.
     * Does nothing if the player is already added.
     */
    override fun <B, P> addPlayer(bossBar: B, player: P) {
        if (!getPlayers<B, P>(bossBar).contains(player)) {
            getBossBarMethod("addPlayer", Player::class.java).invoke(bossBar, player)
        }
    }

    /**
     * Removes the given [player] from this bossbar.
     * Does nothing if the player is already removed.
     */
    override fun <B, P> removePlayer(bossBar: B, player: P) {
        if (getPlayers<B, P>(bossBar).contains(player)) {
            getBossBarMethod("removePlayer", Player::class.java).invoke(bossBar, player)
        }
    }

    /**
     * Returns a list of all players watching thie bossbar.
     */
    override fun <B, P> getPlayers(bossBar: B): List<P> {
        return getBossBarMethod("getPlayers").invoke(bossBar) as List<P>
    }

    /**
     * Changes the style of the bossbar with given [bossBarMeta].
     */
    override fun <B, P> changeConfiguration(bossBar: B, title: String, bossBarMeta: BossBarMeta, player: P) {
        getBossBarMethod("setVisible", Boolean::class.java).invoke(bossBar, bossBarMeta.enabled)
        getBossBarMethod("setTitle", String::class.java).invoke(bossBar, title)
        getBossBarMethod("setProgress", Double::class.java).invoke(bossBar, bossBarMeta.percentage / 100.0)
        getBossBarMethod("setColor", Class.forName("org.bukkit.boss.BarColor")).invoke(
                bossBar,
                Class.forName("org.bukkit.boss.BarColor").getDeclaredMethod("valueOf", String::class.java)
                    .invoke(null, bossBarMeta.color.name)
            )
        getBossBarMethod("setStyle", Class.forName("org.bukkit.boss.BarStyle")).invoke(
                bossBar,
                Class.forName("org.bukkit.boss.BarStyle").getDeclaredMethod("valueOf", String::class.java)
                    .invoke(null, bossBarMeta.style.name)
            )

        val convertFlagMethod =
            Class.forName("org.bukkit.boss.BarFlag").getDeclaredMethod("valueOf", String::class.java)
        val flagsMethod = getBossBarMethod("addFlag", Class.forName("org.bukkit.boss.BarFlag"))

        bossBarMeta.flags.filter { f -> f != BossBarFlag.NONE }.forEach { f ->
            flagsMethod.invoke(bossBar, convertFlagMethod.invoke(null, f.name))
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
        val color = Class.forName("org.bukkit.boss.BarColor").getDeclaredMethod("valueOf", String::class.java)
            .invoke(null, bossBarMeta.color.name)
        val style = Class.forName("org.bukkit.boss.BarStyle").getDeclaredMethod("valueOf", String::class.java)
            .invoke(null, bossBarMeta.style.name)

        val bossBar = method.invoke(
            null, bossBarMeta.message, color, style, Array.newInstance(Class.forName("org.bukkit.boss.BarFlag"), 0)
        ) as B
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
