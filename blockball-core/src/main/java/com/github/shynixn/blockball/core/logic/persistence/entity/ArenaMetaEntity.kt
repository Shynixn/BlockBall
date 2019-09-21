@file:Suppress("UNCHECKED_CAST")

package com.github.shynixn.blockball.core.logic.persistence.entity

import com.github.shynixn.blockball.api.business.annotation.YamlSerialize
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.ArenaMeta
import com.github.shynixn.blockball.api.persistence.entity.HologramMeta

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
    override val bungeeCordMeta: BungeeCordLobbyMetaEntity = BungeeCordLobbyMetaEntity()
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
    override val ballMeta: BallMetaEntity = BallMetaEntity("http://textures.minecraft.net/texture/8e4a70b7bbcd7a8c322d522520491a27ea6b83d60ecf961d2b4efbbf9f605d")
    /** Meta data of the blueTeam. */
    @YamlSerialize(orderNumber = 3, value = "team-blue")
    override val blueTeamMeta: TeamMetaEntity = TeamMetaEntity("Team Blue", "&9", PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.BLUE_GOALS.placeHolder + " : " + PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.RED_GOALS.placeHolder, PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.LASTHITBALL.placeHolder + " scored for " + PlaceHolder.TEAM_BLUE.placeHolder, PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.TEAM_BLUE.placeHolder, PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.TEAM_BLUE.placeHolder + "&a has won the match", PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.TEAM_BLUE.placeHolder, "&eMatch ended in a draw.")
    /** Meta data of the redTeam. */
    @YamlSerialize(orderNumber = 2, value = "team-red")
    override val redTeamMeta: TeamMetaEntity = TeamMetaEntity("Team Red", "&c", PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.RED_GOALS.placeHolder + " : " + PlaceHolder.BLUE_COLOR.placeHolder + PlaceHolder.BLUE_GOALS.placeHolder, PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.LASTHITBALL.placeHolder + " scored for " + PlaceHolder.TEAM_RED.placeHolder, PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.TEAM_RED.placeHolder, PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.TEAM_RED.placeHolder + "&a has won the match", PlaceHolder.RED_COLOR.placeHolder + PlaceHolder.TEAM_RED.placeHolder, "&eMatch ended in a draw.")

    @YamlSerialize(orderNumber = 9, value = "holograms")
    private val internalHologramMetas: ArrayList<HologramMetaEntity> = ArrayList()

    init {
        val partMetaSpawn = ParticleEntity()
        partMetaSpawn.type = ParticleType.EXPLOSION_NORMAL
        partMetaSpawn.amount = 10
        partMetaSpawn.speed = 0.1
        partMetaSpawn.offset.x = 2.0
        partMetaSpawn.offset.y = 2.0
        partMetaSpawn.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONSPAWN] = partMetaSpawn

        val partMetaInteraction = ParticleEntity()
        partMetaInteraction.type = ParticleType.CRIT
        partMetaInteraction.amount = 5
        partMetaInteraction.speed = 0.1
        partMetaInteraction.offset.x = 2.0
        partMetaInteraction.offset.y = 2.0
        partMetaInteraction.offset.z = 2.0

        ballMeta.particleEffects[BallActionType.ONINTERACTION] = partMetaInteraction

        val partMetaKick = ParticleEntity()
        partMetaKick.type = ParticleType.EXPLOSION_LARGE
        partMetaKick.amount = 5
        partMetaKick.speed = 0.1
        partMetaKick.offset.x = 0.2
        partMetaKick.offset.y = 0.2
        partMetaKick.offset.z = 0.2

        ballMeta.particleEffects[BallActionType.ONKICK] = partMetaKick

        val partMetaShoot = ParticleEntity()
        partMetaShoot.type = ParticleType.EXPLOSION_NORMAL
        partMetaShoot.amount = 5
        partMetaShoot.speed = 0.1
        partMetaShoot.offset.x = 0.1
        partMetaShoot.offset.y = 0.1
        partMetaShoot.offset.z = 0.1

        ballMeta.particleEffects[BallActionType.ONPASS] = partMetaShoot

        val soundMetaKick = SoundEntity()
        soundMetaKick.name = "ZOMBIE_WOOD"
        soundMetaKick.volume = 10.0
        soundMetaKick.pitch = 1.5

        ballMeta.soundEffects[BallActionType.ONKICK] = soundMetaKick
    }
}