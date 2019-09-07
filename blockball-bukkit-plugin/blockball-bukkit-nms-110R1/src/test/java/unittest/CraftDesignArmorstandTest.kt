package unittest

import com.github.shynixn.blockball.bukkit.logic.business.nms.v1_10_R1.CraftDesignArmorstand
import net.minecraft.server.v1_10_R1.EntityArmorStand
import org.bukkit.craftbukkit.v1_10_R1.CraftServer
import org.bukkit.entity.EntityType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class CraftDesignArmorstandTest {
    /**
     * Given
     *    a ball
     * When
     *    deleteFromWorld is called.
     * Then
     *    ball should die.
     */
    @Test
    fun deleteFromWorld_CraftBall_ShouldExecuteDie() {
        // Arrange
        val entityInsentient = Mockito.mock(EntityArmorStand::class.java)
        val classUnderTest = createWithDependencies(entityInsentient)

        var called = false

        Mockito.`when`(entityInsentient.die()).then {
            called = true
            Unit
        }

        // Act
        classUnderTest.deleteFromWorld()

        // Assert
        Assertions.assertTrue(called)
    }

    /**
     * Given
     *    a ball
     * When
     *    remove is called.
     * Then
     *    ball should not die.
     */
    @Test
    fun remove_CraftBall_ShouldNotExecuteDie() {
        // Arrange
        val entityInsentient = Mockito.mock(EntityArmorStand::class.java)
        val classUnderTest = createWithDependencies(entityInsentient)

        var called = false

        Mockito.`when`(entityInsentient.die()).then {
            called = true
            Unit
        }

        // Act
        classUnderTest.remove()

        // Assert
        Assertions.assertFalse(called)
    }

    /**
     * Given
     *    a ball
     * When
     *    getType is called.
     * Then
     *    armorstand type should always be returned.
     */
    @Test
    fun getType_CraftBall_ShouldReturnRabbitType() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        val entityType = classUnderTest.type

        // Assert
        Assertions.assertEquals(EntityType.ARMOR_STAND, entityType)
    }

    /**
     * Given
     *    a ball
     * When
     *    toString is called.
     * Then
     *    ball identifier should be returned.
     */
    @Test
    fun toString_CraftBall_ShouldReturnIdentifier() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        val identifier = classUnderTest.toString()

        // Assert
        Assertions.assertEquals("BlockBall{ArmorstandEntity}", identifier)
    }

    companion object {
        fun createWithDependencies(entityInsentient: EntityArmorStand = Mockito.mock(EntityArmorStand::class.java)): CraftDesignArmorstand {
            val server = Mockito.mock(CraftServer::class.java)

            return CraftDesignArmorstand(server, entityInsentient)
        }
    }
}