package unittest

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.business.service.ScoreboardService
import com.github.shynixn.blockball.api.business.service.StatsCollectingService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.bukkit.logic.business.service.StatsCollectingServiceImpl
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.CompletableFuture
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
class StatsCollectingServiceTest {
    /**
     * Given
     *      a valid player with no stats scoreboard
     * When
     *      cleanResources is called
     * Then
     *     should return immediatly.
     */
    @Test
    fun cleanResources_PlayerWithNoScoreboard_ShouldReturnImmediatly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val player = Mockito.mock(Player::class.java)

        // Act
        val value = classUnderTest.cleanResources(player)

        // Assert
        Assertions.assertEquals(Unit, value)
    }

    /**
     * Given
     *      an invalid player
     * When
     *      cleanResources is called
     * Then
     *     should throw exception.
     */
    @Test
    fun cleanResources_InvalidPlayer_ShouldThrowException() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.cleanResources("I'm a player!")
        }
    }

    companion object {
        fun createWithDependencies(
            configurationService: ConfigurationService = MockedConfigurationService(),
            persistenceStatsService: PersistenceStatsService = MockedPersistenceStatsService(),
            scoreboardService: ScoreboardService = MockedScoreboardService(),
            plugin: Plugin = Mockito.mock(Plugin::class.java)
        ): StatsCollectingService {
            if (Bukkit.getServer() == null) {
                val server = Mockito.mock(Server::class.java)
                `when`(server.logger).thenReturn(Logger.getGlobal())
                Bukkit.setServer(server)
            }

            val server = Bukkit.getServer()

            `when`(plugin.server).thenReturn(Bukkit.getServer())
            val scheduler = Mockito.mock(BukkitScheduler::class.java)

            `when`(server.logger).thenReturn(Logger.getGlobal())
            `when`(server.scheduler).thenReturn(scheduler)

            return StatsCollectingServiceImpl(plugin, configurationService, persistenceStatsService, scoreboardService)
        }
    }

    class MockedScoreboardService : ScoreboardService {
        /**
         * Sets the bungeeCordConfiguration of the given scoreboard.
         */
        override fun <S> setConfiguration(scoreboard: S, displaySlot: Any, title: String) {
        }

        /**
         * Sets the [text] at the given [scoreboard] and [lineNumber].
         */
        override fun <S> setLine(scoreboard: S, lineNumber: Int, text: String) {
        }
    }

    class MockedPersistenceStatsService : PersistenceStatsService {
        /**
         * Gets the [Stats] from the given player.
         * This call will never return null.
         */
        override fun <P> getStatsFromPlayer(player: P): Stats {
            throw IllegalArgumentException()
        }

        /**
         * Gets or creates stats from the player.
         * Call getsStatsFromPlayer instead. This is only intended for internal useage.
         */
        override fun <P> refreshStatsFromPlayer(player: P): CompletableFuture<Stats> {
            throw IllegalArgumentException()
        }

        /**
         * Saves the given [Stats] to the storage.
         */
        override fun save(stats: Stats): CompletableFuture<Stats> {
            throw IllegalArgumentException()
        }

        /**
         * Clears the cache of the player and saves the allocated resources.
         */
        override fun <P> clearResources(player: P): CompletableFuture<Void?> {
            throw IllegalArgumentException()
        }

        /**
         * Closes all resources immediately.
         */
        override fun close() {
            throw IllegalArgumentException()
        }
    }

    class MockedConfigurationService : ConfigurationService {
        /**
         * Opens an inputStream to the given resource name.
         */
        override fun openResource(name: String): InputStream {
            throw IllegalArgumentException()
        }

        /**
         * Checks if the given [path] contains a value.
         */
        override fun containsValue(path: String): Boolean {
            return true
        }

        /**
         * Reloads the config.
         */
        override fun reload() {
        }

        /**
         * Gets the path to the folder where the application is allowed to store
         * save data.
         */
        override val applicationDir: Path
            get() = Paths.get("")

        /**
         * Tries to load the config value from the given [path].
         * Throws a [IllegalArgumentException] if the path could not be correctly
         * loaded.
         */
        override fun <C> findValue(path: String): C {
            throw IllegalArgumentException()
        }
    }
}