package com.github.shynixn.blockball.entity.compatibility

import com.github.shynixn.blockball.api.business.proxy.BallProxy

class BallTeleportEventEntity(ball: BallProxy, var targetLocation: Any) : BallEventEntity(ball)
