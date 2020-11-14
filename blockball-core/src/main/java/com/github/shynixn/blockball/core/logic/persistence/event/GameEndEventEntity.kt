package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.persistence.entity.Game

class GameEndEventEntity(game: Game, var winningTeam: Team?) : GameEventEntity(game)
