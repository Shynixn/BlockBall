package com.github.shynixn.blockball.bukkit.logic.persistence.configuration

import com.google.inject.Inject
import com.google.inject.Singleton
import java.util.logging.Logger

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
@Singleton
internal object Config : SimpleConfig() {
    @Inject
    var Logger: Logger? = null

    /**
     * Returns the general [prefix] of the plugin.
     */
    val prefix: String?
        get() {
            return this.getData<String>("messages.prefix")
        }

    /**
     * Returns the message being played when a user cannot join a game because of permission issues.
     */
    val joinGamePermissionmessage: String
        get() {
            val tmp = this.getData<String>("messages.no-permission-join-game")
            if (tmp == null) {
                throw IllegalArgumentException("Config option 'no-permission-join-game' could not be loaded!")
            }

            return tmp
        }

    /**
     * Returns the message being played when a user cannot spectate a game because of permission issues.
     */
    val spectateGamePermissionmessage: String
        get() {
            val tmp = this.getData<String>("messages.no-permission-spectate-game")
            if (tmp == null) {
                throw IllegalArgumentException("Config option 'no-permission-spectate-game' could not be loaded!")
            }

            return tmp
        }

    val stateSignEnabled: String?
        get() {
            return this.getData<String>("messages.state-sign-enabled")
        }

    val stateSignDisabled: String?
        get() {
            return this.getData<String>("messages.state-sign-disabled")
        }

    val stateSignRunning: String?
        get() {
            return this.getData<String>("messages.state-sign-running")
        }

    val allowServerLinking: Boolean?
        get() {
            return this.getData<Boolean>("")
        }

    val statsScoreboardEnabled: Boolean?
        get() {
            return this.getData<Boolean>("stats-scoreboard.enabled")
        }

    val statsScoreboardTitle: String?
        get() {
            return this.getData<String>("stats-scoreboard.title")
        }

    val statsScoreboardLines: List<String>?
        get() {
            return this.getData<List<String>>("stats-scoreboard.lines")
        }

    /**
     * Reloads the plugin configuration.
     */
    override fun reload() {
        super.reload()
        this.plugin?.reloadConfig()
    }
}

