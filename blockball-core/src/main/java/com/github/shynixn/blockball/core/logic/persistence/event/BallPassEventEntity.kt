package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

class BallPassEventEntity(
    ball: BallProxy,
    player: Any,
    velocity: Any
) : BallTouchEventEntity(ball, player, velocity)
