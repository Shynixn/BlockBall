package com.github.shynixn.blockball.entity.compatibility

import com.github.shynixn.blockball.api.business.proxy.BallProxy

open class BallEventEntity(val ballProxy: BallProxy) : BlockBallEventEntity() {
}
