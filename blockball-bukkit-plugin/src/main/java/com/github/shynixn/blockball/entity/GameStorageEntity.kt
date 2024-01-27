package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.enumeration.GameMode
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import java.util.*

class GameStorageEntity(override var uuid: UUID) : GameStorage {
    /**
     * Scoreboard of the player.
     */
    override var scoreboard: Any = ""

    /** Team of the player. */
    override var team: Team? = null

    /**
     * Team of the goal which may or may not be the same of the team depending on the swapping state.
     */
    override var goalTeam: Team? = null

    /**
     * Exp level of the player.
     */
    override var level: Int = 0

    /**
     * Actual exp of the player.
     */
    override var exp: Double = 0.0

    /**
     * Max health of the player.
     */
    override var maxHealth: Double = 20.0

    /**
     * Health of the player.
     */
    override var health: Double = 20.0

    /**
     * Hunger of the player.
     */
    override var hunger: Int = 10

    /**
     * Storage belongs to this [GameType].
     */
    override var gameType: GameType = GameType.BUNGEE

    /**
     * Gamemode of the player.
     */
    override var gameMode: GameMode = GameMode.SURVIVAL

    /**
     * Walking Speed of the player.
     */
    override var walkingSpeed: Double = 1.0

    /**
     * Was the player flying?
     */
    override var flying: Boolean = false

    /**
     * Was the player allowed to fly?
     */
    override var allowedFlying: Boolean = false

    /**
     * Inventory cache.
     */
    override var inventoryContents: Array<Any?> = arrayOfNulls(0)

    /**
     * Inventory armor cache.
     */
    override var armorContents: Array<Any?> = arrayOfNulls(0)
}
