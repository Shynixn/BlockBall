package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.GameType
import com.github.shynixn.blockball.enumeration.Team
import org.bukkit.GameMode
import java.util.*

class GameStorage(var uuid: UUID){
    /**
     * Scoreboard of the player.
     */
    var scoreboard: Any = ""

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
     * Storage belongs to this [GameType].
     */
    var gameType: GameType = GameType.BUNGEE

    /**
     * Gamemode of the player.
     */
    var gameMode: GameMode = GameMode.SURVIVAL

    /**
     * Walking Speed of the player.
     */
    var walkingSpeed: Double = 1.0

    /**
     * Was the player flying?
     */
    var flying: Boolean = false

    /**
     * Was the player allowed to fly?
     */
    var allowedFlying: Boolean = false

    /**
     * Inventory cache.
     */
    var inventoryContents: Array<Any?> = arrayOfNulls(0)

    /**
     * Inventory armor cache.
     */
    var armorContents: Array<Any?> = arrayOfNulls(0)
}
