package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.Version
import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.proxy.NMSBallProxy
import com.github.shynixn.blockball.api.business.service.BallEntityService
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.google.inject.Inject
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
class BallEntityServiceImpl @Inject constructor(private val version: Version, private val plugin: Plugin) : BallEntityService, Runnable {
    private val balls = ArrayList<BallProxy>()

    init {
        plugin.server.scheduler.runTaskTimer(plugin, this, 0L, 20L * 60 * 5)
    }

    /**
     * Spawns a temporary ball.
     */
    override fun <L> spawnTemporaryBall(location: L, meta: BallMeta): BallProxy {
        val designClazz = Class.forName("com.github.shynixn.blockball.bukkit.logic.business.nms.VERSION.BallDesign".replace("VERSION", version.bukkitId))
        val nmsProxy = designClazz.getDeclaredConstructor(Location::class.java, BallMeta::class.java, Boolean::class.java, UUID::class.java, LivingEntity::class.java)
                .newInstance(location, meta, false, UUID.randomUUID(), null) as NMSBallProxy

        val ballProxy = nmsProxy.proxy
        balls.add(ballProxy)

        return ballProxy
    }

    /**
     * Checks the entity collection for invalid ball entities and removes them.
     */
    override fun <E> cleanUpInvalidEntities(entities: Collection<E>) {
        for (entity in entities) {
            if (entity is ArmorStand && entity.customName != null && entity.customName == "ResourceBallsPlugin") {
                val optProxy = findBallFromEntity(entity)
                if (!optProxy.isPresent) {
                    entity.remove()
                    plugin.logger.log(Level.INFO, "Removed invalid BlockBall in chunk.")
                }
            }
        }
    }

    /**
     * Finds Ball from the given entity.
     */
    override fun <E> findBallFromEntity(entity: E): Optional<BallProxy> {
        balls.forEach { ball ->
            if (!ball.isDead) {
                if (ball.getDesignArmorstand<ArmorStand>() == entity || ball.getHitboxArmorstand<ArmorStand>() == entity) {
                    return Optional.of(ball)
                }
            }
        }

        return Optional.empty()
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