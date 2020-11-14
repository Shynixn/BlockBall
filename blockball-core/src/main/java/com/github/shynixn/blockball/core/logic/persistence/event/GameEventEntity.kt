package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.persistence.entity.Game

open class GameEventEntity(var game : Game) : BlockBallEventEntity()
