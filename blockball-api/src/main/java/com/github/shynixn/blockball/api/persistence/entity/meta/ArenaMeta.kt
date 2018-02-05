package com.github.shynixn.blockball.api.persistence.entity.meta

import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.display.BossBarMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.display.ScoreboardMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.lobby.HubLobbyMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.ArenaProtectionMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.DoubleJumpMeta
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta

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
interface ArenaMeta<Location, ItemStack, Vector, Player, Material, Block
        , ParticleEffectEntity : ParticleEffectMeta<Location, Player, Material>, SoundEffectEntity : SoundEffectMeta<Location, Player>> {

    /** Meta data of the hublobby. */
    val hubLobbyMeta: HubLobbyMeta

    /** Meta data of the redTeam. */
    val redTeamMeta: TeamMeta<Location, ItemStack>

    /** Meta data of the blueTeam. */
    val blueTeamMeta: TeamMeta<Location, ItemStack>

    /** Meta data of the ball. */
    val ballMeta: BallMeta<Location, Material, Player, Block, ParticleEffectEntity, SoundEffectEntity>

    /** Meta data of proection. */
    val protectionMeta: ArenaProtectionMeta<Vector>

    /** Meta data of the scoreboard. */
    val scoreboardMeta: ScoreboardMeta

    /** Meta data of the bossbar. */
    val bossBarMeta: BossBarMeta

    /** Meta data of the doubleJump. */
    val doubleJumpMeta: DoubleJumpMeta<Location, Player, Material>
}