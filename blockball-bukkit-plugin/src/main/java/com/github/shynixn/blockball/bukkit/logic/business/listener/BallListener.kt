@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.ProtocolService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.core.logic.business.extension.accessible
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.event.world.ChunkUnloadEvent

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
class BallListener @Inject constructor(
    private val ballEntityService: BallEntityService,
    private val particleService: ParticleService,
    private val soundService: SoundService,
    private val protocolService: ProtocolService
) : Listener {
    private val packetPlayInUseEntityActionField by lazy {
        findClazz("net.minecraft.server.VERSION.PacketPlayInUseEntity")
            .getDeclaredField("action").accessible(true)
    }
    private val packetPlayInUseEntityIdField by lazy {
        findClazz("net.minecraft.server.VERSION.PacketPlayInUseEntity")
            .getDeclaredField("a").accessible(true)
    }

    /**
     * Gets called when a packet arrives.
     */
    @EventHandler
    fun onPacketEvent(event: PacketEvent) {
        val action = packetPlayInUseEntityActionField.get(event.packet)
        val entityId = packetPlayInUseEntityIdField.get(event.packet) as Int
        val ball = ballEntityService.findBallByEntityId(entityId) ?: return
        val isPass = action.toString() != "ATTACK"

        if (isPass) {
            ball.passByPlayer(event.player)
        } else {
            ball.shootByPlayer(event.player)
        }
    }

    /**
     * Registers the player on join.
     */
    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        protocolService.register(event.player)
    }

    /**
     * Unregisters the player on leave.
     */
    @EventHandler
    fun playerQuitEvent(event: PlayerQuitEvent) {
        protocolService.unRegister(event.player)
    }

    /**
     * Avoids saving the ball into the chunk data.
     *
     * @param event event
     */
    @EventHandler
    fun onChunkSaveEvent(event: ChunkUnloadEvent) {
        ballEntityService.cleanUpInvalidEntities(event.chunk.entities.toList())
    }

    /**
     * Checks if a ball armorstand is inside of the chunk and remove it.
     */
    @EventHandler
    fun onChunkLoadEvent(event: ChunkLoadEvent) {
        ballEntityService.cleanUpInvalidEntities(event.chunk.entities.toList())
    }

    /**
     * Gets called when a ball dies.
     *
     * @param event event
     */
    @EventHandler
    fun ballDeathEvent(event: BallDeathEvent) {
        this.ballEntityService.removeTrackedBall(event.ball)
    }

    /**
     * Gets called when a player left clicks a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballKickEvent(event: BallKickEvent) {
        this.playEffects(event.ball, BallActionType.ONKICK)
    }

    /**
     * Gets called when a player interacts a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballInteractEvent(event: BallInteractEvent) {
        this.playEffects(event.ball, BallActionType.ONINTERACTION)
    }

    /**
     * Gets called when a player throws a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballThrowEvent(event: BallThrowEvent) {
        this.playEffects(event.ball, BallActionType.ONTHROW)
    }

    /**
     * Gets called when a player grabs a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballGrabEvent(event: BallGrabEvent) {
        this.playEffects(event.ball, BallActionType.ONGRAB)
    }

    /**
     * Gets called when the ball spawns.
     *
     * @param event event
     */
    @EventHandler
    fun ballSpawnEvent(event: BallSpawnEvent) {
        this.playEffects(event.ball, BallActionType.ONSPAWN)
    }

    /**
     * Gets called when a ball moves.
     *
     * @param event event
     */
    @EventHandler
    fun ballMoveEvent(event: BallPreMoveEvent) {
        if (!event.ball.isDead) {
            this.playEffects(event.ball, BallActionType.ONMOVE)
        }
    }

    /**
     * Gets called when a ball gets shot into goal.
     *
     * @param event event
     */
    @EventHandler
    fun gameGoalEvent(event: GameGoalEvent) {
        playEffects(event.game.ball!!, BallActionType.ONGOAL)
    }

    /**
     * Plays effects.
     */
    private fun playEffects(ball: BallProxy, actionEffect: BallActionType) {
        if (ball.meta.particleEffects.containsKey(actionEffect)) {
            this.particleService.playParticle(
                ball.getLocation<Any>(),
                ball.meta.particleEffects[actionEffect]!!,
                ball.getLocation<Location>().world!!.players
            )
        }

        if (ball.meta.soundEffects.containsKey(actionEffect)) {
            this.soundService.playSound(
                ball.getLocation<Any>(),
                ball.meta.soundEffects[actionEffect]!!,
                ball.getLocation<Location>().world!!.players
            )
        }
    }
}
