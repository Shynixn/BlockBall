package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallForceFieldService
import com.github.shynixn.blockball.api.persistence.entity.Game
import com.github.shynixn.blockball.bukkit.logic.business.extension.isLocationInSelection
import com.github.shynixn.blockball.bukkit.logic.business.extension.toLocation
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.util.BlockIterator
import org.bukkit.util.Vector

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
class BallForceFieldServiceImpl : BallForceFieldService {
    private val maxBlockIteratorSize = 10000

    /**
     * Calculates forcefield interactions and applies correct knockback
     * velocity regarding on the enabled game ball forcefield and the velocity of the ball.
     */
    override fun calculateForcefieldInteractions(game: Game, ball: BallProxy) {
        if (!game.arena.meta.customizingMeta.ballForceField) {
            return
        }

        val ballLocation = ball.getLocation<Location>()

        if (game.arena.isLocationInSelection(ballLocation)) {
            game.ballForceFieldArenaPosition = ballLocation.toPosition()
        } else if (game.ballForceFieldArenaPosition != null) {
            val velocity = ball.getVelocity<Vector>()
            val location = game.ballForceFieldArenaPosition!!.toLocation()

            val adder = game.arena.center.toLocation().toVector().subtract(location.toVector()).normalize().multiply(2.0)
            location.add(adder)

            ball.teleport(location)
            adder.y = velocity.y
            ball.setVelocity(adder.multiply(0.5))
        }

        if (game.ballForceFieldBlockPosition != null) {
            game.ballForceFieldBlockPosition!!.toLocation().block.type = Material.AIR
        }

        val ballVelocity = ball.getVelocity<Vector>()

        try {
            val iterator = BlockIterator(ballLocation.world!!, ballLocation.toVector(), ballVelocity, 0.0, maxBlockIteratorSize)
            var prevBlock: Block? = null

            while (iterator.hasNext()) {
                val block = iterator.next()

                if (prevBlock != null && !game.arena.isLocationInSelection(block.location)) {

                    if (prevBlock.type == Material.AIR) {
                        prevBlock.type = Material.BARRIER
                        game.ballForceFieldBlockPosition = prevBlock.location.toPosition()
                    }

                    return
                }

                prevBlock = block
            }
        } catch (e: Exception) {
            // BlockIterator implementation is unstable.
        }
    }
}