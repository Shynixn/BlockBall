package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.entity.SoccerBallSettings
import com.github.shynixn.blockball.event.BallSpawnEvent
import com.github.shynixn.blockball.impl.SoccerBallCrossPlatformProxy
import com.github.shynixn.blockball.impl.BallDesignEntity
import com.github.shynixn.blockball.impl.BallHitboxEntity
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mccoroutine.bukkit.ticks
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.toVector3d
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class SoccerBallFactoryImpl @Inject constructor(
    private val packetService: PacketService,
    private val itemService: ItemService,
    private val rayTracingService: RayTracingService,
    private val plugin: Plugin
) : SoccerBallFactory {

    private val ballHitBoxTracked = HashMap<Int, SoccerBall>()
    private val ballDesignTracked = HashMap<Int, SoccerBall>()
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
     * Creates a new SoccerBall.
     */
    override fun createSoccerBall(location: Location, meta: SoccerBallSettings): SoccerBall {
        val position = location.toVector3d()
        position.yaw = 0.0
        position.pitch = 0.0

        val ballHitBoxEntity = BallHitboxEntity(packetService.getNextEntityId(), meta.spawnpoint!!)
        ballHitBoxEntity.position = position
        ballHitBoxEntity.rayTracingService = rayTracingService
        ballHitBoxEntity.packetService = packetService

        val ballDesignEntity = BallDesignEntity(packetService.getNextEntityId())
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemService

        val ball = SoccerBallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity, plugin)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball
        ballHitBoxEntity.plugin = plugin

        val event = BallSpawnEvent(ball)
        Bukkit.getPluginManager().callEvent(event)

        ballHitBoxTracked[ballHitBoxEntity.entityId] = ball
        ballDesignTracked[ballDesignEntity.entityId] = ball

        return ball
    }

    /**
     * Tries to locate the ball by the given id.
     */
    override fun findBallByEntityId(id: Int): SoccerBall? {
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
    override fun removeTrackedBall(ball: SoccerBall) {
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
    override fun getAllBalls(): List<SoccerBall> {
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
