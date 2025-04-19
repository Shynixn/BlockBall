package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.mcutils.common.repository.Comment

class MatchTimeMeta {
    @Comment("Timespan of this period in a game in seconds.")
    var duration: Int = 300

    @Comment("Defines when this period is over. If set to TIME_OVER, the match ends when the duration has passed. If set to NEXT_GOAL, the match ends when the duration has passed or if a goal gets scored.")
    var closeType: MatchTimeCloseType = MatchTimeCloseType.TIME_OVER

    @Comment("Should teams switch sides when this period starts?")
    var isSwitchGoalsEnabled: Boolean = false

    @Comment("Is the ball spawned during this period?")
    var playAbleBall: Boolean = true

    @Comment("Should players respawn at their spawnpoint when this period starts?")
    var respawnEnabled: Boolean = true

    @Comment("A title message being displayed.")
    var startMessageTitle: String = ""

    @Comment("A subtitle message being displayed.")
    var startMessageSubTitle: String = ""

    @Comment("Title message property.")
    var startMessageFadeIn: Int = 20

    @Comment("Title message property.")
    var startMessageStay: Int = 60

    @Comment("Title message property.")
    var startMessageFadeOut: Int = 20
}
