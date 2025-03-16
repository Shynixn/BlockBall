package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.mcutils.common.sound.SoundMeta
import com.github.shynixn.mcutils.sign.SignMeta

class ArenaMeta {
    /** Meta data for spectating setting. */
    val spectatorMeta: SpectatorMeta = SpectatorMeta()

    /** Metadata of the customizing Properties. */
    val customizingMeta: CustomizationMeta = CustomizationMeta()

    /** Meta data of a generic lobby. */
    val lobbyMeta: LobbyMeta = LobbyMeta()

    /** Metadata of the hub lobby. */
    var hubLobbyMeta: HubLobbyMeta = HubLobbyMeta()

    /** Meta data of the minigame lobby. */
    val minigameMeta: MinigameLobbyMeta = MinigameLobbyMeta()

    /** Meta data of the doubleJump. */
    val doubleJumpMeta: DoubleJumpMeta = DoubleJumpMeta()

    /** Meta data of the bossbar. */
    val bossBarMeta: BossBarMeta = BossBarMeta()

    /** Meta data of proection. */
    val protectionMeta: ArenaProtectionMeta = ArenaProtectionMeta()

    /** Metadata of the ball. */
    val ballMeta: SoccerBallSettings = SoccerBallSettings()

    /** Metadata of the blueTeam. */
    val blueTeamMeta: TeamMeta = TeamMeta()

    /** Meta data of the redTeam. */
    val redTeamMeta: TeamMeta = TeamMeta()

    /** Metadata of the refereeTeam. */
    val refereeTeamMeta: TeamMeta = TeamMeta()

    /** Integration into MCPlayerStats */
    val mcPlayerStatsEnabled : Boolean = true

    init {
        val soundMetaKick = SoundMeta()
        soundMetaKick.name = "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5
        blueTeamMeta.displayName = "&9Team Blue"
        redTeamMeta.displayName = "&cTeam Red"
        refereeTeamMeta.displayName = "&fTeam Referee"
        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }

    /**
     * Compatibility until signs are refactored to external addon.
     */
    fun getAllSigns(): List<SignMeta> {
        val signs = ArrayList<SignMeta>()
        signs.addAll(lobbyMeta.joinSigns)
        signs.addAll(lobbyMeta.leaveSigns)
        signs.addAll(redTeamMeta.teamSigns)
        signs.addAll(blueTeamMeta.teamSigns)
        return signs
    }
}
