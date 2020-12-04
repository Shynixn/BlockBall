package com.github.shynixn.blockball.api.business.service

interface ProtocolService {
    /**
     * Registers the following packets classes for events.
     */
    fun registerPackets(packets: List<Class<*>>)

    /**
     * Registers the player.
     */
    fun <P> register(player: P)

    /**
     * Clears the player cache.
     */
    fun <P> unRegister(player: P)

    /**
     * Disposes the protocol service.
     */
    fun dispose()
}
