package com.github.shynixn.blockball.service

import com.github.shynixn.blockball.api.business.proxy.DataWatcherProxy
import net.minecraft.server.v1_8_R3.DataWatcher
import net.minecraft.server.v1_8_R3.EntityArmorStand
import net.minecraft.server.v1_8_R3.EntityTypes
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment

class WrappedDataWatcher18R1 : DataWatcherProxy {
    private val dataWatcher = DataWatcher(null)

    /**
     * Sets the value at the given index.
     */
    override fun <T> set(index: Int, value: T) {
        dataWatcher.a(index, value)
    }

    /**
     * Gets the nms handle.
     */
    override fun getHandle(): Any {
        return dataWatcher
    }
}
