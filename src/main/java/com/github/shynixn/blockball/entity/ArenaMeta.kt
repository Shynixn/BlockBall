package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.ParticleType
import com.github.shynixn.mcutils.common.sound.SoundMeta

class ArenaMeta {
    /** Meta data for spectating setting. */
    val spectatorMeta: SpectatorMeta = SpectatorMeta()

    /** Meta data of the customizing Properties. */
    val customizingMeta: CustomizationMeta = CustomizationMeta()

    /** Meta data for rewards */
    val rewardMeta: Reward = Reward()

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
        val partMetaSpawn = Particle()
        partMetaSpawn.typeName = ParticleType.EXPLOSION_NORMAL.name
        partMetaSpawn.amount = 10
        partMetaSpawn.speed = 0.1
        partMetaSpawn.offset.x = 2.0
        partMetaSpawn.offset.y = 2.0
        partMetaSpawn.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONSPAWN] = partMetaSpawn

        val partMetaInteraction = Particle()
        partMetaInteraction.typeName = ParticleType.CRIT.name
        partMetaInteraction.amount = 5
        partMetaInteraction.speed = 0.1
        partMetaInteraction.offset.x = 2.0
        partMetaInteraction.offset.y = 2.0
        partMetaInteraction.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONINTERACTION] = partMetaInteraction

        val partMetaKick = Particle()
        partMetaKick.typeName = ParticleType.EXPLOSION_LARGE.name
        partMetaKick.amount = 5
        partMetaKick.speed = 0.1
        partMetaKick.offset.x = 0.2
        partMetaKick.offset.y = 0.2
        partMetaKick.offset.z = 0.2

        ballMeta.particleEffects[BallActionType.ONKICK] = partMetaKick

        val partMetaShoot = Particle()
        partMetaShoot.typeName = ParticleType.EXPLOSION_NORMAL.name
        partMetaShoot.amount = 5
        partMetaShoot.speed = 0.1
        partMetaShoot.offset.x = 0.1
        partMetaShoot.offset.y = 0.1
        partMetaShoot.offset.z = 0.1

        ballMeta.particleEffects[BallActionType.ONPASS] = partMetaShoot

        val soundMetaKick = SoundMeta()
        soundMetaKick.name = "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5

        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }
}
