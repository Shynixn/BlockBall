package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Team
import com.github.shynixn.blockball.api.business.service.GameExecutionService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.google.inject.Inject

/**
 * Created by Shynixn 2019.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2019 by Shynixn
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
class GameExecutionServiceImpl @Inject constructor(private val proxyService: ProxyService) : GameExecutionService {
    /**
     * Applies points to the belonging teams when the given [player] dies in the given [game].
     */
    override fun <P, G : Game> applyDeathPoints(game: G, player: P) {
        val team = game.ingamePlayersStorage[player as Any]!!.team!!

        if (team == Team.RED) {
            game.blueScore += game.arena.meta.blueTeamMeta.pointsPerEnemyDeath
        } else {
            game.redScore += game.arena.meta.redTeamMeta.pointsPerEnemyDeath
        }
    }

    /**
     * Lets the given [player] in the given [game] respawn at the specified spawnpoint.
     */
    override fun <P, G : Game> respawn(game: G, player: P) {
        val team = game.ingamePlayersStorage[player as Any]!!.goalTeam!!

        val teamMeta = if (team == Team.RED) {
            game.arena.meta.redTeamMeta
        } else {
            game.arena.meta.blueTeamMeta
        }

        if (teamMeta.spawnpoint == null) {
            proxyService.setPlayerLocation(player, game.arena.meta.ballMeta.spawnpoint!!)
        } else {
            proxyService.setPlayerLocation(player, teamMeta.spawnpoint!!)
        }
    }
}