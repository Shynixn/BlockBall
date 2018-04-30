package com.github.shynixn.blockball.bukkit.logic.business.helper

import com.github.shynixn.blockball.api.bukkit.business.entity.BukkitGame
import com.github.shynixn.blockball.api.business.enumeration.GameStatus
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.api.business.enumeration.PlaceHolder
import com.github.shynixn.blockball.api.persistence.entity.basic.StorageLocation
import com.github.shynixn.blockball.api.persistence.entity.meta.misc.TeamMeta
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.LowLevelGame
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.Minigame
import com.github.shynixn.blockball.bukkit.logic.business.entity.game.SoccerGame
import com.github.shynixn.blockball.bukkit.logic.persistence.configuration.Config
import com.github.shynixn.blockball.bukkit.logic.persistence.entity.basic.LocationBuilder
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.LeatherArmorMeta

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

internal fun setServerModt(text: String) {
    ModtHelper.setModt(text)
}

internal fun List<String>.toSingleLine(): String {
    val builder = StringBuilder()
    this.forEachIndexed { index, p ->
        builder.append(org.bukkit.ChatColor.translateAlternateColorCodes('&', p))
        if (index + 1 != this.size)
            builder.append('\n')
        builder.append(org.bukkit.ChatColor.RESET)
    }
    return builder.toString()
}

internal fun String.replaceGamePlaceholder(game: BukkitGame, teamMeta: TeamMeta<Location, ItemStack>? = null, team: List<Player>? = null): String {
    var cache = this.replace(PlaceHolder.TEAM_RED.placeHolder, game.arena.meta.redTeamMeta.displayName)
            .replace(PlaceHolder.ARENA_DISPLAYNAME.placeHolder, game.arena.displayName)
            .replace(PlaceHolder.TEAM_BLUE.placeHolder, game.arena.meta.blueTeamMeta.displayName)
            .replace(PlaceHolder.RED_COLOR.placeHolder, game.arena.meta.redTeamMeta.prefix)
            .replace(PlaceHolder.BLUE_COLOR.placeHolder, game.arena.meta.blueTeamMeta.prefix)
            .replace(PlaceHolder.RED_GOALS.placeHolder, game.redPoints.toString())
            .replace(PlaceHolder.BLUE_GOALS.placeHolder, game.bluePoints.toString())
            .replace(PlaceHolder.ARENA_SUM_CURRENTPLAYERS.placeHolder, (game as LowLevelGame).ingameStats.size.toString())
            .replace(PlaceHolder.ARENA_SUM_MAXPLAYERS.placeHolder, (game.arena.meta.blueTeamMeta.maxAmount + game.arena.meta.redTeamMeta.maxAmount).toString())


    if (teamMeta != null) {
        cache = cache.replace(PlaceHolder.ARENA_TEAMCOLOR.placeHolder, teamMeta.prefix)
                .replace(PlaceHolder.ARENA_TEAMDISPLAYNAME.placeHolder, teamMeta.displayName)
                .replace(PlaceHolder.ARENA_MAX_PLAYERS_ON_TEAM.placeHolder, teamMeta.maxAmount.toString())
    }

    if (team != null) {
        cache = cache.replace(PlaceHolder.ARENA_PLAYERS_ON_TEAM.placeHolder, team.size.toString())
    }

    when {
        game.status == GameStatus.RUNNING -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignRunning!!)
        game.status == GameStatus.ENABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignEnabled!!)
        game.status == GameStatus.DISABLED -> cache = cache.replace(PlaceHolder.ARENA_STATE.placeHolder, Config.stateSignDisabled!!)
    }

    if (game.arena.gameType == GameType.HUBGAME) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, "∞")
    } else if (game is Minigame) {
        cache = cache.replace(PlaceHolder.TIME.placeHolder, game.gameCountdown.toString())
                .replace(PlaceHolder.REMAINING_PLAYERS_TO_START.placeHolder, (game.arena.meta.redTeamMeta.minAmount + game.arena.meta.blueTeamMeta.minAmount - game.ingameStats.size).toString())
    }

    if (game is SoccerGame) {
        if (game.lastInteractedEntity != null && game.lastInteractedEntity is Player) {
            cache = cache.replace(PlaceHolder.LASTHITBALL.placeHolder, (game.lastInteractedEntity as Player).name)
        }
    }

    return cache.convertChatColors()
}

internal fun Player.sendScreenMessage(title: String, subTitle: String, game: BukkitGame) {
    ScreenUtils.setTitle(title.replaceGamePlaceholder(game), subTitle.replaceGamePlaceholder(game), 20, 20 * 3, 20, this)
}

internal fun Player.sendActionBarMessage(message: String) {
    ScreenUtils.setActionBar(message, this)
}

internal fun Array<Player>.sendActionBarMessage(message: String) {
    ScreenUtils.setActionBar(message, *this)
}


internal fun ItemStack.setColor(color: Color): ItemStack {
    if (this.itemMeta is LeatherArmorMeta) {
        val leatherMeta = this.itemMeta as LeatherArmorMeta
        leatherMeta.color = color
        this.itemMeta = leatherMeta
    }
    return this
}

internal fun String.stripChatColors(): String {
    return ChatColor.stripColor(this)
}

internal fun String.convertChatColors(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

internal fun Location.toPosition(): StorageLocation {
    return LocationBuilder(this)
}

internal fun StorageLocation.toBukkitLocation(): Location {
    return (this as LocationBuilder).toLocation()
}

