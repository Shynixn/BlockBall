package com.github.shynixn.blockball.bukkit.logic.business.service

import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.business.service.*
import com.google.inject.Inject
import kotlinx.coroutines.Dispatchers
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

/**
 * Handles the connection to the placeholder API plugin.
 */
class DependencyPlaceholderApiServiceImpl @Inject constructor(
    private val plugin: Plugin,
    private val gameService: GameService,
    private val placeHolderService: PlaceholderService,
    private val persistenceStatsService: PersistenceStatsService,
    private val coroutineSessionService: CoroutineSessionService
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

        coroutineSessionService.launch(Dispatchers.Unconfined) {
            try {
                val stats = persistenceStatsService.getStatsFromPlayerAsync(player!!).await()

                PlaceHolder.values().asSequence().filter { p -> s != null && s.startsWith(p.placeHolder) }
                    .forEach { p ->
                        if (p == PlaceHolder.STATS_WINRATE) {
                            result = String.format(
                                "%.2f",
                                stats.winRate
                            )
                        } else if (p == PlaceHolder.STATS_PLAYEDGAMES) {
                            result = stats.amountOfPlayedGames.toString()
                        } else if (p == PlaceHolder.STATS_GOALS_PER_GAME) {
                            result = String.format(
                                "%.2f",
                                stats.goalsPerGame
                            )
                        } else if (p == PlaceHolder.STATS_GOALS_AMOUNT) {
                            result = stats.amountOfGoals.toString()
                        } else if (p == PlaceHolder.STATS_WINS_AMOUNT) {
                            result = stats.amountOfWins.toString()
                        } else {
                            val data = s!!.split("_")
                            val game = gameService.getGameFromName(data[1])

                            if (game.isPresent) {
                                result = placeHolderService.replacePlaceHolders(data[0], game.get())
                            }
                        }
                    }
            } catch (ignored: Exception) {

            }
        }

        return result
    }
}
