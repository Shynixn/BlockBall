@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.github.shynixn.blockball.api.compatibility.ActionEffect
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
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
class BallListener @Inject constructor(private val ballEntityService: BallEntityService, private val particleService: ParticleService, private val soundService: SoundService) : Listener {
    /**
     * Avoids saving the ball into the chunk data.
     *
     * @param event event
     */
    @EventHandler
    fun onChunkSaveEvent(event: ChunkUnloadEvent) {
        for (entity in event.chunk.entities) {
            if (entity is ArmorStand) {
                ballEntityService.findBallFromEntity(entity).ifPresent { ball ->
                    ball.remove()
                }
            }
        }
    }

    /**
     * Gets called when a player hits the ball and kicks the ball.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerInteractBallEvent(event: PlayerInteractEvent) {
        for (ball in this.ballEntityService.getAllBalls()) {
            if (ball.getLastInteractionEntity<Entity>().isPresent && ball.getLastInteractionEntity<Entity>().get() == event.player) {
                ball.throwByEntity(event.player)
                event.isCancelled = true
            }
        }
    }


    /**
     * Gets called when a player rightClicks on a ball.
     *
     * @param event event
     */
    @EventHandler
    fun entityRightClickBallEvent(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked !is ArmorStand) {
            return
        }

        this.dropBall(event.player)

        ballEntityService.findBallFromEntity(event.rightClicked).ifPresent { ball ->
            if (ball.meta.isCarryable && !ball.isGrabbed) {
                ball.grab(event.player)
            }
            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player hits the ball and kicks the ball.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerDamageBallEvent(event: EntityDamageByEntityEvent) {
        if (event.entity is ArmorStand) {
            val optBall = this.ballEntityService.findBallFromEntity(event.entity)
            if (optBall.isPresent) {
                val ball = optBall.get()
                ball.kickByEntity(event.damager)
            }
        }
    }


    /**
     * Gets called when the ball takes damage and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        if (event.entity is ArmorStand) {
            val optBall = ballEntityService.findBallFromEntity(event.entity)
            if (optBall.isPresent) {
                event.isCancelled = true
            }
        }
        if (event.entity is Player) {
            this.dropBall(event.entity as Player)
        }
    }


    /**
     * Drops the ball on command.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerCommandEvent(event: PlayerCommandPreprocessEvent) {
        this.dropBall(event.player)
    }

    /**
     * Drops the ball on inventory open.
     *
     * @param event event
     */
    @EventHandler
    fun onInventoryOpenEvent(event: InventoryOpenEvent) {
        this.dropBall(event.player as Player)
    }

    /**
     * Drops the ball on interact.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerEntityEvent(event: PlayerInteractEntityEvent) {
        this.dropBall(event.player)
    }

    /**
     * Drops the ball on death.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        this.dropBall(event.entity)
    }

    /**
     * Drops the ball on left.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        this.dropBall(event.player)
    }

    /**
     * Drops the ball on inventory click.
     *
     * @param event event
     */
    @EventHandler
    fun onInventoryOpen(event: InventoryClickEvent) {
        for (ball in this.ballEntityService.getAllBalls()) {
            if (ball.isGrabbed && ball.getLastInteractionEntity<Entity>().isPresent && ball.getLastInteractionEntity<Entity>().get() == event.whoClicked) {
                ball.deGrab()
                event.isCancelled = true
                (event.whoClicked as Player).updateInventory()
                event.whoClicked.closeInventory()
            }
        }
    }

    /**
     * Drops the ball on teleport.
     *
     * @param event event
     */
    @EventHandler
    fun onTeleportEvent(event: PlayerTeleportEvent) {
        this.dropBall(event.player)
    }

    /**
     * Drops the ball on item drop.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        this.dropBall(event.player)
    }

    /**
     * Drops the ball on Slot change.
     *
     * @param event event
     */
    @EventHandler
    fun onSlotChange(event: PlayerItemHeldEvent) {
        this.dropBall(event.player)
    }

    /**
     * Gets called when a player tries to leah a ball and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    fun entityLeashEvent(event: PlayerLeashEntityEvent) {
        if (event is LivingEntity) {
            val optBall = ballEntityService.findBallFromEntity(event.entity)
            if (optBall.isPresent) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Gets called when a player kicks a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballKickEvent(event: BallKickEvent) {
        this.playEffects(event.ball, ActionEffect.ONKICK)

        if (event.entity is HumanEntity) {
            val entity = event.entity as HumanEntity
           // Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Ball"), { this@BallListener.setMagnusForce(entity.eyeLocation.direction, event.resultVelocity, event.ball) }, this.spinDelay)
        }
    }

    /**
     * Gets called when a player interacts a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballInteractEvent(event: BallInteractEvent) {
        this.playEffects(event.ball, ActionEffect.ONINTERACTION)
    }

    /**
     * Gets called when a player throws a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballThrowEvent(event: BallThrowEvent) {
        this.playEffects(event.ball, ActionEffect.ONTHROW)
    }

    /**
     * Gets called when a player grabs a ball.
     *
     * @param event event
     */
    @EventHandler
    fun ballGrabEvent(event: BallGrabEvent) {
        this.playEffects(event.ball, ActionEffect.ONGRAB)
    }

    /**
     * Gets called when the ball spawns.
     *
     * @param event event
     */
    @EventHandler
    fun ballSpawnEvent(event: BallSpawnEvent) {
        this.playEffects(event.ball, ActionEffect.ONSPAWN)
    }

    /**
     * Gets called when a ball moves.
     *
     * @param event event
     */
    @EventHandler
    fun ballMoveEvent(event: BallPreMoveEvent) {
        if (!event.ball.isDead) {
            this.playEffects(event.ball, ActionEffect.ONMOVE)
        }
    }

    /**
     * Calculates post spinning.
     */
    @EventHandler
    fun ballPostMoveEvent(event: BallPostMoveEvent) {
        val ball = event.ball
        val force = ball.spinningForce

        if (ball.isDead || !event.hasMoved || force == 0.0) {
            return
        }

        ball.spin(event.resultVelocity, force).ifPresent { velocity ->
            event.resultVelocity = velocity
        }
    }


    /**
     * Plays effects.
     */
    private fun playEffects(ball: BallProxy, actionEffect: ActionEffect) {
        this.particleService.playParticle(ball.getLocation<Any>(), ball.meta.getParticleEffectOf(actionEffect), ball.getLocation<Location>().world.players)
        this.soundService.playSound(ball.getLocation<Any>(), ball.meta.getSoundEffectOf(actionEffect), ball.getLocation<Location>().world.players)
    }

    /**
     * Drops ball.
     */
    private fun dropBall(player: Player) {
        for (ball in this.ballEntityService.getAllBalls()) {
            if (ball.isGrabbed && ball.getLastInteractionEntity<Entity>().isPresent && ball.getLastInteractionEntity<Entity>().get() == player) {
                ball.deGrab()
            }
        }
    }
}