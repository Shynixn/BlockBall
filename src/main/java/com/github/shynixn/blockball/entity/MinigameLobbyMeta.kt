package com.github.shynixn.blockball.entity

import com.github.shynixn.mcutils.common.repository.Comment
import com.github.shynixn.mcutils.common.sound.SoundMeta

class MinigameLobbyMeta {

    @Comment("A list of periods in this soccer game. You can freely add and remove periods here.")
    var matchTimes: MutableList<MatchTimeMeta> = ArrayList()

    @Comment("The lobby timer in seconds until the match starts.")
    var lobbyDuration: Int = 20

    @Comment("The sound effect being played during the countdown. The names are separated by comma to support different Minecraft versions. An empty name disables the sound.")
    var countdownSound: SoundMeta = SoundMeta().also {
        it.name = "BLOCK_NOTE_BLOCK_PLING,BLOCK_NOTE_PLING,NOTE_PLING"
        it.volume = 10.0
        it.pitch = 2.0
    }

    @Comment("If set to true, players cannot leave the soccer field and the forcefield pushes them back into the direction of the ball spawn location of the arena.")
    var forceFieldEnabled: Boolean = true

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
        secondPeriod.switchGoals = true

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
