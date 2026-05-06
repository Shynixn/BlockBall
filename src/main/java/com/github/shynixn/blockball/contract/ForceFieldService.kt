package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.ForceField
import org.bukkit.Location
import org.bukkit.entity.Player

interface ForceFieldService {
    /**
     * Adds a new tracked forceField.
     */
    fun addForceField(forceField: ForceField)

    /**
     * Removes a tracked forcefield.
     */
    fun removeForceField(forceField: ForceField)

    /**
     * Checks if the player is in flight status.
     */
    fun isInFlightStatus(player: Player): Boolean

    /**
     * Resets.
     */
    fun resetFlightStatus(player: Player)

    /**
     * Checks if the player is currently interacting with a forceField and applies the according effects.
     */
    fun checkInteractionsWithForceField(player: Player, location: Location)

    /**
     * Clears the resources from the given player.
     */
    fun clear(player: Player)

    /**
     * Knocks a player into the center of the forceField.
     */
    fun knockBackInside(forceField: ForceField, player: Player, strength : Float = 1.5f)

    /**
     * Knocks a player out the center of the forceField.
     */
    fun knockBackOutside(forceField: ForceField, player: Player, strength : Float = 1.5f)
}