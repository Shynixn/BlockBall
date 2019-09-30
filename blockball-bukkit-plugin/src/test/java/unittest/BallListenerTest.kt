@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Sound
import com.github.shynixn.blockball.bukkit.logic.business.listener.BallListener
import org.bukkit.Chunk
import org.bukkit.entity.Entity
import org.bukkit.entity.Slime
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.util.*
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
class BallListenerTest {
    /**
     * Given
     *      a valid ball in the current chunk
     * When
     *     onChunkSaveEvent is called
     * Then
     *     ball should be removed.
     */
    @Test
    fun onChunkSaveEvent_BallInChunk_ShouldCallRemove() {
        // Arrange
        val ball = MockedBallProxy()
        val mockedEntityService = MockedBallEntityService(ball)
        val classUnderTest = createWithDependencies(mockedEntityService)
        val chunk = mock(Chunk::class.java)
        val entities = arrayOf(ball.getDesignArmorstand<Entity>())
        Mockito.`when`(chunk.entities).thenReturn(entities)
        val chunkUnloadEvent = ChunkUnloadEvent(chunk)

        // Act
        classUnderTest.onChunkSaveEvent(chunkUnloadEvent)

        // Assert
        Assertions.assertTrue(mockedEntityService.cleanUpCalled)
    }

    /**
     * Given
     *      a valid chunk load
     * When
     *      onChunkLoadEvent is called
     * Then
     *     cleanUpInvalidEntities should be called.
     */
    @Test
    fun onChunkLoadEvent_ValidChunkLoad_ShouldCallClean() {
        // Arrange
        val ball = MockedBallProxy()
        val mockedEntityService = MockedBallEntityService(ball)
        val classUnderTest = createWithDependencies(mockedEntityService)
        val chunk = mock(Chunk::class.java)
        val entities = arrayOf(ball.getDesignArmorstand<Entity>())
        Mockito.`when`(chunk.entities).thenReturn(entities)
        val chunkLoadEvent = ChunkLoadEvent(chunk, false)

        // Act
        classUnderTest.onChunkLoadEvent(chunkLoadEvent)

        // Assert
        Assertions.assertTrue(mockedEntityService.cleanUpCalled)
    }

    companion object {
        fun createWithDependencies(ballEntityService: BallEntityService, soundService: SoundService = MockedSoundService(), particleService: ParticleService = MockedParticleService()): BallListener {
            return BallListener(ballEntityService, particleService, soundService)
        }
    }

    class MockedBallProxy : BallProxy {
        /**
         * Runnable. Should not be called directly.
         */
        override fun run() {
        }

        private val entity: Slime = Mockito.mock(Slime::class.java)

        /**
         * Gets the meta data.
         */
        override val meta: BallMeta = Mockito.mock(BallMeta::class.java)
        /**
         * Runnable Value yaw change which reprents internal yaw change calculation.
         * Returns below 0 if yaw did not change.
         */
        override var yawChange: Float = 1.0F

        /**
         * Is the ball currently grabbed by some entity?
         */
        override val isGrabbed: Boolean = false
        /**
         * Is the entity dead?
         */
        override var isDead: Boolean = false

        /**
         * Unique id.
         */
        override val uuid: UUID = UUID.randomUUID()

        /**
         * Is the entity persistent and can be stored.
         */
        override var persistent: Boolean = false

        /**
         * Current spinning force value.
         */
        override var angularVelocity: Double = 0.0

        /**
         * Remaining time in ticks until players regain the ability to kick this ball.
         */
        override var skipKickCounter: Int = 0

        /**
         * Returns the armorstand for the design.
         */
        override fun <A> getDesignArmorstand(): A {
            return entity as A
        }

        /**
         * Returns the hitbox entity.
         */
        override fun <A> getHitbox(): A {
            return entity as A
        }

        /**
         * Gets the optional living entity owner of the ball.
         */
        override fun <L> getOwner(): Optional<L> {
            throw IllegalArgumentException()
        }

        /**
         * Gets the last interaction entity.
         */
        override fun <L> getLastInteractionEntity(): Optional<L> {
            throw IllegalArgumentException()
        }

        /**
         * Teleports the ball to the given [location].
         */
        override fun <L> teleport(location: L) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the location of the ball.
         */
        override fun <L> getLocation(): L {
            throw IllegalArgumentException()
        }

        /**
         * Sets the velocity of the ball.
         */
        override fun <V> setVelocity(vector: V) {
            throw IllegalArgumentException()
        }

        /**
         * Gets the velocity of the ball.
         */
        override fun <V> getVelocity(): V {
            throw IllegalArgumentException()
        }

        /**
         * Shoot the ball by the given player.
         * The calculated velocity can be manipulated by the BallKickEvent.
         *
         * @param player
         */
        override fun <E> shootByPlayer(player: E) {
            throw IllegalArgumentException()
        }

        /**
         * Pass the ball by the given player.
         * The calculated velocity can be manipulated by the BallKickEvent
         *
         * @param player
         */
        override fun <E> passByPlayer(player: E) {
            throw IllegalArgumentException()
        }

        /**
         * Throws the ball by the given player.
         * The calculated velocity can be manipulated by the BallThrowEvent.
         *
         * @param player
         */
        override fun <E> throwByPlayer(player: E) {
            throw IllegalArgumentException()
        }

        /**
         * Lets the given living entity grab the ball.
         */
        override fun <L> grab(entity: L) {
            throw IllegalArgumentException()
        }

        /**
         * Calculates the angular velocity in order to spin the ball.
         *
         * @return The angular velocity
         */
        override fun <V> calculateSpinVelocity(postVector: V, initVector: V): Double {
            throw IllegalArgumentException()
        }

        /**
         * DeGrabs the ball.
         */
        override fun deGrab() {
            throw IllegalArgumentException()
        }

        /**
         * Removes the ball.
         */
        override fun remove() {
            isDead = true
        }

        /**
         * Calculates post movement.
         */
        override fun calculatePostMovement(collision: Boolean) {
            throw IllegalArgumentException()
        }

        /**
         * Calculates the movement vectors.
         */
        override fun <V> calculateMoveSourceVectors(movementVector: V, motionVector: V, onGround: Boolean): Optional<V> {
            throw IllegalArgumentException()
        }

        /**
         * Calculates spin movement. The spinning will slow down
         * if the ball stops moving, hits the ground or hits the wall.
         *
         * @param collision if knockback were applied
         */
        override fun calculateSpinMovement(collision: Boolean) {
            throw IllegalArgumentException()
        }

        /**
         * Calculates the knockback for the given [sourceVector] and [sourceBlock]. Uses the motion values to correctly adjust the
         * wall.
         *
         * @return if collision was detected and the knockback was applied
         */
        override fun <V, B> calculateKnockBack(sourceVector: V, sourceBlock: B, mot0: Double, mot2: Double, mot6: Double, mot8: Double): Boolean {
            throw IllegalArgumentException()
        }

    }

    class MockedBallEntityService(private val ball: BallProxy = MockedBallProxy()) : BallEntityService {

        var cleanUpCalled = false

        /**
         * Registers entities on the server when not already registered.
         * Returns true if registered. Returns false when not registered.
         */
        override fun registerEntitiesOnServer(): Boolean {
            return true
        }

        /**
         * Spawns a temporary ball.
         */
        override fun <L> spawnTemporaryBall(location: L, meta: BallMeta): BallProxy {
            throw IllegalArgumentException()
        }

        /**
         * Finds Ball from the given entity.
         */
        override fun <E> findBallFromEntity(entity: E): Optional<BallProxy> {
            if (ball.getHitbox<E>() == entity || ball.getDesignArmorstand<E>() == entity) {
                return Optional.of(ball)
            }

            return Optional.empty()
        }

        /**
         * Checks the entity collection for invalid ball entities and removes them.
         */
        override fun <E> cleanUpInvalidEntities(entities: Collection<E>) {
            cleanUpCalled = true
        }

        /**
         * Returns all balls managed by the plugin.
         */
        override fun getAllBalls(): List<BallProxy> {
            return arrayListOf(ball)
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