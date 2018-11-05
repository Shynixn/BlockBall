package unittest

import com.github.shynixn.blockball.api.business.enumeration.MaterialType
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.listener.BallListener
import com.github.shynixn.blockball.bukkit.logic.business.listener.GameListener
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.event.world.ChunkLoadEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.lang.IllegalArgumentException
import java.util.*
import java.util.concurrent.CompletableFuture

class GameListenerTest {
    /**
     * Given
     *      a valid chunk load
     * When
     *      onChunkLoadEvent is called
     * Then
     *     cleanUpInvalidEntities should be called.
     */
    @Test
    fun onChunkLoadEvent_ValidChunkLoad_ShouldCallClean() {
        // Arrange
        val ball = BallListenerTest.MockedBallProxy()
        val mockedEntityService = BallListenerTest.MockedBallEntityService(ball)
        val classUnderTest = createWithDependencies(mockedEntityService)
        val chunk = Mockito.mock(Chunk::class.java)
        val entities = arrayOf(ball.getDesignArmorstand<Entity>())
        Mockito.`when`(chunk.entities).thenReturn(entities)
        val chunkLoadEvent = ChunkLoadEvent(chunk, false)

        // Act
        classUnderTest.onChunkLoadEvent(chunkLoadEvent)

        // Assert
        Assertions.assertTrue(mockedEntityService.cleanUpCalled)
    }

    companion object {
        fun createWithDependencies(mockedGameService : GameService = MockedGameService()): GameListener {
            return GameListener(mockedGameService, )
        }
    }

    private class MockedItemService : ItemService{
        /**
         * Gets the numeric material value. Throws a [IllegalArgumentException]
         * if the numeric value could not get located.
         */
        override fun <M> getNumericMaterialValue(material: M): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * Gets the material from the numeric value.
         * Throws a [IllegalArgumentException] if the numeric value could
         * not get applied to a material.
         */
        override fun <M> getMaterialFromNumericValue(value: Int): M {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * Gets the material from the material type. Throws a [IllegalArgumentException]
         * if mapping is not possible.
         */
        override fun <M> getMaterialFromMaterialType(materialType: MaterialType): M {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        /**
         * Creates a new itemStack from the given [materialType] [dataValue] [amount].
         */
        override fun <I> createItemStack(materialType: MaterialType, dataValue: Int, amount: Int): I {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private class MockedGameService : GameService {
        /**
         * Restarts all games on the server.
         */
        override fun restartGames(): CompletableFuture<Void?> {
            throw IllegalArgumentException()
        }

        /**
         * Returns the game if the given [player] is playing a game.
         */
        override fun <P> getGameFromPlayer(player: P): Optional<Game> {
            throw IllegalArgumentException()
        }

        /**
         * Returns the game if the given [player] is spectating a game.
         */
        override fun <P> getGameFromSpectatingPlayer(player: P): Optional<Game> {
            throw IllegalArgumentException()
        }

        /**
         * Returns the game at the given location.
         */
        override fun <L> getGameFromLocation(location: L): Optional<Game> {
            throw IllegalArgumentException()
        }

        /**
         * Returns the game with the given name or displayName.
         */
        override fun getGameFromName(name: String): Optional<Game> {
            throw IllegalArgumentException()
        }

        /**
         * Returns all currently loaded games on the server.
         */
        override fun getAllGames(): List<Game> {
            throw IllegalArgumentException()
        }

        /**
         * Closes all games permanently and should be executed on server shutdown.
         */
        override fun close() {
            throw IllegalArgumentException()
        }
    }
}