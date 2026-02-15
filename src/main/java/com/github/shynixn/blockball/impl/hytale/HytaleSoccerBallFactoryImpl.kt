package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.contract.SoccerBallFactory
import com.github.shynixn.blockball.contract.SoccerGame
import com.github.shynixn.blockball.entity.SoccerBallMeta
import com.github.shynixn.mcutils.packet.api.PacketService
import org.bukkit.Location

class HytaleSoccerBallFactoryImpl(private val packetService: PacketService) : SoccerBallFactory {
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
        val entityId = packetService.getNextEntityId(location.world!!)
        soccerBalls[entityId] = HytaleSoccerBall(meta, location, entityId)
        return soccerBalls[entityId]!!
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
    }
}