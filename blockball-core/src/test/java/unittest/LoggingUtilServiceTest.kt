package unittest

import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.core.logic.business.service.LoggingUtilServiceImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.logging.Level
import java.util.logging.Logger

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
class LoggingUtilServiceTest {
    /**
     * Given
     *      a info log message without throwable.
     * When
     *      info is called
     * Then
     *     utils info should be called.
     */
    @Test
    fun info_LogMessage_ShouldCallInfo() {
        // Arrange
        var infoCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString())).then {
            infoCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)

        // Act
        classUnderTest.info("Sample log message.")

        // Assert
        Assertions.assertTrue(infoCalled)
    }

    /**
     * Given
     *      a info log message with throwable.
     * When
     *      info is called
     * Then
     *     utils info should be called.
     */
    @Test
    fun info_LogMessageWithError_ShouldCallInfo() {
        // Arrange
        var infoCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString(), Mockito.any(Throwable::class.java))).then {
            infoCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)
        val exception = RuntimeException()

        // Act
        classUnderTest.info("Sample log message.", exception)

        // Assert
        Assertions.assertTrue(infoCalled)
    }

    /**
     * Given
     *      a warn log message without throwable.
     * When
     *      warn is called
     * Then
     *     utils warn should be called.
     */
    @Test
    fun warn_LogMessage_ShouldCallWarn() {
        // Arrange
        var warnCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString())).then {
            warnCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)

        // Act
        classUnderTest.warn("Sample log message.")

        // Assert
        Assertions.assertTrue(warnCalled)
    }

    /**
     * Given
     *      a warn log message with throwable.
     * When
     *      warn is called
     * Then
     *     utils warn should be called.
     */
    @Test
    fun warn_LogMessageWithError_ShouldCallWarn() {
        // Arrange
        var warnCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString(), Mockito.any(Throwable::class.java))).then {
            warnCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)
        val exception = RuntimeException()

        // Act
        classUnderTest.warn("Sample log message.", exception)

        // Assert
        Assertions.assertTrue(warnCalled)
    }

    /**
     * Given
     *      a error log message without throwable.
     * When
     *      error is called
     * Then
     *     utils error should be called.
     */
    @Test
    fun error_LogMessage_ShouldCallError() {
        // Arrange
        var errorCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString())).then {
            errorCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)

        // Act
        classUnderTest.error("Sample log message.")

        // Assert
        Assertions.assertTrue(errorCalled)
    }

    /**
     * Given
     *      a error log message with throwable.
     * When
     *      error is called
     * Then
     *     util error should be called.
     */
    @Test
    fun error_LogMessageWithError_ShouldCallError() {
        // Arrange
        var errorCalled = false
        val logger = Mockito.mock(Logger::class.java)
        Mockito.`when`(logger.log(Mockito.any(Level::class.java), Mockito.anyString(), Mockito.any(Throwable::class.java))).then {
            errorCalled = true
            Unit
        }

        val classUnderTest = createWithDependencies(logger)
        val exception = RuntimeException()

        // Act
        classUnderTest.error("Sample log message.", exception)

        // Assert
        Assertions.assertTrue(errorCalled)
    }

    companion object {
        fun createWithDependencies(logger: Logger): LoggingService {
            return LoggingUtilServiceImpl(logger)
        }
    }
}