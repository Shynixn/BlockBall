package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.GameMode

class GameStorage {
    /** Team of the player. */
    var team: Team? = null

    /**
     * Team of the goal which may or may not be the same of the team depending on the swapping state.
     */
    var goalTeam: Team? = null

    /**
     * Exp level of the player.
     */
    var level: Int = 0

    /**
     * Actual exp of the player.
     */
    var exp: Double = 0.0

    /**
     * Max health of the player.
     */
    var maxHealth: Double = 20.0

    /**
     * Health of the player.
     */
    var health: Double = 20.0

    /**
     * Hunger of the player.
     */
    var hunger: Int = 10

    /**
     * Gamemode of the player.
     */
    var gameMode: GameMode = GameMode.SURVIVAL

    /**
     * Counter of enemy goals.
     */
    var scoredGoals : Int = 0

    /**
     * Counter of scored own goals.
     */
    var scoredOwnGoals : Int = 0

    /**
     * Inventory cache.
     */
    var inventoryContents: Array<String?> = arrayOfNulls(0)

    /**
     * Inventory armor cache.
     */
    var armorContents: Array<String?> = arrayOfNulls(0)
}
