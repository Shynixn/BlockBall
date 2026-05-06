package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment

class BallOutOfBoundsMeta {
    @Comment("If set to true, the ball simply bounces back when it goes out of bounds. If set to false, a throw-in, corner kick or goal kick is triggered instead.")
    var forceField = true

    @Comment("The time in seconds until the selected player is teleported to the throw-in, corner kick or goal kick position.")
    var timeToTeleportSec: Int = 3

    @Comment("The time in seconds until teleported player can perform an action.")
    var timeToStartSec: Int = 5

    @Comment("The time in seconds until teleported has no longer exclusive control of the ball.")
    var timeOutStartSec: Int = 5
}