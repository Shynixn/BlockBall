package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.MatchTimeCloseType
import com.github.shynixn.blockball.api.persistence.entity.MatchTimeMeta
import com.github.shynixn.blockball.api.persistence.entity.MinigameLobbyMeta
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.mcutils.common.ChatColor

class MinigameLobbyMetaEntity : MinigameLobbyMeta {
    /**
     * Match Times.
     */
    override val matchTimes: MutableList<MatchTimeMeta>
        get() {
            return internalMatchTimes as MutableList<MatchTimeMeta>
        }
    @YamlSerialize(orderNumber = 1, value = "match-times")
    private var internalMatchTimes: ArrayList<MatchTimeMetaEntity>
    /** Duration the match will max last. */
    @YamlSerialize(orderNumber = 2, value = "lobby-duration")
    override var lobbyDuration: Int = 20
    /** Spawnpoint of the player in the lobby. */
    @YamlSerialize(orderNumber = 3, value = "lobby-spawnpoint", implementation = PositionEntity::class)
    override var lobbySpawnpoint: Position? = null
    /** Message being played in the action bar displaying the joined players how many players are remaining to start. */
    @YamlSerialize(orderNumber = 4, value = "remaining-players-message")
    override var playersRequiredToStartMessage: String =
        "%blockball_lang_miniGameRemainingPlayers%"

    init {
        val firstPeriod = MatchTimeMetaEntity()
        firstPeriod.duration = 150
        firstPeriod.respawnEnabled = true

        val firstPeriodOverTime = MatchTimeMetaEntity()
        firstPeriodOverTime.closeType = MatchTimeCloseType.NEXT_GOAL
        firstPeriodOverTime.duration = 15
        firstPeriodOverTime.respawnEnabled = false
        firstPeriodOverTime.startMessageTitle = ChatColor.GOLD.toString() + "Overtime"
        firstPeriodOverTime.startMessageSubTitle = "Only a few seconds left"

        val breakPeriod = MatchTimeMetaEntity()
        breakPeriod.duration = 10
        breakPeriod.playAbleBall = false
        breakPeriod.respawnEnabled = false
        breakPeriod.startMessageTitle = ChatColor.GOLD.toString() + "Break"
        breakPeriod.startMessageSubTitle = "Take a short break"

        val secondPeriod = MatchTimeMetaEntity()
        secondPeriod.duration = 150
        secondPeriod.respawnEnabled = true
        secondPeriod.isSwitchGoalsEnabled = true

        val secondPeriodOverTime = MatchTimeMetaEntity()
        secondPeriodOverTime.closeType = MatchTimeCloseType.NEXT_GOAL
        secondPeriodOverTime.duration = 15
        secondPeriodOverTime.respawnEnabled = false
        secondPeriodOverTime.startMessageTitle = ChatColor.GOLD.toString() + "Overtime"
        secondPeriodOverTime.startMessageSubTitle = "Only a few seconds left"

        val coolDownPeriod = MatchTimeMetaEntity()
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
