@file:Suppress("DEPRECATION")

package com.github.shynixn.blockball.impl.listener

import com.github.shynixn.blockball.contract.GameService
import com.github.shynixn.mcutils.common.sound.SoundService
import com.github.shynixn.shyparticles.contract.ParticleEffectService
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class DoubleJumpListener(
    private val gameService: GameService,
    private val soundService: SoundService,
    private val effectService: ParticleEffectService
) : Listener {
    /**
     * Gets called when a player moves. Allows the executing player to start flying
     * for double jump calculation if the action is enabled and the player is in a game.
     */
    @EventHandler
    fun onPlayerMoveEvent(event: PlayerMoveEvent) {
        if (!event.player.isOnGround) {
            return
        }

        val game = gameService.getByPlayer(event.player) ?: return

        if (game.arena.meta.doubleJumpMeta.enabled) {
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

        val game = gameService.getByPlayer(event.player) ?: return

        val player = event.player
        val meta = game.arena.meta.doubleJumpMeta

        player.allowFlight = false
        player.isFlying = false
        event.isCancelled = true

        // This has to be to ensure that the minigame force field does not grant the players flight.
        if (!game.arena.meta.doubleJumpMeta.enabled) {
            return
        }

        if (game.doubleJumpCoolDownPlayers.containsKey(player)) {
            return
        }

        game.doubleJumpCoolDownPlayers[player] = meta.cooldown
        player.velocity = player.location.direction
            .multiply(meta.horizontalStrength)
            .setY(meta.verticalStrength)

        val effect = effectService.getEffectMetaFromName(game.arena.meta.doubleJumpMeta.effectName)
        if (effect != null) {
            effectService.startEffect(effect, { player.location }, null, null)
        }
    }
}
