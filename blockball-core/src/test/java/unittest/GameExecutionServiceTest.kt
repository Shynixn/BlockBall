@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.GameExecutionService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.business.service.GameExecutionServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameStorageEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

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
class GameExecutionServiceTest {
    /**
     * Given
     *      player in team without spawnpoint
     * When
     *      respawn is called
     * Then
     *     player should be teleported to ball spawnpoint.
     */
    @Test
    fun respawn_PlayerInTeamWithoutSpawnpoint_ShouldSetLocationToBallSpawnpoint() {
        // Arrange
        val proxyService = Mockito.mock(ProxyService::class.java)
        val classUnderTest = createWithDependencies(proxyService)
        val game = GameEntity(ArenaEntity())
        val player = "Player"
        var position: Position = PositionEntity(0.0, 0.0, 0.0)
        val storage = GameStorageEntity(UUID.randomUUID())
        storage.team = Team.RED
        storage.goalTeam = Team.RED
        game.ingamePlayersStorage[player] = storage
        game.arena.meta.ballMeta.spawnpoint = PositionEntity("world", 34.0, 5.0, 2.0)

        Mockito.`when`(proxyService.setPlayerLocation(Mockito.any<Any>(), Mockito.any<Any>())).then { mock ->
            position = mock.getArgument(1)
            ""
        }

        // Act
        classUnderTest.respawn(game, player)

        // Assert
        Assertions.assertEquals(34.0, position.x)
        Assertions.assertEquals(5.0, position.y)
        Assertions.assertEquals(2.0, position.z)
    }

    /**
     * Given
     *      player in team with spawnpoint
     * When
     *      respawn is called
     * Then
     *     player should be teleported to team spawnpoint.
     */
    @Test
    fun respawn_PlayerInTeamWithSpawnpoint_ShouldSetLocationToTeamSpawnpoint() {
        // Arrange
        val proxyService = Mockito.mock(ProxyService::class.java)
        val classUnderTest = createWithDependencies(proxyService)
        val game = GameEntity(ArenaEntity())
        val player = "Player"
        var position: Position = PositionEntity(0.0, 0.0, 0.0)
        val storage = GameStorageEntity(UUID.randomUUID())
        storage.team = Team.BLUE
        storage.goalTeam = Team.BLUE
        game.ingamePlayersStorage[player] = storage
        game.arena.meta.blueTeamMeta.spawnpoint = PositionEntity("world", 37.0, 5.0, 2.0)

        Mockito.`when`(proxyService.setPlayerLocation(Mockito.any<Any>(), Mockito.any<Any>())).then { mock ->
            position = mock.getArgument(1)
            ""
        }

        // Act
        classUnderTest.respawn(game, player)

        // Assert
        Assertions.assertEquals(37.0, position.x)
        Assertions.assertEquals(5.0, position.y)
        Assertions.assertEquals(2.0, position.z)
    }

    companion object {
        fun createWithDependencies(
            proxyService: ProxyService = Mockito.mock(ProxyService::class.java)
        ): GameExecutionService {
            return GameExecutionServiceImpl(proxyService)
        }
    }
}