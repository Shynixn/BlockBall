package unittest

import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.core.logic.business.serializer.ParticleTypeSerializer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

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
class ParticleTypeSerializerTest {
    /**
     * Given
     *      an existing particle type
     * When
     *     onSerialization is called
     * Then
     *     name should be returned.
     */
    @Test
    fun onSerialization_ExistingParticelType_ShouldReturnName() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        val name = classUnderTest.onSerialization(ParticleType.CLOUD)

        // Assert
        Assertions.assertEquals("CLOUD", name)
    }

    /**
     * Given
     *      an existing particle name
     * When
     *     onDeserialization is called
     * Then
     *     particle type should be deSerialized.
     */
    @Test
    fun onDeserialization_ExistingParticelType_ShouldCorrectlyDeserialize() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        val particleType = classUnderTest.onDeserialization("explode")

        // Assert
        Assertions.assertEquals(ParticleType.EXPLOSION_NORMAL, particleType)
    }

    /**
     * Given
     *      an non existing particle name
     * When
     *     onDeserialization is called
     * Then
     *     exception should be thrown.
     */
    @Test
    fun onDeserialization_NonExistingParticelType_ShouldThrowException() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        assertThrows<RuntimeException> {
            classUnderTest.onDeserialization("someparticle")
        }
    }

    companion object {
        fun createWithDependencies(): ParticleTypeSerializer {
            return ParticleTypeSerializer()
        }
    }
}