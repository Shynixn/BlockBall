package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.blockball.contract.ParticleService
import com.github.shynixn.blockball.impl.extension.toSoundMeta
import com.github.shynixn.mcutils.common.sound.SoundService
import com.google.inject.Inject
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class DoubleJumpListener @Inject constructor(private val gameService: GameService, private val soundService: SoundService, private val particleService: ParticleService) : Listener {
    /**
     * Gets called when a player moves. Allows the executing player to start flying
     * for double jump calculation if the action is enabled and the player is in a game.
     */
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (!event.player.isOnGround) {
            return
        }

        val game = gameService.getGameFromPlayer(event.player)

        if (game.isPresent && game.get().arena.meta.doubleJumpMeta.enabled) {
            event.player.allowFlight = true
        }
    }

    /**
     * Gets called when a player doule presses the space key to start flying. Performs a double
     * jump action if the player is in a game and double jump is available.
     */
    @EventHandler
    fun onPlayerToggleFlightEvent(event: PlayerToggleFlightEvent) {
        if (event.player.gameMode == GameMode.CREATIVE || event.player.gameMode == GameMode.SPECTATOR) {
            return
        }

        val game = gameService.getGameFromPlayer(event.player)

        if (!game.isPresent || !game.get().arena.meta.doubleJumpMeta.enabled) {
            return
        }

        val player = event.player
        val meta = game.get().arena.meta.doubleJumpMeta

        player.allowFlight = false
        player.isFlying = false
        event.isCancelled = true

        if (game.get().doubleJumpCoolDownPlayers.containsKey(player)) {
            return
        }

        game.get().doubleJumpCoolDownPlayers[player] = meta.cooldown
        player.velocity = player.location.direction
                .multiply(meta.horizontalStrength)
                .setY(meta.verticalStrength)

        soundService.playSound(player.location,player.world.players, meta.soundEffect.toSoundMeta() )
        particleService.playParticle(player.location, meta.particleEffect, player.world.players)
    }
}
