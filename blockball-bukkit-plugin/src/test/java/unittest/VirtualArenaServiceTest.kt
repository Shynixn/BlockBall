package unittest

import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.VirtualArenaService
import com.github.shynixn.blockball.api.persistence.entity.*
import com.github.shynixn.blockball.bukkit.logic.business.service.VirtualArenaServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
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
     *     playParticle should be called with correct particle and location.
     */
    @Test
    fun displayForPlayers_ValidPlayerArena_ShouldCallPlayParticles() {
        // Arrange
        val mockedParticleService = MockedParticleService()
        val player = mock(Player::class.java)
        val world = mock(World::class.java)
        val arena = mock(Arena::class.java)
        val arenaMeta = mock(ArenaMeta::class.java)
        val teamMeta = mock(TeamMeta::class.java)
        val goal = mock(Selection::class.java)

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
            x = 150.0
            y = 140.0
            z = 150.0
        }

        Mockito.`when`(player.world).thenReturn(world)
        val classUnderTest = createWithDependencies(mockedParticleService)

        // Act
        classUnderTest.displayForPlayer(player, arena)

        // Assert
        Assertions.assertTrue(mockedParticleService.playParticleCalled)
        Assertions.assertEquals(255, mockedParticleService.usedParticle!!.colorRed)
        Assertions.assertEquals(20, mockedParticleService.usedParticle!!.amount)
    }

    /**
     * Given
     *      a invalid player
     * When
     *      displayForPlayers is called
     * Then
     *     a exception should be thrown.
     */
    @Test
    fun displayForPlayers_InvalidPlayer_ShouldThrowException() {
        // Arrange
        val player = "This is a invalid player."
        val arena = mock(Arena::class.java)
        val classUnderTest = createWithDependencies()

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.displayForPlayer(player, arena)
        }
    }

    companion object {
        fun createWithDependencies(particleService: ParticleService = MockedParticleService()): VirtualArenaService {
            val plugin = Mockito.mock(Plugin::class.java)
            val server = Mockito.mock(Server::class.java)
            val scheduler = Mockito.mock(BukkitScheduler::class.java)
            Mockito.`when`(scheduler.runTaskAsynchronously(Mockito.any(Plugin::class.java), Mockito.any(Runnable::class.java))).then { p ->
                (p.arguments[1] as Runnable).run()
                mock(BukkitTask::class.java)
            }

            Mockito.`when`(server.scheduler).thenReturn(scheduler)
            Mockito.`when`(plugin.server).thenReturn(server)


            return VirtualArenaServiceImpl(plugin, particleService)
        }
    }

    class MockedParticleService : ParticleService {
        var playParticleCalled = false
        var usedParticle: Particle? = null

        /**
         * Plays the given [particle] at the given [location] for the given [players].
         */
        override fun <L, P> playParticle(location: L, particle: Particle, players: Collection<P>) {
            playParticleCalled = true
            usedParticle = particle
        }
    }
}