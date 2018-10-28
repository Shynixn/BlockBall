package unittest

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.HologramProxy
import com.github.shynixn.blockball.api.business.service.HologramService
import com.github.shynixn.blockball.bukkit.logic.business.service.HologramServiceImpl
import com.github.shynixn.blockball.core.logic.persistence.entity.HologramMetaEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitScheduler
import org.bukkit.scheduler.BukkitTask
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
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
class HologramServiceTest {
    /**
     * Given
     *   a valid plugin and meta configuration
     * When
     *    createNewHologram is called
     * Then
     *   a hologram proxy should be returned.
     */
    @Test
    fun createNewHologram_ValidPluginAndMeta_ShouldReturnHologramProxy() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val meta = HologramMetaEntity()
        meta.position = PositionEntity()
        with(meta.position!!) {
            x = 2.0
            y = 3.0
            z = 3.0
        }
        meta.lines.add("Line")

        // Act
        val hologram = classUnderTest.createNewHologram(meta)

        // Assert
        Assertions.assertNotNull(hologram)
    }

    /**
     * Given
     *   a valid proxy and meta
     * When
     *   changeConfiguration is called
     * Then
     *   hologram proxy should be called.
     */
    @Test
    fun changeConfiguration_ValidPluginProxyAndMeta_ShouldCallProxy() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val meta = HologramMetaEntity()
        meta.position = PositionEntity()
        with(meta.position!!) {
            x = 2.0
            y = 3.0
            z = 3.0
        }
        meta.lines.add("LineUp")
        val mockedPluginHologramProxy = MockedPluginHologramProxy()

        // Act
        classUnderTest.changeConfiguration(mockedPluginHologramProxy, meta)

        // Assert
        Assertions.assertTrue(mockedPluginHologramProxy.setLinesCalled)
    }

    /**
     * Given
     *   a valid proxy and player
     * When
     *   addPlayer is called
     * Then
     *   hologram proxy should be called.
     */
    @Test
    fun addPlayer_ValidPluginProxyAndPlayer_ShouldCallProxy() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val player = mock(Player::class.java)
        val mockedPluginHologramProxy = MockedPluginHologramProxy()

        // Act
        classUnderTest.addPlayer(mockedPluginHologramProxy, player)

        // Assert
        Assertions.assertTrue(mockedPluginHologramProxy.addWatcherCalled)
    }

    /**
     * Given
     *   a valid proxy and player
     * When
     *   removePlayer is called
     * Then
     *   hologram proxy should be called.
     */
    @Test
    fun removePlayer_ValidPluginProxyAndPlayer_ShouldCallProxy() {
        // Arrange
        val classUnderTest = createWithDependencies()
        val player = mock(Player::class.java)
        val mockedPluginHologramProxy = MockedPluginHologramProxy()

        // Act
        classUnderTest.removePlayer(mockedPluginHologramProxy, player)

        // Assert
        Assertions.assertTrue(mockedPluginHologramProxy.removeWatcherCalled)
    }

    companion object {
        fun createWithDependencies(): HologramService {
            val plugin = mock(Plugin::class.java)
            val world = mock(World::class.java)
            val server = mock(Server::class.java)
            val scheduler = mock(BukkitScheduler::class.java)
            val bukkitTask = mock(BukkitTask::class.java)

            `when`(server.logger).thenReturn(Logger.getGlobal())
            if (Bukkit.getServer() == null) {
                Bukkit.setServer(server)
            }
            `when`(plugin.server).thenReturn(server)
            `when`(scheduler.runTaskTimerAsynchronously(ArgumentMatchers.any(Plugin::class.java), ArgumentMatchers.any(Runnable::class.java), ArgumentMatchers.any(Long::class.java), ArgumentMatchers.any(Long::class.java)))
                    .thenReturn(bukkitTask)
            `when`(plugin.server.scheduler).thenReturn(scheduler)
            `when`(Bukkit.getWorld(ArgumentMatchers.anyString())).thenReturn(world)

            return HologramServiceImpl(plugin, Version.VERSION_1_12_R1)
        }
    }

    class MockedPluginHologramProxy : HologramProxy {
        var setLinesCalled = false
        var addWatcherCalled = false
        var removeWatcherCalled = false

        /**
         * Adds a line to the hologram.
         */
        override fun addLine(line: String) {
        }

        /**
         * Adds a watcher to this hologram.
         * Does nothing if already added.
         */
        override fun <P> addWatcher(player: P) {
            addWatcherCalled = true
        }

        /**
         * Removes the hologram. If it is not already removed.
         */
        override fun remove() {
        }

        /**
         * Removes a watcher from this hologram.
         * Does nothing if the [player] is not aded.
         */
        override fun <P> removeWatcher(player: P) {
            removeWatcherCalled = true
        }

        /**
         * Changes the lines of the hologram.
         */
        override fun setLines(lines: Collection<String>) {
            setLinesCalled = true
        }
    }
}