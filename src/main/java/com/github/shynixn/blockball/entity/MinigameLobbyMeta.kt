package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.MatchTimeCloseType
import com.github.shynixn.mcutils.common.ChatColor

class MinigameLobbyMeta {
    /**
     * Match Times.
     */
    val matchTimes: MutableList<MatchTimeMeta>
        get() {
            return internalMatchTimes as MutableList<MatchTimeMeta>
        }
    private var internalMatchTimes: ArrayList<MatchTimeMeta>
    /** Duration the match will max last. */
    var lobbyDuration: Int = 20
    /** Spawnpoint of the player in the lobby. */
    var lobbySpawnpoint: Position? = null
    /** Message being played in the action bar displaying the joined players how many players are remaining to start. */
    var playersRequiredToStartMessage: String =
        "%blockball_lang_miniGameRemainingPlayers%"

    init {
        val firstPeriod = MatchTimeMeta()
        firstPeriod.duration = 150
        firstPeriod.respawnEnabled = true

        val firstPeriodOverTime = MatchTimeMeta()
        firstPeriodOverTime.closeType = MatchTimeCloseType.NEXT_GOAL
        firstPeriodOverTime.duration = 15
        firstPeriodOverTime.respawnEnabled = false
        firstPeriodOverTime.startMessageTitle = ChatColor.GOLD.toString() + "Overtime"
        firstPeriodOverTime.startMessageSubTitle = "Only a few seconds left"

        val breakPeriod = MatchTimeMeta()
        breakPeriod.duration = 10
        breakPeriod.playAbleBall = false
        breakPeriod.respawnEnabled = false
        breakPeriod.startMessageTitle = ChatColor.GOLD.toString() + "Break"
        breakPeriod.startMessageSubTitle = "Take a short break"

        val secondPeriod = MatchTimeMeta()
        secondPeriod.duration = 150
        secondPeriod.respawnEnabled = true
        secondPeriod.isSwitchGoalsEnabled = true

        val secondPeriodOverTime = MatchTimeMeta()
        secondPeriodOverTime.closeType = MatchTimeCloseType.NEXT_GOAL
        secondPeriodOverTime.duration = 15
        secondPeriodOverTime.respawnEnabled = false
        secondPeriodOverTime.startMessageTitle = ChatColor.GOLD.toString() + "Overtime"
        secondPeriodOverTime.startMessageSubTitle = "Only a few seconds left"

        val coolDownPeriod = MatchTimeMeta()
        coolDownPeriod.duration = 10
        coolDownPeriod.playAbleBall = false
        coolDownPeriod.respawnEnabled = false

        internalMatchTimes = arrayListOf(
            firstPeriod,
            firstPeriodOverTime,
            breakPeriod,
            secondPeriod,
            secondPeriodOverTime,
            coolDownPeriod
        )
    }
}
