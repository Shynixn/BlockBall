package com.github.shynixn.blockball.core.logic.persistence.event

import com.github.shynixn.blockball.api.business.proxy.BallProxy

class BallTeleportEventEntity(ball: BallProxy, var targetLocation: Any) : BallEventEntity(ball)
