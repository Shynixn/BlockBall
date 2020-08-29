package unittest

import com.github.shynixn.blockball.api.business.enumeration.ScoreboardDisplaySlot
import com.github.shynixn.blockball.api.business.service.ScoreboardService
import com.github.shynixn.blockball.bukkit.logic.business.service.ScoreboardServiceImpl
import org.bukkit.scoreboard.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
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
class ScoreboardServiceTest {

    /**
     * Given
     *      a valid configuration as parameter
     * When
     *      setConfiguration is called
     * Then
     *     a new objective should be registered.
     */
    @Test
    fun setConfiguration_ValidScoreboardDisplaySlotTitle_ShouldRegisterObjective() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val scoreboard = mock(Scoreboard::class.java)
        val displaySlot = ScoreboardDisplaySlot.SIDEBAR
        val title = "Custom"
        var called = false

        // Act
        `when`(scoreboard.registerNewObjective(any(), any())).then {
            called = true
            mock(Objective::class.java)
        }

        classUnderTest.setConfiguration(scoreboard, displaySlot, title)

        // Assert
        Assertions.assertTrue(called)
    }

    /**
     * Given
     *      a valid configuration as parameter and no valid scoreboard
     * When
     *      setConfiguration is called
     * Then
     *     Exception should be thrown.
     */
    @Test
    fun setConfiguration_ValidScoreboardNoDisplaySlotTitle_ShouldRegisterObjective() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val displaySlot = ScoreboardDisplaySlot.SIDEBAR
        val title = "Custom"

        // Act
        assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.setConfiguration("wrong parameter", displaySlot, title)
        }
    }

    /**
     * Given
     *      a valid line as parameter
     * When
     *      setLine is called
     * Then
     *     a new line should be added regardless of the state.
     */
    @Test
    fun setLine_ValidLineText_ShouldAddLine() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val scoreboard = mock(Scoreboard::class.java)
        var called = false

        // Act
        `when`(scoreboard.registerNewTeam(any())).then {
            val team = mock(Team::class.java)
            `when`(team.addEntry(any())).then {
                called = true
                ""
            }

            team
        }
        `when`(scoreboard.getObjective(any(String::class.java))).then {
            val objective = mock(Objective::class.java)
            `when`(objective.getScore(any(String::class.java))).then {
                mock(Score::class.java)
            }

            objective
        }

        classUnderTest.setLine(scoreboard, 0, "SampleText")

        // Assert
        Assertions.assertTrue(called)
    }


    /**
     * Given
     *      a valid line as parameter but invalid scoreboard
     * When
     *      setLine is called
     * Then
     *    Exception should be thrown.
     */
    @Test
    fun setLine_ValidLineTextInvalidScoreboard_ShouldAddLine() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        assertThrows(IllegalArgumentException::class.java) {
            classUnderTest.setLine("wrong parameter", 0, "SampleText")
        }
    }

    companion object {
        fun createWithDependencies(): ScoreboardService {
            return ScoreboardServiceImpl()
        }
    }
}