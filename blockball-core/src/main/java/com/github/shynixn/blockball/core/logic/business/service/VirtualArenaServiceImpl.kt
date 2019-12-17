package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.ParticleType
import com.github.shynixn.blockball.api.business.service.ConcurrencyService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.ProxyService
import com.github.shynixn.blockball.api.business.service.VirtualArenaService
import com.github.shynixn.blockball.api.persistence.entity.Arena
import com.github.shynixn.blockball.api.persistence.entity.Particle
import com.github.shynixn.blockball.api.persistence.entity.Position
import com.github.shynixn.blockball.core.logic.business.extension.async
import com.github.shynixn.blockball.core.logic.persistence.entity.ParticleEntity
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.google.inject.Inject

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
class VirtualArenaServiceImpl @Inject constructor(
    private val concurrencyservice: ConcurrencyService,
    private val proxyservice: ProxyService,
    private val particleService: ParticleService
) : VirtualArenaService {

    /**
     * Displays the [arena] virtual locations for the given [player].
     */
    override fun <P> displayForPlayer(player: P, arena: Arena) {

        val particle = ParticleEntity(ParticleType.REDSTONE.name)
        particle.colorRed = 255
        particle.colorBlue = 0
        particle.colorGreen = 0
        particle.amount = 20
        particle.speed = 0.02
        async(concurrencyservice) {
            displayParticles(
                player,
                particle,
                arena.meta.redTeamMeta.goal.lowerCorner,
                arena.meta.redTeamMeta.goal.upperCorner
            )
            displayParticles(
                player,
                particle,
                arena.meta.blueTeamMeta.goal.lowerCorner,
                arena.meta.blueTeamMeta.goal.upperCorner
            )
        }
    }

    /**
     * Displays the particles between the given [lowCorner] and [upCorner] location for the given [player].
     */
    private fun <P> displayParticles(player: P, particle: Particle, lowCorner: Position, upCorner: Position) {
        var j = lowCorner.y
        while (j <= upCorner.y) {
            var i = lowCorner.x
            while (i <= upCorner.x) {
                var k = lowCorner.z
                while (k <= upCorner.z) {
                    val location = PositionEntity(proxyservice.getWorldName(player), i, j, k)
                    particleService.playParticle(location, particle, arrayListOf(player))
                    k++
                }
                i++
            }
            j++
        }
    }
}