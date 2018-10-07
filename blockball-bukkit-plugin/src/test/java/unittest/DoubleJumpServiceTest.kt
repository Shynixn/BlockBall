package unittest

import com.github.shynixn.blockball.api.business.service.DoubleJumpService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.bukkit.logic.business.service.DoubleJumpServiceImpl
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.DoubleJumpMetaEntity
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.GameEntity
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import java.util.concurrent.CompletableFuture
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
class DoubleJumpServiceTest {
    /**
     * Given
     *      Valid conditions for double jump
     * When
     *      handleDoubleClick is called
     * Then
     *     Correct velocity should be applied, true returned and sound and particle service called.
     */
    @Test
    fun handleDoubleClick_ValidPlayerConditions_ShouldReturnTrue() {
        // Arrange
        val mockedGameService = MockedGameService()
        val mockedParticleService = MockedParticleService()
        val mockedSoundServie = MockedSoundService()
        val player = mock(Player::class.java)
        val location = mock(Location::class.java)
        val world = mock(World::class.java)
        Mockito.`when`(world.players).thenReturn(ArrayList<Player>())
        Mockito.`when`(location.direction).thenReturn(Vector(1, 1, 1))
        Mockito.`when`(player.location).thenReturn(location)
        Mockito.`when`(player.world).thenReturn(world)
        Mockito.`when`(player.velocity).thenReturn(Vector(1, 1, 0))

        val classUnderTest = createWithDependencies(mockedGameService, mockedSoundServie, mockedParticleService)

        // Act
        val success = classUnderTest.handleDoubleClick(player)

        // Assert
        Assertions.assertTrue(success)
        Assertions.assertEquals(1.0, player.velocity.x)
        Assertions.assertEquals(1.0, player.velocity.y)
        Assertions.assertTrue(mockedSoundServie.playSoundCalled)
        Assertions.assertTrue(mockedParticleService.playParticleCalled)
    }

    /**
     * Given
     *      player which is not in a game.
     * When
     *      handleDoubleClick is called
     * Then
     *     False should be returned.
     */
    @Test
    fun handleDoubleClick_ValidPlayerNotInGame_ShouldReturnFalse() {
        // Arrange
        val mockedGameService = MockedGameService()
        mockedGameService.shouldReturnGame = false
        val mockedParticleService = MockedParticleService()
        val mockedSoundServie = MockedSoundService()
        val player = mock(Player::class.java)
        val classUnderTest = createWithDependencies(mockedGameService, mockedSoundServie, mockedParticleService)

        // Act
        val success = classUnderTest.handleDoubleClick(player)

        // Assert
        Assertions.assertFalse(success)
        Assertions.assertFalse(mockedSoundServie.playSoundCalled)
        Assertions.assertFalse(mockedParticleService.playParticleCalled)
    }

    /**
     * Given
     *      invalid player
     * When
     *      handleDoubleClick is called
     * Then
     *     Exception should be thrown.
     */
    @Test
    fun handleDoubleClick_InvalidPlayer_ShouldThrowException() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.handleDoubleClick("I'm a player")
        }
    }

    companion object {
        fun createWithDependencies(gameService: GameService = MockedGameService(), soundService: SoundService = MockedSoundService(), particleService: ParticleService = MockedParticleService()): DoubleJumpService {
            return DoubleJumpServiceImpl(gameService, soundService, particleService)
        }
    }

    class MockedGameService : GameService {
        var shouldReturnGame = true
        private var gameEntity: Game? = null

        init {
            val arena = mock(Arena::class.java)
            val meta = mock(ArenaMeta::class.java)
            `when`(meta.doubleJumpMeta).thenReturn(DoubleJumpMetaEntity())
            gameEntity = GameEntity(arena)
            `when`(arena.meta).thenReturn(meta)
        }

        /**
         * Restarts all games on the server.
         */
        override fun restartGames(): CompletableFuture<Void?> {
            return CompletableFuture()
        }

        /**
         * Returns the game if the given [player] is playing a game.
         */
        override fun <P> getGameFromPlayer(player: P): Optional<Game> {
            if (shouldReturnGame) {
                return Optional.of(gameEntity!!)
            }

            return Optional.empty()
        }

        /**
         * Returns the game if the given [player] is spectating a game.
         */
        override fun <P> getGameFromSpectatingPlayer(player: P): Optional<Game> {
            return Optional.empty()
        }

        /**
         * Returns the game at the given location.
         */
        override fun <L> getGameFromLocation(location: L): Optional<Game> {
            return Optional.empty()
        }

        /**
         * Returns the game with the given name or displayName.
         */
        override fun getGameFromName(name: String): Optional<Game> {
            return Optional.empty()
        }

        /**
         * Returns all currently loaded games on the server.
         */
        override fun getAllGames(): List<Game> {
            return ArrayList()
        }

        /**
         * Closes all games permanently and should be executed on server shutdown.
         */
        override fun close() {
        }
    }

    class MockedSoundService : SoundService {
        var playSoundCalled = false

        /**
         * Plays the given [sound] at the given [location] for the given [players].
         */
        override fun <L, P> playSound(location: L, sound: Sound, players: Collection<P>) {
            playSoundCalled = true
        }
    }

    class MockedParticleService : ParticleService {
        var playParticleCalled = false

        /**
         * Plays the given [particle] at the given [location] for the given [players].
         */
        override fun <L, P> playParticle(location: L, particle: Particle, players: Collection<P>) {
            playParticleCalled = true
        }
    }
}