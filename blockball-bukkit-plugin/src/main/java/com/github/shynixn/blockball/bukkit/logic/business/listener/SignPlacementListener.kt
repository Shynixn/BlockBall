package com.github.shynixn.blockball.bukkit.logic.business.listener

import com.github.shynixn.blockball.api.bukkit.business.entity.BukkitGame
import com.github.shynixn.blockball.api.persistence.entity.basic.IPosition
import com.github.shynixn.blockball.bukkit.logic.business.controller.GameRepository
import com.github.shynixn.blockball.bukkit.logic.business.helper.toPosition
import com.google.inject.Inject
import com.google.inject.Singleton
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.Plugin

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
@Singleton
class SignPlacementListener @Inject constructor(plugin: Plugin) : SimpleListener(plugin) {

    var placementCallBack: MutableMap<Player,CallBack> = HashMap()

    /**
     * Repository.
     */
    @Inject
    private var gameController: GameRepository? = null

    @EventHandler
    fun onClickOnPlacedSign(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK)
            return
        if (event.clickedBlock.type != Material.SIGN_POST && event.clickedBlock.type != Material.WALL_SIGN)
            return
        if (placementCallBack.containsKey(event.player)) {
            placementCallBack[event.player]!!.run(event.clickedBlock.location.toPosition())
            placementCallBack.remove(event.player)
        }
        val game = this.getGameFromSign(event.clickedBlock.state as Sign)
        if(game != null)
        {
            //CHECK DIFFERENT SIGNS
        }
    }


    private fun getGameFromSign(sign : Sign) : BukkitGame? {
        val lineIndex = - 1
        //DOS SOMETHING TRY CATCH
        return null
    }

    interface CallBack {
        fun run(position: IPosition)
    }
}