package com.github.shynixn.blockball.contract

import org.bukkit.entity.Player

interface DependencyBossBarApiService {
    /**
     * Sets the bossbar [message] for the given [player] with the given [percent].
     */
    fun setBossbarMessage(player: Player, message: String, percent: Double = 1.0)

    /**
     * Removes the bossbar from the given [player] if it is enabled.
     */
    fun removeBossbarMessage(player: Player)
}
