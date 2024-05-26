package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.sound.SoundMeta

open class MiniGame(
        /**
         *  Arena of the game.
         */
        arena: Arena
) : Game(arena){
    /**
     * Is the lobby countdown active.
     */
    var lobbyCountDownActive: Boolean = false
    /**
     * Actual countdown.
     */
    var lobbyCountdown: Int = 20

    /**
     * Actual game coutndown.
     */
    var gameCountdown: Int = 20
    /**
     * Index of the current match time.
     */
    var matchTimeIndex: Int = 0

    /**
     * Returns if the lobby is full.
     */
    val isLobbyFull: Boolean
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
    val blingSound: SoundMeta = SoundMeta().also {
        it.name = "BLOCK_NOTE_BLOCK_PLING,BLOCK_NOTE_PLING,NOTE_PLING"
        it.volume = 10.0
        it.pitch = 2.0
    }
    /**
     * Are the players currently waiting in the lobby?
     */
    var inLobby: Boolean = false

    /**
     * Storage for [spectatorPlayers],
     */
    val spectatorPlayersStorage: MutableMap<Any, GameStorage> = HashMap()

    /**
     * List of players which are spectating the game.
     */
    val spectatorPlayers: List<Any>
        get() {
            return spectatorPlayersStorage.keys.toList()
        }
}
