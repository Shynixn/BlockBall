package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.service.DoubleJumpService
import com.github.shynixn.blockball.api.business.service.GameService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.google.inject.Inject
import org.bukkit.entity.Player

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
class DoubleJumpServiceImpl @Inject constructor(private val gameService: GameService, private val soundService: SoundService, private val particleService: ParticleService) : DoubleJumpService {
    /**
     * Handles the double click of the given [player] and executes the double jump if available.
     */
    override fun <P> handleDoubleClick(player: P): Boolean {
        if (player !is Player) {
            throw IllegalArgumentException("Player has to be a BukkitPlayer!")
        }

        val game = gameService.getGameFromPlayer(player)

        if (!game.isPresent) {
            return false
        }

        player.allowFlight = false
        player.isFlying = false

        val meta = game.get().arena.meta.doubleJumpMeta

        if (meta.enabled && !game.get().doubleJumpCoolDownPlayers.containsKey(player)) {
            @Suppress("USELESS_CAST")
            game.get().doubleJumpCoolDownPlayers[player as Any] = meta.cooldown
            player.velocity = player.location.direction
                    .multiply(meta.horizontalStrength)
                    .setY(meta.verticalStrength)

            soundService.playSound(player.location, meta.soundEffect, player.world.players)
            particleService.playParticle(player.location, meta.particleEffect, player.world.players)
        }

        return true
    }
}