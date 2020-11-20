@file:Suppress("UNCHECKED_CAST")

package unittest

import com.github.shynixn.blockball.api.business.service.ConfigurationService
import com.github.shynixn.blockball.api.business.service.LoggingService
import com.github.shynixn.blockball.api.business.service.YamlSerializationService
import com.github.shynixn.blockball.api.business.service.YamlService
import com.github.shynixn.blockball.core.logic.business.service.LoggingUtilServiceImpl
import com.github.shynixn.blockball.core.logic.business.service.YamlSerializationServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.ArenaEntity
import com.github.shynixn.blockball.core.logic.persistence.repository.ArenaFileRepository
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.*
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class ArenaFileRepositoryTest {
    /**
     * Given
     *      multiple arena files
     * When
     *      getall is called
     * Then
     *     the arenas should be returned in correct order.
     */
    @Test
    fun getAll_MultipleArenas_ShouldBeCorrectlyLoadable() {
        // Arrange
        val classUnderTest = createWithDependencies()

        // Act
        val arena1 = ArenaEntity()
        arena1.name = "10"
        classUnderTest.save(arena1)
        val arena2 = ArenaEntity()
        arena2.name = "1"
        classUnderTest.save(arena2)
        val arena3 = ArenaEntity()
        arena3.name = "2"
        classUnderTest.save(arena3)
        val arenas = classUnderTest.getAll()

        // Assert
        Assertions.assertEquals(3, arenas.size)
        Assertions.assertEquals("1", arenas[0].name)
        Assertions.assertEquals("2", arenas[1].name)
        Assertions.assertEquals("10", arenas[2].name)
    }

    /**
     * Given
     *      a new arena
     * When
     *      save is called
     * Then
     *     an file with the correct amount of bytes should be created.
     */
    @Test
    fun save_NewArenaEntity_ShouldBeCorrectlySaved() {
        // Arrange
        val arena = ArenaEntity()
        arena.name = "1"
        val classUnderTest = createWithDependencies()

        // Act
        classUnderTest.save(arena)
        val actualDataLength = FileUtils.readFileToString(File("build/repository-test/arena/arena_1.yml"), "UTF-8")

        // Assert
        Assertions.assertEquals(9515, actualDataLength.length)
    }

    /**
     * Given
     *      an existing arena file
     * When
     *      delete is called
     * Then
     *     the file should be deleted.
     */
    @Test
    fun delete_ExistingArenaFile_ShouldBeDeleted() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val arena = ArenaEntity()
        arena.name = "1"
        classUnderTest.save(arena)

        // Act
        val fileExisted = File("build/repository-test/arena/arena_1.yml").exists()
        classUnderTest.delete(arena)
        val fileExistsNow = File("build/repository-test/arena/arena_1.yml").exists()

        // Assert
        Assertions.assertTrue(fileExisted)
        Assertions.assertFalse(fileExistsNow)
    }

    companion object {
        fun createWithDependencies(
            configurationService: ConfigurationService = MockedConfigurationService(),
            yamlSerializationService: YamlSerializationService = YamlSerializationServiceImpl(),
            loggingService: LoggingService = LoggingUtilServiceImpl(Logger.getAnonymousLogger())
        ): ArenaFileRepository {
            return ArenaFileRepository(configurationService, yamlSerializationService, MockedYamlService(), loggingService)
        }
    }

    class MockedYamlService : YamlService {
        /**
         * Writes the given [content] to the given [path].
         */
        override fun write(path: Path, content: Map<String, Any?>) {
            val options = DumperOptions()
            options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            options.isPrettyFlow = true

            val yaml = Yaml(options)

            OutputStreamWriter(FileOutputStream(path.toFile()), StandardCharsets.UTF_8).use { fw ->
                yaml.dump(content, fw)
            }
        }

        /**
         * Reads the content from the given [path].
         */
        override fun read(path: Path): Map<String, Any?> {
            return InputStreamReader(FileInputStream(path.toFile()), StandardCharsets.UTF_8).use { fr ->
                val yaml = Yaml()
                yaml.load(fr) as Map<String, Any?>
            }
        }
    }

    class MockedConfigurationService : ConfigurationService {
        /**
         * Opens an inputStream to the given resource name.
         */
        override fun openResource(name: String): InputStream {
            throw IllegalArgumentException()
        }

        /**
         * Checks if the given [path] contains a value.
         */
        override fun containsValue(path: String): Boolean {
            return true
        }

        /**
         * Reloads the config.
         */
        override fun reload() {
        }

        private var path: Path = Paths.get("build/repository-test")

        init {
            if (Files.exists(path)) {
                FileUtils.deleteDirectory(path.toFile())
            }

            Files.createDirectories(path)
        }


        /**
         * Gets the path to the folder where the application is allowed to store
         * save data.
         */
        override val applicationDir: Path
            get() {
                return path
            }

        /**
         * Tries to load the config value from the given [path].
         * Throws a [IllegalArgumentException] if the path could not be correctly
         * loaded.
         */
        override fun <C> findValue(path: String): C {
            throw IllegalArgumentException()
        }
    }
}
