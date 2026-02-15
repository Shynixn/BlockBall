package com.github.shynixn.blockball.impl.hytale

import com.github.shynixn.blockball.contract.SoccerBall
import com.github.shynixn.blockball.entity.SoccerBallMeta
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class HytaleSoccerBall(override val meta: SoccerBallMeta, private var locationRef : Location, private val globalEntityId : Int) : SoccerBall {
    override var isDead: Boolean = false
    override val hitBoxEntityId: Int = globalEntityId
    override val designEntityId: Int = globalEntityId
    override val isOnGround: Boolean = false
    override var isInteractable: Boolean = false
    private var velocity : Vector = Vector(0.0, 0.0, 0.0)

    override fun teleport(location: Location) {
        locationRef = location
    }

    override fun getLocation(): Location {
        return locationRef
    }

    override fun getVelocity(): Vector {
        return velocity
    }

    override fun kickByPlayer(player: Player) {
    }

    override fun passByPlayer(player: Player) {
    }

    override fun remove() {
    }
}