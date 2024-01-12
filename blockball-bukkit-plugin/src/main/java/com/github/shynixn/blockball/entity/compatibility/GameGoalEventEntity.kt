package com.github.shynixn.blockball.entity.compatibility

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.Game

class GameGoalEventEntity(
    game: Game,
    var player: Any?,
    var team: Team
) : GameEventEntity(game)
