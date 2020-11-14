package com.github.shynixn.blockball.api.bukkit.event

import com.github.shynixn.blockball.api.persistence.entity.Game

/**
 * Base Event for all game events.
 */
open class GameEvent(
    /**
     * Game firing this event.
     */
    val game: Game
) : BlockBallEvent()
