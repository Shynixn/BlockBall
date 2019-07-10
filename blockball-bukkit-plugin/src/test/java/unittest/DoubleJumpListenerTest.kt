package unittest

import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.blockball.bukkit.logic.business.listener.DoubleJumpListener
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameEntity
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.bukkit.util.Vector
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
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
class DoubleJumpListenerTest {
    /**
     * Given
     *      a player on the ground in game with double jump enabled
     * When
     *      onPlayerMoveEvent is called
     * Then
     *    player should change state.
     */
    @Test
    fun onPlayerMoveEvent_PlayerInGame_ShouldAllowFlight() {
        // Arrange
        val location = Location(Mockito.mock(World::class.java), 1.0, 1.0, 1.0)
        val gameService = MockedGameService()
        @Suppress("DEPRECATION")
        Mockito.`when`(gameService.player.isOnGround).thenReturn(true)
        val playerMoveEvent = PlayerMoveEvent(gameService.player, location, location)
       gameService.game.arena.meta.doubleJumpMeta.enabled = true
        val classUnderTest = createWithDependencies(gameService)

        // Act
        classUnderTest.onPlayerMoveEvent(playerMoveEvent)

        // Assert
        Assertions.assertTrue(gameService.requestedGame)
    }

    /**
     * Given
     *      a player in mid air
     * When
     *      onPlayerMoveEvent is called
     * Then
     *    player should not change state.
     */
    @Test
    fun onPlayerMoveEvent_PlayerInMidAir_ShouldNotRequestGame() {
        // Arrange
        val player = Mockito.mock(Player::class.java)
        val location = Location(Mockito.mock(World::class.java), 1.0, 1.0, 1.0)
        val playerMoveEvent = PlayerMoveEvent(player, location, location)
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)

        // Act
        classUnderTest.onPlayerMoveEvent(playerMoveEvent)

        // Assert
        Assertions.assertFalse(gameService.requestedGame)
        Assertions.assertFalse(playerMoveEvent.player.allowFlight)
    }

    /**
     * Given
     *      a player not in game.
     * When
     *      onPlayerMoveEvent is called
     * Then
     *    player should not change state.
     */
    @Test
    fun onPlayerMoveEvent_PlayerNotInGame_ShouldNotAllowFlight() {
        // Arrange
        val player = Mockito.mock(Player::class.java)
        @Suppress("DEPRECATION")
        Mockito.`when`(player.isOnGround).thenReturn(true)
        val location = Location(Mockito.mock(World::class.java), 1.0, 1.0, 1.0)
        val playerMoveEvent = PlayerMoveEvent(player, location, location)
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)

        // Act
        classUnderTest.onPlayerMoveEvent(playerMoveEvent)

        // Assert
        Assertions.assertTrue(gameService.requestedGame)
        Assertions.assertFalse(playerMoveEvent.player.allowFlight)
    }

    /**
     * Given
     *      a player in game with no cooldown.
     * When
     *    onPlayerToggleFlightEvent is called
     * Then
     *    new velocity should be applied.
     */
    @Test
    fun onPlayerToggleFlightEvent_PlayerReadyForJump_ShouldChangeVelocity() {
        // Arrange
        val gameService = MockedGameService()
        val player = gameService.player
        Mockito.`when`(player.isFlying).thenReturn(true)
        Mockito.`when`(player.location).thenReturn(Location(Mockito.mock(World::class.java), 20.2, 20.2, 30.2))
        Mockito.`when`(player.world).thenReturn(Mockito.mock(World::class.java))
        Mockito.`when`(player.allowFlight).thenReturn(true)
        Mockito.`when`(player.velocity).thenReturn(Vector(5, 3, 4))
        val event = PlayerToggleFlightEvent(player, false)
        val soundService = MockedSoundService()
        val particleService = MockedParticleService()
        val classUnderTest = createWithDependencies(gameService, soundService, particleService)

        // Act
        classUnderTest.onPlayerToggleFlightEvent(event)

        // Assert
        Assertions.assertTrue(gameService.requestedGame)
        Assertions.assertTrue(soundService.playSoundCalled)
        Assertions.assertTrue(particleService.playParticleCalled)
    }

    /**
     * Given
     *      a player in creative or spectator mode.
     * When
     *    onPlayerToggleFlightEvent is called
     * Then
     *    game should not be requested.
     */
    @Test
    fun onPlayerToggleFlightEvent_PlayerInCreativeOrSpectator_ShouldNotRequestGame() {
        // Arrange
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.isFlying).thenReturn(true)
        Mockito.`when`(player.allowFlight).thenReturn(true)
        Mockito.`when`(player.gameMode).thenReturn(GameMode.SPECTATOR)
        val event = PlayerToggleFlightEvent(player, false)
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)

        // Act
        classUnderTest.onPlayerToggleFlightEvent(event)

        // Assert
        Assertions.assertFalse(gameService.requestedGame)
        Assertions.assertTrue(player.allowFlight)
        Assertions.assertTrue(player.isFlying)
    }

    /**
     * Given
     *      a player not in game.
     * When
     *    onPlayerToggleFlightEvent is called
     * Then
     *    state should not change.
     */
    @Test
    fun onPlayerToggleFlightEvent_PlayerNotInGame_ShouldNotChangeState() {
        // Arrange
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.isFlying).thenReturn(true)
        Mockito.`when`(player.allowFlight).thenReturn(true)
        val event = PlayerToggleFlightEvent(player, false)
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)

        // Act
        classUnderTest.onPlayerToggleFlightEvent(event)

        // Assert
        Assertions.assertTrue(gameService.requestedGame)
        Assertions.assertTrue(player.allowFlight)
        Assertions.assertTrue(player.isFlying)
    }

    /**
     * Given
     *      a player in game with cooldown.
     * When
     *    onPlayerToggleFlightEvent is called
     * Then
     *    no new velocity should be applied.
     */
    @Test
    fun onPlayerToggleFlightEvent_PlayerInJumpCooldown_ShouldNotChangeVelocity() {
        // Arrange
        val gameService = MockedGameService()
        val player = gameService.player
        gameService.game.doubleJumpCoolDownPlayers[player] = 20
        Mockito.`when`(player.isFlying).thenReturn(true)
        Mockito.`when`(player.velocity).thenReturn(Vector(5, 3, 4))
        val event = PlayerToggleFlightEvent(player, false)
        val soundService = MockedSoundService()
        val particleService = MockedParticleService()
        val classUnderTest = createWithDependencies(gameService, soundService, particleService)

        // Act
        classUnderTest.onPlayerToggleFlightEvent(event)

        // Assert
        Assertions.assertTrue(gameService.requestedGame)
        Assertions.assertFalse(soundService.playSoundCalled)
        Assertions.assertFalse(particleService.playParticleCalled)
    }

    companion object {
        fun createWithDependencies(gameService: GameService, soundService: SoundService = MockedSoundService(), particleService: ParticleService = MockedParticleService()): DoubleJumpListener {
            return DoubleJumpListener(gameService, soundService, particleService)
        }
    }

    class MockedGameService(var requestedGame: Boolean = false, var game: Game = GameEntity(ArenaEntity()), var player: Player = Mockito.mock(Player::class.java)) : GameService {
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
            requestedGame = true

            if (player != this.player) {
                return Optional.empty()
            }

            return Optional.of(game)
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
        }
    }

    class MockedSoundService(override val soundNames: List<String> = ArrayList()) : SoundService {
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