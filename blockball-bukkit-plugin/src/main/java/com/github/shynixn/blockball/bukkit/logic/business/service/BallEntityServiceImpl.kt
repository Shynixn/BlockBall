package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.BlockBallApi
import com.github.shynixn.blockball.api.bukkit.event.BallSpawnEvent
import com.github.shynixn.blockball.api.business.enumeration.EntityType
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.bukkit.logic.business.extension.findClazz
import com.github.shynixn.blockball.bukkit.logic.business.extension.toPosition
import com.github.shynixn.blockball.bukkit.logic.business.proxy.BallCrossPlatformProxy
import com.github.shynixn.blockball.bukkit.logic.business.proxy.BallDesignEntity
import com.github.shynixn.blockball.bukkit.logic.business.proxy.BallHitboxEntity
import com.github.shynixn.blockball.bukkit.logic.business.proxy.HologramProxyImpl
import com.github.shynixn.blockball.core.logic.business.extension.stripChatColors
import com.github.shynixn.blockball.core.logic.persistence.entity.PositionEntity
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.LivingEntity
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.logging.Level
import kotlin.collections.ArrayList

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
class BallEntityServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val entityRegistry: EntityRegistrationService,
    private val proxyService: ProxyService,
    private val packetService: PacketService,
    private val concurrencyService: ConcurrencyService,
    private val itemTypeService: ItemTypeService
) : BallEntityService, Runnable {

    private var registered = false
    private val balls = ArrayList<BallProxy>()

    init {
        plugin.server.scheduler.runTaskTimer(plugin, this, 0L, 20L * 60 * 5)
        plugin.server.scheduler.runTaskTimer(plugin, Runnable {
            for (ball in balls) {
                ball.run()
            }
        }, 0L, 1L)
    }

    /**
     * Spawns a temporary ball.
     * Returns a ball or null if the ball spawn event was cancelled.
     */
    override fun <L> spawnTemporaryBall(location: L, meta: BallMeta): BallProxy? {
        require(location is Location)

        location.yaw = 0.0F

        val ballHitBoxEntity = BallHitboxEntity(
            proxyService.createNewEntityId()
        )
        ballHitBoxEntity.position = PositionEntity(location.world!!.name, location.x, location.y, location.z)
        ballHitBoxEntity.proxyService = proxyService
        ballHitBoxEntity.concurrencyService = concurrencyService
        ballHitBoxEntity.packetService = packetService

        val ballDesignEntity = BallDesignEntity(proxyService.createNewEntityId())
        ballDesignEntity.proxyService = proxyService
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemTypeService

        val ball = BallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball

        val event = BallSpawnEvent(ball)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return null
        }

        balls.add(ball)

        return ball
    }

    /**
     * Tries to locate the ball by the given id.
     */
    override fun findBallByEntityId(id: Int): BallProxy? {
        return balls.firstOrNull { b -> b.hitBoxEntityId == id || b.designEntityId == id }
    }

    /**
     * Disables a ball from tracking.
     */
    override fun removeTrackedBall(ball: BallProxy) {
        if (balls.contains(ball)) {
            balls.remove(ball)
        }
    }

    /**
     * Registers entities on the server when not already registered.
     * Returns true if registered. Returns false when not registered.
     */
    override fun registerEntitiesOnServer(): Boolean {
        if (registered) {
            return false
        }

        val slimeClazz = findClazz("com.github.shynixn.blockball.bukkit.logic.business.nms.VERSION.BallHitBox")

        entityRegistry.register(slimeClazz, EntityType.SLIME)
        registered = true
        return true
    }

    /**
     * Checks the entity collection for invalid ball entities and removes them.
     */
    override fun <E> cleanUpInvalidEntities(entities: Collection<E>) {
        for (entity in entities) {
            if (entity !is LivingEntity) {
                continue
            }

            // Holograms hide a boots marker of every spawned armorstand.
            if (entity is ArmorStand && entity.equipment != null && entity.equipment!!.boots != null) {
                val boots = entity.equipment!!.boots

                if (boots!!.itemMeta != null && boots.itemMeta!!.lore != null && boots.itemMeta!!.lore!!.size > 0) {
                    val lore = boots.itemMeta!!.lore!![0]

                    if (lore.stripChatColors() == "BlockBallHologram") {
                        var exists = false

                        for (game in BlockBallApi.resolve(GameService::class.java).getAllGames()) {
                            for (hologram in game.holograms) {
                                if ((hologram as HologramProxyImpl).armorstands.contains(entity)) {
                                    exists = true
                                }

                            }
                        }

                        if (!exists) {
                            entity.remove()
                        }

                        plugin.logger.log(Level.INFO, "Removed invalid Hologram in chunk.")
                    }
                }
            }
        }
    }

    /**
     * Returns all balls managed by the plugin.
     */
    override fun getAllBalls(): List<BallProxy> {
        return balls
    }

    /**
     * When an object implementing interface `Runnable` is used
     * to create a thread, starting the thread causes the object's
     * `run` method to be called in that separately executing
     * thread.
     *
     *
     * The general contract of the method `run` is that it may
     * take any action whatsoever.
     *
     * @see java.lang.Thread.run
     */
    override fun run() {
        balls.toTypedArray().forEach { ball ->
            if (ball.isDead) {
                this.balls.remove(ball)
            }
        }
    }
}
