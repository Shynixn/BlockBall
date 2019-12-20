package unittest

import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.core.logic.business.service.YamlSerializationServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.ParticleEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

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
class YamlSerializationServiceTest {

    /**
     * Given
     *      a arena
     * When
     *      serialize is called
     * Then
     *     the arena should be serialized.
     */
    @Test
    fun serialize_Arena_ShouldSerializeCorrectly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val arena = ArenaEntity()
        arena.name = "CustomName1"
        arena.displayName = "Super awesome arena"
        arena.enabled = false
        arena.gameType = GameType.MINIGAME

        // Act
        val data = classUnderTest.serialize(arena)

        // Assert
        Assertions.assertEquals(arena.gameType, GameType.valueOf(data["gamemode"] as String))
    }

    /**
     * Given
     *      a arena
     * When
     *      serialize and deserialize is called
     * Then
     *     the arena should be serialized and correctly deserialized.
     */
    @Test
    fun deserialize_Arena_ShouldSerializeCorrectly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val arena = ArenaEntity()
        arena.name = "CustomName2"
        arena.displayName = "Super awesome arena"
        arena.enabled = false
        arena.gameType = GameType.MINIGAME

        // Act
        val data = classUnderTest.serialize(arena)
        val arena2 = classUnderTest.deserialize(ArenaEntity::class.java, data)

        // Assert
        Assertions.assertEquals(arena.gameType, arena2.gameType)
    }

    /**
     * Given
     *      a particle
     * When
     *      serialize is called
     * Then
     *     the particle should be serialized.
     */
    @Test
    fun serialize_Particle_ShouldSerializeCorrectly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val particle = ParticleEntity(ParticleType.BLOCK_DUST.name)

        // Act
        val data = classUnderTest.serialize(particle)

        // Assert
        Assertions.assertEquals("BLOCK_DUST", data["name"])
    }

    /**
     * Given
     *      a particle
     * When
     *      deserialize is called
     * Then
     *     the particle should be deSerialized.
     */
    @Test
    fun deSerialize_Particle_ShouldDeSerializeCorrectly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val dataSource = HashMap<String, Any>()
        dataSource["name"] = "BLOCK_DUST"

        // Act
        val data = classUnderTest.deserialize(ParticleEntity::class.java,dataSource)

        // Assert
        Assertions.assertEquals(ParticleType.BLOCK_DUST.name, data.typeName)
    }

    /**
     * Given
     *      a minecraft particle
     * When
     *      deserialize is called
     * Then
     *     the particle should be deSerialized.
     */
    @Test
    fun deSerialize_MinecraftParticle_ShouldDeSerializeCorrectly() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val dataSource = HashMap<String, Any>()
        dataSource["name"] = "EXPLOSION_NORMAL"

        // Act
        val data = classUnderTest.deserialize(ParticleEntity::class.java,dataSource)

        // Assert
        Assertions.assertEquals(ParticleType.EXPLOSION_NORMAL.name, data.typeName)
    }

    companion object {
        fun createWithDependencies(): YamlSerializationService {
            return YamlSerializationServiceImpl()
        }
    }
}