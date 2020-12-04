package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.persistence.entity.Game

class GameJoinEventEntity(game: Game, var player : Any) : GameEventEntity(game)
