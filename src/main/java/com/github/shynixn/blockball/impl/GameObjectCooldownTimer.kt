package com.github.shynixn.blockball.impl

class GameObjectCooldownTimer(val cooldownMs: Int) {
    private var earliestNextExecutionTimeStamp = 0L

    /**
     * Executes the action.
     */
    fun execute() {
        earliestNextExecutionTimeStamp = System.currentTimeMillis() + cooldownMs
    }

    /**
     * Checks if the cooldown is over.
     */
    fun canExecute(): Boolean {
        return System.currentTimeMillis() >= earliestNextExecutionTimeStamp
    }

    /**
     * Resets the cooldown.
     */
    fun reset() {
        earliestNextExecutionTimeStamp = 0L
    }
}