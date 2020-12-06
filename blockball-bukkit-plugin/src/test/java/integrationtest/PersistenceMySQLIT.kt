@file:Suppress("UNCHECKED_CAST")

package integrationtest

import ch.vorburger.mariadb4j.DB
import com.github.shynixn.blockball.api.business.enumeration.GameMode
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
import helper.MockedCoroutineSessionService
import kotlinx.coroutines.runBlocking
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
import java.util.stream.Stream
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
        val actual = runBlocking {
            classUnderTest.getStatsFromPlayerAsync(player).await()
        }

        // Assert
        Assertions.assertEquals(1, actual.id)
        Assertions.assertEquals(0, actual.amountOfGoals)
        Assertions.assertEquals(0, actual.amountOfWins)
        Assertions.assertEquals(0, actual.amountOfPlayedGames)
        Assertions.assertEquals(uuid.toString(), actual.playerMeta.uuid)
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
        val stats = runBlocking {
            classUnderTest.getStatsFromPlayerAsync(player).await()
        }

        stats.amountOfPlayedGames = 5
        stats.amountOfGoals = 4
        stats.amountOfWins = 1
        stats.playerMeta.name = "Superman"

        val actual = runBlocking {
            classUnderTest.getStatsFromPlayerAsync(player).await()
        }

        // Assert
        Assertions.assertEquals(1, actual.id)
        Assertions.assertEquals(4, actual.amountOfGoals)
        Assertions.assertEquals(1, actual.amountOfWins)
        Assertions.assertEquals(5, actual.amountOfPlayedGames)
        Assertions.assertEquals(uuid.toString(), actual.playerMeta.uuid)
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

            dbContext =
                SqlDbContextImpl(ConfigurationServiceImpl(plugin), LoggingUtilServiceImpl(Logger.getAnonymousLogger()))

            val sqlite = StatsSqlRepository(dbContext!!)
            return PersistenceStatsServiceImpl(sqlite, MockedProxyService(), MockedCoroutineSessionService())
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
         * Gets the world from name.
         */
        override fun <W> getWorldFromName(name: String): W? {
            throw IllegalArgumentException()
        }

        /**
         * Gets all available gamemodes.
         */
        override val gameModes: List<String>
            get() = ArrayList()

        /**
         * Teleports the player to the given location.
         */
        override fun <P, L> teleport(player: P, location: L) {
            throw IllegalArgumentException()
        }

        /**
         * Kicks the given player with the given message.
         */
        override fun <P> kickPlayer(player: P, message: String) {
            throw IllegalArgumentException()
        }

        /**
         * Is player online.
         */
        override fun <P> isPlayerOnline(player: P): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Performs a player command.
         */
        override fun <P> performPlayerCommand(player: P, command: String) {
            throw IllegalArgumentException()
        }

        /**
         * Performs a server command.
         */
        override fun performServerCommand(command: String) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the location of the player.
         */
        override fun <L, P> getEntityLocation(player: P): L {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player eye location.
         */
        override fun <P, L> getPlayerEyeLocation(player: P): L {
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
         * Sets the player gameMode.
         */
        override fun <P> setGameMode(player: P, gameMode: GameMode) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player gameMode.
         */
        override fun <P> getPlayerGameMode(player: P): GameMode {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player flying.
         */
        override fun <P> setPlayerFlying(player: P, enabled: Boolean) {
            throw IllegalArgumentException()
        }

        /**
         * Gets if the player is flying.
         */
        override fun <P> getPlayerFlying(player: P): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player walkingSpeed.
         */
        override fun <P> setPlayerWalkingSpeed(player: P, speed: Double) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player walkingSpeed.
         */
        override fun <P> getPlayerWalkingSpeed(player: P): Double {
            throw IllegalArgumentException()
        }

        /**
         * Generates a new scoreboard.
         */
        override fun <S> generateNewScoreboard(): S {
            throw IllegalArgumentException()
        }

        /**
         * Gets if the given instance is a player instance.
         */
        override fun <P> isPlayerInstance(player: P): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Gets if the given instance is an itemFrame instance.
         */
        override fun <E> isItemFrameInstance(entity: E): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player scoreboard.
         */
        override fun <P, S> setPlayerScoreboard(player: P, scoreboard: S) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player velocity.
         */
        override fun <P> setEntityVelocity(player: P, position: Position) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player direction.
         */
        override fun <P> getPlayerDirection(player: P): Position {
            throw IllegalArgumentException()
        }

        /**
         * Gets the location direction.
         */
        override fun <L> getLocationDirection(location: L): Position {
            TODO("Not yet implemented")
        }

        /**
         * Gets the player scoreboard.
         */
        override fun <P, S> getPlayerScoreboard(player: P): S {
            throw IllegalArgumentException()
        }

        /**
         * Sets if the player is allowed to fly.
         */
        override fun <P> setPlayerAllowFlying(player: P, enabled: Boolean) {
            throw IllegalArgumentException()
        }

        /**
         * Gets if the player is allowed to fly.
         */
        override fun <P> getPlayerAllowFlying(player: P): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player level.
         */
        override fun <P> getPlayerLevel(player: P): Int {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player exp.
         */
        override fun <P> getPlayerExp(player: P): Double {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player exp.
         */
        override fun <P> setPlayerExp(player: P, exp: Double) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player level.
         */
        override fun <P> setPlayerLevel(player: P, level: Int) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player max health.
         */
        override fun <P> getPlayerMaxHealth(player: P): Double {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player health.
         */
        override fun <P> getPlayerHealth(player: P): Double {
            throw IllegalArgumentException()
        }

        /**
         * Gets the player hunger.
         */
        override fun <P> getPlayerHunger(player: P): Int {
            throw IllegalArgumentException()
        }

        /**
         * Sets the given inventory items.
         */
        override fun <P, I> setInventoryContents(player: P, mainInventory: Array<I>, armorInventory: Array<I>) {
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
         * Converts the given [position] to a [Location].
         */
        override fun <L> toLocation(position: Position): L {
            throw IllegalArgumentException()
        }

        /**
         * Converts the given [position] to a [Vector].
         */
        override fun <V> toVector(position: Position): V {
            throw IllegalArgumentException()
        }

        /**
         * Sets the location direction.
         */
        override fun <L> setLocationDirection(location: L, position: Position) {
            throw IllegalArgumentException()
        }

        /**
         * Gets a list of players in the given world of the given location.
         */
        override fun <P, L> getPlayersInWorld(location: L): List<P> {
            throw IllegalArgumentException()
        }

        /**
         * Gets a stream of entities in the given world of the given location.
         */
        override fun <P, L> getEntitiesInWorld(location: L): Stream<Any> {
            throw IllegalArgumentException()
        }

        /**
         * Has player permission?
         */
        override fun <P> hasPermission(player: P, permission: String): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Gets the custom name from an entity.
         */
        override fun <E> getCustomNameFromEntity(entity: E): String? {
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

        /**
         * Sets the player max health.
         */
        override fun setPlayerMaxHealth(player: Any, health: Double) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player health.
         */
        override fun setPlayerHealth(player: Any, health: Double) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the player hunger.
         */
        override fun setPlayerHunger(player: Any, hunger: Int) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the block type at the given location from the hint.
         */
        override fun <L> setBlockType(location: L, hint: Any) {
            throw IllegalArgumentException()
        }

        /**
         * Sets the sign lines at the given location.
         * Return true if the block is valid sign with changed lines.
         */
        override fun <L> setSignLines(location: L, lines: List<String>): Boolean {
            throw IllegalArgumentException()
        }

        /**
         * Creates a new entity id.
         */
        override fun createNewEntityId(): Int {
            throw IllegalArgumentException()
        }

        override fun <P> sendPacket(player: P, packet: Any) {
            throw IllegalArgumentException()
        }
    }
}
