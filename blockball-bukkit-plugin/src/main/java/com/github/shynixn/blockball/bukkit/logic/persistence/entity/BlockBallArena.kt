package com.github.shynixn.blockball.bukkit.logic.persistence.entity

import com.github.shynixn.blockball.api.bukkit.persistence.entity.BukkitArena
import com.github.shynixn.blockball.api.business.enumeration.GameType
import com.github.shynixn.blockball.bukkit.logic.business.entity.action.YamlSerializer
import org.bukkit.Location

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
class BlockBallArena() : SelectedArea(), BukkitArena {

    constructor(name: String, corner1: Location, corner2: Location, customId: Long = 0) : this() {
        this.id = customId
        this.name = name
        this.displayName = name
        setCorners(corner1, corner2)
    }

    /** Unique [name] of the arena. */
    @YamlSerializer.YamlSerialize(orderNumber = 1, value = "name")
    override var name: String = ""

    /** [displayName] of the arena on signs or messages. */
    @YamlSerializer.YamlSerialize(orderNumber = 2, value = "displayname")
    override var displayName: String = ""

    /** Is the arena ready to be placed. */
    @YamlSerializer.YamlSerialize(orderNumber = 3, value = "enabled")
    override var enabled: Boolean = true

    /** [gameType] of the arena */
    @YamlSerializer.YamlSerialize(orderNumber = 4, value = "gamemode")
    override var gameType: GameType = GameType.HUBGAME

    /** Collection of the arena meta data. */
    @YamlSerializer.YamlSerialize(orderNumber = 7, value = "meta")
    override val meta: BlockBallMetaCollection = BlockBallMetaCollection()
}