@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.event.*
import com.github.shynixn.blockball.api.business.enumeration.BallActionType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.business.service.ParticleService
import com.github.shynixn.blockball.api.business.service.SoundService
import com.google.inject.Inject
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.entity.PlayerLeashEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.player.*
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
    private val soundService: SoundService
) : Listener {
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
     * Gets called when a player interacts on a ball item.
     * This action generally performs throwing the ball.
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerInteractBallEvent(event: PlayerInteractEvent) {
        for (ball in this.ballEntityService.getAllBalls()) {
            if (ball.isGrabbed) {
                ball.getLastInteractionEntity<Entity>().ifPresent {
                    if (it is Player && it.uniqueId == event.player.uniqueId) {
                        ball.throwByPlayer(event.player)
                        event.isCancelled = true
                    }
                }
            }
        }
    }


    /**
     * Gets called when a player right-click a ball.
     * 1) Player grabs the ball if he were sneaking (SHIFT)
     * 2) Otherwise, player performs passing
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerRightClickBallEvent(event: PlayerInteractAtEntityEvent) {
        if (event.rightClicked.customName != "ResourceBallsPlugin") {
            return
        }

        this.dropBall(event.player)

        ballEntityService.findBallFromEntity(event.rightClicked).ifPresent { ball ->
            if (event.player.isSneaking) {
                ball.grab(event.player)
            } else {
                ball.passByPlayer(event.player)
            }

            event.isCancelled = true
        }
    }

    /**
     * Gets called when a player left-click a ball.
     * 1) Player grabs the ball if he were sneaking (SHIFT)
     * 2) Otherwise, player performs shooting
     *
     * @param event event
     */
    @EventHandler
    fun onPlayerDamageBallEvent(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity.customName == "ResourceBallsPlugin") {
            val optBall = this.ballEntityService.findBallFromEntity(event.entity)
            if (optBall.isPresent) {
                val ball = optBall.get()
                val player = event.damager as Player

                if (player.isSneaking) {
                    ball.grab(player)
                } else {
                    ball.shootByPlayer(player)
                }
            }
        }
    }


    /**
     * Gets called when an entity takes damage.
     * 1) Cancel all the damage if victim is a ball entity
     * 2) If victim is a player grabbing a ball, he/she will drop it
     *
     * @param event event
     */
    @EventHandler
    fun entityDamageEvent(event: EntityDamageEvent) {
        if (event.entity.customName == "ResourceBallsPlugin") {
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
     * Gets called when a player tries to leash a ball and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    fun entityLeashEvent(event: PlayerLeashEntityEvent) {
        if (event.entity is LivingEntity) {
            val optBall = ballEntityService.findBallFromEntity(event.entity)
            if (optBall.isPresent) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Gets called when a player left clicks a ball.
     *
     * @param event event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun ballKickEvent(event: BallKickEvent) {
        if (!event.isCancelled) {
            this.playEffects(event.ball, BallActionType.ONKICK)
        }
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
            this.particleService.playParticle(ball.getLocation<Any>(), ball.meta.particleEffects[actionEffect]!!, ball.getLocation<Location>().world!!.players)
        }

        if (ball.meta.soundEffects.containsKey(actionEffect)) {
            this.soundService.playSound(ball.getLocation<Any>(), ball.meta.soundEffects[actionEffect]!!, ball.getLocation<Location>().world!!.players)
        }
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