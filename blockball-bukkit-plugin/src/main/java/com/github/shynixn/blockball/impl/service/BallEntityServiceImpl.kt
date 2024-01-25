package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.event.BallSpawnEvent
import com.github.shynixn.blockball.impl.BallCrossPlatformProxy
import com.github.shynixn.blockball.impl.BallDesignEntity
import com.github.shynixn.blockball.impl.BallHitboxEntity
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.packet.api.PacketService
import com.google.inject.Inject
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin

class BallEntityServiceImpl @Inject constructor(
    private val proxyService: ProxyService,
    private val packetService: PacketService,
    private val concurrencyService: ConcurrencyService,
    private val itemService: ItemService,
    private val rayTracingService: RayTracingService,
    private val plugin: Plugin
) : BallEntityService {

    private val ballHitBoxTracked = HashMap<Int, BallProxy>()
    private val ballDesignTracked = HashMap<Int, BallProxy>()

    init {
        concurrencyService.runTaskSync(0L, 1L) {
            for (ball in ballHitBoxTracked.values) {
                ball.run()
            }
        }
    }

    /**
     * Spawns a temporary ball.
     * Returns a ball or null if the ball spawn event was cancelled.
     */
    override fun <L> spawnTemporaryBall(location: L, meta: BallMeta): BallProxy? {
        val position = proxyService.toPosition(location)
        position.yaw = 0.0
        position.pitch = 0.0

        val ballHitBoxEntity = BallHitboxEntity(proxyService.createNewEntityId())
        ballHitBoxEntity.position = position
        ballHitBoxEntity.rayTracingService = rayTracingService
        ballHitBoxEntity.concurrencyService = concurrencyService
        ballHitBoxEntity.packetService = packetService
        ballHitBoxEntity.proxyService = proxyService

        val ballDesignEntity = BallDesignEntity(proxyService.createNewEntityId())
        ballDesignEntity.proxyService = proxyService
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemService

        val ball = BallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity, plugin)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball
        ball.proxyService = proxyService

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
    override fun findBallByEntityId(id: Int): BallProxy? {
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
    override fun removeTrackedBall(ball: BallProxy) {
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
    override fun getAllBalls(): List<BallProxy> {
        return ballDesignTracked.values.toList()
    }
}
