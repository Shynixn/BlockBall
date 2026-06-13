package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerBallService
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.blockball.event.BallSpawnEvent
import com.github.shynixn.blockball.impl.CustomRayTracingServiceNativeImpl
import com.github.shynixn.blockball.impl.SoccerBallImpl
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.regionDispatcher
import com.github.shynixn.mcutils.common.item.ItemService
import com.github.shynixn.mcutils.common.repository.CacheRepository
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.shyparticles.contract.ParticleEffectService
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.Plugin

class SoccerBallServiceImpl(
    private val repository: CacheRepository<SoccerBallMeta>,
    private val packetService: PacketService,
    private val rayTracingService: CustomRayTracingServiceNativeImpl,
    private val itemService: ItemService,
    private val particleEffectService: ParticleEffectService,
    private val plugin: Plugin
) : SoccerBallService {
    private val soccerBallByEntity = HashMap<Int, SoccerBall>()

    /**
     * Spawns a soccer ball of the given name.
     */
    override fun spawn(name: String, location: Location): SoccerBall {
        val renderEntityId = packetService.getNextEntityId(location.world!!)
        val hitBoxEntityId = packetService.getNextEntityId(location.world!!)
        val ballMeta = repository.getCache()?.firstOrNull() { e -> e.name.equals(name, true) }

        if (ballMeta == null) {
            throw IllegalArgumentException("Ball with name $name does not exist!")
        }

        val soccerBallImpl = SoccerBallImpl(
            location,
            packetService,
            particleEffectService,
            rayTracingService,
            plugin,
            itemService,
            ballMeta,
            hitBoxEntityId,
            renderEntityId
        )
        soccerBallByEntity[renderEntityId] = soccerBallImpl
        soccerBallByEntity[hitBoxEntityId] = soccerBallImpl

        plugin.launch {
            var lastMilliSeconds = System.currentTimeMillis()
            while (!soccerBallImpl.isDead) {
                withContext(plugin.regionDispatcher(soccerBallImpl.getLocation())) {
                    val currentMilliSeconds = System.currentTimeMillis()
                    soccerBallImpl.update((currentMilliSeconds - lastMilliSeconds).toInt())
                    lastMilliSeconds = currentMilliSeconds
                }
            }
            soccerBallByEntity.remove(soccerBallImpl.renderEntityId)
            soccerBallByEntity.remove(soccerBallImpl.hitBoxEntityId)
        }

        plugin.launch(plugin.regionDispatcher(location)) {
            val event = BallSpawnEvent(soccerBallImpl)
            Bukkit.getPluginManager().callEvent(event)
        }
        return soccerBallImpl
    }

    /**
     * Tries to get the ball by entity id.
     */
    override fun getByEntityId(id: Int): SoccerBall? {
        return soccerBallByEntity[id]
    }

    /**
     * Gets all active soccer balls.
     */
    override fun getAllActive(): List<SoccerBall> {
        return HashSet(soccerBallByEntity.values).toList()
    }

    override fun close() {
        val soccerBalls = HashSet(soccerBallByEntity.values)

        for (soccerBall in soccerBalls) {
            soccerBall.remove()
        }

        soccerBallByEntity.clear()
    }
}