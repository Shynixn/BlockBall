@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.listener.GameListener
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryView
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.logging.Logger
import kotlin.collections.ArrayList

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

    // region onPlayerRespawnEvent

    /**
     * Given
     *      player dieing in team without teamspawnpoint
     * When
     *     onPlayerRespawnEvent
     * Then
     *    respawn location should be at ball spawnpoint.
     */
    @Test
    fun onPlayerRespawnEvent_PlayerDieingDuringGame_RespawnLocationShouldBeAtBallSpawnpoint() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val classUnderTest = createWithDependencies(gameService, rightClickService)

        val storage = GameStorageEntity(UUID.randomUUID())
        storage.team = Team.RED

        val game = GameEntity(ArenaEntity())
        game.ingamePlayersStorage[gameService.players[0]] = storage
        game.arena.meta.ballMeta.spawnpoint = PositionEntity("world", 34.0, 5.0, 2.0)
        gameService.games.add(game)

        val event = PlayerRespawnEvent(
            gameService.players[0],
            Location(null, 2.0, 2.0, 2.0),
            false
        )

        // Act
        classUnderTest.onPlayerRespawnEvent(event)
        val respawnLocation = event.respawnLocation

        // Assert
        Assertions.assertEquals(34.0, respawnLocation.x)
        Assertions.assertEquals(5.0, respawnLocation.y)
        Assertions.assertEquals(2.0, respawnLocation.z)
    }

    /**
     * Given
     *      player dieing in team withteamspawnpoint
     * When
     *     onPlayerRespawnEvent
     * Then
     *    respawn location should be at ball spawnpoint.
     */
    @Test
    fun onPlayerRespawnEvent_PlayerDieingDuringGame_RespawnLocationShouldBeAtTeamSpawnpoint() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val classUnderTest = createWithDependencies(gameService, rightClickService)

        val storage = GameStorageEntity(UUID.randomUUID())
        storage.team = Team.BLUE

        val game = GameEntity(ArenaEntity())
        game.ingamePlayersStorage[gameService.players[0]] = storage
        game.arena.meta.blueTeamMeta.spawnpoint = PositionEntity("world", 37.0, 5.0, 2.0)
        gameService.games.add(game)

        val event = PlayerRespawnEvent(
            gameService.players[0],
            Location(null, 2.0, 2.0, 2.0),
            false
        )

        // Act
        classUnderTest.onPlayerRespawnEvent(event)
        val respawnLocation = event.respawnLocation

        // Assert
        Assertions.assertEquals(37.0, respawnLocation.x)
        Assertions.assertEquals(5.0, respawnLocation.y)
        Assertions.assertEquals(2.0, respawnLocation.z)
    }

    /**
     * Given
     *      player dieing without game.
     * When
     *     onPlayerRespawnEvent
     * Then
     *    respawn location should not change.
     */
    @Test
    fun onPlayerRespawnEvent_PlayerNotInGame_RespawnLocationShouldNotChange() {
        // Arrange
        val classUnderTest = createWithDependencies()

        val event = PlayerRespawnEvent(
            Mockito.mock(Player::class.java),
            Location(null, 2.0, 2.0, 2.0),
            false
        )

        // Act
        classUnderTest.onPlayerRespawnEvent(event)

        // Assert
        Assertions.assertEquals(2.0, event.respawnLocation.x)
    }

    // endregion

    //region onClickOnPlacedSign

    /**
     * Given
     *      a non rightclick block event
     * When
     *     onClickOnPlacedSign
     * Then
     *     should not call watchers.
     */
    @Test
    fun onClickOnPlacedSign_NoRightClickBlock_ShouldNotCallWatchers() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val classUnderTest = createWithDependencies(gameService, rightClickService)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.LEFT_CLICK_AIR,
                null,
                null,
                BlockFace.DOWN
            )
        )
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.LEFT_CLICK_BLOCK,
                null,
                null,
                BlockFace.DOWN
            )
        )
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.PHYSICAL,
                null,
                null,
                BlockFace.DOWN
            )
        )
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_AIR,
                null,
                null,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertFalse(rightClickService.called)
    }

    /**
     * Given
     *     a rightclick on a wall block
     * When
     *     onClickOnPlacedSign
     * Then
     *    should call watchers.
     */
    @Test
    fun onClickOnPlacedSign_RightClickOnWallBlock_ShouldCallWatchers() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val classUnderTest = createWithDependencies(gameService, rightClickService)
        val block = Mockito.mock(Block::class.java)
        val location = Location(Mockito.mock(World::class.java), 5.0, 28.0, 392.0)
        Mockito.`when`(block.type).thenReturn(null)
        Mockito.`when`(block.state).thenReturn(Mockito.mock(Sign::class.java))
        Mockito.`when`(block.location).thenReturn(location)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_BLOCK,
                null,
                block,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertTrue(rightClickService.called)
    }

    /**
     * Given
     *     a rightclick on a join sign
     * When
     *     onClickOnPlacedSign
     * Then
     *    should join game.
     */
    @Test
    fun onClickOnPlacedSign_RightClickOnJoinSign_ShouldJoinGame() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val gameActionService = MockedGameActionService()
        val classUnderTest = createWithDependencies(gameService, rightClickService, gameActionService)
        val location = Location(Mockito.mock(World::class.java), 5.0, 28.0, 392.0)
        val block = Mockito.mock(Block::class.java)
        Mockito.`when`(block.type).thenReturn(null)
        Mockito.`when`(block.state).thenReturn(Mockito.mock(Sign::class.java))
        Mockito.`when`(block.location).thenReturn(location)

        rightClickService.watcherReturns = false
        val game = GameEntity(ArenaEntity())
        game.arena.meta.lobbyMeta.joinSigns.add(location.toPosition())
        gameService.games.add(game)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_BLOCK,
                null,
                block,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertTrue(rightClickService.called)
        Assertions.assertTrue(gameActionService.joinCalled)
        Assertions.assertFalse(gameActionService.leaveCalled)
        Assertions.assertNull(gameActionService.joinedTeam)
    }

    /**
     * Given
     *     a rightclick on a leave sign
     * When
     *     onClickOnPlacedSign
     * Then
     *    should leave game.
     */
    @Test
    fun onClickOnPlacedSign_RightClickOnLeaveSign_ShouldLeaveGame() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val gameActionService = MockedGameActionService()
        val classUnderTest = createWithDependencies(gameService, rightClickService, gameActionService)
        val location = Location(Mockito.mock(World::class.java), 5.0, 28.0, 392.0)
        val block = Mockito.mock(Block::class.java)
        Mockito.`when`(block.type).thenReturn(null)
        Mockito.`when`(block.state).thenReturn(Mockito.mock(Sign::class.java))
        Mockito.`when`(block.location).thenReturn(location)

        rightClickService.watcherReturns = false
        val game = GameEntity(ArenaEntity())
        game.arena.meta.lobbyMeta.leaveSigns.add(location.toPosition())
        gameService.games.add(game)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_BLOCK,
                null,
                block,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertTrue(rightClickService.called)
        Assertions.assertFalse(gameActionService.joinCalled)
        Assertions.assertTrue(gameActionService.leaveCalled)
    }

    /**
     * Given
     *     a rightclick on a red team sign.
     * When
     *     onClickOnPlacedSign
     * Then
     *    should join red team.
     */
    @Test
    fun onClickOnPlacedSign_RightClickOnRedTeamSign_ShouldJoinRedTeamGame() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val gameActionService = MockedGameActionService()
        val classUnderTest = createWithDependencies(gameService, rightClickService, gameActionService)
        val location = Location(Mockito.mock(World::class.java), 5.0, 28.0, 392.0)
        val block = Mockito.mock(Block::class.java)
        Mockito.`when`(block.type).thenReturn(null)
        Mockito.`when`(block.state).thenReturn(Mockito.mock(Sign::class.java))
        Mockito.`when`(block.location).thenReturn(location)

        rightClickService.watcherReturns = false
        val game = GameEntity(ArenaEntity())
        game.arena.meta.redTeamMeta.signs.add(location.toPosition())
        gameService.games.add(game)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_BLOCK,
                null,
                block,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertTrue(rightClickService.called)
        Assertions.assertTrue(gameActionService.joinCalled)
        Assertions.assertFalse(gameActionService.leaveCalled)
        Assertions.assertEquals(Team.RED, gameActionService.joinedTeam)
    }

    /**
     * Given
     *     a rightclick on a blue team sign.
     * When
     *     onClickOnPlacedSign
     * Then
     *    should join blue team.
     */
    @Test
    fun onClickOnPlacedSign_RightClickOnBlueTeamSign_ShouldJoinBlueTeamGame() {
        // Arrange
        val gameService = MockedGameService()
        val rightClickService = MockedRightClickService()
        val gameActionService = MockedGameActionService()
        val classUnderTest = createWithDependencies(gameService, rightClickService, gameActionService)
        val location = Location(Mockito.mock(World::class.java), 5.0, 28.0, 392.0)
        val block = Mockito.mock(Block::class.java)
        Mockito.`when`(block.type).thenReturn(null)
        Mockito.`when`(block.state).thenReturn(Mockito.mock(Sign::class.java))
        Mockito.`when`(block.location).thenReturn(location)

        rightClickService.watcherReturns = false
        val game = GameEntity(ArenaEntity())
        game.arena.meta.blueTeamMeta.signs.add(PositionEntity(location.x, location.y, location.z))
        gameService.games.add(game)

        // Act
        classUnderTest.onClickOnPlacedSign(
            PlayerInteractEvent(
                gameService.players[0],
                Action.RIGHT_CLICK_BLOCK,
                null,
                block,
                BlockFace.DOWN
            )
        )

        // Assert
        Assertions.assertTrue(rightClickService.called)
        Assertions.assertTrue(gameActionService.joinCalled)
        Assertions.assertFalse(gameActionService.leaveCalled)
        Assertions.assertEquals(Team.BLUE, gameActionService.joinedTeam)
    }

    //endregion

    //region onPlayerHungerEvent

    /**
     * Given
     *      a FoodLevelChangeEvent with a player in game
     * When
     *      onPlayerHungerEvent is called
     * Then
     *     event should be canceled.
     */
    @Test
    fun onPlayerHungerEvent_PlayerInGame_ShouldCancelHunger() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val event = FoodLevelChangeEvent(gameService.players[0], 2)

        // Act
        classUnderTest.onPlayerHungerEvent(event)

        // Assert
        Assertions.assertTrue(event.isCancelled)
    }

    /**
     * Given
     *      a FoodLevelChangeEvent with a player not in game.
     * When
     *      onPlayerHungerEvent is called
     * Then
     *     event should not be canceled.
     */
    @Test
    fun onPlayerHungerEvent_PlayerNotInGame_ShouldNotCancelHunger() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val event = FoodLevelChangeEvent(Mockito.mock(Player::class.java), 2)

        // Act
        classUnderTest.onPlayerHungerEvent(event)

        // Assert
        Assertions.assertFalse(event.isCancelled)
    }

    //endregion

    //region onPlayerClickInventoryEvent

    /**
     * Given
     *      a InventoryClickEvent with player in game without permission
     * When
     *      onPlayerClickInventoryEvent is called
     * Then
     *     event should be canceled.
     */
    @Test
    fun onPlayerClickInventoryEvent_PlayerInGameWithoutPermission_ShouldCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(gameService.players[0])

        val event = InventoryClickEvent(
            inventoryView,
            InventoryType.SlotType.CRAFTING,
            2,
            ClickType.LEFT,
            InventoryAction.PLACE_ONE
        )

        // Act
        classUnderTest.onPlayerClickInventoryEvent(event)

        // Assert
        Assertions.assertTrue(event.isCancelled)
    }

    /**
     * Given
     *      a InventoryClickEvent with player not in game without permission
     * When
     *      onPlayerClickInventoryEvent is called
     * Then
     *     event should not be canceled.
     */
    @Test
    fun onPlayerClickInventoryEvent_PlayerNotInGameWithoutPermission_ShouldNotCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(Mockito.mock(Player::class.java))

        val event = InventoryClickEvent(
            inventoryView,
            InventoryType.SlotType.CRAFTING,
            2,
            ClickType.LEFT,
            InventoryAction.PLACE_ONE
        )

        // Act
        classUnderTest.onPlayerClickInventoryEvent(event)

        // Assert
        Assertions.assertFalse(event.isCancelled)
    }

    /**
     * Given
     *      a InventoryClickEvent with player in game with permission
     * When
     *      onPlayerClickInventoryEvent is called
     * Then
     *     event should not be canceled.
     */
    @Test
    fun onPlayerClickInventoryEvent_PlayerInGameWithPermission_ShouldNotCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(gameService.players[0])
        Mockito.`when`(gameService.players[0].hasPermission(Mockito.anyString())).thenReturn(true)

        val event = InventoryClickEvent(
            inventoryView,
            InventoryType.SlotType.CRAFTING,
            2,
            ClickType.LEFT,
            InventoryAction.PLACE_ONE
        )

        // Act
        classUnderTest.onPlayerClickInventoryEvent(event)

        // Assert
        Assertions.assertFalse(event.isCancelled)
    }

    //endregion

    //region onPlayerOpenInventoryEvent

    /**
     * Given
     *      a InventoryOpenEvent with player in game without permission
     * When
     *      onPlayerOpenInventoryEvent is called
     * Then
     *     event should be canceled.
     */
    @Test
    fun onPlayerOpenInventoryEvent_PlayerInGameWithoutPermission_ShouldCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(gameService.players[0])

        val event = InventoryOpenEvent(inventoryView)

        // Act
        classUnderTest.onPlayerOpenInventoryEvent(event)

        // Assert
        Assertions.assertTrue(event.isCancelled)
    }

    /**
     * Given
     *      a InventoryOpenEvent with player not in game without permission
     * When
     *      onPlayerOpenInventoryEvent is called
     * Then
     *     event should not be canceled.
     */
    @Test
    fun onPlayerOpenInventoryEvent_PlayerNotInGameWithoutPermission_ShouldNotCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(Mockito.mock(Player::class.java))

        val event = InventoryOpenEvent(inventoryView)

        // Act
        classUnderTest.onPlayerOpenInventoryEvent(event)

        // Assert
        Assertions.assertFalse(event.isCancelled)
    }

    /**
     * Given
     *      a InventoryOpenEvent with player in game with permission
     * When
     *      onPlayerOpenInventoryEvent is called
     * Then
     *     event should not be canceled.
     */
    @Test
    fun onPlayerOpenInventoryEvent_PlayerInGameWithPermission_ShouldNotCancelEvent() {
        // Arrange
        val gameService = MockedGameService()
        val classUnderTest = createWithDependencies(gameService)
        val inventoryView = Mockito.mock(InventoryView::class.java)
        Mockito.`when`(inventoryView.topInventory).thenReturn(Mockito.mock(Inventory::class.java))
        Mockito.`when`(inventoryView.player).thenReturn(gameService.players[0])
        Mockito.`when`(gameService.players[0].hasPermission(Mockito.anyString())).thenReturn(true)

        val event = InventoryOpenEvent(inventoryView)

        // Act
        classUnderTest.onPlayerOpenInventoryEvent(event)

        // Assert
        Assertions.assertFalse(event.isCancelled)
    }

    //endregion

    companion object {
        fun createWithDependencies(
            gameService: GameService = Mockito.mock(GameService::class.java),
            rightclickManageService: RightclickManageService = MockedRightClickService(),
            gameActionService: GameActionService = MockedGameActionService()
        ): GameListener {

            val server = Mockito.mock(Server::class.java)
            Mockito.`when`(server.getWorld(Mockito.anyString())).thenReturn(Mockito.mock(World::class.java))
            Mockito.`when`(server.logger).thenReturn(Logger.getAnonymousLogger())
            val field = Bukkit::class.java.getDeclaredField("server")
            field.isAccessible = true
            field.set(null, server)

            return GameListener(
                gameService,
                rightclickManageService,
                gameActionService,
                MockedGameExecutionService(),
                Mockito.mock(ConcurrencyService::class.java),
                Mockito.mock(GameSoccerService::class.java),
                Mockito.mock(ProxyService::class.java),
                Mockito.mock(BallEntityService::class.java)
            )
        }
    }

    class MockedGameActionService(
        var joinCalled: Boolean = false,
        var leaveCalled: Boolean = false,
        var joinedTeam: Team? = null
    ) : GameActionService {
        /**
         * Closes the given game and all underlying resources.
         */
        override fun closeGame(game: Game) {
        }

        /**
         * Lets the given [player] leave join the given [game]. Optional can the prefered
         * [team] be specified but the team can still change because of arena settings.
         * Does nothing if the player is already in a Game.
         */
        override fun <P> joinGame(game: Game, player: P, team: Team?): Boolean {
            joinedTeam = team
            joinCalled = true
            return true
        }

        /**
         * Lets the given [player] leave the given [game].
         * Does nothing if the player is not in the game.
         */
        override fun <P> leaveGame(game: Game, player: P) {
            leaveCalled = true
        }

        /**
         * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
         * 0 - 20 for each second.
         */
        override fun handle(game: Game, ticks: Int) {
        }
    }

    class MockedRightClickService(var called: Boolean = false, var watcherReturns: Boolean = true) :
        RightclickManageService {
        /**
         * Gets called one time when a location gets rightlicked by [player].
         */
        override fun <P, L> watchForNextRightClickSign(player: P, f: (L) -> Unit) {
        }

        /**
         * Executes the watcher for the given [player] if he has registered one.
         * Returns if watchers has been executed.
         */
        override fun <P, L> executeWatchers(player: P, location: L): Boolean {
            called = true
            return watcherReturns
        }

        /**
         * Clears all resources this [player] has allocated from this service.
         */
        override fun <P> cleanResources(player: P) {
        }
    }

    class MockedGameExecutionService : GameExecutionService {
        /**
         * Applies points to the belonging teams when the given [player] dies in the given [game].
         */
        override fun <P, G : Game> applyDeathPoints(game: G, player: P) {
        }

        /**
         * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
         */
        override fun <P, G : Game> respawn(game: G, player: P) {
        }
    }

    class MockedGameService(
        var players: List<Player> = arrayListOf(Mockito.mock(Player::class.java)),
        val games: ArrayList<Game> = ArrayList()
    ) : GameService {
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
                return if (games.size == 0) {
                    Optional.of(Mockito.mock(Game::class.java))
                } else {
                    Optional.of(games[0])
                }
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
            return games
        }

        /**
         * Closes all games permanently and should be executed on server shutdown.
         */
        override fun close() {
            throw IllegalArgumentException()
        }
    }
}
