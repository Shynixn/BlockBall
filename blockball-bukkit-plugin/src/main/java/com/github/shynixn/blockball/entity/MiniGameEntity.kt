package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.GameStorage
import com.github.shynixn.blockball.api.persistence.entity.MiniGame
import com.github.shynixn.blockball.api.persistence.entity.Sound

open class MiniGameEntity(
        /**
         *  Arena of the game.
         */
        override val arena: Arena
) : GameEntity(arena), MiniGame {
    /**
     * Is the lobby countdown active.
     */
    override var lobbyCountDownActive: Boolean = false
    /**
     * Actual countdown.
     */
    override var lobbyCountdown: Int = 20

    /**
     * Actual game coutndown.
     */
    override var gameCountdown: Int = 20
    /**
     * Index of the current match time.
     */
    override var matchTimeIndex: Int = 0

    /**
     * Returns if the lobby is full.
     */
    override val isLobbyFull: Boolean
        get() {
            val amount = arena.meta.redTeamMeta.maxAmount + arena.meta.blueTeamMeta.maxAmount

            if (this.ingamePlayersStorage.size >= amount) {
                return true
            }

            return false
        }


    /**
     * Returns the bling sound.
     */
    override val blingSound: Sound = SoundEntity("NOTE_PLING", 1.0, 2.0)
    /**
     * Are the players currently waiting in the lobby?
     */
    override var inLobby: Boolean = false

    /**
     * Storage for [spectatorPlayers],
     */
    override val spectatorPlayersStorage: MutableMap<Any, GameStorage> = HashMap()

    /**
     * List of players which are spectating the game.
     */
    override val spectatorPlayers: List<Any>
        get() {
            return spectatorPlayersStorage.keys.toList()
        }
}
