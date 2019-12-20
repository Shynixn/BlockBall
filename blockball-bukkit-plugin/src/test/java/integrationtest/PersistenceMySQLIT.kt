@file:Suppress("UNCHECKED_CAST")

package integrationtest

import ch.vorburger.mariadb4j.DB
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.context.SqlDbContext
import com.github.shynixn.blockball.api.persistence.entity.ChatBuilder
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.bukkit.logic.business.service.ConfigurationServiceImpl
import com.github.shynixn.blockball.core.logic.business.service.LoggingUtilServiceImpl
import com.github.shynixn.blockball.core.logic.business.service.PersistenceStatsServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.context.SqlDbContextImpl
import com.github.shynixn.blockball.core.logic.persistence.repository.StatsSqlRepository
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.io.File
import java.io.FileInputStream
import java.sql.DriverManager
import java.util.*
import java.util.logging.Logger
import kotlin.collections.ArrayList

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
class PersistenceMySQLIT {
    /**
     * Given
     *      initial empty database and production configuration in config.yml
     * When
     *      when getOrCreateFromPlayerUUID with a new uuid is called
     * Then
     *     the default stats with the default production configuration from the config.yml should be generated.
     */
    @Test
    fun getOrCreateFromPlayerUUID_ProductionConfiguration_ShouldGenerateCorrectStats() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val uuid = UUID.randomUUID()
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.name).thenReturn("Pikachu")
        Mockito.`when`(player.uniqueId).thenReturn(uuid)

        // Act
        val actual = classUnderTest.getStatsFromPlayer(player)

        // Assert
        Assertions.assertEquals(1, actual.id)
        Assertions.assertEquals(0, actual.amountOfGoals)
        Assertions.assertEquals(0, actual.amountOfWins)
        Assertions.assertEquals(0, actual.amountOfPlayedGames)
        Assertions.assertEquals(uuid.toString(), actual.playerMeta.uuid.toString())
        Assertions.assertEquals("Pikachu", actual.playerMeta.name)
    }

    /**
     * Given
     *      initial empty database and production configuration in config.yml
     * When
     *      when getOrCreateFromPlayerUUID is called
     * Then
     *     the default stats with the default production configuration should be correctly editable and storeAble again.
     */
    @Test
    fun getOrCreateFromPlayerUUID_ProductionConfiguration_ShouldAllowChangingPet() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val uuid = UUID.randomUUID()
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.name).thenReturn("Pikachu")
        Mockito.`when`(player.uniqueId).thenReturn(uuid)

        // Act
        val stats = classUnderTest.getStatsFromPlayer(player)

        stats.amountOfPlayedGames = 5
        stats.amountOfGoals = 4
        stats.amountOfWins = 1
        stats.playerMeta.name = "Superman"

        classUnderTest.save(stats).get()
        val actual = classUnderTest.getStatsFromPlayer(player)

        // Assert
        Assertions.assertEquals(1, actual.id)
        Assertions.assertEquals(4, actual.amountOfGoals)
        Assertions.assertEquals(1, actual.amountOfWins)
        Assertions.assertEquals(5, actual.amountOfPlayedGames)
        Assertions.assertEquals(uuid.toString(), actual.playerMeta.uuid.toString())
        Assertions.assertEquals("Superman", actual.playerMeta.name)
    }

    companion object {
        private var database: DB? = null
        private var dbContext: SqlDbContext? = null

        fun createWithDependencies(): PersistenceStatsService {
            val configuration = YamlConfiguration()
            configuration.load(File("../blockball-core/src/main/resources/assets/blockball/config.yml"))
            configuration.set("sql.enabled", true)
            configuration.set("sql.database", "db")

            if (dbContext != null) {
                dbContext!!.close()
            }

            if (database != null) {
                database!!.stop()
            }

            database = DB.newEmbeddedDB(3306)
            database!!.start()

            DriverManager.getConnection("jdbc:mysql://localhost:3306/?user=root&password=").use { conn ->
                conn.createStatement().use { statement ->
                    statement.executeUpdate("CREATE DATABASE db")
                }
            }

            val plugin = Mockito.mock(Plugin::class.java)
            Mockito.`when`(plugin.config).thenReturn(configuration)
            Mockito.`when`(plugin.dataFolder).thenReturn(File("integrationtest-sqlite"))
            Mockito.`when`(plugin.getResource(Mockito.anyString())).then { parameter ->
                if (parameter.arguments[0].toString() == "assets/blockball/sql/create-mysql.sql") {
                    FileInputStream(File("../blockball-core/src/main/resources/assets/blockball/sql/create-mysql.sql"))
                } else {
                    Unit
                }
            }

            dbContext = SqlDbContextImpl(ConfigurationServiceImpl(plugin), LoggingUtilServiceImpl(Logger.getAnonymousLogger()))

            val sqlite = StatsSqlRepository(dbContext!!)
            return PersistenceStatsServiceImpl(sqlite, MockedProxyService(), MockedConcurrencyService())
        }
    }

    class MockedConcurrencyService : ConcurrencyService {
        /**
         * Runs the given [function] synchronised with the given [delayTicks] and [repeatingTicks].
         */
        override fun runTaskSync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
            function.invoke()
        }

        /**
         * Runs the given [function] asynchronous with the given [delayTicks] and [repeatingTicks].
         */
        override fun runTaskAsync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
            function.invoke()
        }
    }

    class MockedProxyService : ProxyService {
        /**
         * Gets the name of the World the player is in.
         */
        override fun <P> getWorldName(player: P): String {
            throw IllegalArgumentException()
        }

        /**
         * Gets all available gamemodes.
         */
        override val gameModes: List<String>
            get() = ArrayList()

        /**
         * Performs a player command.
         */
        override fun <P> performPlayerCommand(player: P, command: String) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the location of the player.
         */
        override fun <L, P> getPlayerLocation(player: P): L {
            throw IllegalArgumentException()
        }

        /**
         * Gets the name of a player.
         */
        override fun <P> getPlayerName(player: P): String {
            return (player as Player).name
        }

        /**
         * Gets the player uuid.
         */
        override fun <P> getPlayerUUID(player: P): String {
            return (player as Player).uniqueId.toString()
        }

        /**
         * Sets the location of the player.
         */
        override fun <L, P> setPlayerLocation(player: P, location: L) {
            throw IllegalArgumentException()
        }

        /**
         * Gets a copy of the player inventory.
         */
        override fun <P> getPlayerInventoryCopy(player: P): Array<Any?> {
            throw IllegalArgumentException()
        }

        /**
         * Gets a copy of the player armor inventory.
         */
        override fun <P> getPlayerInventoryArmorCopy(player: P): Array<Any?> {
            throw IllegalArgumentException()
        }

        /**
         * Gets a list of all online players.
         */
        override fun <P> getOnlinePlayers(): List<P> {
            throw IllegalArgumentException()
        }

        /**
         * Sends a plugin message through the given channel.
         */
        override fun <P> sendPlayerPluginMessage(player: P, channel: String, content: ByteArray) {
            throw IllegalArgumentException()
        }

        /**
         * Converts the given [location] to a [Position].
         */
        override fun <L> toPosition(location: L): Position {
            throw IllegalArgumentException()
        }

        /**
         * Sends a chat message to the [sender].
         */
        override fun <S> sendMessage(sender: S, chatBuilder: ChatBuilder) {
        }

        /**
         * Sends a message to the [sender].
         */
        override fun <S> sendMessage(sender: S, message: String) {
        }
    }
}