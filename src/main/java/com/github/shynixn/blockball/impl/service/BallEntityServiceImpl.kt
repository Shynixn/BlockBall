package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.Ball
import com.github.shynixn.blockball.contract.BallEntityService
import com.github.shynixn.blockball.entity.BallMeta
import com.github.shynixn.blockball.event.BallSpawnEvent
import com.github.shynixn.blockball.impl.BallCrossPlatformProxy
import com.github.shynixn.blockball.impl.BallDesignEntity
import com.github.shynixn.blockball.impl.BallHitboxEntity
import com.github.shynixn.blockball.impl.extension.toPosition
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.packet.api.EntityService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class BallEntityServiceImpl @Inject constructor(
    private val packetService: PacketService,
    private val itemService: ItemService,
    private val rayTracingService: RayTracingService,
    private val plugin: Plugin,
    private val entityService: EntityService
) : BallEntityService {

    private val ballHitBoxTracked = HashMap<Int, Ball>()
    private val ballDesignTracked = HashMap<Int, Ball>()
    private var isDisposed = false

    init {
        plugin.launch {
            while (!isDisposed) {
                for (ball in ballHitBoxTracked.values) {
                    ball.run()
                }
                delay(1.ticks)
            }
        }
    }

    /**
     * Spawns a temporary ball.
     * Returns a ball or null if the ball spawn event was cancelled.
     */
    override fun spawnTemporaryBall(location: Location, meta: BallMeta): Ball? {
        val position = location.toPosition()
        position.yaw = 0.0
        position.pitch = 0.0

        val ballHitBoxEntity = BallHitboxEntity(entityService.createNewEntityId(), meta.spawnpoint!!)
        ballHitBoxEntity.position = position
        ballHitBoxEntity.rayTracingService = rayTracingService
        ballHitBoxEntity.packetService = packetService

        val ballDesignEntity = BallDesignEntity(entityService.createNewEntityId())
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemService

        val ball = BallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity, plugin)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball
        ballHitBoxEntity.plugin = plugin

        val event = BallSpawnEvent(ball)
        Bukkit.getPluginManager().callEvent(event)

        if (event.isCancelled) {
            return null
        }

        ballHitBoxTracked[ballHitBoxEntity.entityId] = ball
        ballDesignTracked[ballDesignEntity.entityId] = ball

        return ball
    }

    /**
     * Tries to locate the ball by the given id.
     */
    override fun findBallByEntityId(id: Int): Ball? {
        if (ballDesignTracked.containsKey(id)) {
            return ballDesignTracked[id]
        }

        if (ballHitBoxTracked.containsKey(id)) {
            return ballHitBoxTracked[id]
        }

        return null
    }

    /**
     * Disables a ball from tracking.
     */
    override fun removeTrackedBall(ball: Ball) {
        if (ballDesignTracked.containsKey(ball.designEntityId)) {
            ballDesignTracked.remove(ball.designEntityId)
        }

        if (ballHitBoxTracked.containsKey(ball.hitBoxEntityId)) {
            ballHitBoxTracked.remove(ball.hitBoxEntityId)
        }
    }

    /**
     * Returns all balls managed by the plugin.
     */
    override fun getAllBalls(): List<Ball> {
        return ballDesignTracked.values.toList()
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * `try`-with-resources statement.
     */
    override fun close() {
        isDisposed = true
    }
}
