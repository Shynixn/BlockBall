package unittest

import com.github.shynixn.blockball.api.business.service.BlockSelectionService
import com.github.shynixn.blockball.bukkit.logic.business.listener.BlockSelectionListener
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

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
class BlockSelectionListenerTest {
    /**
     * Given
     *   a valid left click of a player
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should call selectLeftLocation.
     */
    @Test
    fun onPlayerInteractEvent_ValidPlayerLeftClick_ShouldCallLeftSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(true)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertTrue(playerInteractEvent.isCancelled)
        Assertions.assertTrue(mockedBlockSelectionService.selectLeftLocationCalled)
        Assertions.assertFalse(mockedBlockSelectionService.selectRightLocationCalled)
    }

    /**
     * Given
     *   a valid right click of a player
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should call selectRightLocation.
     */
    @Test
    fun onPlayerInteractEvent_ValidPlayerRightClick_ShouldCallRightSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(true)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertTrue(playerInteractEvent.isCancelled)
        Assertions.assertTrue(mockedBlockSelectionService.selectRightLocationCalled)
        Assertions.assertFalse(mockedBlockSelectionService.selectLeftLocationCalled)
    }

    /**
     * Given
     *   a invalid left click of a player
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should call selectLeftLocation but not cancel the event.
     */
    @Test
    fun onPlayerInteractEvent_InvalidPlayerLeftClick_ShouldCallLeftSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(false)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertFalse(playerInteractEvent.isCancelled)
        Assertions.assertTrue(mockedBlockSelectionService.selectLeftLocationCalled)
        Assertions.assertFalse(mockedBlockSelectionService.selectRightLocationCalled)
    }

    /**
     * Given
     *   a invalid right click of a player
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should call selectRightLocation but not cancel the event.
     */
    @Test
    fun onPlayerInteractEvent_InvalidPlayerRightClick_ShouldCallRightSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(false)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertFalse(playerInteractEvent.isCancelled)
        Assertions.assertTrue(mockedBlockSelectionService.selectRightLocationCalled)
        Assertions.assertFalse(mockedBlockSelectionService.selectLeftLocationCalled)
    }

    /**
     * Given
     *   a already cancelled event
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should not uncancel the event if already cancelled.
     */
    @Test
    fun onPlayerInteractEvent_AlreadyCancelLeftClickPlayerEvent_ShouldCallLeftSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(false)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)
        playerInteractEvent.isCancelled = true

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertTrue(playerInteractEvent.isCancelled)
        Assertions.assertFalse(mockedBlockSelectionService.selectRightLocationCalled)
        Assertions.assertTrue(mockedBlockSelectionService.selectLeftLocationCalled)
    }

    /**
     * Given
     *   a already cancelled event
     * When
     *    onPlayerInteractEvent is called
     * Then
     *   blockSelectionService should not uncancel the event if already cancelled.
     */
    @Test
    fun onPlayerInteractEvent_AlreadyCancelRightClickPlayerEvent_ShouldCallRightSelection() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService(false)
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerInteractEvent = PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, Mockito.mock(Block::class.java), BlockFace.DOWN)
        playerInteractEvent.isCancelled = true

        // Act
        classUnderTest.onPlayerInteractEvent(playerInteractEvent)

        // Assert
        Assertions.assertTrue(playerInteractEvent.isCancelled)
        Assertions.assertTrue(mockedBlockSelectionService.selectRightLocationCalled)
        Assertions.assertFalse(mockedBlockSelectionService.selectLeftLocationCalled)
    }

    /**
     * Given
     *   a playerQuitEvent
     * When
     *    onPlayerQuitEvent is called
     * Then
     *   blockSelectionService cleanResources should be called.
     */
    @Test
    fun onPlayerQuitEvent_ValidPlayerQuit_ShouldCallCleanResources() {
        // Arrange
        val mockedBlockSelectionService = MockedBlockSelectionService()
        val classUnderTest = createWithDependencies(mockedBlockSelectionService)
        val player = Mockito.mock(Player::class.java)
        val playerQuitEvent = PlayerQuitEvent(player, null)

        // Act
        classUnderTest.onPlayerQuitEvent(playerQuitEvent)

        // Assert
        Assertions.assertTrue(mockedBlockSelectionService.cleanResourcesCalled)
    }

    companion object {
        fun createWithDependencies(blockSelectionService: BlockSelectionService = MockedBlockSelectionService()): BlockSelectionListener {
            return BlockSelectionListener(blockSelectionService)
        }
    }

    class MockedBlockSelectionService(private var selectionSuccess: Boolean = false) : BlockSelectionService {
        var cleanResourcesCalled = false
        var selectLeftLocationCalled = false
        var selectRightLocationCalled = false

        /**
         * Selects the left location internally.
         */
        override fun <L, P> selectLeftLocation(player: P, location: L): Boolean {
            selectLeftLocationCalled = true
            return selectionSuccess
        }

        /**
         * Selects the right location internally.
         */
        override fun <L, P> selectRightLocation(player: P, location: L): Boolean {
            selectRightLocationCalled = true
            return selectionSuccess
        }

        /**
         * Returns the leftclick internal or worledit selection of the given [player].
         */
        override fun <L, P> getLeftClickLocation(player: P): Optional<L> {
            return Optional.empty()
        }

        /**
         * Returns the rightclick internal or worledit selection of the given [player].
         */
        override fun <L, P> getRightClickLocation(player: P): Optional<L> {
            return Optional.empty()
        }

        /**
         * Gives the given [player] the selection tool if he does not
         * already have it.
         */
        override fun <P> setSelectionToolForPlayer(player: P) {
        }

        /**
         * Cleans open resources.
         */
        override fun <P> cleanResources(player: P) {
            cleanResourcesCalled = true
        }
    }
}
