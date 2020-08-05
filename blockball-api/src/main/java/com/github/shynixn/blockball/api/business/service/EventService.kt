package com.github.shynixn.blockball.api.business.service

interface EventService {
    /**
     * Sends a custom event.
     */
    fun sendEvent(event: Any)
}