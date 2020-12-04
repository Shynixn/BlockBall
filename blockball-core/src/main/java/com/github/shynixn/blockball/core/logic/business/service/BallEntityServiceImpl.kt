package com.github.shynixn.blockball.core.logic.business.service

import com.github.shynixn.blockball.api.business.proxy.BallProxy
import com.github.shynixn.blockball.api.business.service.*
import com.github.shynixn.blockball.api.persistence.entity.BallMeta
import com.github.shynixn.blockball.core.logic.business.proxy.BallCrossPlatformProxy
import com.github.shynixn.blockball.core.logic.business.proxy.BallDesignEntity
import com.github.shynixn.blockball.core.logic.business.proxy.BallHitboxEntity
import com.github.shynixn.blockball.core.logic.persistence.event.BallSpawnEventEntity
import com.google.inject.Inject

class BallEntityServiceImpl @Inject constructor(
    private val proxyService: ProxyService,
    private val packetService: PacketService,
    private val concurrencyService: ConcurrencyService,
    private val itemTypeService: ItemTypeService,
    private val rayTracingService: RayTracingService,
    private val loggingService: LoggingService,
    private val eventService: EventService
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
        ballHitBoxEntity.eventService = eventService
        ballHitBoxEntity.proxyService = proxyService

        val ballDesignEntity = BallDesignEntity(proxyService.createNewEntityId())
        ballDesignEntity.proxyService = proxyService
        ballDesignEntity.packetService = packetService
        ballDesignEntity.itemService = itemTypeService

        val ball = BallCrossPlatformProxy(meta, ballDesignEntity, ballHitBoxEntity)
        ballDesignEntity.ball = ball
        ballHitBoxEntity.ball = ball
        ball.loggingService = loggingService
        ball.eventService = eventService
        ball.proxyService = proxyService

        val event = BallSpawnEventEntity(ball)
        eventService.sendEvent(event)

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
