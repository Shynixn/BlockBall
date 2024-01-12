package com.github.shynixn.blockball.entity.compatibility

import com.github.shynixn.blockball.api.persistence.entity.Game

class GameLeaveEventEntity(game: Game, var player : Any) : GameEventEntity(game)
