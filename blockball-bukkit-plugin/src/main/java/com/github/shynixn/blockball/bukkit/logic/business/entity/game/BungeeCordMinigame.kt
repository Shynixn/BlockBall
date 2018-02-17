package com.github.shynixn.blockball.bukkit.logic.business.entity.game

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.bukkit.logic.business.helper.YamlSerializer
import com.github.shynixn.blockball.bukkit.logic.business.helper.setServerModt
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.bungeecord.BungeeCordConfiguration
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
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
class BungeeCordMinigame(arena: BukkitArena) : Minigame(arena) {

    private var bungeeCordConfiguration: BungeeCordConfiguration? = null

    init {
        saveBungeeCordConfiguration()
    }

    override fun onTwentyTicks() {
        super.onTwentyTicks()
        if (this.isGameRunning) {
            Bukkit.getServer().setServerModt(bungeeCordConfiguration!!.inGameMotd)
        }
        else if (this.isLobbyCountdownRunning) {
            Bukkit.getServer().setServerModt(bungeeCordConfiguration!!.waitingForPlayersMotd)
        }
    }

    private fun saveBungeeCordConfiguration() {
        try {
            val file = File(plugin.dataFolder, "bungeecord.yml")
            if (!file.exists()) {
                file.createNewFile()
                val configuration = YamlConfiguration()
                configuration.set("bungeecord", BungeeCordConfiguration().serialize())
                configuration.save(file)
            }
            val configuration = YamlConfiguration()
            configuration.load(file)
            this.bungeeCordConfiguration = YamlSerializer.deserializeObject(BungeeCordConfiguration::class.java, configuration.get("bungeecord"))
        } catch (e: IOException) {
            plugin.logger.log(Level.WARNING, "Failed to load bungeecord.yml.", e)
        }
    }

    override fun close() {
        super.close()
        Bukkit.getServer().setServerModt(bungeeCordConfiguration!!.restartingMotd)
    }
}