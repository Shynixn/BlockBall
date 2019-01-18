package unittest

import com.github.shynixn.blockball.api.business.service.DoubleJumpService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.ArenaMeta
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.blockball.bukkit.logic.business.service.DoubleJumpServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.DoubleJumpMetaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.GameEntity
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
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
        val arena = mock(Arena::class.java)
        val meta = mock(ArenaMeta::class.java)
        `when`(meta.doubleJumpMeta).thenReturn(DoubleJumpMetaEntity())
        val game = GameEntity(arena)
        `when`(arena.meta).thenReturn(meta)
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

        val classUnderTest = createWithDependencies(mockedSoundServie, mockedParticleService)

        // Act
        val success = classUnderTest.handleDoubleClick(game, player)

        // Assert
        Assertions.assertTrue(success)
        Assertions.assertEquals(1.0, player.velocity.x)
        Assertions.assertEquals(1.0, player.velocity.y)
        Assertions.assertTrue(mockedSoundServie.playSoundCalled)
        Assertions.assertTrue(mockedParticleService.playParticleCalled)
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
        val arena = mock(Arena::class.java)
        val meta = mock(ArenaMeta::class.java)
        `when`(meta.doubleJumpMeta).thenReturn(DoubleJumpMetaEntity())
        val game = GameEntity(arena)
        `when`(arena.meta).thenReturn(meta)

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.handleDoubleClick(game, "I'm a player")
        }
    }

    companion object {
        fun createWithDependencies(soundService: SoundService = MockedSoundService(), particleService: ParticleService = MockedParticleService()): DoubleJumpService {
            return DoubleJumpServiceImpl(soundService, particleService)
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