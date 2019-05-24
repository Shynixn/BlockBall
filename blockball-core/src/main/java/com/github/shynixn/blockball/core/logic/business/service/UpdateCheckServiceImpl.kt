package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ChatColor
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.UpdateCheckService
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.google.inject.Inject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.concurrent.CompletableFuture
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
class UpdateCheckServiceImpl @Inject constructor(
    private val plugin: PluginProxy,
    private val loggingService: LoggingService,
    private val concurrencyService: ConcurrencyService
) : UpdateCheckService {
    private val baseUrl = "https://api.spigotmc.org/legacy/update.php?resource="
    private val spigotResourceId: Long = 15320

    /**
     * Returns if there are any new updates for the BlockBall plugin.
     */
    override fun checkForUpdates(): CompletableFuture<Boolean> {
        val completableFuture = CompletableFuture<Boolean>()

        async(concurrencyService) {
            try {
                val resourceVersion = getLatestReleaseVersion()

                if (resourceVersion == plugin.version) {
                    completableFuture.complete(false)
                } else {
                    if (plugin.version.endsWith("SNAPSHOT")) {
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "================================================")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "You are using a snapshot of BlockBall")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "Please check if there is a new version available")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "================================================")
                    } else {
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "================================================")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "BlockBall is outdated")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "Please download the latest version from github")
                        plugin.sendConsoleMessage(ChatColor.YELLOW.toString() + "================================================")
                    }

                    completableFuture.complete(true)
                }
            } catch (e: IOException) {
                loggingService.warn("Failed to check for updates.", e)
                completableFuture.complete(false)
            }
        }

        return completableFuture
    }

    /**
     * Makes a webrequest and returns the latest version id.
     */
    @Throws(IOException::class)
    private fun getLatestReleaseVersion(): String {
        val httpsURLConnection = URL(baseUrl + spigotResourceId).openConnection() as HttpsURLConnection
        httpsURLConnection.inputStream.use { stream ->
            InputStreamReader(stream).use { reader ->
                BufferedReader(reader).use { bufferedReader ->
                    return bufferedReader.readLine()
                }
            }
        }
    }
}