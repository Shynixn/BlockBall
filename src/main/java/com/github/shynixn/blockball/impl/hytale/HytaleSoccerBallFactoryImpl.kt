package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.htutils.plugin.HytaleWorldProxy
import com.github.shynixn.mcutils.common.CoroutineHandler
import com.github.shynixn.mcutils.packet.api.PacketService
import com.github.shynixn.mcutils.packet.api.RayTracingService
import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.entity.Entity
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.npc.NPCPlugin
import kotlinx.coroutines.runBlocking
import org.bukkit.Location

class HytaleSoccerBallFactoryImpl(
    private val packetService: PacketService,
    private val coroutineHandler: CoroutineHandler,
    private val rayTracingService: RayTracingService
) : SoccerBallFactory {
    private val soccerBalls = HashMap<Int, HytaleSoccerBall>()

    override fun createSoccerBall(
        location: Location,
        meta: SoccerBallMeta
    ): SoccerBall {
        return createSoccerBallForGame(location, meta, null)
    }

    override fun createSoccerBallForGame(
        location: Location,
        meta: SoccerBallMeta,
        game: SoccerGame?
    ): SoccerBall {
        val hytaleWorld = (location.world as HytaleWorldProxy).handle
        var ball: SoccerBall? = null
        runBlocking {
            coroutineHandler.execute(coroutineHandler.fetchLocationDispatcher(location)) {
                val store = hytaleWorld!!.entityStore.store
                val npcPlugin = NPCPlugin.get()
                val pos = Vector3d(
                    location.x,
                    location.y, location.z
                )
                val rot = Vector3f(
                    0f, location.yaw,
                    location.pitch
                )
                val pair = npcPlugin.spawnNPC(store, "BlockBall_Ball", null, pos, rot)
                val ref = pair!!.first()
                // It's a generic entity (or NPC). Find the Entity component.
                val archetype = store.getArchetype(ref)
                var entity: Entity? = null
                for (i in archetype.getMinIndex()..<archetype.length()) {
                    val type = archetype.get(i)
                    if (type != null && com.hypixel.hytale.server.core.entity.Entity::class.java.isAssignableFrom(type.getTypeClass())) {
                        entity = store.getComponent<Component<EntityStore?>?>(
                            ref,
                            type as ComponentType<EntityStore, Component<EntityStore?>>
                        ) as Entity?
                    }
                }
                val field = Entity::class.java.getDeclaredField("networkId").also { field ->
                    field.isAccessible = true

                }
                val entityId = field.get(entity) as Int
                soccerBalls[entityId] =
                    HytaleSoccerBall(meta, entityId, entity, coroutineHandler, location, game, rayTracingService)
                ball = soccerBalls[entityId]
            }.join()
        }

        return ball!!
    }

    override fun findBallByEntityId(id: Int): SoccerBall? {
        return soccerBalls[id]
    }

    override fun removeTrackedBall(ball: SoccerBall) {
        soccerBalls.remove(ball.designEntityId)
    }

    override fun getAllBalls(): List<SoccerBall> {
        return soccerBalls.values.toList()
    }

    override fun close() {
        for (soccerBall in soccerBalls.values) {
            soccerBall.remove()
        }
        soccerBalls.clear()
    }
}