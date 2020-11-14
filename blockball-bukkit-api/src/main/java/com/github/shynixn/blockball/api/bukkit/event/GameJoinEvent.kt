package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.persistence.entity.Game
import org.bukkit.entity.Player

/**
 * Game Join event.
 */
class GameJoinEvent(
        /**
         * Player joining the game.
         */
        val player: Player, game: Game) : GameEvent(game)
