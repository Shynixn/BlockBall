package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.persistence.entity.Game

class GameLeaveEventEntity(
    /**
     * Joining player.
     */
    var player: Any,
    /**
     * Joining game.
     */
    var game: Game
) : GameCancelableEventEntity()