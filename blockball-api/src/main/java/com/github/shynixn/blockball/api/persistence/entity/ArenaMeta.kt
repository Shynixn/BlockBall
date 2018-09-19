package com.github.shynixn.blockball.api.persistence.entity

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
interface ArenaMeta {

    /** Meta data of the hub lobby. */
    val hubLobbyMeta: HubLobbyMeta

    /** Meta data for spectating setting. */
    val spectatorMeta: SpectatorMeta

    /** Meta data of a generic lobby. */
    val lobbyMeta: LobbyMeta

    /** Meta data of the minigame lobby. */
    val minigameMeta: MinigameLobbyMeta

    /** Meta data of the bungeecord lobby. */
    val bungeeCordMeta: BungeeCordLobbyMeta

    /** Meta data of the redTeam. */
    val redTeamMeta: TeamMeta

    /** Meta data of the blueTeam. */
    val blueTeamMeta: TeamMeta

    /** Meta data of all holograms. */
    val hologramMetas: MutableList<HologramMeta>

    /** Meta data of the ball. */
    val ballMeta: BallMeta

    /** Meta data of protection. */
    val protectionMeta: ArenaProtectionMeta

    /** Meta data for rewards */
    val rewardMeta: RewardMeta

    /** Meta data of the scoreboard. */
    val scoreboardMeta: ScoreboardMeta

    /** Meta data of the bossbar. */
    val bossBarMeta: BossBarMeta

    /** Meta data of the doubleJump. */
    val doubleJumpMeta: DoubleJumpMeta

    /** Meta data of the customizing Properties. */
    val customizingMeta: CustomizationMeta
}