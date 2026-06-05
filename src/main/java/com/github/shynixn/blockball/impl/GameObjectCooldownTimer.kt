package com.github.shynixn.blockball.impl

class GameObjectCooldownTimer(val cooldownMs: Int) {
    private var remainingTimeMs: Int = 0

    /**
     * Checks if the cooldown is over. If ready, it consumes the cooldown
     * immediately and returns true. Requires delta time from your tick loop.
     */
    fun tryExecute(deltaMs: Int): Boolean {
        if (remainingTimeMs > 0) {
            remainingTimeMs -= deltaMs
            if (remainingTimeMs < 0) {
                remainingTimeMs = 0
            }
        }

        if (remainingTimeMs == 0) {
            remainingTimeMs = cooldownMs
            return true
        }

        return false
    }

    /**
     * Checks if the cooldown is over.
     */
    fun canExecute(): Boolean {
        return remainingTimeMs == 0
    }
}