package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.bukkit.event.PacketEvent
import com.github.shynixn.blockball.api.business.proxy.PluginProxy
import com.github.shynixn.blockball.api.business.service.ProtocolService
import com.google.inject.Inject
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import org.apache.commons.io.FileUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.charset.Charset
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ProtocolServiceImpl @Inject constructor(private val plugin: PluginProxy, private val internalPlugin: Plugin) :
    ProtocolService {
    private val handlerName = "BlockBall" + "-" + UUID.randomUUID().toString()
    private val playerToNmsPlayer by lazy {
        plugin.findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer")
            .getDeclaredMethod("getHandle")
    }
    private val playerConnectionField by lazy {
        plugin.findClazz("net.minecraft.server.VERSION.EntityPlayer")
            .getDeclaredField("playerConnection")
    }
    private val cachedPlayerChannels = HashMap<Player, Channel>()
    private val registeredPackets = HashSet<Class<*>>()

    companion object {
        val messageFlush = ArrayList<String>()
        var timeOut = 0L
        var pluginRef: Plugin? = null
    }

    init {
        pluginRef = internalPlugin
    }

    /**
     * Registers the following packets classes for events.
     */
    override fun registerPackets(packets: List<Class<*>>) {
        registeredPackets.addAll(packets)
    }

    /**
     * Registers the player.
     */
    override fun <P> register(player: P) {
        require(player is Player)

        if (cachedPlayerChannels.containsKey(player)) {
            return
        }

        val nmsPlayer = playerToNmsPlayer
            .invoke(player)
        val connection = playerConnectionField
            .get(nmsPlayer)
        val netWorkManager = plugin.findClazz("net.minecraft.server.VERSION.PlayerConnection")
            .getDeclaredField("networkManager")
            .get(connection)
        val channel = plugin.findClazz("net.minecraft.server.VERSION.NetworkManager")
            .getDeclaredField("channel")
            .get(netWorkManager) as Channel

        val internalInterceptor = PacketInterceptor(player, this)
        channel.pipeline().addBefore("packet_handler", handlerName, internalInterceptor)
        cachedPlayerChannels[player] = channel
    }

    /**
     * Clears the player cache.
     */
    override fun <P> unRegister(player: P) {
        require(player is Player)

        if (!cachedPlayerChannels.containsKey(player)) {
            return
        }

        val channel = cachedPlayerChannels[player]
        channel!!.eventLoop().execute {
            try {
                channel.pipeline().remove(handlerName)
            } catch (e: Exception) {
                // Ignored
            }
        }
        cachedPlayerChannels.remove(player)
    }

    /**
     * Disposes the protocol service.
     */
    override fun dispose() {
        for (player in cachedPlayerChannels.keys.toTypedArray()) {
            unRegister(player)
        }

        registeredPackets.clear()
    }

    /**
     * On Message receive.
     */
    private fun onMessageReceive(player: Player, packet: Any): Boolean {
        if (cachedPlayerChannels.isEmpty()) {
            return false
        }

        if (!this.registeredPackets.contains(packet.javaClass)) {
            return false
        }

        internalPlugin.server.scheduler.runTask(internalPlugin, Runnable {
            val packetEvent = PacketEvent(player, packet)
            Bukkit.getPluginManager().callEvent(packetEvent)
        })

        return false
    }

    private class PacketInterceptor(
        private val player: Player,
        private val protocolServiceImpl: ProtocolServiceImpl
    ) :
        ChannelDuplexHandler() {
        /**
         * Incoming packet.
         */
        override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
            val cancelled = try {
                protocolServiceImpl.onMessageReceive(player, msg)
            } catch (e: Exception) {
                Bukkit.getServer().logger.log(Level.SEVERE, "Failed to read packet.", e)
                false
            }

            if (!cancelled) {
                super.channelRead(ctx, msg)
            }
        }

        /**
         * Outgoing packet.
         */
        override fun write(ctx: ChannelHandlerContext?, msg: Any, promise: ChannelPromise?) {
            val stringerWriter = StringWriter()
            stringerWriter.write(SimpleDateFormat("dd HH-mm-ss").format(Date()) + " ")
            stringerWriter.write(player.name + " ")
            stringerWriter.write(msg.javaClass.name + "" + System.lineSeparator())
            messageFlush.add(stringerWriter.toString());

            if (System.currentTimeMillis() - timeOut > 2000) {
                for (data in messageFlush) {
                    FileUtils.write(
                        pluginRef!!.dataFolder.resolve("packet.log"),
                        data,
                        Charset.forName("UTF-8"),
                        true
                    )
                }

                messageFlush.clear()
                timeOut = System.currentTimeMillis()
            }

            super.write(ctx, msg, promise)
        }
    }
}
