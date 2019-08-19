package com.github.shynixn.blockball.api.business.service

import com.github.shynixn.blockball.api.persistence.entity.BungeeCordConfiguration
import java.util.concurrent.CompletableFuture

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
interface BungeeCordService {
    /**
     * Gets the configuration.
     */
    val bungeeCordConfiguration: BungeeCordConfiguration

    /**
     * Connects the given [player] to the given [server]. Does
     * nothing when the server does not exist or connection fails.
     */
    fun <P> connectToServer(player: P, server: String)

    /**
     * Gets the lines of a sign connecting to the given server.
     */
    fun getServerSignLines(serverName: String, ip: String, port: Int): CompletableFuture<Array<String>>

    /**
     * Restarts all connection handling and caching between servers.
     */
    fun restartConnectionHandling()
}