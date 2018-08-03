package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.UpdateCheckService
import com.github.shynixn.blockball.bukkit.logic.business.extension.async
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.Plugin
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import javax.net.ssl.HttpsURLConnection

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
class UpdateCheckServiceImpl  @Inject constructor(private val plugin: Plugin) : UpdateCheckService {
    private val baseUrl = "https://api.spigotmc.org/legacy/update.php?resource="
    private val spigotResourceId: Long = 15320
    private val prefix: String = ChatColor.BLUE.toString() + "[BlockBall] "

    /**
     * Returns if there are any new updates for the BlockBall plugin.
     */
    override fun checkForUpdates(): CompletableFuture<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()

        async(plugin) {
            try {
                val resourceVersion = getLatestReleaseVersion(spigotResourceId)

                if (resourceVersion == plugin.description.version) {
                    completableFuture.complete(false)
                } else {
                    val sender = Bukkit.getServer().consoleSender
                    val pluginName = plugin.name

                    if (plugin.description.version.endsWith("SNAPSHOT")) {
                        sender.sendMessage(prefix + ChatColor.YELLOW + "================================================")
                        sender.sendMessage(prefix + ChatColor.YELLOW + "You are using a snapshot of " + pluginName)
                        sender.sendMessage(prefix + ChatColor.YELLOW + "Please check if there is a new version available")
                        sender.sendMessage(prefix + ChatColor.YELLOW + "================================================")
                    } else {
                        sender.sendMessage(prefix + ChatColor.YELLOW + "================================================")
                        sender.sendMessage(prefix + ChatColor.YELLOW + pluginName + " is outdated")
                        sender.sendMessage(prefix + ChatColor.YELLOW + "Please download the latest version from github")
                        sender.sendMessage(prefix + ChatColor.YELLOW + "================================================")
                    }

                    completableFuture.complete(true)
                }
            } catch (e: IOException) {
                plugin.logger.log(Level.WARNING, "Failed to check for updates.")
                completableFuture.complete(false)
            }
        }

        return completableFuture
    }

    /**
     * Makes a webrequest and returns the latest version id.
     */
    @Throws(IOException::class)
    private fun getLatestReleaseVersion(resourceId: Long): String {
        val httpsURLConnection = URL(baseUrl + resourceId).openConnection() as HttpsURLConnection
        httpsURLConnection.inputStream.use { stream ->
            InputStreamReader(stream).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    return bufferedReader.readLine()
                }
            }
        }
    }
}