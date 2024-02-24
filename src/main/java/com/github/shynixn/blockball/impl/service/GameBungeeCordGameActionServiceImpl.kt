package com.github.shynixn.blockball.impl.service

import com.github.shynixn.blockball.BlockBallLanguage
import com.github.shynixn.blockball.BlockBallPlugin
import com.github.shynixn.blockball.contract.GameBungeeCordGameActionService
import com.github.shynixn.blockball.entity.BungeeCordGame
import com.github.shynixn.mccoroutine.bukkit.launch
import com.github.shynixn.mcutils.common.ChatColor
import com.github.shynixn.mcutils.common.translateChatColors
import com.google.common.io.ByteStreams
import com.google.inject.Inject
import kotlinx.coroutines.delay
import org.bukkit.entity.Player

class GameBungeeCordGameActionServiceImpl @Inject constructor(
    private val plugin: BlockBallPlugin,
) :
    GameBungeeCordGameActionService {
    /**
     * Closes the given game and all underlying resources.
     */
    override fun closeGame(game: BungeeCordGame) {
        setMotd(game, BlockBallLanguage.bungeeCordMotdRestarting)

        if (!plugin.isEnabled()) {
            return
        }

        plugin.launch {
            delay(1000L * 20L)
            plugin.shutdownServer()
        }
    }

    /**
     * Lets the given [player] leave the given [game].
     * Does nothing if the player is not in the game.
     */
    override fun leaveGame(game: BungeeCordGame, player: Player) {
        if (game.arena.meta.bungeeCordMeta.fallbackServer.isEmpty()) {
            player.kickPlayer(BlockBallLanguage.bungeeCordKickMessage)
        } else {
            val out = ByteStreams.newDataOutput()
            out.writeUTF("Connect")
            out.writeUTF(game.arena.meta.bungeeCordMeta.fallbackServer)
            player.sendPluginMessage(plugin, "BungeeCord",out.toByteArray())
        }
    }

    /**
     * Handles the game actions per tick. [ticks] parameter shows the amount of ticks
     * 0 - 20 for each second.
     */
    override fun handle(game: BungeeCordGame, ticks: Int) {
        if (ticks < 20) {
            return
        }

        if (game.playing) {
            setMotd(game, BlockBallLanguage.bungeeCordMotdRunning)
        } else {
            setMotd(game, BlockBallLanguage.bungeeCordMotdJoinAble)
        }
    }

    private fun setMotd(game: BungeeCordGame, message: String) {
        val builder = java.lang.StringBuilder("[")
        builder.append((message.replace("[", "").replace("]", "")))
        builder.append(ChatColor.RESET.toString())
        builder.append("]")
        game.modt = builder.toString().translateChatColors()
    }
}
