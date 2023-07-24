package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.service.*
import com.google.inject.Inject
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * Handles the connection to the placeholder API plugin.
 */
class DependencyPlaceholderApiServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val gameService: GameService,
    private val placeHolderService: PlaceholderService
) : PlaceholderExpansion(),
    DependencyPlaceholderApiService {
    private var registerd: Boolean = false

    /**
     * Registers the placeholder hook if it is not already registered.
     */
    override fun registerListener() {
        if (!registerd) {
            this.register()
            registerd = true
        }
    }

    /**
     * Gets the expansion version which is the same of the plugin version.
     */
    override fun getVersion(): String {
        return plugin.description.version
    }

    /**
     * Gets the expansion author for placeholderapi.
     */
    override fun getAuthor(): String {
        return plugin.description.authors[0]
    }

    /**
     * Gets the identifier which is required by placeholderapi to match the placeholder against this plugin.
     */
    override fun getIdentifier(): String {
        return "blockball"
    }

    /**
     * OnPlaceHolder Request
     *
     * @param player player
     * @param s      customText
     * @return result
     */
    override fun onPlaceholderRequest(player: Player?, s: String?): String? {
        var result: String? = null

        try {
            PlaceHolder.values().asSequence().filter { p -> s != null && s.startsWith(p.placeHolder) }
                .forEach { p ->
                    val data = s!!.split("_")
                    val optGame = gameService.getGameFromName(data[1])

                    if (optGame.isPresent) {
                        val game = optGame.get()
                        val teamData = if (game.redTeam.contains(player!!)) {
                            Pair(game.arena.meta.redTeamMeta, game.redTeam.size)
                        } else if (game.blueTeam.contains(player!!)) {
                            Pair(game.arena.meta.blueTeamMeta, game.blueTeam.size)
                        } else {
                            Pair(null, null)
                        }

                        result = placeHolderService.replacePlaceHolders(
                            data[0],
                            optGame.get(),
                            teamData.first,
                            teamData.second
                        )
                    }
                }
        } catch (ignored: Exception) {

        }

        return result
    }
}
