package com.github.shynixn.blockball.api.business.proxy

interface DataWatcherProxy {
    /**
     * Sets the value at the given index.
     */
    fun <T> set(index: Int, value: T)

    /**
     * Gets the nms handle.
     */
    fun getHandle(): Any
}
