package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.mcutils.common.sound.SoundMeta

class ArenaMeta {
    /** Meta data for spectating setting. */
    val spectatorMeta: SpectatorMeta = SpectatorMeta()

    /** Meta data of the customizing Properties. */
    val customizingMeta: CustomizationMeta = CustomizationMeta()

    /** Meta data of all holograms. */
    val hologramMetas: ArrayList<HologramMeta>
        get() = this.internalHologramMetas

    /** Meta data of a generic lobby. */
    val lobbyMeta: LobbyMeta = LobbyMeta()

    /** Meta data of the hub lobby. */
    var hubLobbyMeta: HubLobbyMeta = HubLobbyMeta()

    /** Meta data of the minigame lobby. */
    val minigameMeta: MinigameLobbyMeta = MinigameLobbyMeta()

    /** Meta data of the bungeecord lobby. */
    val bungeeCordMeta: BungeeCordMeta = BungeeCordMeta()

    /** Meta data of the doubleJump. */
    val doubleJumpMeta: DoubleJumpMeta = DoubleJumpMeta()

    /** Meta data of the bossbar. */
    val bossBarMeta: BossBarMeta = BossBarMeta()

    /** Meta data of the scoreboard. */
    val scoreboardMeta: ScoreboardMeta = ScoreboardMeta()

    /** Meta data of proection. */
    val protectionMeta: ArenaProtectionMeta = ArenaProtectionMeta()

    /** Meta data of the ball. */
    val ballMeta: BallMeta = BallMeta()

    /** Meta data of the blueTeam. */
    val blueTeamMeta: TeamMeta = TeamMeta(
        "%blockball_lang_teamBlueDisplayName%",
        "%blockball_lang_teamBlueColor%",
        "%blockball_lang_teamBlueScoreTitle%",
        "%blockball_lang_teamBlueScoreSubTitle%",
        "%blockball_lang_teamBlueWinTitle%",
        "%blockball_lang_teamBlueWinSubTitle%",
        "%blockball_lang_teamBlueDrawTitle%",
        "%blockball_lang_teamBlueDrawSubTitle%"
    )

    /** Meta data of the redTeam. */
    val redTeamMeta: TeamMeta = TeamMeta(
        "%blockball_lang_teamRedDisplayName%",
        "%blockball_lang_teamRedColor%",
        "%blockball_lang_teamRedScoreTitle%",
        "%blockball_lang_teamRedScoreSubTitle%",
        "%blockball_lang_teamRedWinTitle%",
        "%blockball_lang_teamRedWinSubTitle%",
        "%blockball_lang_teamRedDrawTitle%",
        "%blockball_lang_teamRedDrawSubTitle%"
    )

    private val internalHologramMetas: ArrayList<HologramMeta> = ArrayList()

    init {
        val soundMetaKick = SoundMeta()
        soundMetaKick.name = "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5

        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }
}
