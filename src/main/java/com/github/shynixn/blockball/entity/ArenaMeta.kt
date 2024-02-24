package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.deprecated.YamlSerialize
import com.github.shynixn.blockball.enumeration.BallActionType
import com.github.shynixn.blockball.enumeration.ParticleType

class ArenaMeta {
    /** Meta data for spectating setting. */
    @YamlSerialize(orderNumber = 12, value = "spectator-meta")
    val spectatorMeta: SpectatorMeta = SpectatorMeta()

    /** Meta data of the customizing Properties. */
    @YamlSerialize(orderNumber = 13, value = "customizing-meta")
    val customizingMeta: CustomizationMeta = CustomizationMeta()

    /** Meta data for rewards */
    @YamlSerialize(orderNumber = 10, value = "reward-meta")
    val rewardMeta: Reward = Reward()

    /** Meta data of all holograms. */
    val hologramMetas: ArrayList<HologramMeta>
        get() = this.internalHologramMetas as ArrayList<HologramMeta>

    /** Meta data of a generic lobby. */
    @YamlSerialize(orderNumber = 1, value = "meta")
    val lobbyMeta: LobbyMeta = LobbyMeta()

    /** Meta data of the hub lobby. */
    @YamlSerialize(orderNumber = 2, value = "hubgame-meta")
    var hubLobbyMeta: HubLobbyMeta = HubLobbyMeta()

    /** Meta data of the minigame lobby. */
    @YamlSerialize(orderNumber = 3, value = "minigame-meta")
    val minigameMeta: MinigameLobbyMeta = MinigameLobbyMeta()

    /** Meta data of the bungeecord lobby. */
    @YamlSerialize(orderNumber = 4, value = "bungeecord-meta")
    val bungeeCordMeta: BungeeCordMeta = BungeeCordMeta()

    /** Meta data of the doubleJump. */
    @YamlSerialize(orderNumber = 8, value = "double-jump")
    val doubleJumpMeta: DoubleJumpMeta = DoubleJumpMeta()

    /** Meta data of the bossbar. */
    @YamlSerialize(orderNumber = 7, value = "bossbar")
    val bossBarMeta: BossBarMeta = BossBarMeta()

    /** Meta data of the scoreboard. */
    @YamlSerialize(orderNumber = 6, value = "scoreboard")
    val scoreboardMeta: ScoreboardMeta = ScoreboardMeta()

    /** Meta data of proection. */
    @YamlSerialize(orderNumber = 5, value = "protection")
    val protectionMeta: ArenaProtectionMeta = ArenaProtectionMeta()

    /** Meta data of the ball. */
    @YamlSerialize(orderNumber = 4, value = "ball")
    val ballMeta: BallMeta = BallMeta()

    /** Meta data of the blueTeam. */
    @YamlSerialize(orderNumber = 3, value = "team-blue")
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
    @YamlSerialize(orderNumber = 2, value = "team-red")
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

    @YamlSerialize(orderNumber = 9, value = "holograms")
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

        val soundMetaKick = Sound()
        soundMetaKick.name = "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5

        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }
}
