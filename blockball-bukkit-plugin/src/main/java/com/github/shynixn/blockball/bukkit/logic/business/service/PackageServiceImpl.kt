package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.PackageService
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz

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
class PackageServiceImpl : PackageService {
    /**
     * Sends the given [packet] to the given [player].
     */
    override fun <P> sendPacket(player: P, packet: Any) {
        val craftPlayerClazz = findClazz("org.bukkit.craftbukkit.VERSION.entity.CraftPlayer")
        val getHandleMethod = craftPlayerClazz.getDeclaredMethod("getHandle")
        val nmsPlayer = getHandleMethod.invoke(player)

        val nmsPlayerClazz = findClazz("net.minecraft.server.VERSION.EntityPlayer")
        val playerConnectionField = nmsPlayerClazz.getDeclaredField("playerConnection")
        playerConnectionField.isAccessible = true
        val connection = playerConnectionField.get(nmsPlayer)

        val playerConnectionClazz = findClazz("net.minecraft.server.VERSION.PlayerConnection")
        val packetClazz = findClazz("net.minecraft.server.VERSION.Packet")
        val sendPacketMethod = playerConnectionClazz.getDeclaredMethod("sendPacket", packetClazz)
        sendPacketMethod.invoke(connection, packet)
    }
}