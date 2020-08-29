package unittest

import com.github.shynixn.blockball.api.business.service.PersistenceStatsService
import com.github.shynixn.blockball.api.persistence.entity.Stats
import com.github.shynixn.blockball.bukkit.logic.business.listener.StatsListener
import com.github.shynixn.blockball.core.logic.persistence.entity.PlayerMetaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.StatsEntity
import helper.MockedConcurrencyService
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.test.assertTrue

class StatsListenerTest {
    /**
     * Given
     *      PlayerJoinEvent with valid player
     * When
     *      onPlayerJoinEvent is called
     * Then
     *     Refresh stats should be called.
     */
    @Test
    fun onPlayerJoinEvent_ValidNewPlayer_CallRefreshStats() {
        val persistenceStatsService = MockedStatsService()
        val classUnderTest = createWithDependencies(persistenceStatsService)
        val player = Mockito.mock(Player::class.java)
        val uuid = UUID.randomUUID()
        Mockito.`when`(player.uniqueId).thenReturn(uuid)
        Mockito.`when`(player.world).thenReturn(Mockito.mock(World::class.java))
        Mockito.`when`(player.isOnline).thenReturn(true)

        classUnderTest.onPlayerJoinEvent(PlayerJoinEvent(player, "Test"))

        assertTrue(persistenceStatsService.calledRefresh)
    }

    private fun createWithDependencies(statsService: PersistenceStatsService = MockedStatsService()): StatsListener {
        val concurrencyService = MockedConcurrencyService()
        return StatsListener(statsService, concurrencyService)
    }

    private class MockedStatsService : PersistenceStatsService {
        var calledRefresh = false
        var calledSave = false
        var clearResources = false

        override fun <P> getStatsFromPlayer(player: P): Stats {
            require(player is Player)
            val playerMeta = PlayerMetaEntity(UUID.randomUUID().toString(), "test-user")
            val statsEntity = StatsEntity(playerMeta)
            statsEntity.amountOfGoals = 5
            statsEntity.amountOfPlayedGames = 2
            statsEntity.amountOfWins = 10
            return statsEntity
        }

        override fun <P> refreshStatsFromPlayer(player: P): CompletableFuture<Stats> {
            calledRefresh = true
            return CompletableFuture()
        }

        override fun save(stats: Stats): CompletableFuture<Stats> {
            calledSave = true
            return CompletableFuture()
        }

        override fun <P> clearResources(player: P): CompletableFuture<Void?> {
            clearResources = true
            return CompletableFuture()
        }


        override fun close() {
        }
    }
}