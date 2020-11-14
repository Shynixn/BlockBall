package com.github.shynixn.blockball.api.bukkit.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Base BlockBall event where all BlockBall events inherit from.
 */
open class BlockBallEvent : Event(), Cancellable {
    private var cancelled: Boolean = false

    /**
     * Event.
     */
    companion object {
        private var handlers = HandlerList()

        /**
         * Handlerlist.
         */
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlers
        }
    }

    /**
     * Returns all handles.
     */
    override fun getHandlers(): HandlerList {
        return BlockBallEvent.handlers
    }

    /**
     * Is the event cancelled.
     */
    override fun isCancelled(): Boolean {
        return cancelled
    }

    /**
     * Sets the event cancelled.
     */
    override fun setCancelled(flag: Boolean) {
        this.cancelled = flag
    }
}
