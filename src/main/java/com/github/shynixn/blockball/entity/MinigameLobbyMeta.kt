package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.Vector3d

class MinigameLobbyMeta {
    /**
     * Match Times.
     */
    var matchTimes: MutableList<MatchTimeMeta> = ArrayList()

    /** Duration the match will max last. */
    var lobbyDuration: Int = 20

    init {
        val firstPeriod = MatchTimeMeta()
        firstPeriod.duration = 150
        firstPeriod.respawnEnabled = true

        val breakPeriod = MatchTimeMeta()
        breakPeriod.duration = 10
        breakPeriod.playAbleBall = false
        breakPeriod.respawnEnabled = false
        breakPeriod.startMessageTitle = ""
        breakPeriod.startMessageSubTitle = "&0&l[&f&lBreak&0&l]&7"

        val secondPeriod = MatchTimeMeta()
        secondPeriod.duration = 150
        secondPeriod.respawnEnabled = true
        secondPeriod.isSwitchGoalsEnabled = true

        val coolDownPeriod = MatchTimeMeta()
        coolDownPeriod.duration = 10
        coolDownPeriod.playAbleBall = false
        coolDownPeriod.respawnEnabled = false

        matchTimes = arrayListOf(
            firstPeriod,
            breakPeriod,
            secondPeriod,
            coolDownPeriod
        )
    }
}
