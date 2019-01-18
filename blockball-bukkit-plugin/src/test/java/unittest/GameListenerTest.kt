@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.listener.GameListener
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerToggleFlightEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class GameListenerTest {
    /**
     * Given
     *      a toggleFlightEvent with a player in game
     * When
     *      onToggleFlightEvent is called
     * Then
     *     double jump should be called.
     */
    @Test
    fun onToggleFlightEvent_PlayerInGame_ShouldCallDoubleJump() {
        // Arrange
        val doubleJumpService = MockedDoubleJumpService()
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(doubleJumpService, gameService)
        val event = PlayerToggleFlightEvent(gameService.players[0], true)

        // Act
        classUnderTest.onToggleFlightEvent(event)

        // Assert
        Assertions.assertTrue(doubleJumpService.called)
        Assertions.assertTrue(event.isCancelled)
    }

    /**
     * Given
     *      a toggleFlightEvent with a creative player in game
     * When
     *      onToggleFlightEvent is called
     * Then
     *     double jump should not be called.
     */
    @Test
    fun onToggleFlightEvent_CreativePlayerInGame_ShouldNotCallDoubleJump() {
        // Arrange
        val doubleJumpService = MockedDoubleJumpService()
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(doubleJumpService, gameService)
        Mockito.`when`(gameService.players[0].gameMode).thenReturn(GameMode.CREATIVE)
        val event = PlayerToggleFlightEvent(gameService.players[0], true)

        // Act
        classUnderTest.onToggleFlightEvent(event)

        // Assert
        Assertions.assertFalse(doubleJumpService.called)
        Assertions.assertTrue(event.isFlying)
        Assertions.assertFalse(event.isCancelled)
    }

    /**
     * Given
     *      a toggleFlightEvent with a spectator player in game
     * When
     *      onToggleFlightEvent is called
     * Then
     *     double jump should not be called.
     */
    @Test
    fun onToggleFlightEvent_SpectatorPlayerInGame_ShouldNotCallDoubleJump() {
        // Arrange
        val doubleJumpService = MockedDoubleJumpService()
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(doubleJumpService, gameService)
        Mockito.`when`(gameService.players[0].gameMode).thenReturn(GameMode.SPECTATOR)
        val event = PlayerToggleFlightEvent(gameService.players[0], true)

        // Act
        classUnderTest.onToggleFlightEvent(event)

        // Assert
        Assertions.assertFalse(doubleJumpService.called)
        Assertions.assertTrue(event.isFlying)
        Assertions.assertFalse(event.isCancelled)
    }

    /**
     * Given
     *      a toggleFlightEvent with a player not in game
     * When
     *      onToggleFlightEvent is called
     * Then
     *     double jump should not be called.
     */
    @Test
    fun onToggleFlightEvent_PlayerNotInGame_ShouldNotCallDoubleJump() {
        // Arrange
        val doubleJumpService = MockedDoubleJumpService()
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(doubleJumpService, gameService)
        val event = PlayerToggleFlightEvent(Mockito.mock(Player::class.java), true)

        // Act
        classUnderTest.onToggleFlightEvent(event)

        // Assert
        Assertions.assertFalse(doubleJumpService.called)
        Assertions.assertTrue(event.isFlying)
        Assertions.assertFalse(event.isCancelled)
    }

    companion object {
        fun createWithDependencies(doubleJumpService: DoubleJumpService, gameService: GameService = Mockito.mock(GameService::class.java)): GameListener {
            val itemService = Mockito.mock(ItemService::class.java)
            val rightClickManagerService = Mockito.mock(RightclickManageService::class.java)
            val gameActionService = Mockito.mock(GameActionService::class.java)

            return GameListener(gameService, itemService, rightClickManagerService, doubleJumpService, gameActionService as GameActionService<Game>)
        }
    }

    class MockedGameService(var players: List<Player> = arrayListOf(Mockito.mock(Player::class.java))) : GameService {
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
            if (players.contains(player as Player)) {
                return Optional.of(Mockito.mock(Game::class.java))
            }

            return Optional.empty()
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

    class MockedDoubleJumpService(var called: Boolean = false) : DoubleJumpService {
        /**
         * Handles the double click of the given [player] in the given [game] and executes the double jump if available.
         * Returns if the jump has been activated.
         */
        override fun <P> handleDoubleClick(game: Game, player: P): Boolean {
            called = true
            return true
        }
    }
}