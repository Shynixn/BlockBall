package unittest

import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.VirtualArenaService
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.core.logic.business.service.VirtualArenaServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

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
class VirtualArenaServiceTest {
    /**
     * Given
     *      a valid player and valid arena
     * When
     *      displayForPlayers is called
     * Then
     *     playParticle should be called, generating correct particles within the bounds of the arena.
     */
    @Test
    fun displayForPlayers_ValidPlayerArena_ShouldCallPlayParticles() {
        // Arrange
        val mockedParticleService = MockedParticleService()
        val arena = mock(Arena::class.java)
        val arenaMeta = mock(ArenaMeta::class.java)
        val teamMeta = mock(TeamMeta::class.java)
        val goal = mock(Selection::class.java)
        val player = "Player"
        `when`(teamMeta.goal).thenReturn(goal)
        `when`(arenaMeta.redTeamMeta).thenReturn(teamMeta)
        `when`(arenaMeta.blueTeamMeta).thenReturn(teamMeta)
        `when`(arena.meta).thenReturn(arenaMeta)
        `when`(goal.lowerCorner).thenReturn(PositionEntity())
        `when`(goal.upperCorner).thenReturn(PositionEntity())

        with(goal.lowerCorner) {
            x = 100.0
            y = 120.0
            z = 100.0
        }

        with(goal.upperCorner) {
            x = 110.0
            y = 130.0
            z = 110.0
        }

        val classUnderTest = createWithDependencies(mockedParticleService)

        // Act
        classUnderTest.displayForPlayer(player, arena)

        // Assert
        Assertions.assertDoesNotThrow{
            classUnderTest.displayForPlayer(player, arena)
        }
        Assertions.assertTrue(mockedParticleService.playParticleCalled)
        Assertions.assertEquals(255, mockedParticleService.usedParticle!!.colorRed)
        Assertions.assertEquals(20, mockedParticleService.usedParticle!!.amount)
        Assertions.assertTrue(mockedParticleService.locations.foldRight(true){
            position, acc ->  acc && isWithinBounds(goal.lowerCorner, goal.upperCorner, position)
        })
    }

    private fun isWithinBounds(lowerC: Position, upperC: Position, pos: Position): Boolean{
        return pos.x >= lowerC.x && pos.y >= lowerC.y &&
                pos.z >= lowerC.z && pos.x <= upperC.x &&
                pos.y <= upperC.y && pos.z <= upperC.z
    }

    companion object {
        fun createWithDependencies(particleService: ParticleService = MockedParticleService()): VirtualArenaService {
            val concurrencyService = MockedConcurrencyService()
            val proxyService = Mockito.mock(ProxyService::class.java)
            Mockito.`when`(proxyService.getWorldName<String>(Mockito.anyString()))
                .thenReturn("World")
            return VirtualArenaServiceImpl(concurrencyService, proxyService, particleService)
        }
    }

    class MockedParticleService : ParticleService {
        var playParticleCalled = false
        var usedParticle: Particle? = null
        var locations = arrayListOf<Position>()

        /**
         * Plays the given [particle] at the given [location] for the given [players].
         */
        override fun <L, P> playParticle(location: L, particle: Particle, players: Collection<P>) {
            playParticleCalled = true
            usedParticle = particle
            locations.add(location as Position)
        }
    }

    class MockedConcurrencyService: ConcurrencyService{
        /**
         * Runs the given [function] synchronised with the given [delayTicks] and [repeatingTicks].
         */
        override fun runTaskSync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
        }

        /**
         * Runs the given [function] asynchronous with the given [delayTicks] and [repeatingTicks].
         */
        override fun runTaskAsync(delayTicks: Long, repeatingTicks: Long, function: () -> Unit) {
            function()
        }
    }
}