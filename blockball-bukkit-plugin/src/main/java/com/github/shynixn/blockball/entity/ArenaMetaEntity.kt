package com.github.shynixn.blockball.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.persistence.entity.ArenaMeta
import com.github.shynixn.blockball.api.persistence.entity.HologramMeta

class ArenaMetaEntity : ArenaMeta {
    /** Meta data for spectating setting. */
    @YamlSerialize(orderNumber = 12, value = "spectator-meta")
    override val spectatorMeta: SpectatorMetaEntity = SpectatorMetaEntity()

    /** Meta data of the customizing Properties. */
    @YamlSerialize(orderNumber = 13, value = "customizing-meta")
    override val customizingMeta: CustomizationMetaEntity = CustomizationMetaEntity()

    /** Meta data for rewards */
    @YamlSerialize(orderNumber = 10, value = "reward-meta")
    override val rewardMeta: RewardEntity = RewardEntity()

    /** Meta data of all holograms. */
    override val hologramMetas: ArrayList<HologramMeta>
        get() = this.internalHologramMetas as ArrayList<HologramMeta>

    /** Meta data of a generic lobby. */
    @YamlSerialize(orderNumber = 1, value = "meta")
    override val lobbyMeta: LobbyMetaEntity = LobbyMetaEntity()

    /** Meta data of the hub lobby. */
    @YamlSerialize(orderNumber = 2, value = "hubgame-meta")
    override var hubLobbyMeta: HubLobbyMetaEntity = HubLobbyMetaEntity()

    /** Meta data of the minigame lobby. */
    @YamlSerialize(orderNumber = 3, value = "minigame-meta")
    override val minigameMeta: MinigameLobbyMetaEntity = MinigameLobbyMetaEntity()

    /** Meta data of the bungeecord lobby. */
    @YamlSerialize(orderNumber = 4, value = "bungeecord-meta")
    override val bungeeCordMeta: BungeeCordMetaEntity = BungeeCordMetaEntity()

    /** Meta data of the doubleJump. */
    @YamlSerialize(orderNumber = 8, value = "double-jump")
    override val doubleJumpMeta: DoubleJumpMetaEntity = DoubleJumpMetaEntity()

    /** Meta data of the bossbar. */
    @YamlSerialize(orderNumber = 7, value = "bossbar")
    override val bossBarMeta: BossBarMetaEntity = BossBarMetaEntity()

    /** Meta data of the scoreboard. */
    @YamlSerialize(orderNumber = 6, value = "scoreboard")
    override val scoreboardMeta: ScoreboardEntity = ScoreboardEntity()

    /** Meta data of proection. */
    @YamlSerialize(orderNumber = 5, value = "protection")
    override val protectionMeta: ArenaProtectionMetaEntity = ArenaProtectionMetaEntity()

    /** Meta data of the ball. */
    @YamlSerialize(orderNumber = 4, value = "ball")
    override val ballMeta: BallMetaEntity = BallMetaEntity()

    /** Meta data of the blueTeam. */
    @YamlSerialize(orderNumber = 3, value = "team-blue")
    override val blueTeamMeta: TeamMetaEntity = TeamMetaEntity(
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
    override val redTeamMeta: TeamMetaEntity = TeamMetaEntity(
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
    private val internalHologramMetas: ArrayList<HologramMetaEntity> = ArrayList()

    init {
        val partMetaSpawn = ParticleEntity()
        partMetaSpawn.typeName = ParticleType.EXPLOSION_NORMAL.name
        partMetaSpawn.amount = 10
        partMetaSpawn.speed = 0.1
        partMetaSpawn.offset.x = 2.0
        partMetaSpawn.offset.y = 2.0
        partMetaSpawn.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONSPAWN] = partMetaSpawn

        val partMetaInteraction = ParticleEntity()
        partMetaInteraction.typeName = ParticleType.CRIT.name
        partMetaInteraction.amount = 5
        partMetaInteraction.speed = 0.1
        partMetaInteraction.offset.x = 2.0
        partMetaInteraction.offset.y = 2.0
        partMetaInteraction.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONINTERACTION] = partMetaInteraction

        val partMetaKick = ParticleEntity()
        partMetaKick.typeName = ParticleType.EXPLOSION_LARGE.name
        partMetaKick.amount = 5
        partMetaKick.speed = 0.1
        partMetaKick.offset.x = 0.2
        partMetaKick.offset.y = 0.2
        partMetaKick.offset.z = 0.2

        ballMeta.particleEffects[BallActionType.ONKICK] = partMetaKick

        val partMetaShoot = ParticleEntity()
        partMetaShoot.typeName = ParticleType.EXPLOSION_NORMAL.name
        partMetaShoot.amount = 5
        partMetaShoot.speed = 0.1
        partMetaShoot.offset.x = 0.1
        partMetaShoot.offset.y = 0.1
        partMetaShoot.offset.z = 0.1

        ballMeta.particleEffects[BallActionType.ONPASS] = partMetaShoot

        val soundMetaKick = SoundEntity()
        soundMetaKick.name = "ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5

        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }
}
