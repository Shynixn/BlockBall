package com.github.shynixn.blockball.contract

import com.github.shynixn.blockball.entity.Particle
import org.bukkit.Location
import org.bukkit.entity.Player

interface ParticleService {
    /**
     * Plays the given [particle] at the given [location] for the given [players].
     */
    fun playParticle(location: Location, particle: Particle, players: Collection<Player>)
}
